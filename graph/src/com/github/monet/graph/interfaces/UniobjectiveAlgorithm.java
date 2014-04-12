package com.github.monet.graph.interfaces;

import com.github.monet.graph.weighted.GraphElementWeightAnnotator;

/**
 * Models an interface for an algorithm to compute an optima for an uniobjective
 * optimization problem
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 *            type of graph, upper bounded by type Graph
 * @param <GE>
 *            type of graph element, upper bounded by type GraphElement
 */
public interface UniobjectiveAlgorithm<N extends Node, E extends Edge, G extends Graph<N, E, G>, GE extends GraphElement> {

	/**
	 * @param type
	 *            of graph, upper bounded by type Graph
	 * @param annotator
	 *            of type GraphElementWeightAnnotator
	 * @return iterable object of edges, which represent an optima
	 */
	public Iterable<E> computeUniobjectiveOptimum(G graph,
			GraphElementWeightAnnotator<GE> annotator);
}
