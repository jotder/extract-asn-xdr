package com.sigma.asn1.core.exception;

/**
 * Thrown when the encountered data does not match the ASN.1 grammar definition.
 * For example, an unexpected tag is found.
 */
public class GrammarMismatchException extends ASN1Exception {
    private final String expectedTag; // Could be more complex, e.g., list of expected tags for CHOICE
    private final String actualTag;   // The tag that was actually found

    public GrammarMismatchException(String message, String expectedTag, String actualTag, long approximateByteOffset, String tagPath) {
        super(message, approximateByteOffset, tagPath);
        this.expectedTag = expectedTag;
        this.actualTag = actualTag;
    }

    public GrammarMismatchException(String message, Throwable cause, String expectedTag, String actualTag, long approximateByteOffset, String tagPath) {
        super(message, cause, approximateByteOffset, tagPath);
        this.expectedTag = expectedTag;
        this.actualTag = actualTag;
    }

    public String getExpectedTag() {
        return expectedTag;
    }

    public String getActualTag() {
        return actualTag;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + String.format(" Expected: %s, Actual: %s.", expectedTag, actualTag);
    }
}
