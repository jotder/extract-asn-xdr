ASN.1 Processing Library: Enhanced High-Level Design (v2.0)
This document outlines an enhanced software architecture for a high-performance, modular ASN.1 processing library in Java. The design is centered around a streaming-first philosophy to ensure memory scalability and incorporates robust error handling and advanced data transformation capabilities.

Overall Architecture: A Streaming Pipeline
The library follows a sequential, event-driven pipeline model. Raw binary data is processed as a stream, preventing the need to load entire files into memory. Each component processes a stream of events or records from the previous stage, enriching the data as it flows through the system.

Component Deep Dive
1. The Grammar Parser & AST
   Objective: Parse a textual ASN.1 grammar file (.asn) into a structured, in-memory ASN1Schema object that acts as a "rulebook" for the entire system.

Key Classes:

ASN1GrammarParser: The entry point. Uses a parser generator like ANTLR to interpret the .asn file.

ASN1Schema: The thread-safe, in-memory representation of the grammar. Models all ASN.1 types, including first-class support for complex types like CHOICE and ANY DEFINED BY.

ASN1TypeDefinition: An interface for all type definitions (e.g., SequenceDef, IntegerDef).

Enhancement (Performance): After the initial parse, the ASN1Schema object is serialized to a binary cache file. On subsequent application startups, the library deserializes this cache file, bypassing the expensive parsing step entirely.

2. The TLV Event Streamer
   Objective: Read the raw binary data stream and emit structural events without building a full in-memory tree. This component is purely about structure, not interpretation.

Key Classes:

TLVEventStreamer: The core class that reads an InputStream.

Logic:

Reads the input byte-by-byte to identify Tag-Length-Value (TLV) structures.

Instead of creating a RawNode object, it emits a sequence of events to a listener (the Semantic Mapper).

Example events: onStartTag(tag, length, isConstructed), onPrimitiveValue(bytes), onEndTag(tag).

It is designed to handle files containing a continuous sequence of top-level records, emitting events until the input stream is exhausted.

3. The Semantic Event Mapper
   Objective: Listen to events from the TLVEventStreamer and, using the ASN1Schema, assemble complete, meaningful records one at a time.

Key Classes:

SemanticEventMapper: Implements the listener interface for TLVEventStreamer events.

DecodedNode: A temporary, in-memory tree structure representing only the current record being built.

Logic:

Maintains a state machine, tracking its current position in both the data stream and the ASN1Schema grammar.

On onStartTag, it traverses deeper into its understanding of the grammar. For a CHOICE type, it uses the tag to select the correct path.

On onPrimitiveValue, it looks up the expected primitive type from the schema, passes the raw byte[] to the appropriate decoder in the Decoder Library, and stores the typed result.

On onEndTag for a top-level record (e.g., CallEventRecord), it passes the fully assembled DecodedNode for that single record downstream to the Flattener, then discards its state to begin the next record.

4. The Decoder Library
   Objective: A stateless utility library for converting raw byte arrays into standard Java types.

Key Classes:

ASN1Decoders: A final utility class with public static methods.

Logic: Contains a collection of highly optimized, easily testable functions like decodeInteger(byte[]), decodeIA5String(byte[]), decodeBCD(byte[]), etc. This component is unchanged but is now called by the SemanticEventMapper.

5. The Flattener & Formatter
   Objective: Convert the hierarchical DecodedNode record stream into a stream of flat Map<String, Object> records and generate a corresponding Avro schema, driven by a flexible configuration.

Key Classes:

TreeFlattener: Consumes a DecodedNode and the configuration to produce a flat map.

AvroSchemaGenerator: Consumes the configuration to produce a valid Avro schema string.

Logic: This component is driven by an enhanced configuration file that provides fine-grained control over the output.

Enhanced Configuration (rules.yaml):

# rules.yaml
schema_name: "EnrichedCDRRecord"
fields:
# Simple mapping with explicit type and documentation
- name: "imsi"
  path: "callEventRecord.servingNetwork.imsi"
  type: "string"
  doc: "The International Mobile Subscriber Identity."

# Auto-named field using path
- path: "callEventRecord.callDuration"
  type: "long"
  doc: "Duration of the call in seconds."
  # Final name will be 'callEventRecord_callDuration'

# Rule to reduce a list of sub-records into parent record fields
- reduce: "callEventRecord.listOfServiceChanges"
  as: "service" # prefix for flattened field names
  key_by: "serviceCode" # Optional: create a map instead of a list
  rules:
  # function: fieldName
  "min": "timeOfFirstUsage"
  "max": "timeOfLastUsage"
  "sum": "dataVolumeGPRSDownlink"
  "first": "initialServiceState"
  "count": "" # Counts all items in the list, flattened as 'service_count'
  # Default value can also be provided
- groupBy:["ratingGroup", "localSequenceNumber"]
# Rule for Cartesian product (use with caution)
- expand: "callEventRecord.locations"
  # This will create a separate output record for each location,
  # joining it with all parent fields.

Error Handling and Resilience
Malformed data is an expected condition. The library must handle it gracefully.

Error Context: All thrown exceptions (e.g., GrammarMismatchException, CorruptTLVException) will contain rich context, including the approximate byte offset, the current tag path, and the raw data that caused the failure.

Configurable Processing Modes: The library's main entry point will allow the user to select an error handling strategy:

Fail Fast (Default): Throws an exception and stops all processing immediately. Ideal for development and testing.

Skip Record: Upon error, logs the full error context, discards the current record, and attempts to recover by finding the start of the next top-level record in the stream.

Skip Field: Upon error, logs the context, leaves the problematic field as null in the output record, and continues processing the current record.# extract-asn-xdr