package com.sigma.asn1.flattener.rules;

import java.util.List;

/**
 * Represents the configuration loaded from rules.yaml, defining how
 * DecodedNode trees should be flattened and what the output schema should look like.
 */
public class FlattenerRules {
    private String schemaName;
    private List<FieldRule> fields;
    private List<String> groupBy; // For groupBy operations, if any

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

    // toString, equals, hashCode for easier debugging if needed
}
