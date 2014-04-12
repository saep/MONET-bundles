package com.github.monet.algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimplePriorityQueue;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.interfaces.UniobjectiveAlgorithm;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

/**
 * Implementation of Disjkstra's SSSP algorithm
 *
 * @author Jakob Bossek
 *
 * @param <N> type of node
 * @param <E> type of edge
 * @param <G> type of graph
 */
public class Dijkstra<N extends Node, E extends UndirectedEdge, G extends Graph<N, E, G>>
	implements UniobjectiveAlgorithm<N, E, G, E> {

	private N source = null;
	private N dest = null;

	/**
	 * Generate a Dijkstra object.
	 *
	 * @param source
	 * 	Source node.
	 * @param dest
	 * 	Destination node.
	 */
	public Dijkstra(N source, N dest) {
		this.source = source;
		this.dest = dest;
	}

	@Override
	public Iterable<E> computeUniobjectiveOptimum(G graph,
      GraphElementWeightAnnotator<E> annotator) {

		/*
		 * Structure for the edges of the computed shortest path.
		 */
		ArrayList<E> shortestPath = new ArrayList<>();

		/*
		 * Distances computed by algorithm.
		 */
		HashMap<N, Double> distance = new HashMap<>();
		/*
		 * Predecessor map.
		 */
		HashMap<N, N> predecessor = new HashMap<>();

		int numNodes = graph.getNumNodes();

		/*
		 * Build up simple priority queue (priority corresponds to
		 * currently shortest path weight).
		 */
		SimplePriorityQueue<N> pq = new SimplePriorityQueue<N>(numNodes);

		/*
		 * Helper map to check if elements are in queue.
		 */
		HashMap<N, Boolean> inQueue = new HashMap<N, Boolean>();

		/*
		 * Initialization, i. e., set all distances to oo (beside the source node).
		 */
		for (N node:graph.getAllNodes()) {
			distance.put(node, Double.POSITIVE_INFINITY);
			predecessor.put(node, null);
			if (node.equals(this.source)) {
				continue;
			}
			pq.add(node, distance.get(node));
			inQueue.put(node, true);
		}

		/*
		 * Initialize source node (this is the starting point of
		 * the algorithm).
		 */
		distance.put(this.source, 0.0);
		pq.add(this.source, 0);
		inQueue.put(this.source, true);


		while (pq.getSize() != 0) {
			/*
			 *  get node with minimal priority, i.e., minimal distance
			 *  (in the first iteration this is the source node).
			 */
			N node = pq.poll();
			inQueue.put(node, false);

			System.out.println("Distance of selected node: " + distance.get(node));
			if (distance.get(node) == Double.POSITIVE_INFINITY) {
				System.out.println("Graph not connected!");
				return null;
			}

			/*
			 * Iterate over all adjacent nodes and update the distances to this
			 * nodes, if the current path is shorter.
			 */
			for (E incEdge:graph.getIncidentEdges(node)) {
				N adjNode = graph.getIncidentNode(node, incEdge);
				if (inQueue.containsKey(adjNode)) {

					/*
					 * Compute alternative distance ...
					 */
					Double alt = distance.get(node) + annotator.
							getAnnotation(graph.getEdge(node, adjNode)).
							getFirstWeight();
					System.out.println("Alternative length: " + alt);

					/*
					 * ... and update if shorter.
					 */
					if (alt < distance.get(adjNode)) {
						System.out.println("Updating length: " + alt);
						distance.put(adjNode, alt);
						predecessor.put(adjNode, node);
						pq.update(adjNode, alt);
					}
				}
			}
		}

		/*
		 * Finally reconstruct the path from source to destination node.
		 */
		N current = this.dest;
		while (predecessor.get(current) != null) {
			N pred = predecessor.get(current);
			shortestPath.add(graph.getEdge(pred, current));
			current = pred;
		}

		return shortestPath;
	}

	public static void main(String[] args) {
		// build up simple undirected graph
		SimpleUndirectedGraph g = new SimpleUndirectedGraph();
		SimpleNode a = g.addNode();
		SimpleNode b = g.addNode();
		SimpleNode c = g.addNode();
		SimpleNode d = g.addNode();
		SimpleEdge ab = g.addEdge(a, b);
		SimpleEdge bc = g.addEdge(b, c);
		SimpleEdge cd = g.addEdge(c, d);

		// set weight annotations
		GraphElementHashAnnotator<SimpleEdge, Weight> raw_weights =
				new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<SimpleEdge> weights =
				new GraphElementWeightAnnotator<>(
				raw_weights);
		weights.setAnnotation(ab, new Weight(new double[]{3.0}));
		weights.setAnnotation(bc, new Weight(new double[]{5.0}));
		weights.setAnnotation(cd, new Weight(new double[]{2.0}));

		// apply Disjkstra's algorithm to the instance
		Dijkstra<SimpleNode, SimpleEdge, SimpleUndirectedGraph> alg = new Dijkstra<SimpleNode, SimpleEdge, SimpleUndirectedGraph>(a, d);
		ArrayList<SimpleEdge> shortestPath = (ArrayList<SimpleEdge>) alg.computeUniobjectiveOptimum(g, weights);
		for (UndirectedEdge e:shortestPath) {
			System.out.println(weights.getAnnotation((SimpleEdge)e));
		}
	}
}
