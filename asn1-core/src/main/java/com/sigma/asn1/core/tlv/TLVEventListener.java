package com.sigma.asn1.core.tlv;

/**
 * Interface for listening to TLV events.
 */
public interface TLVEventListener {
    /**
     * Called when a new TLV tag starts.
     *
     * @param tag             The ASN.1 tag.
     * @param length          The length of the value field.
     * @param isConstructed   True if the P/C bit indicates a constructed type, false otherwise.
     * @param rawTagBytes     The raw bytes of the tag.
     * @param rawLengthBytes  The raw bytes of the length.
     */
    void onStartTag(int tag, int length, boolean isConstructed, byte[] rawTagBytes, byte[] rawLengthBytes);

    /**
     * Called when a primitive value is encountered.
     *
     * @param valueBytes The raw bytes of the primitive value.
     */
    void onPrimitiveValue(byte[] valueBytes);

    /**
     * Called when a TLV tag ends.
     *
     * @param tag The ASN.1 tag.
     */
    void onEndTag(int tag);

    /**
     * Called when the end of the input stream is reached.
     */
    void onEndOfStream();

    /**
     * Called when an error occurs during TLV streaming.
     * @param e The exception that occurred.
     * @param approximateByteOffset The approximate byte offset in the stream where the error occurred.
     */
    void onError(Exception e, long approximateByteOffset);
}
