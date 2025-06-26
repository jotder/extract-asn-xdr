package com.gamma.asn1.core.exception;

/**
 * Thrown when the encountered data does not match the ASN.1 grammar definition.
 * For example, an unexpected tag is found, or a required field is missing.
 */
public class GrammarMismatchException extends ASN1ProcessingException {
    private final String expectedElement; // Description of what was expected (e.g., "Tag 0x02 (INTEGER)", "SEQUENCE component 'userId'")
    private final String actualElement;   // Description of what was found (e.g., "Tag 0x04 (OCTET STRING)", "End of SEQUENCE")

    public GrammarMismatchException(String message, long byteOffset, String tagPath, Throwable cause, String expectedElement, String actualElement) {
        super(message, byteOffset, tagPath, cause);
        this.expectedElement = expectedElement;
        this.actualElement = actualElement;
    }

    public GrammarMismatchException(String message, long byteOffset, String tagPath, String expectedElement, String actualElement) {
        this(message, byteOffset, tagPath, null, expectedElement, actualElement);
    }

    public String getExpectedElement() {
        return expectedElement;
    }

    public String getActualElement() {
        return actualElement;
    }

    @Override
    public String getMessage() {
        // ASN1ProcessingException's getMessage() already adds path and offset.
        // We just prepend the specific mismatch info.
        String baseMessage = super.getMessage();
        return String.format("Expected: %s, Found: %s. %s",
                expectedElement,
                actualElement,
                baseMessage
        );
    }
}
