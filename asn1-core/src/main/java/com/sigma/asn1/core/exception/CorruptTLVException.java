package com.sigma.asn1.core.exception;

/**
 * Thrown when a malformed Tag-Length-Value structure is detected in the input stream.
 * For example, an invalid length encoding.
 */
public class CorruptTLVException extends ASN1Exception {
    private final byte[] rawDataChunk; // The chunk of data that caused the parsing failure

    public CorruptTLVException(String message, byte[] rawDataChunk, long approximateByteOffset, String tagPath) {
        super(message, approximateByteOffset, tagPath);
        this.rawDataChunk = rawDataChunk;
    }

    public CorruptTLVException(String message, Throwable cause, byte[] rawDataChunk, long approximateByteOffset, String tagPath) {
        super(message, cause, approximateByteOffset, tagPath);
        this.rawDataChunk = rawDataChunk;
    }

    public byte[] getRawDataChunk() {
        return rawDataChunk;
    }

    // Optionally, override getMessage() to include information about the raw data if it's printable or its length.
}
