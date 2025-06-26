package com.gamma.asn1.core.tlv;

/**
 * A listener interface for receiving structural events from the TLVEventStreamer.
 * This follows the event-driven, streaming philosophy.
 */
public interface TLVListener {


    /**
     * Called when a new TLV tag is encountered.
     * @param tag The identifier tag (raw bytes).
     * @param length The length of the value field.
     * @param isConstructed True if the value contains more TLV structures, false if it's a primitive.
     * @param offset The starting byte offset of this tag in the original stream.
     * @throws Exception if an error occurs during processing.
     */
    void onStartTag(byte[] tag, int length, boolean isConstructed, long offset) throws Exception;

    /**
     * Called when a primitive (non-constructed) value is read.
     * @param value The raw bytes of the primitive value.
     * @throws Exception if an error occurs during processing.
     */
    void onPrimitiveValue(byte[] value) throws Exception;

    /**
     * Called when the end of a constructed tag's scope is reached.
     * @param tag The identifier tag (raw bytes) that is now ending.
     * @throws Exception if an error occurs during processing.
     */
    void onEndTag(byte[] tag) throws Exception;

}