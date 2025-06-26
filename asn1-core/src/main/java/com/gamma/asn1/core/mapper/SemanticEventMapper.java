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
            elementDef = schema.findElementByTag(tag)
                    .orElseThrow(() -> new Exception("Unknown top-level tag"));
        } else {
            // This is a nested element, look for it within the parent's definition
            if (!parentState.isConstructed) {
                throw new Exception("Data stream contains nested element where schema expects a primitive");
            }
            elementDef = parentState.definition.findChildByTag(tag)
                    .orElseThrow(() -> new Exception("Unknown nested tag"));
        }

        stateStack.push(new StateTuple(elementDef, isConstructed));
    }

    /**
     * Note: This method exists for compatibility with the current TLVListener interface.
     * It is recommended to update the interface to have only the version with the 'offset' parameter.
     */
    @Override
    public void onStartTag(byte[] tag, int length, boolean isConstructed) throws Exception {
        // Delegate to the more specific method, using -1 to indicate an unknown offset.
        onStartTag(tag, length, isConstructed, -1L);
    }

    @Override
    public void onPrimitiveValue(byte[] value) {
        StateTuple currentState = stateStack.peek();
        if (currentState == null || currentState.isConstructed) {
            // This indicates a structural mismatch between the stream and our state machine.
            // It should ideally be caught by other checks, but serves as a safeguard.
            throw new IllegalStateException("Received primitive value for a constructed type or in an invalid state.");
        }

        String path = buildCurrentPath();
        ASN1Type type = currentState.definition.getType();
        listener.onField(path, value, type);
    }

    @Override
    public void onEndTag(byte[] tag) {
        if (stateStack.isEmpty()) {
            throw new IllegalStateException("Received onEndTag event with an empty state stack. Mismatched tags.");
        }
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
                .collect(Collectors.toCollection(ArrayDeque::new))
                .descendingIterator().
                next().toString();
    }
}