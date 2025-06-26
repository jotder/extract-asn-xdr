package com.gamma.asn1.model;

import java.io.Serializable;

/**
 * Holds information about an ASN.1 tag, including its class, number, and tagging mode.
 */
public class ASN1TagInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TagClass tagClass;
    private final int tagNumber;
    private final TaggingMode taggingMode; // This might be more relevant to the context of a field definition

    /**
     * Constructs an ASN1TagInfo object.
     * @param tagClass The class of the tag (UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE).
     * @param tagNumber The tag number.
     * @param taggingMode The tagging mode (IMPLICIT or EXPLICIT), can be null if not applicable (e.g. for UNIVERSAL tags not in a sequence).
     */
    public ASN1TagInfo(TagClass tagClass, int tagNumber, TaggingMode taggingMode) {
        this.tagClass = tagClass;
        this.tagNumber = tagNumber;
        this.taggingMode = taggingMode;
    }

    public TagClass getTagClass() {
        return tagClass;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public TaggingMode getTaggingMode() {
        return taggingMode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (tagClass != null) {
            sb.append(tagClass.name());
            sb.append(" ");
        }
        sb.append(tagNumber);
        sb.append("]");
        if (taggingMode != null) {
            sb.append(" ").append(taggingMode.name());
        }
        return sb.toString();
    }
}
