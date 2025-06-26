package com.gamma.asn1.core.decoders;

import com.gamma.asn1.core.exception.DecoderException; // New exception class needed

import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

/**
 * A stateless utility library for converting raw byte arrays from ASN.1 TLV values
 * into standard Java types, based on the ASN.1 type definition.
 * This class provides highly optimized, easily testable functions.
 * (Corresponds to the "Decoder Library" in the README).
 */
public final class PrimitiveDecoders {

    private PrimitiveDecoders() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Decodes an ASN.1 INTEGER value.
     * @param bytes The raw byte array representing the integer value.
     * @return The decoded long value.
     * @throws DecoderException If the bytes cannot be decoded as an integer or are out of range for long.
     */
    public static long decodeInteger(byte[] bytes) throws DecoderException {
        if (bytes == null || bytes.length == 0) {
            throw new DecoderException("Input bytes for INTEGER cannot be null or empty.");
        }
        try {
            return new BigInteger(bytes).longValueExact();
        } catch (ArithmeticException e) {
            throw new DecoderException("INTEGER value " + new BigInteger(bytes).toString() + " out of range for Java long.", e);
        }
    }

    /**
     * Decodes an ASN.1 IA5String value. IA5String is typically ASCII.
     * @param bytes The raw byte array representing the IA5String value.
     * @return The decoded String.
     * @throws DecoderException If the bytes cannot be decoded as an IA5String.
     */
    public static String decodeIA5String(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            // Allow empty string from empty bytes if that's desired, else throw.
            // For now, let's be strict as per previous design.
            throw new DecoderException("Input bytes for IA5String cannot be null.");
        }
        for (byte b : bytes) {
            if ((b & 0x80) != 0) { // Check if MSB is set (ASCII chars are 0-127)
                throw new DecoderException("Invalid character in IA5String: value " + (b & 0xFF) + " is outside 7-bit ASCII range.");
            }
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Decodes an ASN.1 OCTET STRING value.
     * @param bytes The raw byte array representing the OCTET STRING value.
     * @return The byte array (no actual "decoding" needed, but method provided for consistency and validation).
     * @throws DecoderException If the input is invalid (e.g., null).
     */
    public static byte[] decodeOctetString(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            throw new DecoderException("Input bytes for OCTET STRING cannot be null.");
        }
        return bytes;
    }

    /**
     * Decodes an ASN.1 BOOLEAN value.
     * @param bytes The raw byte array representing the boolean value (should be 1 byte).
     * @return The decoded boolean.
     * @throws DecoderException If the bytes are not a valid boolean encoding.
     */
    public static boolean decodeBoolean(byte[] bytes) throws DecoderException {
        if (bytes == null || bytes.length != 1) {
            throw new DecoderException("BOOLEAN value must be a single byte and not null.");
        }
        // DER encoding: 0x00 for FALSE, any other value for TRUE (typically 0xFF for TRUE).
        return bytes[0] != 0x00;
    }

    // TODO: Add other decoders as needed, e.g.:
    // decodeUTF8String(byte[] bytes)
    // decodeBitString(byte[] bytes)
    // decodeObjectIdentifier(byte[] bytes)
    // decodeReal(byte[] bytes)
    // decodeDate(byte[] bytes), decodeTimeOfDay(byte[] bytes), decodeDateTime(byte[] bytes)
    // decodeBCD(byte[] bytes) - as mentioned in README for specific application
}
