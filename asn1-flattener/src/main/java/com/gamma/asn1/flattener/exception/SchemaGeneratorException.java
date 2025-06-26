package com.gamma.asn1.flattener.exception;

/**
 * Exception thrown by the AvroSchemaGenerator if an error occurs during Avro schema generation.
 * This might happen if the FlattenerRules contain configurations that are invalid,
 * ambiguous, or cannot be translated into a valid Avro schema.
 */
public class SchemaGeneratorException extends Exception {
    public SchemaGeneratorException(String message) {
        super(message);
    }

    public SchemaGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
