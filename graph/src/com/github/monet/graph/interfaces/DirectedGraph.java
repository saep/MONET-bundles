package com.github.monet.graph.interfaces;

import java.util.Collection;

/**
 * Models a generic directed Graph.
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type DirectedEdge
 * @param <G>
 *            mtype of implementation of interface Graph
 */
public interface DirectedGraph<N extends Node, E extends DirectedEdge, G extends DirectedGraph<N, E, G>>
		extends Graph<N, E, G> {

	/**
	 * Returns the negative incident node of directed edge e.
	 *
	 * @param e
	 *            edge, whose negative incident node is to be returned
	 * @return the node negative incident to the directed edge e
	 */
	public N getSource(E e);

	/**
	 * Returns the positive incident node of directed edge e.
	 *
	 * @param e
	 *            edge, whose positive incident node is to be returned
	 * @return the node positive incident to the directed edge e
	 */
	public N getTarget(E e);


	/**
	 * Returns the positive incident edges of node n.
	 * @param n node, whose positive incident edges are to be returned
	 * @return the positive incident edges of node n
	 */
	public Collection<E> getIncomingEdges(N n);


	/**
	 * Returns the negative incident edges of node n.
	 * @param n node, whose negative incident edges are to be returned
	 * @return the negative incident edges of node n
	 */
	public Collection<E> getOutgoingEdges(N n);

	/**
	 * Returns predecessors of node n
	 * @param n node, whose preceding nodes are to be returned
	 * @return Predecessors of n
	 */
	public Collection<N> getPrecedingNodes(N n);

	/**
	 * Returns successors of node n
	 * @param n node, whose suceeding nodes are to be returned
	 * @return Successors of n
	 */
	public Collection<N> getSucceedingNodes(N n);
}
