package com.sigma.asn1.flattener;

import com.sigma.asn1.flattener.rules.FlattenerRules; // To be created

/**
 * Interface for generating an Avro schema string based on FlattenerRules.
 */
public interface AvroSchemaGenerator {

    /**
     * Generates an Avro schema string.
     *
     * @param rules The FlattenerRules that define the structure of the output records.
     * @return A String representing the Avro schema.
     * @throws SchemaGeneratorException if an error occurs during schema generation.
     */
    String generateSchema(FlattenerRules rules) throws SchemaGeneratorException;
}
