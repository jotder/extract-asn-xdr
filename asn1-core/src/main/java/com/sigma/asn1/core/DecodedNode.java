package com.sigma.asn1.core;

import com.sigma.asn1.core.types.ASN1TypeDefinition;
import java.util.List;
import java.util.Map;

/**
 * A temporary, in-memory tree structure representing a single, fully decoded ASN.1 record.
 * This node can represent a primitive value, a sequence of other nodes, or a list of nodes.
 */
public class DecodedNode {
    private final ASN1TypeDefinition typeDefinition; // Link to the schema definition for this node
    private final Object value; // For primitive types (Integer, String, byte[], etc.)
    private final List<DecodedNode> elements; // For SEQUENCE OF / SET OF
    private final Map<String, DecodedNode> children; // For SEQUENCE / SET (keyed by component name)
    private final String fieldName; // The name of this field/node

    // Constructor for primitive types
    public DecodedNode(String fieldName, ASN1TypeDefinition typeDefinition, Object value) {
        this.fieldName = fieldName;
        this.typeDefinition = typeDefinition;
        this.value = value;
        this.elements = null;
        this.children = null;
    }

    // Constructor for SEQUENCE OF / SET OF types
    public DecodedNode(String fieldName, ASN1TypeDefinition typeDefinition, List<DecodedNode> elements) {
        this.fieldName = fieldName;
        this.typeDefinition = typeDefinition;
        this.elements = elements;
        this.value = null;
        this.children = null;
    }

    // Constructor for SEQUENCE / SET types
    public DecodedNode(String fieldName, ASN1TypeDefinition typeDefinition, Map<String, DecodedNode> children) {
        this.fieldName = fieldName;
        this.typeDefinition = typeDefinition;
        this.children = children;
        this.value = null;
        this.elements = null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ASN1TypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public Object getValue() {
        return value;
    }

    public List<DecodedNode> getElements() {
        return elements;
    }

    public Map<String, DecodedNode> getChildren() {
        return children;
    }

    public boolean isPrimitive() {
        return value != null;
    }

    public boolean isList() {
        return elements != null;
    }

    public boolean isStructured() {
        return children != null;
    }

    // Potentially add methods to navigate the tree, e.g., getChild(String name), getElement(int index)
}
