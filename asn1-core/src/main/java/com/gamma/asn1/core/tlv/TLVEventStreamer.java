package com.gamma.asn1.core.tlv;

import com.gamma.asn1.core.exception.CorruptTLVException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Reads a raw binary ASN.1 data stream and emits structural events to a {@link TLVListener}.
 * <p>
 * This class implements a streaming, forward-only parser. It decodes the Tag and Length
 * fields of each TLV triplet and pushes events to a listener, which is responsible for
 * handling the Value. This design is memory-efficient, as it does not need to load the
 * entire data stream into memory. It can process files of virtually any size.
 * <p>
 * The streamer is responsible for identifying:
 * <ul>
 *     <li>The start of a new data element (onStartTag).</li>
 *     <li>Whether the element is constructed (contains other TLV elements) or primitive (contains a raw value).</li>
 *     <li>The raw byte value of a primitive element (onPrimitiveValue).</li>
 *     <li>The end of a data element (onEndTag).</li>
 * </ul>
 */
public class TLVEventStreamer {

    private long bytesRead = 0;

    /**
     * Processes the entire input stream and sends events to the provided listener.
     *
     * @param inputStream The binary data stream to process. Must support {@code read()}.
     * @param listener    The listener that will receive TLV events.
     * @throws IOException         If an I/O error occurs while reading from the stream.
     * @throws CorruptTLVException If the TLV structure is malformed (e.g., unexpected end of stream).
     */
    public void process(InputStream inputStream, TLVListener listener) throws IOException, ASN1ProcessingException {
        // The initial call processes until the stream ends.
        processRecursive(inputStream, listener, Long.MAX_VALUE, "root"); // Assuming "root" for top-level path
    }

    /**
     * A recursive helper to process a bounded part of the stream, used for handling constructed types.
     *
     * @param inputStream   The stream to read from.
     * @param listener      The listener for events.
     * @param bytesToProcess The number of bytes that constitute the current constructed element's value.
     * @param currentPath   The current ASN.1 path for context in exceptions.
     * @return The total number of bytes read during this invocation.
     */
    private long processRecursive(InputStream inputStream, TLVListener listener, long bytesToProcess, String currentPath) throws IOException, ASN1ProcessingException {
        long localBytesRead = 0;
        long tagStartOffset;

        while (localBytesRead < bytesToProcess) {
            tagStartOffset = this.bytesRead;
            int firstTagByte = inputStream.read();

            if (firstTagByte == -1) {
                if (bytesToProcess != Long.MAX_VALUE && localBytesRead < bytesToProcess) {
                    throw new CorruptTLVException("Unexpected end of stream while processing a constructed type.", this.bytesRead, currentPath, null);
                }
                break;
            }
            this.bytesRead++;
            localBytesRead++;

            byte[] tag = {(byte) firstTagByte};
            boolean isConstructed = (firstTagByte & 0x20) != 0;

            // Store current global offset before parsing length, as parseLength also increments bytesRead
            long lengthFieldStartOffset = this.bytesRead;
            int length;
            try {
                length = parseLength(inputStream);
            } catch (CorruptTLVException e) {
                // Enhance exception with current context
                throw new CorruptTLVException(e.getMessage(), e.getByteOffset(), currentPath + ".length", e.getCause());
            }
            // localBytesRead for length field is accounted for by parseLength's increment of this.bytesRead
            // and then taking the difference if needed, but simpler to track overall consumption.

            try {
                listener.onStartTag(tag, length, isConstructed, tagStartOffset);
            } catch (Exception e) { // Listener can throw generic Exception as per its signature
                 throw new ASN1ProcessingException("Listener failed onStartTag for tag " + bytesToHex(tag), tagStartOffset, currentPath, e);
            }


            if (isConstructed) {
                String nextPath = currentPath + "." + bytesToHex(tag); // Example path segment
                long nestedBytesRead = processRecursive(inputStream, listener, length, nextPath);
                if (nestedBytesRead != length) {
                     throw new CorruptTLVException(
                                String.format("Constructed type with tag %s and declared length %d contained %d bytes.", bytesToHex(tag), length, nestedBytesRead),
                                tagStartOffset, nextPath, null);
                }
                localBytesRead += nestedBytesRead; // Value bytes are the sum of children's full TLV sizes
            } else {
                if (length > 0) {
                    byte[] value = readValue(inputStream, length, currentPath + "." + bytesToHex(tag) + ".value");
                    try {
                        listener.onPrimitiveValue(value);
                    } catch (Exception e) {
                        throw new ASN1ProcessingException("Listener failed onPrimitiveValue for tag " + bytesToHex(tag), this.bytesRead - length, currentPath, e);
                    }
                } else {
                    try {
                        listener.onPrimitiveValue(new byte[0]);
                    } catch (Exception e) {
                         throw new ASN1ProcessingException("Listener failed onPrimitiveValue (empty) for tag " + bytesToHex(tag), this.bytesRead, currentPath, e);
                    }
                }
                localBytesRead += length; // Value bytes
            }

            try {
                listener.onEndTag(tag);
            } catch (Exception e) {
                 throw new ASN1ProcessingException("Listener failed onEndTag for tag " + bytesToHex(tag), this.bytesRead, currentPath, e);
            }
        }
        return localBytesRead;
    }

    private int parseLength(InputStream inputStream) throws IOException, CorruptTLVException {
        long lengthStartOffset = this.bytesRead;
        int firstLengthByte = inputStream.read();
        if (firstLengthByte == -1) {
            throw new CorruptTLVException("Unexpected end of stream while reading length.", lengthStartOffset, "currentPath.length", null);
        }
        this.bytesRead++;

        if ((firstLengthByte & 0x80) == 0) {
            return firstLengthByte;
        } else {
            int numLengthBytes = firstLengthByte & 0x7F;
            if (numLengthBytes == 0) {
                 throw new CorruptTLVException("Indefinite length form not supported.", lengthStartOffset, "currentPath.length", null);
            }
            if (numLengthBytes > 4) {
                throw new CorruptTLVException("Length field too long (max 4 bytes for int): " + numLengthBytes, lengthStartOffset, "currentPath.length", null);
            }

            byte[] lengthBytes = readValue(inputStream, numLengthBytes, "currentPath.lengthValue");
            int length = 0;
            for (byte b : lengthBytes) {
                length = (length << 8) | (b & 0xFF);
            }
            // Check for impossibly large lengths if needed, e.g., if length would cause int overflow during accumulation.
            // For now, numLengthBytes > 4 handles this for standard int.
            return length;
        }
    }

    private byte[] readValue(InputStream inputStream, int length, String pathContext) throws IOException, CorruptTLVException {
        if (length < 0) {
            throw new CorruptTLVException("Invalid negative length specified: " + length, this.bytesRead, pathContext, null);
        }
        byte[] value = new byte[length];
        int totalBytesActuallyRead = 0;
        int bytesReadThisTime;

        long valueStartOffset = this.bytesRead;

        while(totalBytesActuallyRead < length) {
            bytesReadThisTime = inputStream.read(value, totalBytesActuallyRead, length - totalBytesActuallyRead);
            if (bytesReadThisTime == -1) {
                 throw new CorruptTLVException(
                    String.format("Expected to read %d bytes for value, but stream ended after %d at path %s.", length, totalBytesActuallyRead, pathContext),
                    valueStartOffset, pathContext, null);
            }
            totalBytesActuallyRead += bytesReadThisTime;
        }
        this.bytesRead += totalBytesActuallyRead; // totalBytesActuallyRead should be equal to length here
        return value;
    }

    // Helper to convert byte array to hex string for logging/exceptions
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}