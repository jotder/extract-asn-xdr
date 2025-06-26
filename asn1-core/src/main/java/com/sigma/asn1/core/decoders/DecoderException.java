package com.sigma.asn1.core.decoders;

/**
 * Exception thrown by ASN1Decoders when an error occurs during data decoding.
 */
public class DecoderException extends Exception {

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }
}
