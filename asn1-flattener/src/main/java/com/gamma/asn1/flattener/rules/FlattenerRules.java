package com.gamma.asn1.flattener.rules;

import java.util.List;
import java.util.Objects;

/**
 * Represents the configuration loaded from a rules file (e.g., rules.yaml),
 * defining how DecodedNode trees should be flattened and what the output Avro schema should look like.
 * This class corresponds to the top-level structure of such a rules file.
 */
public class FlattenerRules {
    private String schemaName; // Name for the output Avro schema
    private List<FieldRule> fields; // List of rules for each field to be generated
    private List<String> groupBy; // Optional: list of fields to group by before applying rules

    // Constructors, getters, and setters

    public FlattenerRules() {
    }

    public FlattenerRules(String schemaName, List<FieldRule> fields, List<String> groupBy) {
        this.schemaName = schemaName;
        this.fields = fields;
        this.groupBy = groupBy;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<FieldRule> getFields() {
        return fields;
    }

    public void setFields(List<FieldRule> fields) {
        this.fields = fields;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlattenerRules that = (FlattenerRules) o;
        return Objects.equals(schemaName, that.schemaName) &&
               Objects.equals(fields, that.fields) &&
               Objects.equals(groupBy, that.groupBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaName, fields, groupBy);
    }

    @Override
    public String toString() {
        return "FlattenerRules{" +
               "schemaName='" + schemaName + '\'' +
               ", fields=" + fields +
               ", groupBy=" + groupBy +
               '}';
    }
}
