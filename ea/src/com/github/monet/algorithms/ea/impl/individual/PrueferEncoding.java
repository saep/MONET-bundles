package com.github.monet.algorithms.ea.impl.individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

/**
 * Pruefer-Encoding for MSTs. A genotype of this encoding is a list of valid
 * symbols of the given length.
 *
 * The encoding uses the unique node IDs as symbols of the Pruefer-number.
 *
 * @author Sven Selmke
 *
 */
public class PrueferEncoding extends Encoding {

	/**
	 * Valid symbols to use in the encoding.
	 */
	private List<Integer> validSymbols;

	/**
	 * Unique ID for each node
	 */
	private HashMap<Integer, Node> idNodeMap;
	private HashMap<Node, Integer> nodeIdMap;

	/**
	 * Length of genotypes created using this encoding. For the
	 * Pruefer-Genotype, this is equal to n-2 (n being the number of nodes in
	 * the given graph)
	 */
	private int length;


	@Override
	public String getName() {
		return "Pruefer-Encoding";
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> parameters) {
		// Get Node IDs from parameters
		//int numNodes = Functions.getParam(parameters, "numNodes", Integer.class, null);
		this.idNodeMap = Functions.getParam(parameters, "idNodeMap", HashMap.class, null);
		this.nodeIdMap = Functions.getParam(parameters, "nodeIdMap", HashMap.class, null);

		// Set length of the encoding
		this.setLength(this.idNodeMap.size()-2);

		// Set valid symbols (use Node IDs)
		List<Integer> validSymbols = new ArrayList<Integer>( idNodeMap.keySet() );
		this.setValidSymbols(validSymbols);

		return true;
	}


	/**
	 * Creates a PrueferNumber from given Spanning Tree. The given spanning tree
	 * is represented by an edge-list of the given graph.
	 *
	 * @param g
	 *            graph for which the spanning tree is given
	 * @param edges
	 *            edges defining the spanning tree
	 * @return PrueferNumber
	 */
	@SuppressWarnings("unchecked")
	public <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Integer> tree2Pruefer(Graph<N, E, G> g, List<Edge> edges) {
		// Create Hash for "NodeID -> EdgeList"
		HashMap<Integer, List<Edge>> nodeEdgeMap = new HashMap<Integer, List<Edge>>();
		for (Edge e : edges) {
			for (Node n : g.getIncidentNodes((E)e)) {
				int NodeId = this.nodeIdMap.get(n);
				if (nodeEdgeMap.get(NodeId) == null) {
					List<Edge> edgeList = new ArrayList<Edge>();
					edgeList.add(e);
					nodeEdgeMap.put(NodeId, edgeList);
				} else {
					nodeEdgeMap.get(NodeId).add(e);
				}
			}
		}

		// Priority Queue for Leafs (Leaf = 1 Edge; Later: Add new leaf if #Edges=1)
		PriorityQueue<Integer> leafQueue = new PriorityQueue<Integer>();
		Iterator<Integer> iterator = nodeEdgeMap.keySet().iterator();
		while (iterator.hasNext()) {
		      int nodeId = iterator.next();
		      if (nodeEdgeMap.get(nodeId).size() == 1) {
		    	  leafQueue.add(nodeId);
		      }
		}

		// Main Algorithm:
		// Always remove the smallest leaf and add its neighbors(!) ID to the number
		List<Integer> prueferNumber = new ArrayList<Integer>();
		for (int i = 1; i <= this.length; i++) {
			// 1. remove smallest leaf
			int leafId = leafQueue.remove();
			Node leaf = this.idNodeMap.get(leafId);
			// 2. get neighbor ID
			Edge edge = nodeEdgeMap.get(leafId).get(0);
			Node neighbor = g.getIncidentNode((N)leaf, (E)edge);
			int neighborId = this.nodeIdMap.get(neighbor);
			prueferNumber.add(neighborId);
			// 3. add neighbor to leaf queue (if it has become one)
			nodeEdgeMap.get(neighborId).remove(edge);
			if (nodeEdgeMap.get(neighborId).size() == 1)
				leafQueue.add(neighborId);
		}
		return prueferNumber;
	}


	public List<Integer> getValidSymbols() {
		return validSymbols;
	}
	public void setValidSymbols(List<Integer> validSymbols) {
		this.validSymbols = validSymbols;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public HashMap<Integer, Node> getIdNodeMap() {
		return idNodeMap;
	}
	public void setIdNodeMap(HashMap<Integer, Node> idNodeMap) {
		this.idNodeMap = idNodeMap;
	}
	public HashMap<Node, Integer> getNodeIdMap() {
		return nodeIdMap;
	}
	public void setNodeIdMap(HashMap<Node, Integer> nodeIdMap) {
		this.nodeIdMap = nodeIdMap;
	}

}
