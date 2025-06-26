package com.sigma.asn1.core;

import com.sigma.asn1.core.types.ASN1TypeDefinition;
import java.util.Map;

/**
 * Represents the in-memory ASN.1 grammar.
 * This class is thread-safe.
 */
public class ASN1Schema {
    private final Map<String, ASN1TypeDefinition> typeDefinitions;

    /**
     * Constructs an ASN1Schema with the given type definitions.
     *
     * @param typeDefinitions Map of type names to their definitions.
     */
    public ASN1Schema(Map<String, ASN1TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }

    /**
     * Gets the type definition for the given type name.
     *
     * @param typeName Name of the type.
     * @return ASN1TypeDefinition for the type, or null if not found.
     */
    public ASN1TypeDefinition getTypeDefinition(String typeName) {
        return typeDefinitions.get(typeName);
    }

    // TODO: Add methods for serialization and deserialization to/from binary cache file.
}
