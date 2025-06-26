package com.sigma.asn1.flattener.rules;

import java.util.Map;

/**
 * Represents a single rule for a field in the flattener configuration.
 * This can be a simple mapping, a reduce rule, or an expand rule.
 */
public class FieldRule {
    // Common attributes
    private String name; // Explicit output field name
    private String path; // Path to the field in DecodedNode (for simple mapping or source of reduce/expand)
    private String type; // Explicit output type (e.g., "string", "long")
    private String doc;  // Documentation for the field

    // For 'reduce' rules
    private String reduce; // Path to the list of sub-records to reduce
    private String as;     // Prefix for flattened field names from reduced list
    private String keyBy;  // Optional: field within sub-records to use as a key for a map output
    private Map<String, String> rules; // Aggregation functions (e.g., "min": "timeOfFirstUsage")

    // For 'expand' rules
    private String expand; // Path to the list to be expanded (Cartesian product)

    // Getters and Setters for all fields (omitted for brevity but should be generated)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getReduce() {
        return reduce;
    }

    public void setReduce(String reduce) {
        this.reduce = reduce;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getKeyBy() {
        return keyBy;
    }

    public void setKeyBy(String keyBy) {
        this.keyBy = keyBy;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    // Logic to determine rule type (simple, reduce, expand) can be added here
    public boolean isSimpleMapping() {
        return path != null && reduce == null && expand == null;
    }

    public boolean isReduceRule() {
        return reduce != null;
    }

    public boolean isExpandRule() {
        return expand != null;
    }
}
