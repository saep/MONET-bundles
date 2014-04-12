package com.github.monet.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import com.github.monet.datastructures.TreeUnionFind;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.interfaces.UniobjectiveAlgorithm;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;

public class  MstEdgeInsertion<N extends Node, E extends UndirectedEdge, G extends Graph<N, E, G>>
implements UniobjectiveAlgorithm<N, E, G, E> {

	@Override
	public Iterable<E> computeUniobjectiveOptimum(G graph,
			GraphElementWeightAnnotator<E> annotator) {

		// Sort edges according to scalar weights (first weight if vector)
		ArrayList<E> minimumSpanningTree = new ArrayList<>();
		ArrayList<E> edges = new ArrayList<>(graph.getAllEdges());

		EdgeComparator<E> edgeComparator  = new EdgeComparator<E>(annotator);
		Collections.sort(edges, edgeComparator);

		TreeUnionFind<N> unionFind = new TreeUnionFind<>();

		ArrayList<N> nodes = new ArrayList<>(graph.getAllNodes());
		for (N n: nodes) {
			unionFind.add(n);
		}

		int numNodes = graph.getNumNodes();
		int e = 0;

		while (e != (numNodes-1)) {
			E minEdge = edges.get(0);
			edges.remove(0);

			// Get incident nodes of minEdge
			ArrayList<N> minEdgeNodes = new ArrayList<>(graph.getIncidentNodes(minEdge));

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
