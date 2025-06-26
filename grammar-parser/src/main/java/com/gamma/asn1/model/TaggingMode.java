package com.gamma.asn1.model;

/**
 * Represents the tagging mode for an ASN.1 tag (e.g., IMPLICIT or EXPLICIT).
 */
public enum TaggingMode {
    IMPLICIT,
    EXPLICIT,
    AUTOMATIC // Typically used in schema definition, implies EXPLICIT unless overridden by IMPLICIT module default.
}
