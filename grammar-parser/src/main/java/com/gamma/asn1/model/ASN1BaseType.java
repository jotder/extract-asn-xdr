package com.gamma.asn1.model;

/**
 * Defines the base ASN.1 types.
 */
public enum ASN1BaseType {
    INTEGER,
    OCTET_STRING,
    SEQUENCE,
    SEQUENCE_OF,
    SET,
    SET_OF,
    CHOICE,
    BOOLEAN,
    NULL,
    ENUMERATED,
    OBJECT_IDENTIFIER,
    RELATIVE_OID,
    BIT_STRING,
    REAL,
    DATE,
    TIME_OF_DAY,
    DATE_TIME,
    DURATION,
    UTF8_STRING,
    PRINTABLE_STRING,
    IA5_STRING,
    VISIBLE_STRING,
    GENERAL_STRING,
    NUMERIC_STRING,
    BMP_STRING,
    UNIVERSAL_STRING
    // TODO: Add other base types as needed from relevant ASN.1 standards like X.680
}
