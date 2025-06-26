package com.gamma.asn1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable model representing a single type definition from the ASN.1 schema.
 * <p>
 * For example, this could represent a 'CallEventRecord' which is a SEQUENCE,
 * or a 'CallDuration' which is an INTEGER.
 * </p>
 */
public class ASN1TypeDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private String baseType; // e.g., "SEQUENCE", "INTEGER", "OCTET STRING"
    private List<ASN1Field> fields; // Used for constructed types like SEQUENCE

    public ASN1TypeDefinition(String name) {
        this.name = name;
        this.fields = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public String getName() { return name; }
    public String getBaseType() { return baseType; }
    public void setBaseType(String baseType) { this.baseType = baseType; }
    public List<ASN1Field> getFields() { return fields; }
    public void addField(ASN1Field field) { this.fields.add(field); }

    /**
     * Represents a single field within a constructed type like a SEQUENCE.
     */
    public static class ASN1Field implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final String type; // The name of the type for this field, e.g., "IMSI"
        private final boolean isOptional;

        public ASN1Field(String name, String type, boolean isOptional) {
            this.name = name;
            this.type = type;
            this.isOptional = isOptional;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public boolean isOptional() { return isOptional; }
    }
}