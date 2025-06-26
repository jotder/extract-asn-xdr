package com.gamma.asn1.core.tlv;

/**
 * A listener interface for receiving structural events from the TLVEventStreamer.
 * This follows the event-driven, streaming philosophy.
 */
public interface TLVListener {

//    void onStartTag(byte[] tag, int length, boolean isConstructed, long offset);

    /**
     * Called when a new TLV tag is encountered.
     * @param tag The identifier tag.
     * @param length The length of the value field.
     * @param isConstructed True if the value contains more TLV structures, false if it's a primitive.
     */
    void onStartTag(byte[] tag, int length, boolean isConstructed) throws Exception;

    /**
     * Called when a primitive (non-constructed) value is read.
     * @param value The raw bytes of the primitive value.
     */
//    void onPrimitiveValue(byte[] value);

    /**
     * Called when the end of a constructed tag's scope is reached.
     * @param tag The identifier tag that is now ending.
     */
    void onEndTag(byte[] tag);
    void onStartTag(byte[] tag, int length, boolean isConstructed, long offset) throws Exception;
    void onPrimitiveValue(byte[] value);

}