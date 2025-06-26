package com.gamma.asn1.core.mapper;

import com.gamma.asn1.grammar.ASN1Type; /**
 * A listener interface for receiving events from a flattening process.
 * It is called for each primitive value encountered in the ASN.1 stream.
 */
@FunctionalInterface
public interface FlattenerListener {

    /**
     * Called when a primitive ASN.1 field is fully parsed and identified.
     *
     * @param path     The dot-separated path to the field, derived from the schema (e.g., "record.header.id").
     * @param rawValue The raw bytes of the primitive value.
     * @param type     The {@link ASN1Type} of the field as defined in the schema, which guides final decoding.
     */
    void onField(String path, byte[] rawValue, ASN1Type type);
}
