package com.gamma.asn1.core.mapper;

import com.gamma.asn1.core.exception.ASN1ProcessingException;
import com.gamma.asn1.core.tlv.TLVListener;
import com.gamma.asn1.grammar.ASN1Schema;
import com.gamma.asn1.grammar.SchemaElement;
import com.gamma.asn1.grammar.ASN1Type;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Listens to low-level TLV events and, using an {@link ASN1Schema}, maps them to semantic "field" events.
 * It maintains a state machine to track its position in the grammar and the data stream,
 * effectively acting as a bridge between the structural parser and a schema-aware consumer.
 * This implementation performs direct flattening, avoiding the creation of an intermediate node tree.
 */
public class SemanticEventMapper implements TLVListener {

    private final ASN1Schema schema;
    private final FlattenerListener listener;
    private final Deque<StateTuple> stateStack = new ArrayDeque<>();

    /**
     * Internal class to hold the state for each level of the ASN.1 structure.
     */
    private static class StateTuple {
        final SchemaElement definition;
        final boolean isConstructed;

        StateTuple(SchemaElement definition, boolean isConstructed) {
            this.definition = Objects.requireNonNull(definition);
            this.isConstructed = isConstructed;
        }
    }

    /**
     * Constructs a new SemanticEventMapper.
     *
     * @param schema   The schema to validate and interpret the stream against.
     * @param listener The listener to receive high-level, flattened field events.
     */
    public SemanticEventMapper(ASN1Schema schema, FlattenerListener listener) {
        this.schema = Objects.requireNonNull(schema, "Schema cannot be null");
        this.listener = Objects.requireNonNull(listener, "FlattenerListener cannot be null");
    }

    @Override
    public void onStartTag(byte[] tag, int length, boolean isConstructed, long offset) throws Exception {
        StateTuple parentState = stateStack.peek();
        SchemaElement elementDef;

        if (parentState == null) {
            // This is a top-level element
            // TODO: Ensure schema.findElementByTag and parentState.definition.findChildByTag are robust
            // and throw appropriate exceptions (e.g., GrammarMismatchException).
            // The existing grammar classes (ASN1Schema, SchemaElement) from grammar-parser module need to be available.
            elementDef = schema.findElementByTag(tag)
                    .orElseThrow(() -> new ASN1ProcessingException("Unknown top-level tag: " + bytesToHex(tag), offset, buildCurrentPath(), null));
        } else {
            // This is a nested element, look for it within the parent's definition
            if (!parentState.isConstructed) {
                throw new ASN1ProcessingException("Data stream contains nested element where schema expects a primitive", offset, buildCurrentPath(), null);
            }
            elementDef = parentState.definition.findChildByTag(tag)
                    .orElseThrow(() -> new ASN1ProcessingException("Unknown nested tag: " + bytesToHex(tag) + " within " + parentState.definition.getName(), offset, buildCurrentPath(), null));
        }

        stateStack.push(new StateTuple(elementDef, isConstructed));
    }

    @Override
    public void onPrimitiveValue(byte[] value) throws ASN1ProcessingException {
        StateTuple currentState = stateStack.peek();
        if (currentState == null) {
            throw new ASN1ProcessingException("Received primitive value in an invalid null state.", -1, buildCurrentPath(), null);
        }
        if (currentState.isConstructed) {
             throw new ASN1ProcessingException("Received primitive value for a constructed type: " + currentState.definition.getName(), -1, buildCurrentPath(), null);
        }

        String path = buildCurrentPath();
        ASN1Type type = currentState.definition.getType();
        // TODO: Ensure listener.onField can handle potential exceptions or declared them.
        listener.onField(path, value, type);
    }

    @Override
    public void onEndTag(byte[] tag) throws ASN1ProcessingException {
        if (stateStack.isEmpty()) {
            throw new ASN1ProcessingException("Received onEndTag event with an empty state stack. Mismatched tags for tag: " + bytesToHex(tag), -1, buildCurrentPath(), null);
        }
        // TODO: Optionally, verify that the ending tag matches currentState.definition.getTag()
        stateStack.pop();
    }

    /**
     * Constructs the current dot-notation path from the state stack.
     * @return A path string like "record.header.id".
     */
    private String buildCurrentPath() {
        if (stateStack.isEmpty()) {
            return "";
        }
        // The stream of definitions is reversed to build the path from root to leaf.
        return stateStack.stream()
                .map(s -> s.definition.getName())
                .collect(Collectors.joining("."));
    }

    // Helper to convert byte array to hex string for logging/exceptions
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}