package com.github.monet.graph.interfaces;

/**
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>type of graph, upper bounded by type UndirectedEdge
 */
public interface UndirectedGraph<N extends Node, E extends UndirectedEdge, G extends UndirectedGraph<N, E, G>>
		extends Graph<N, E, G> {
}
