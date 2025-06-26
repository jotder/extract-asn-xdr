package com.gamma.asn1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Import for the new external enum
import com.gamma.asn1.model.ASN1BaseType;
// Imports for the new external Tagging classes
import com.gamma.asn1.model.ASN1TagInfo;
import com.gamma.asn1.model.TagClass;
import com.gamma.asn1.model.TaggingMode;


/**
 * A serializable model representing a single type definition from the ASN.1 schema.
 * <p>
 * For example, this could represent a 'CallEventRecord' which is a SEQUENCE,
 * or a 'CallDuration' which is an INTEGER.
 * </p>
 */
public class ASN1TypeDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class NamedNumber implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final int value;

        public NamedNumber(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public int getValue() { return value; }
    }

    private final String name;
    private ASN1BaseType baseType;
    private List<ASN1Field> fields; // Used for constructed types like SEQUENCE, SET, CHOICE
    private ASN1TagInfo tagInfo; // Tagging information for this type definition itself
    private String constraints; // e.g., "SIZE (1..10)", "INTEGER (0..255)"
    private String elementTypeName; // For SEQUENCE OF, SET OF: the name of the type of the elements
    private List<NamedNumber> namedNumbers; // For ENUMERATED types

    public ASN1TypeDefinition(String name) {
        this.name = name;
        this.fields = new ArrayList<>();
        this.namedNumbers = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public String getName() { return name; }
    public ASN1BaseType getBaseType() { return baseType; }
    public void setBaseType(ASN1BaseType baseType) { this.baseType = baseType; }
    public List<ASN1Field> getFields() { return fields; }
    public void addField(ASN1Field field) { this.fields.add(field); }

    public ASN1TagInfo getTagInfo() { return tagInfo; }
    public void setTagInfo(ASN1TagInfo tagInfo) { this.tagInfo = tagInfo; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getElementTypeName() { return elementTypeName; }
    public void setElementTypeName(String elementTypeName) { this.elementTypeName = elementTypeName; }

    public List<NamedNumber> getNamedNumbers() { return namedNumbers; }
    public void addNamedNumber(NamedNumber namedNumber) { this.namedNumbers.add(namedNumber); }


    /**
     * Represents a single field within a constructed type like a SEQUENCE.
     */
    public static class ASN1Field implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final String typeName; // The name of the ASN.1 type for this field, e.g., "IMSI", "INTEGER"
        private final boolean isOptional;
        private ASN1TagInfo tagInfo; // Tagging information for this field
        // TODO: Add defaultValue, constraints for fields if necessary

        public ASN1Field(String name, String typeName, boolean isOptional) {
            this.name = name;
            this.typeName = typeName;
            this.isOptional = isOptional;
        }

        public String getName() { return name; }
        public String getTypeName() { return typeName; }
        public boolean isOptional() { return isOptional; }

        public ASN1TagInfo getTagInfo() { return tagInfo; }
        public void setTagInfo(ASN1TagInfo tagInfo) { this.tagInfo = tagInfo; }
    }
}