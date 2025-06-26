package com.sigma.asn1.core.types;

/**
 * Represents an ASN.1 INTEGER type definition.
 */
public class IntegerDef implements ASN1TypeDefinition {
    private final String name;
    private final int tag; // Typically a universal tag for INTEGER

    public IntegerDef(String name, int tag) {
        this.name = name;
        this.tag = tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTag() {
        return tag;
    }

    public boolean isConstructed() {
        return false;
    }
}
