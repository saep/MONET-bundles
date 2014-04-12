package com.github.monet.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.monet.graph.interfaces.DirectedGraph;

/**
 * Implements the interface DirectedGraph.
 *
 * @author Michael Capelle, Christopher Morris
 *
 */
public class SimpleDirectedGraph extends
		SimpleAbstractGraph<SimpleDirectedGraph> implements
		DirectedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> {

	/**
	 * Maps node to it's positive incident edges.
	 */
	protected HashMap<SimpleNode, List<SimpleEdge>> outMap;

	/**
	 * Maps node to it's negative incident edges.
	 */
	protected HashMap<SimpleNode, List<SimpleEdge>> inMap;

	public SimpleDirectedGraph() {
		super();

		this.outMap = new HashMap<>();
		this.inMap = new HashMap<>();
	}

	@Override
	public SimpleEdge addEdge(SimpleNode u, SimpleNode v) {

		// Check if both nodes do exist in graph
		if (!this.safeMode || (this.nodes.contains(u) && this.nodes.contains(v))) {
			SimpleEdge e = new SimpleEdge(u, v);

			this.edges.add(e);
			this.outMap.get(u).add(e);
			this.inMap.get(v).add(e);

			return e;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteEdge(SimpleEdge e) {
		if (edges.remove(e)) {
			if (this.outMap.get(e.u).remove(e) && this.inMap.get(e.v).remove(e)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public SimpleNode addNode() {
		SimpleNode n = new SimpleNode(this.maxNodeId);
		this.maxNodeId++;

		this.nodes.add(n);
		this.outMap.put(n, new ArrayList<SimpleEdge>());
		this.inMap.put(n, new ArrayList<SimpleEdge>());

		return n;
	}

	@Override
	public boolean deleteNode(SimpleNode u) {

		// Check if node u does exists in graph
		if (!this.safeMode || this.nodes.contains(u)) {
			boolean successful = true;

			List<SimpleEdge> outList = this.outMap.get(u);
			while (!outList.isEmpty()) {
				successful &= deleteEdge(outList.get(0));
			}

			List<SimpleEdge> inList = this.inMap.get(u);
			while (!inList.isEmpty()) {
				successful &= deleteEdge(inList.get(0));
			}

			successful &= this.inMap.containsKey(u);
			this.inMap.remove(u);
			successful &= this.outMap.containsKey(u);
			this.outMap.remove(u);
			successful &= this.nodes.remove(u);

			return successful;
		} else {
			return false;
		}
	}

	@Override
	public Collection<SimpleEdge> getIncomingEdges(SimpleNode u) {
		return this.inMap.get(u);
	}

	@Override
	public Collection<SimpleEdge> getOutgoingEdges(SimpleNode u) {
		return this.outMap.get(u);
	}

	@Override
	public Collection<SimpleEdge> getIncidentEdges(SimpleNode u) {
		List<SimpleEdge> retval = new ArrayList<SimpleEdge>();
		retval.addAll(this.outMap.get(u));
		retval.addAll(this.inMap.get(u));
		return retval;
	}

	@Override
	public SimpleDirectedGraph getSubgraph(Iterable<SimpleNode> nodes,
			Iterable<SimpleEdge> edges) {
		SimpleDirectedGraph subgraph = new SimpleDirectedGraph();

		// Add nodes to new subgraph
		for (SimpleNode node : nodes) {

			// Check if node is in original graph
			if (!this.safeMode || this.nodes.contains(node)) {
				subgraph.nodes.add(node);
				subgraph.inMap.put(node, new ArrayList<SimpleEdge>());
				subgraph.outMap.put(node, new ArrayList<SimpleEdge>());

				if (subgraph.maxNodeId < node.getId()) {
					subgraph.maxNodeId = node.getId();
				}
			} else {
				return null;
			}
		}

		// Add edges to new subgraph
		for (SimpleEdge edge : edges) {

			// Check if edge is in original graph
			if (!this.safeMode || this.edges.contains(edge)) {
				subgraph.edges.add(edge);
				subgraph.outMap.get(edge.u).add(edge);
				subgraph.inMap.get(edge.v).add(edge);
			} else {
				return null;
			}
		}

		return subgraph;
	}

	@Override
	public SimpleEdge getEdge(SimpleNode u, SimpleNode v) {
		List<SimpleEdge> incidentU = outMap.get(u);

		for (SimpleEdge edge : incidentU) {
			if (getIncidentNode(u, edge) == v) {
				return edge;
			}
		}

		// Edge does not exist
		return null;
	}

	@Override
	public SimpleNode getSource(SimpleEdge edge) {
		return edge.u;
	}

	@Override
	public SimpleNode getTarget(SimpleEdge edge) {
		return edge.v;
	}

	@Override
	public Collection<SimpleNode> getAdjacentNodes(SimpleNode u) {
		List<SimpleEdge> inEdges = inMap.get(u);
		List<SimpleEdge> outEdges = outMap.get(u);
		HashSet<SimpleNode> adjacent_nodes = new HashSet<>(inEdges.size()
				+ outEdges.size());
		for (SimpleEdge edge : inEdges) {
			adjacent_nodes.add(getIncidentNode(u, edge));
		}
		for (SimpleEdge edge : outEdges) {
			adjacent_nodes.add(getIncidentNode(u, edge));
		}
		return adjacent_nodes;
	}

	@Override
	public Collection<SimpleNode> getPrecedingNodes(SimpleNode n) {
		List<SimpleEdge> inEdges = inMap.get(n);
		HashSet<SimpleNode> adjacent_nodes = new HashSet<>(inEdges.size());
		for (SimpleEdge edge : inEdges) {
			adjacent_nodes.add(getIncidentNode(n, edge));
		}
		return adjacent_nodes;
	}

	@Override
	public Collection<SimpleNode> getSucceedingNodes(SimpleNode n) {
		List<SimpleEdge> outEdges = outMap.get(n);
		HashSet<SimpleNode> adjacent_nodes = new HashSet<>(outEdges.size());
		for (SimpleEdge edge : outEdges) {
			adjacent_nodes.add(getIncidentNode(n, edge));
		}
		return adjacent_nodes;
	}
}
