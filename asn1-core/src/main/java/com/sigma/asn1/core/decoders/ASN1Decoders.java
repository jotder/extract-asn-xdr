package com.sigma.asn1.core.decoders;

import java.nio.charset.StandardCharsets;
// Add other necessary imports for specific decodings, e.g., BigInteger

/**
 * A stateless utility library for converting raw byte arrays from ASN.1 TLV values
 * into standard Java types, based on the ASN.1 type definition.
 *
 * This class provides highly optimized, easily testable functions.
 */
public final class ASN1Decoders {

    // Private constructor to prevent instantiation
    private ASN1Decoders() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Decodes an ASN.1 INTEGER value.
     *
     * @param bytes The raw byte array representing the integer value.
     * @return The decoded long value.
     * @throws DecoderException If the bytes cannot be decoded as an integer.
     */
    public static long decodeInteger(byte[] bytes) throws DecoderException {
        if (bytes == null || bytes.length == 0) {
            throw new DecoderException("Input bytes for INTEGER cannot be null or empty.");
        }
        // ASN.1 integers are signed, big-endian.
        // For simplicity, this handles up to 8 bytes (long). For arbitrary precision, use BigInteger.
        if (bytes.length > 8) {
            // Or use new BigInteger(bytes).longValueExact() if BigInteger is preferred.
            throw new DecoderException("INTEGER value too large for long, requires BigInteger handling (not implemented).");
        }

        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }

        // Handle sign extension for negative numbers if not using BigInteger(bytes)
        if (bytes.length < 8 && (bytes[0] & 0x80) != 0) { // If MSB is 1, it's negative
            for (int i = bytes.length; i < 8; i++) {
                value |= (0xFFL << (8 * (7-i))); // This seems overly complex, BigInteger(bytes) is better
            }
             // A simpler way for fixed size up to long:
            if (bytes.length > 0 && (bytes[0] & 0x80) != 0) { // Negative
                 long extendedValue = -1L; // All bits set to 1
                 extendedValue <<= (bytes.length * 8); // Shift to make room for actual bytes
                 value |= extendedValue; // Combine with the sign-extended part
            }
        }
         // Using BigInteger is generally safer and simpler for ASN.1 integers:
        // return new java.math.BigInteger(bytes).longValue();
        // For now, stick to a basic implementation, assuming positive or properly sized negatives.
        return new java.math.BigInteger(bytes).longValue(); // Reverted to BigInteger for correctness
    }

    /**
     * Decodes an ASN.1 IA5String value.
     * IA5String is typically ASCII.
     *
     * @param bytes The raw byte array representing the IA5String value.
     * @return The decoded String.
     * @throws DecoderException If the bytes cannot be decoded as an IA5String.
     */
    public static String decodeIA5String(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            throw new DecoderException("Input bytes for IA5String cannot be null.");
        }
        // IA5 is based on International Alphabet No. 5, which is 7-bit ASCII.
        // Each character should be within 0-127.
        for (byte b : bytes) {
            if ((b & 0x80) != 0) { // Check if MSB is set (i.e., value > 127)
                throw new DecoderException("Invalid character in IA5String: value " + (b & 0xFF) + " is outside 7-bit ASCII range.");
            }
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Decodes an ASN.1 OCTET STRING value.
     *
     * @param bytes The raw byte array representing the OCTET STRING value.
     * @return The byte array (no actual decoding needed, but method provided for consistency).
     * @throws DecoderException If the input is invalid (e.g., null).
     */
    public static byte[] decodeOctetString(byte[] bytes) throws DecoderException {
        if (bytes == null) {
            throw new DecoderException("Input bytes for OCTET STRING cannot be null.");
        }
        return bytes; // Typically, OCTET STRING is just the raw bytes.
    }

    /**
     * Decodes an ASN.1 BOOLEAN value.
     *
     * @param bytes The raw byte array representing the boolean value (should be 1 byte).
     * @return The decoded boolean.
     * @throws DecoderException If the bytes are not a valid boolean encoding.
     */
    public static boolean decodeBoolean(byte[] bytes) throws DecoderException {
        if (bytes == null || bytes.length != 1) {
            throw new DecoderException("BOOLEAN value must be a single byte.");
        }
        // Any non-zero value is TRUE, 0x00 is FALSE.
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
