package com.github.monet.graph.interfaces;

import java.util.Collection;

/**
 * Models a generic Graph.
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 *            type of graph upper bounded by Graph, thus the return type of
 *            getSubgraph* is known at compile time
 */
public interface Graph<N extends Node, E extends Edge, G extends Graph<N, E, G>> {
	/**
	 * Returns the number of nodes in the graph.
	 *
	 * @return number of nodes in the graph
	 */
	public int getNumNodes();

	/**
	 * Returns the number of edges in the graph.
	 *
	 * @return number of edges in the graph
	 */
	public int getNumEdges();

	/**
	 * Adds a new edge (u,v) into the graph and returns it.
	 *
	 * @param u
	 *            incident node of new edge
	 * @param v
	 *            incident node of new edge
	 *
	 * @return newly added edge, if u and v exist in the graph, otherwise null
	 */
	public E addEdge(N u, N v);

	/**
	 * Deletes edge e.
	 *
	 * @param e
	 *            e, to be deleted
	 *
	 * @return true, if e exists in the graph, otherwiss false
	 */
	public boolean deleteEdge(E e);

	/**
	 * Adds a new node.
	 *
	 * @return newly added edge
	 */
	public N addNode();

	/**
	 * Deletes node u in the graph.
	 *
	 * @param u
	 *            node u, to be deleted
	 * @return true, if node u did exist in the graph, otherwise false
	 */
	public boolean deleteNode(N u);

	/**
	 * Returns all nodes in the graph.
	 *
	 * @return all nodes in the graph
	 */
	public Collection<N> getAllNodes();

	/**
	 * Returns all edges in the graph.
	 *
	 * @return all edges in the graph
	 */
	public Collection<E> getAllEdges();

	/**
	 * Returns incident edges of node u
	 *
	 * @param u
	 *            node whose incident edges are to be returned
	 * @return incident edges of node u, if u exits in the graph, otherwise null
	 */
	public Collection<E> getIncidentEdges(N u);

	/**
	 * Returns incident nodes of edge e.
	 *
	 * @param e
	 *            edge whose incident nodes are to be returned
	 * @return incident nodes of edge e, if e exits in the graph, otherwise null
	 */
	public Collection<N> getIncidentNodes(E e);

	/**
	 * Returns subgraph of the graph.
	 *
	 * @param nodes
	 *            nodes of resulting subgraph
	 * @param edges
	 *            edges in resulting subgraph
	 * @return subgraph consisting of all nodes and all edges in parameters
	 *         nodes and edges, if all such exist in the graph, otherwise null.
	 */
	public G getSubgraph(Iterable<N> nodes, Iterable<E> edges);

	/**
	 * @param edges
	 *            edges in resulting subgraph
	 * @return subgraph consisting of all edges and induced nodes in parameter
	 *         edges, if all edges in parameter edges exits in graph, otherwise
	 *         null
	 */
	public G getSubgraphWithImpliedNodes(Iterable<E> edges);

	/**
	 * Returns the node adjacent to node u and incident to edge e.
	 *
	 * @param u
	 *            node, whose adjacent node it to be returned
	 * @param e
	 *            edge, whose incident node is to be returned
	 * @return node, adjacent to node u and incident to edge e, if both exist in
	 *         the graph, otherwise null
	 */
	public N getIncidentNode(N u, E e);

	/**
	 * Returns the edge incident to nodes u and v
	 *
	 * @param u
	 *            node in the the graph
	 * @param v
	 *            node in the the graph
	 * @return edge incident to nodes u and v, if exists, otherwise null
	 */
	public E getEdge(N u, N v);

	/**
	 * Returns all nodes adjacent to node u
	 * @param u
	 *            node in the the graph
	 * @return All nodes adjacent to u
	 */
	public Collection<N> getAdjacentNodes(N u);
}
