package com.sigma.asn1.core.grammar;

import com.sigma.asn1.core.ASN1Schema;

/**
 * Interface for parsing ASN.1 grammar.
 */
public interface ASN1GrammarParser {
    /**
     * Parses the given ASN.1 grammar file.
     *
     * @param grammarFilePath Path to the ASN.1 grammar file.
     * @return ASN1Schema object representing the parsed grammar.
     * @throws Exception If an error occurs during parsing.
     */
    ASN1Schema parse(String grammarFilePath) throws Exception;
}
