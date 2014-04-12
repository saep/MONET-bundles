package com.github.monet.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.github.monet.datastructures.TreeUnionFind;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;

/**
 * Implementation of kruskals algorithm with "banned" and " forced" edges.
 *
 * @author Christopher Morris
 *
 * @param <N> type of node
 * @param <E> type of edge
 * @param <G> type of graph
 */
public class MSTBannedForcedEdges<N extends Node, E extends Edge, G extends Graph<N, E, G>> {

	public Iterable<E> computeMSTwithForcedEdges(G originalGraph,
			GraphElementWeightAnnotator<E> annotator, Collection<E> forcedEdges,
			Collection<E> bannedEdges) {

		// Create copy of original graph
		G graph = originalGraph.getSubgraphWithImpliedNodes(originalGraph.
				getAllEdges());

		// Delete banned edges
		for (E e : bannedEdges) {
			graph.deleteEdge(e);
		}

		// Sort edges according to scalar weights (first weight if vector)
		ArrayList<E> minimumSpanningTree = new ArrayList<>();
		ArrayList<E> edges = new ArrayList<>(graph.getAllEdges());

		EdgeComparator<E> edgeComparator = new EdgeComparator<>(annotator);

		// Remove forced edges
		for (E e : forcedEdges) {
			edges.remove(e);
		}

		// Insert forced edges at first position of sorted edge list
		Collections.sort(edges, edgeComparator);
		edges.addAll(0, forcedEdges);

		TreeUnionFind<N> unionFind = new TreeUnionFind<>();
		unionFind.setSafeMode(false);

		ArrayList<N> nodes = new ArrayList<>(graph.getAllNodes());
		for (N n : nodes) {
			unionFind.add(n);
		}

		int numNodes = graph.getNumNodes();
		int e = 0;

		while (e != (numNodes - 1)) {
			E minEdge = edges.get(0);
			edges.remove(0);

			// Get incident nodes of minEdge
			ArrayList<N> minEdgeNodes = new ArrayList<>(graph.getIncidentNodes(
					minEdge));

			// Get root
			N n1 = unionFind.find(minEdgeNodes.get(0));
			N n2 = unionFind.find(minEdgeNodes.get(1));

			// Check if the incident nodes of minEdge are NOT in the same partition (-> cycle free)
			if (!n1.equals(n2)) {
				minimumSpanningTree.add(minEdge);  // add minEdge to spanning tree
				e++;
				unionFind.union(n1, n2);  // union partitions
			}
		}

		return minimumSpanningTree;
	}
}
