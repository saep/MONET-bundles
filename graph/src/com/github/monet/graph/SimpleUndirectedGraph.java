package com.github.monet.graph;

import java.util.LinkedList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.github.monet.graph.interfaces.*;

/**
 * Implements interface DirectedGraph.
 *
 * @author Christopher Morris
 *
 */
public class SimpleUndirectedGraph extends
		SimpleAbstractGraph<SimpleUndirectedGraph> implements
		UndirectedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> {

	protected HashMap<SimpleNode, List<SimpleEdge>> adjacencyMap;

	public SimpleUndirectedGraph() {
		this.adjacencyMap = new HashMap<>();
	}

	@Override
	public SimpleEdge addEdge(SimpleNode u, SimpleNode v) {

		// Check if both nodes do exist in graph
		if (!this.safeMode || (this.nodes.contains(u) && this.nodes.contains(v))) {
			SimpleEdge e = new SimpleEdge(u, v);

			this.edges.add(e);
			this.adjacencyMap.get(u).add(e);
			this.adjacencyMap.get(v).add(e);

			return e;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteEdge(SimpleEdge e) {
		if (!this.safeMode || this.edges.contains(e)) {
			boolean successful = true;
			successful &= edges.remove(e);
			successful &= this.adjacencyMap.get(e.u).remove(e);
			successful &= this.adjacencyMap.get(e.v).remove(e);
			return successful;
		} else {
			return false;
		}
	}

	@Override
	public SimpleNode addNode() {
		SimpleNode n = new SimpleNode(this.maxNodeId);
		this.maxNodeId++;

		this.nodes.add(n);
		this.adjacencyMap.put(n, new LinkedList<SimpleEdge>());

		return n;
	}

	@Override
	public boolean deleteNode(SimpleNode u) {
		if (!this.safeMode || this.nodes.contains(u)) {
			boolean successful = true;

			List<SimpleEdge> edgeList = this.adjacencyMap.get(u);
			while (!edgeList.isEmpty()) {
				successful &= deleteEdge(edgeList.get(0));
			}

			successful &= this.adjacencyMap.containsKey(u);
			this.adjacencyMap.remove(u);
			successful &= this.nodes.remove(u);
			return successful;
		} else {
			return false;
		}
	}

	@Override
	public Collection<SimpleEdge> getIncidentEdges(SimpleNode u) {
		return this.adjacencyMap.get(u);
	}

	@Override
	public SimpleUndirectedGraph getSubgraph(Iterable<SimpleNode> nodes,
			Iterable<SimpleEdge> edges) {

		SimpleUndirectedGraph subgraph = new SimpleUndirectedGraph();


		for (SimpleNode node : nodes) {
			// Check if node does exist in original graph
			if (!this.safeMode || this.nodes.contains(node)) {
				subgraph.nodes.add(node);
				subgraph.adjacencyMap.put(node, new LinkedList<SimpleEdge>());
				if (subgraph.maxNodeId < node.getId()) {
					subgraph.maxNodeId = node.getId();
				}
			} else {
				assert false : "Node does not exist in original graph";
				return null;
			}
		}
		for (SimpleEdge edge : edges) {
			// Check if edge does exist in original graph
			if (!this.safeMode || this.edges.contains(edge)) {
				subgraph.edges.add(edge);
				subgraph.adjacencyMap.get(edge.u).add(edge);
				subgraph.adjacencyMap.get(edge.v).add(edge);
			} else {
				assert false : "Edge does not exist in original graph";
				return null;
			}
		}

		return subgraph;
	}

	@Override
	public SimpleEdge getEdge(SimpleNode u, SimpleNode v) {
		List<SimpleEdge> incidentU = adjacencyMap.get(u);

		for (SimpleEdge edge : incidentU) {
			if (getIncidentNode(u, edge) == v) {
				return edge;
			}
		}
		return null;
	}

	@Override
	public Collection<SimpleNode> getAdjacentNodes(SimpleNode u) {
		List<SimpleEdge> adjacent_edges = adjacencyMap.get(u);
		LinkedList<SimpleNode> adjacent_nodes = new LinkedList<>();
		for (SimpleEdge edge : adjacent_edges) {
			adjacent_nodes.add(getIncidentNode(u, edge));
		}
		return adjacent_nodes;
	}
}
