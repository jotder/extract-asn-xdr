package com.gamma.asn1.core;

import com.gamma.asn1.core.exception.ASN1ProcessingException;
import com.gamma.asn1.core.mapper.DecodedNode;
import com.gamma.asn1.core.tlv.TLVEventStreamer;
import com.gamma.asn1.core.tlv.TLVListener;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A utility class providing high-level decoders for ASN.1 data streams.
 * This class acts as a facade over the lower-level streaming components.
 */
public final class ASN1Decoders {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ASN1Decoders() {
    }

    /**
     * Decodes an entire ASN.1 input stream into an in-memory tree of {@link DecodedNode} objects.
     * <p>
     * This method is convenient for processing complete ASN.1 structures but can consume
     * significant memory for very large data streams. For memory-constrained applications
     * or extremely large files, using the {@link TLVEventStreamer} directly with a custom
     * listener is recommended.
     *
     * @param inputStream The ASN.1 data stream to parse. The stream will be fully consumed but not closed.
     * @return The root {@link DecodedNode} of the parsed structure, or {@code null} if the stream is empty.
     * @throws ASN1ProcessingException if the data is malformed, an I/O error occurs, or the stream is truncated.
     */
    public static DecodedNode buildTree(InputStream inputStream) throws ASN1ProcessingException {
        TreeBuildingListener listener = new TreeBuildingListener();
        TLVEventStreamer streamer = new TLVEventStreamer();
        try {
            // The streamer should be modified to pass the byte offset to the listener
            // and to throw ASN1ProcessingException instead of generic Exception.
            streamer.process(inputStream, listener);
        } catch (Exception e) {
            // Wrap generic exceptions from the streamer into our specific exception type for a cleaner API.
            if (e instanceof ASN1ProcessingException) {
                throw (ASN1ProcessingException) e;
            }
            // The streamer in its current form doesn't provide context on error, so we pass placeholders.
            throw new ASN1ProcessingException("Failed to parse ASN.1 stream: " + e.getMessage(), -1, "unknown", e);
        }
        return listener.getRootNode();
    }

    /**
     * A TLVListener implementation that constructs a DecodedNode tree from streamer events.
     * It maintains a stack of nodes to correctly build the nested structure of constructed types.
     * <p>
     * <b>Note:</b> This implementation relies on a modified {@link TLVListener} interface
     * that includes the byte offset in the {@code onStartTag} event.
     * </p>
     */
    private static class TreeBuildingListener implements TLVListener {
        private final Deque<DecodedNode> nodeStack = new ArrayDeque<>();
        private DecodedNode root = null;

        /**
         * Handles the start of a new TLV element.
         *
         * @param tag           The tag identifying the data type.
         * @param length        The length of the value part.
         * @param isConstructed A flag indicating if the node is constructed.
         * @param offset        The starting byte offset of this node in the original stream.
         */
        @Override
        public void onStartTag(byte[] tag, int length, boolean isConstructed, long offset) {
            DecodedNode newNode = new DecodedNode(tag, length, isConstructed, offset);

            if (nodeStack.isEmpty()) {
                // This is the first node encountered, so it becomes the root.
                root = newNode;
            } else {
                // This is a nested node, so add it as a child to the current parent.
                // The parent is the node at the top of the stack.
                nodeStack.peek().addChild(newNode);
            }
            // The new node is now the current context, so push it onto the stack.
            nodeStack.push(newNode);
        }

        /**
         * Handles the value of a primitive TLV element.
         *
         * @param value The raw byte value.
         */
        @Override
        public void onPrimitiveValue(byte[] value) {
            DecodedNode currentNode = nodeStack.peek();
            if (currentNode == null || currentNode.isConstructed()) {
                throw new IllegalStateException("Received primitive value, but the current node is constructed or null.");
            }
            currentNode.setValue(value);
        }

        /**
         * Handles the end of a TLV element. The element is popped from the context stack.
         *
         * @param tag The tag of the element that is ending.
         */
        @Override
        public void onEndTag(byte[] tag) {
            if (nodeStack.isEmpty()) {
                throw new IllegalStateException("Received onEndTag event with an empty node stack. Mismatched tags.");
            }
            // The node at the top of the stack is now complete.
            nodeStack.pop();
        }

        /**
         * Returns the fully constructed root node after parsing is complete.
         *
         * @return The root DecodedNode.
         * @throws IllegalStateException if parsing finished but the structure is incomplete.
         */
        public DecodedNode getRootNode() {
            if (root == null) {
                return null; // Corresponds to an empty input stream.
            }
            if (!nodeStack.isEmpty()) {
                throw new IllegalStateException("Parsing finished but the node stack is not empty. Incomplete ASN.1 structure.");
            }
            return root;
        }
    }
}