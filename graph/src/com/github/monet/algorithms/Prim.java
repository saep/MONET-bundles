package com.github.monet.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.github.monet.graph.SimplePriorityQueue;
import com.github.monet.graph.interfaces.*;
import com.github.monet.graph.weighted.*;

public class Prim<N extends Node, E extends UndirectedEdge, G extends Graph<N, E, G>>
		implements UniobjectiveAlgorithm<N, E, G, E> {

	@Override
	public Iterable<E> computeUniobjectiveOptimum(G graph,
			GraphElementWeightAnnotator<E> annotator) {

		ArrayList<E> minimumSpanningTree = new ArrayList<>();

		// True iff node was already visited by algorithm
		HashMap<N, Boolean> visited = new HashMap<>();

		ArrayList<N> nodeList = new ArrayList<>(graph.getAllNodes().size());
		for (N n : graph.getAllNodes()) {
			visited.put(n, false);
			nodeList.add(n);
		}

		int numEdges = graph.getNumEdges();
		SimplePriorityQueue<E> pq = new SimplePriorityQueue<>(numEdges);

		// Root node of resulting minimum spanning tree
		N next = nodeList.get(0);
		visited.put(next, true);

		int numNodes = graph.getNumNodes();
		for (int i = 1; i < numNodes;) {
			for (E e : graph.getIncidentEdges(next)) {
				N adjacentNode = graph.getIncidentNode(next, e);

				if (!visited.get(adjacentNode)) {
					pq.add(e, annotator.getAnnotation(e).getFirstWeight());
				}
			}

			E minEdge = pq.poll();

			Collection<N> minEdgeNodes = graph.getIncidentNodes(minEdge);

			int c = 0;
			// Find unvisited node of minEdge, if exists
			for (N n : minEdgeNodes) {
				if (!visited.get(n)) {
					next = n;
					break;
				}
				c++;
			}

			if (c == 2) { // MinEdge would lead to cyclic solutions
				continue;
			} else {
				minimumSpanningTree.add(minEdge);
				visited.put(next, true);
				i++;
			}
		}

		return minimumSpanningTree;
	}
}
