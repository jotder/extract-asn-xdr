package com.gamma.asn1.flattener.rules;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a single rule for a field in the flattener configuration (e.g., from rules.yaml).
 * This rule dictates how a field in the output flat record is derived from the input DecodedNode.
 * It can define a simple direct mapping, a reduction of a list, or an expansion (Cartesian product).
 */
public class FieldRule {
    // Common attributes for all rule types
    private String name; // Explicit output field name. If null/empty, might be auto-derived from 'path'.
    private String path; // Path to the source field in the DecodedNode (for simple map, or source for reduce/expand).
    private String type; // Explicit output Avro type (e.g., "string", "long", "int", "boolean", "bytes").
    private String doc;  // Documentation for the field, to be included in the Avro schema.

    // Attributes specific to 'reduce' rules
    private String reduce; // Path to the list of sub-records in DecodedNode to be reduced.
    private String as;     // Prefix for flattened field names that result from the reduction.
    private String keyBy;  // Optional: Field within sub-records to use as a key if the reduction results in a map.
    private Map<String, String> rules; // Aggregation functions (e.g., "min": "timeOfFirstUsage", "sum": "dataVolume").
                                      // The key is the aggregation function (min, max, sum, first, last, count),
                                      // and the value is the field name within the sub-record to aggregate on.
                                      // For "count", the value can be empty or a specific field to count non-null occurrences.

    // Attributes specific to 'expand' rules
    private String expand; // Path to the list in DecodedNode that should be expanded (creating multiple output records).

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDoc() { return doc; }
    public void setDoc(String doc) { this.doc = doc; }
    public String getReduce() { return reduce; }
    public void setReduce(String reduce) { this.reduce = reduce; }
    public String getAs() { return as; }
    public void setAs(String as) { this.as = as; }
    public String getKeyBy() { return keyBy; }
    public void setKeyBy(String keyBy) { this.keyBy = keyBy; }
    public Map<String, String> getRules() { return rules; }
    public void setRules(Map<String, String> rules) { this.rules = rules; }
    public String getExpand() { return expand; }
    public void setExpand(String expand) { this.expand = expand; }

    // Convenience methods to determine rule type
    public boolean isSimpleMapping() {
        return path != null && reduce == null && expand == null;
    }
    public boolean isReduceRule() {
        return reduce != null;
    }
    public boolean isExpandRule() {
        return expand != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldRule fieldRule = (FieldRule) o;
        return Objects.equals(name, fieldRule.name) &&
               Objects.equals(path, fieldRule.path) &&
               Objects.equals(type, fieldRule.type) &&
               Objects.equals(doc, fieldRule.doc) &&
               Objects.equals(reduce, fieldRule.reduce) &&
               Objects.equals(as, fieldRule.as) &&
               Objects.equals(keyBy, fieldRule.keyBy) &&
               Objects.equals(rules, fieldRule.rules) &&
               Objects.equals(expand, fieldRule.expand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, type, doc, reduce, as, keyBy, rules, expand);
    }

    @Override
    public String toString() {
        return "FieldRule{" + // Omitting detailed fields for brevity in default toString
               "name='" + name + '\'' +
               (path != null ? ", path='" + path + '\'' : "") +
               (type != null ? ", type='" + type + '\'' : "") +
               (reduce != null ? ", reduce='" + reduce + '\'' : "") +
               (expand != null ? ", expand='" + expand + '\'' : "") +
               '}';
    }
}
