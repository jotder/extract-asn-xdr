package com.sigma.asn1.flattener;

/**
 * Exception thrown by the AvroSchemaGenerator if an error occurs during schema generation.
 */
public class SchemaGeneratorException extends Exception {
    public SchemaGeneratorException(String message) {
        super(message);
    }

    public SchemaGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
