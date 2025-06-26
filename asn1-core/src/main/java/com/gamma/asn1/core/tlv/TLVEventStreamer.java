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
    public void process(InputStream inputStream, TLVListener listener) throws Exception {
        // The initial call processes until the stream ends.
        processRecursive(inputStream, listener, Long.MAX_VALUE);
    }

    /**
     * A recursive helper to process a bounded part of the stream, used for handling constructed types.
     *
     * @param inputStream   The stream to read from.
     * @param listener      The listener for events.
     * @param bytesToProcess The number of bytes that constitute the current constructed element's value.
     * @return The total number of bytes read during this invocation.
     */
    private long processRecursive(InputStream inputStream, TLVListener listener, long bytesToProcess) throws Exception {
        long localBytesRead = 0;

        // Continue processing as long as we haven't consumed the expected number of bytes
        // for the current scope and the stream has data.
        while (localBytesRead < bytesToProcess) {
            int firstTagByte = inputStream.read();
            if (firstTagByte == -1) {
                // End of stream. If we were expecting more bytes, the data is truncated.
                if (bytesToProcess != Long.MAX_VALUE && localBytesRead < bytesToProcess) {
                    throw new Exception("Unexpected end of stream while processing a constructed type.");
                }
                break; // Clean exit at end of stream.
            }
            this.bytesRead++;
            localBytesRead++;

            // --- 1. Parse Tag ---
            // A full implementation would handle multi-byte tags here.
            // For this example, we assume a single-byte tag.
            byte[] tag = {(byte) firstTagByte};
            boolean isConstructed = (firstTagByte & 0x20) != 0; // Bit 6 indicates if type is constructed

            // --- 2. Parse Length ---
            int length = parseLength(inputStream);
            // The bytes read for the length field are part of the parent's value.
            // This is a simplification; a real implementation would track this more granularly.

            listener.onStartTag(tag, length, isConstructed);

            // --- 3. Process Value ---
            if (isConstructed) {
                // The "value" is a nested sequence of TLV triplets. Recurse.
                long nestedBytes = processRecursive(inputStream, listener, length);
                if (nestedBytes != length) {
                    try {
                        throw new Exception(
                                String.format("Constructed type with length %d contained %d bytes.", length, nestedBytes)
                        );
                    } catch (Exception e) {
                        throw new Exception(e);
                    }
                }
            } else {
                // The "value" is a primitive. Read it directly.
                if (length > 0) {
                    byte[] value = readValue(inputStream, length);
                    listener.onPrimitiveValue(value);
                } else {
                    // Handle zero-length primitive value
                    listener.onPrimitiveValue(new byte[0]);
                }
            }

            listener.onEndTag(tag);
            localBytesRead += length;
        }
        return localBytesRead;
    }

    /**
     * Parses the length octets from the stream.
     * Handles both short form (single byte) and long form (multi-byte) length definitions.
     */
    private int parseLength(InputStream inputStream) throws  Exception {
        int firstLengthByte = inputStream.read();
        this.bytesRead++;

        if (firstLengthByte == -1) {
            throw new Exception();
        }

        if ((firstLengthByte & 0x80) == 0) {
            // Short form: the byte itself is the length (0-127)
            return firstLengthByte;
        } else {
            // Long form: bottom 7 bits indicate how many subsequent bytes define the length
            int numLengthBytes = firstLengthByte & 0x7F;
            if (numLengthBytes == 0 || numLengthBytes > 4) {
                // Indefinite length or length > 4 bytes (fits in an int) is not supported for simplicity.
                throw new Exception("Unsupported length form: " + numLengthBytes);
            }

            byte[] lengthBytes = readValue(inputStream, numLengthBytes);
            int length = 0;
            for (byte b : lengthBytes) {
                length = (length << 8) | (b & 0xFF);
            }
            return length;
        }
    }

    /**
     * Reads a specified number of bytes from the stream.
     */
    private byte[] readValue(InputStream inputStream, int length) throws Exception {
        if (length < 0) {
            throw new Exception("Invalid negative length specified: " + length);
        }
        byte[] value = new byte[length];
        int bytesActuallyRead = inputStream.read(value);
        this.bytesRead += bytesActuallyRead;

        if (bytesActuallyRead != length) {
            throw new Exception(
                    String.format("Expected to read %d bytes for value, but stream ended after %d.", length));
        }
        return value;
    }
}