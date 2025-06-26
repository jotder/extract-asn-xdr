package com.sigma.asn1.core.types;

import java.util.Map;

/**
 * Represents an ASN.1 CHOICE type definition.
 */
public class ChoiceDef implements ASN1TypeDefinition {
    private final String name;
    // The key is the tag of the choice option
    private final Map<Integer, ASN1TypeDefinition> choices;

    public ChoiceDef(String name, Map<Integer, ASN1TypeDefinition> choices) {
        this.name = name;
        this.choices = choices;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * For CHOICE, a single tag is not applicable as it depends on the selected option.
     * This could return a special value or throw an exception.
     * Alternatively, this method could list all possible tags.
     */
    @Override
    public int getTag() {
        // Or throw new UnsupportedOperationException("Tag is not fixed for CHOICE type");
        return -1; // Placeholder for "no single tag"
    }

    public Map<Integer, ASN1TypeDefinition> getChoices() {
        return choices;
    }

    public ASN1TypeDefinition getChoiceOption(int tag) {
        return choices.get(tag);
    }

    public boolean isConstructed() {
        // A CHOICE itself isn't constructed, but its selected option might be.
        // This depends on the specific option chosen at decode time.
        return true; // Assuming the chosen type will determine this.
    }
}
