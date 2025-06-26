package com.gamma.asn1.grammar;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses an ASN.1 grammar definition from a text stream into a structured,
 * serializable {@link ASN1Schema} object.
 * <p>
 * This class acts as the entry point for the grammar processing stage. It uses an
 * ANTLR-generated parser to create a parse tree from the grammar file, and then
 * uses a visitor to walk that tree and build the
 * final schema object. This schema can then be cached and reused by the
 * {@code SemanticEventMapper}.
 * </p>
 */
public class ASN1GrammarParser {

    /**
     * Parses the given ASN.1 grammar stream.
     *
     * @param grammarStream An {@link InputStream} containing the text of the .asn grammar file.
     * @return A fully populated {@link ASN1Schema} object representing the grammar rules.
     * @throws IOException if an error occurs reading from the stream.
     */
    public ASN1Schema parse(InputStream grammarStream) throws IOException {
        // 1. Create a stream of characters from the input stream.
        // ANTLR's CharStreams handles the conversion from bytes to characters.
        ASN1Lexer lexer = new ASN1Lexer(CharStreams.fromStream(grammarStream));

        // 2. Create a stream of tokens from the lexer.
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 3. Create a parser that feeds off the token stream.
        // Ensure ASN1Parser is imported or referenced with its full package if necessary
        com.gamma.asn1.grammar.ASN1Parser parser = new com.gamma.asn1.grammar.ASN1Parser(tokens);

        // 4. Begin parsing at the top-level rule defined in your ASN1.g4 grammar.
        // We assume the entry rule is named 'moduleDefinition'.
        // Adjust 'moduleDefinition()' if your grammar's entry rule is different.
        ParseTree tree = parser.moduleDefinition(); // TODO: Verify 'moduleDefinition' is the correct entry rule name

        // 5. Create the schema object that will be populated by the visitor.
        ASN1Schema schema = new ASN1Schema();

        // 6. Create a visitor to walk the tree and populate the schema with meaningful data.
        // Ensure ASN1SchemaBuilder is correctly implemented and imported.
        ASN1SchemaBuilder visitor = new ASN1SchemaBuilder(schema);
        visitor.visit(tree);

        // 7. Return the fully populated, easy-to-use schema object.
        return schema;
    }
}