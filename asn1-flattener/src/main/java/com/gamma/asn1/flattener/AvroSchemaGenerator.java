package com.gamma.asn1.flattener;

import com.gamma.asn1.flattener.rules.FlattenerRules;
import com.gamma.asn1.flattener.exception.SchemaGeneratorException;

/**
 * Interface for generating an Avro schema string based on FlattenerRules.
 */
public interface AvroSchemaGenerator {

    /**
     * Generates an Avro schema string.
     *
     * @param rules The FlattenerRules that define the structure of the output records.
     *                 These rules are typically loaded from a configuration file (e.g., rules.yaml).
     * @return A String representing the Avro schema.
     * @throws SchemaGeneratorException if an error occurs during schema generation,
     *                                  for example, due to invalid rule configurations or
     *                                  inconsistencies that prevent valid Avro schema construction.
     */
    String generateSchema(FlattenerRules rules) throws SchemaGeneratorException;
}
