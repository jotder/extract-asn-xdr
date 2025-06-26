package com.gamma.asn1.core.exception;

/**
 * Thrown when a malformed Tag-Length-Value structure is detected in the input stream.
 * For example, an invalid length encoding or unexpected end of stream.
 */
public class CorruptTLVException extends ASN1ProcessingException {

    // Optionally, include the specific raw bytes that caused the issue if available and useful.
    // private final byte[] rawDataChunk;

    public CorruptTLVException(String message, long byteOffset, String tagPath, Throwable cause) {
        super(message, byteOffset, tagPath, cause);
        // this.rawDataChunk = null; // Or assign if passed
    }

    public CorruptTLVException(String message, long byteOffset, String tagPath, Throwable cause, byte[] rawDataChunk) {
        super(message, byteOffset, tagPath, cause);
        // this.rawDataChunk = rawDataChunk; // Store if needed
    }

    // public byte[] getRawDataChunk() {
    //     return rawDataChunk;
    // }
}