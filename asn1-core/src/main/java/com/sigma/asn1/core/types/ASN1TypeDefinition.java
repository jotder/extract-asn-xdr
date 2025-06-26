package com.sigma.asn1.core.types;

/**
 * Interface for all ASN.1 type definitions.
 */
public interface ASN1TypeDefinition {
    /**
     * Gets the name of the type.
     *
     * @return Name of the type.
     */
    String getName();

    /**
     * Gets the ASN.1 tag for this type.
     * This might be more complex for CHOICE or ANY types.
     *
     * @return The primary tag as an integer, or a special value if not applicable.
     */
    // This might need to be more sophisticated, e.g., returning a Tag object or a list of possible tags.
    int getTag();


    // Potential common methods for all types:
    // boolean isConstructed();
    // String getDescription();
}
