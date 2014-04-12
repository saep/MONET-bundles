package com.github.monet.graph.interfaces;

/**
 * Cost function: Graph G -> Costs C
 *
 * @param <N> Node type
 * @param <E> Edge type
 * @param <G> Graph type
 * @param <C> Cost type
 *
 *
 */
public interface CostCalculator<N extends Node, E extends Edge, G extends Graph<N, E, G>, C> {

	/**
	 * Evaluates the cost function
	 *
	 * @param graph Input graph
	 * @return Output value
	 * @throws Exception
	 */
	public C calculateCosts(G graph);
}
