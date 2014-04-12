package com.github.monet.graph.tests;

import java.util.LinkedList;

import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

/**
 * Spanning tree tests for graph objects
 *
 *
 */
public class VariousGraphTests<N extends Node, E extends Edge, G extends Graph<N, E, G>> {

	private enum Mark {

		VISITED, QUEUED, UNKNOWN
	}

	public boolean isConnected(G graph) {
		assert graph != null : "graph must not be null";

		// Check for empty graph
		if (graph.getNumNodes() == 0) {
			return true;
		}

		// Create checklist to mark visited nodes
		GraphElementReverseHashAnnotator<Node, Mark> checklist =
				new GraphElementReverseHashAnnotator<>();
		for (N u : graph.getAllNodes()) {
			checklist.setAnnotation(u, Mark.UNKNOWN);
		}

		// Create visit queue
		LinkedList<N> queue = new LinkedList<>();
		queue.offer(graph.getAllNodes().iterator().next());

		// Visit nodes (BFS)
		while (!queue.isEmpty()) {
			N currentNode = queue.poll();
			checklist.setAnnotation(currentNode, Mark.VISITED);
			for (N neighbour : graph.getAdjacentNodes(currentNode)) {
				if (checklist.getAnnotation(neighbour) == Mark.UNKNOWN) {
					queue.add(neighbour);
					checklist.setAnnotation(neighbour, Mark.QUEUED);
				}
			}
		}

		return checklist.getElements(Mark.UNKNOWN).isEmpty() && checklist.
				getElements(Mark.QUEUED).isEmpty();
	}

	public boolean isSpanningTree(G graph) {
		assert graph != null : "graph must not be null";

		if (graph.getNumEdges() != graph.getNumNodes() - 1) {
			return false;
		} else {
			return isConnected(graph);
		}
	}
}
