package com.github.monet.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

import com.github.monet.graph.interfaces.Graph;

/**
 * Contains methods which are implemented by SimpleDirectedGraph and SimpleUnidrectedGraph
 * in the same way.
 *
 * @author Christopher Morris
 *
 * @param <G>
 *            type of graph, upper bounded by type Graph
 */
public abstract class SimpleAbstractGraph<G extends SimpleAbstractGraph<G>>
		implements Graph<SimpleNode, SimpleEdge, G> {

	/**
	 * Manages set of nodes
	 */
	protected LinkedList<SimpleNode> nodes;

	/**
	 * Manages set of edges
	 */
	protected LinkedList<SimpleEdge> edges;

	/**
	 * Holds the maximum node id used so far, + 1
	 */
	protected int maxNodeId;

	/**
	 * Without safe mode no expensive tests are performed
	 */
	protected boolean safeMode;

	public SimpleAbstractGraph() {
		this.nodes = new LinkedList<SimpleNode>();
		this.edges = new LinkedList<SimpleEdge>();
		this.maxNodeId = 0;
		this.safeMode = true;
	}

	@Override
	public int getNumNodes() {
		return this.nodes.size();
	}

	@Override
	public int getNumEdges() {
		return this.edges.size();
	}

	@Override
	public abstract SimpleEdge addEdge(SimpleNode u, SimpleNode v);

	@Override
	public abstract boolean deleteEdge(SimpleEdge SimpleEdge);

	@Override
	public abstract SimpleNode addNode();

	@Override
	public abstract boolean deleteNode(SimpleNode u);

	@Override
	public Collection<SimpleNode> getAllNodes() {
		return this.nodes;
	}

	@Override
	public Collection<SimpleEdge> getAllEdges() {
		return this.edges;
	}

	@Override
	public Collection<SimpleNode> getIncidentNodes(SimpleEdge e) {
		if (!this.safeMode || this.edges.contains(e)) {
			ArrayList<SimpleNode> nodes = new ArrayList<>();
			nodes.add(e.u);
			nodes.add(e.v);
			return nodes;
		} else {
			return null;
		}
	}

	@Override
	public abstract Collection<SimpleEdge> getIncidentEdges(SimpleNode u);

	@Override
	public SimpleNode getIncidentNode(SimpleNode u, SimpleEdge e) {
		if (!this.safeMode || this.edges.contains(e)) {
			if (e.u == u) {
				return e.v;
			} else {
				return e.u;
			}
		} else {
			return null;
		}
	}

	@Override
	public abstract G getSubgraph(Iterable<SimpleNode> nodes,
			Iterable<SimpleEdge> edges);

	@Override
	public G getSubgraphWithImpliedNodes(Iterable<SimpleEdge> edges) {
		HashSet<SimpleNode> nodes = new HashSet<>();

		for (SimpleEdge edge : edges) {

			if (!this.safeMode || this.edges.contains(edge)) {
				nodes.add(edge.u);
				nodes.add(edge.v);
			} else {
				return null;
			}
		}


		G s = getSubgraph(nodes, edges);

		assert s != null : "Subgraph must not be 'null'";

		return s;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof SimpleAbstractGraph)) {
			return false;
		} else {
			SimpleAbstractGraph<G> compareTo = (SimpleAbstractGraph<G>) o;
			boolean isEqual = true;
			isEqual &= this.nodes.containsAll(compareTo.nodes);
			isEqual &= compareTo.nodes.containsAll(this.nodes);
			isEqual &= this.edges.containsAll(compareTo.edges);
			isEqual &= compareTo.edges.containsAll(this.edges);
			return isEqual;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = (47 * hash) + Objects.hashCode(this.nodes);
		hash = (47 * hash) + Objects.hashCode(this.edges);
		return hash;
	}

	/**
	 * @return the safeMode
	 */
	public boolean isSafeMode() {
		return safeMode;
	}

	/**
	 * @param safeMode the safeMode to set
	 */
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
}
