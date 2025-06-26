package com.sigma.asn1.flattener;

import com.sigma.asn1.core.DecodedNode;
import com.sigma.asn1.flattener.rules.FlattenerRules; // To be created
import java.util.Map;
import java.util.stream.Stream;

/**
 * Interface for flattening a hierarchical DecodedNode into a stream of flat records (Map<String, Object>),
 * based on a set of FlattenerRules.
 */
public interface TreeFlattener {

    /**
     * Flattens a single DecodedNode into a stream of one or more flat records.
     * The stream may contain multiple records if 'expand' rules are applied.
     *
     * @param node The DecodedNode to flatten.
     * @param rules The FlattenerRules to apply.
     * @return A Stream of Map<String, Object>, where each map represents a flat record.
     * @throws FlattenerException if an error occurs during flattening.
     */
    Stream<Map<String, Object>> flattenNode(DecodedNode node, FlattenerRules rules) throws FlattenerException;

}
