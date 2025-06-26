package com.gamma.asn1.core.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single decoded Tag-Length-Value (TLV) node from an ASN.1 data stream.
 * <p>
 * This class provides an object-oriented, in-memory representation of an ASN.1 structure.
 * A {@code DecodedNode} can be either:
 * <ul>
 *     <li><b>Primitive:</b> It holds a raw byte array as its value. It has no children.</li>
 *     <li><b>Constructed:</b> Its value is a sequence of other TLV nodes. It holds a list of child {@code DecodedNode}s.</li>
 * </ul>
 * This class is mutable during its construction phase (e.g., by a parser that calls {@code addChild} or {@code setValue}).
 * Once the tree is fully built, it should be treated as an immutable data structure.
 */
public class DecodedNode {

    private final byte[] tag;
    private final int length;
    private final boolean constructed;
    private final long byteOffset;
    private final List<DecodedNode> children;
    private byte[] value;

    /**
     * Constructs a TLV node. Its value or children are expected to be populated later by a parser.
     *
     * @param tag         The tag identifying the data type.
     * @param length      The length of the value part.
     * @param constructed A flag indicating if the node is constructed.
     * @param byteOffset  The starting byte offset of this node in the original stream.
     */
    public DecodedNode(byte[] tag, int length, boolean constructed, long byteOffset) {
        this.tag = Objects.requireNonNull(tag, "Tag cannot be null");
        this.length = length;
        this.constructed = constructed;
        this.byteOffset = byteOffset;

        if (constructed) {
            this.children = new ArrayList<>();
            this.value = null;
        } else {
            this.children = Collections.emptyList();
            // Primitive value will be set later via setValue()
            this.value = null;
        }
    }

    /**
     * Gets the tag bytes.
     *
     * @return A copy of the tag byte array to preserve immutability.
     */
    public byte[] getTag() {
        return tag.clone();
    }

    /**
     * Gets the length of the value part of the TLV.
     *
     * @return The length in bytes.
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the raw value of a primitive node.
     *
     * @return A copy of the value byte array, or {@code null} if this is a constructed node or if the value has not been set.
     */
    public byte[] getValue() {
        if (constructed) {
            return null;
        }
        return value != null ? value.clone() : null;
    }

    /**
     * Gets the list of child nodes for a constructed node.
     *
     * @return An unmodifiable view of the list of child nodes, or an empty list if this is a primitive node.
     */
    public List<DecodedNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Checks if this node is a constructed type.
     *
     * @return {@code true} if constructed, {@code false} if primitive.
     */
    public boolean isConstructed() {
        return constructed;
    }

    /**
     * Gets the starting byte offset of this node in the original input stream.
     *
     * @return The byte offset.
     */
    public long getByteOffset() {
        return byteOffset;
    }

    /**
     * Adds a child node. This method should only be called on a constructed node during parsing.
     *
     * @param child The child node to add.
     * @throws IllegalStateException if called on a primitive node.
     */
    public void addChild(DecodedNode child) {
        if (!constructed) {
            throw new IllegalStateException("Cannot add children to a primitive node.");
        }
        Objects.requireNonNull(child, "Child node cannot be null");
        this.children.add(child);
    }

    /**
     * Sets the value for a primitive node. This method should only be called during parsing.
     *
     * @param value The raw byte value.
     * @throws IllegalStateException if called on a constructed node.
     */
    public void setValue(byte[] value) {
        if (constructed) {
            throw new IllegalStateException("Cannot set value on a constructed node.");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, 0);
        return sb.toString();
    }

    private void toStringHelper(StringBuilder sb, int indentLevel) {
        String indent = String.join("", Collections.nCopies(indentLevel, "  "));
        sb.append(indent)
                .append("Tag: ").append(toHexString(tag))
                .append(", Length: ").append(length)
                .append(", Offset: ~").append(byteOffset);

        if (constructed) {
            sb.append(" (Constructed)\n");
            for (DecodedNode child : children) {
                child.toStringHelper(sb, indentLevel + 1);
            }
        } else {
            sb.append(" (Primitive), Value: ").append(toHexString(value)).append("\n");
        }
    }

    private static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        if (bytes.length == 0) {
            return "[empty]";
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}