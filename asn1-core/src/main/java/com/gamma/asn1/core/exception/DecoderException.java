package com.gamma.asn1.core.exception;

/**
 * Exception thrown by PrimitiveDecoders when an error occurs during data decoding.
 * This is separate from ASN1ProcessingException as it relates to low-level primitive
 * type conversion rather than structural or grammar issues.
 */
public class DecoderException extends Exception {

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
