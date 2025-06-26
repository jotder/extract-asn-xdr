package com.sigma.asn1.core.types;

import java.util.List;
import java.util.Map;

/**
 * Represents an ASN.1 SEQUENCE type definition.
 */
public class SequenceDef implements ASN1TypeDefinition {
    private final String name;
    private final List<ASN1TypeDefinition> components; // Ordered list of components
    private final Map<String, ASN1TypeDefinition> componentMap; // For quick lookup by name
    private final int tag; // Typically a universal tag for SEQUENCE

    public SequenceDef(String name, List<ASN1TypeDefinition> components, Map<String, ASN1TypeDefinition> componentMap, int tag) {
        this.name = name;
        this.components = components;
        this.componentMap = componentMap;
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

    public List<ASN1TypeDefinition> getComponents() {
        return components;
    }

    public ASN1TypeDefinition getComponent(String componentName) {
        return componentMap.get(componentName);
    }

    public boolean isConstructed() {
        return true;
    }
}
