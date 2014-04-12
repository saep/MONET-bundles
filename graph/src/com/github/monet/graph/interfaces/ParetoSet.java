package com.github.monet.graph.interfaces;

import java.util.Set;

/**
 * Models a pareto set (pareto front).
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 *            type of graph, upper bounded by type Graph
 */
public interface ParetoSet<N extends Node, E extends Edge, G extends Graph<N, E, G>>
		extends Set<G> {

	G first();

	G last();

}
