package com.gamma.asn1.flattener.exception;

/**
 * Exception thrown by the TreeFlattener if an error occurs during the flattening process.
 * This could be due to issues with rule application, unexpected data structures in DecodedNode,
 * or inconsistencies that prevent successful flattening.
 */
public class FlattenerException extends Exception {
    public FlattenerException(String message) {
        super(message);
    }

    public FlattenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
