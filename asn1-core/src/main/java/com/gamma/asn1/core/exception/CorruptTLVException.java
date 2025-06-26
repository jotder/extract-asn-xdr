package com.gamma.asn1.core.exception;

/**
 * Base exception for all recoverable processing errors within the library.
 * Contains rich context to help with debugging and error handling strategies.
 */
public class CorruptTLVException extends Exception {

      long byteOffset;
     String tagPath;

    public CorruptTLVException(String message, long byteOffset, String tagPath, Throwable cause) {
        super(message, cause);
        this.byteOffset = byteOffset;
        this.tagPath = tagPath;
    }

    @Override
    public String getMessage() {
        return String.format("%s at byte offset ~%d (path: %s)",
                super.getMessage(), byteOffset, tagPath);
    }
}