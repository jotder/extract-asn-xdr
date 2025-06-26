package com.gamma.asn1.core.exception;

/**
 * Base exception for all recoverable processing errors within the library.
 * Contains rich context to help with debugging and error handling strategies.
 */
public class ASN1ProcessingException extends Exception {

    private final long byteOffset;
    private final String tagPath;

    public ASN1ProcessingException(String message, long byteOffset, String tagPath, Throwable cause) {
        super(message, cause);
        this.byteOffset = byteOffset;
        this.tagPath = (tagPath == null || tagPath.isEmpty()) ? "unknown" : tagPath;
    }

    public long getByteOffset() {
        return byteOffset;
    }

    public String getTagPath() {
        return tagPath;
    }

    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        return String.format("%s (Path: %s, Offset: ~%d)",
                baseMessage == null ? "" : baseMessage,
                tagPath, byteOffset);
    }
}