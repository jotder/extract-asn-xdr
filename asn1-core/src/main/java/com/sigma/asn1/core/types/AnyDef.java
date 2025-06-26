package com.sigma.asn1.core.types;

/**
 * Represents an ASN.1 ANY DEFINED BY type definition.
 * The actual type is determined at runtime by another field in the message.
 */
public class AnyDef implements ASN1TypeDefinition {
    private final String name;
    private final String definedByFieldName; // The name of the field that determines the type

    public AnyDef(String name, String definedByFieldName) {
        this.name = name;
        this.definedByFieldName = definedByFieldName;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * For ANY, the tag is not known until the type is resolved at runtime.
     */
    @Override
    public int getTag() {
        // Or throw new UnsupportedOperationException("Tag is not fixed for ANY type");
        return -1; // Placeholder for "tag determined at runtime"
    }

    public String getDefinedByFieldName() {
        return definedByFieldName;
    }

    // ANY is typically a placeholder for a concrete type which could be primitive or constructed.
    // The isConstructed nature would depend on the actual type it resolves to.
}
