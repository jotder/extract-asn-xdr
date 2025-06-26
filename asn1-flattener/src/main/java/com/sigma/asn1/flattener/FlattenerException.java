package com.sigma.asn1.flattener;

/**
 * Exception thrown by the TreeFlattener if an error occurs during the flattening process.
 */
public class FlattenerException extends Exception {
    public FlattenerException(String message) {
        super(message);
    }

    public FlattenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
