package com.github.monet.algorithms.mst;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import com.github.monet.algorithms.Kruskal;
import com.github.monet.datastructures.TreeUnionFind;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.interfaces.UndirectedGraph;
import com.github.monet.graph.interfaces.UnionFind;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;
import com.github.monet.graph.weighted.WeightedGraphElementComparator;

/**
 * Implementation of Gabow's k-best spanning tree algorithm.
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of nodex
 * @param <E>
 *            type of edge
 * @param <G>
 *            type of graph
 */
public class Gabow<N extends Node, E extends UndirectedEdge, G extends UndirectedGraph<N, E, G>> {
	private LinkedList<E> orderedEdgeList;
	private GraphElementWeightAnnotator<E> weights;
	private G graph;


	public TreeSet<PartitionSet> treePartition;

	public Gabow(G graph, GraphElementWeightAnnotator<E> weights) {
		this.weights = weights;
		this.graph = graph;


		orderedEdgeList = (LinkedList<E>) graph.getAllEdges();

		// Sort edges in decreasing order according to annotated weights
		Collections.sort(orderedEdgeList,
				new WeightedGraphElementComparator<E>(weights));

		// Manages partition of set of trees
		treePartition = new TreeSet<>(new PartitionComparator());

		//Calculate minimal spanning and the first exchange edge
		this.init();
	}


	/**
	 * Calculate the minimal spanning and the first exchange edge.
	 */
	public void init() {

		Kruskal<N,E,G> kruskal = new Kruskal<>();

		// Compute minimal spanning of this.graph
		Iterable<E> mstEdges = kruskal.computeUniobjectiveOptimum(graph, weights);
		G mst = graph.getSubgraphWithImpliedNodes(mstEdges);

		// Calculate weight of minimum spanning tree
		double mstWeight = 0.0;
		for (E e : mstEdges) {
			mstWeight += weights.getAnnotation(e).getFirstWeight();
		}

		// Make MST directed
		GraphElementHashAnnotator<N, N> father = this.directGraph(mst, mst.getAllNodes().iterator().next());
		EdgeExchange ex = this.exchange(mst, father, new LinkedList<E>(), new LinkedList<E>());

		this.addPartition(mstWeight+ex.r, ex.e, ex.f, mst, father,
				new LinkedList<E>(), new LinkedList<E>());
	}

	/**
	 *  Directs the edges of the tree via BFS.
	 *
	 * @param tree graph, without cycles
	 * @return father annotator
	 */
	private GraphElementHashAnnotator<N, N> directGraph(G tree, N root) {

		HashMap<N, Boolean> marked = new HashMap<N, Boolean>();
		GraphElementHashAnnotator<N, N> father = new GraphElementHashAnnotator<>();
		LinkedList<N> queue = new LinkedList<>();

		for (N n : tree.getAllNodes()) {
			marked.put(n, false);
		}
		marked.put(root, true);
		queue.add(root);
		father.setAnnotation(root, null);

		while (queue.size() != 0) {
			N newRoot = queue.get(0);
			queue.remove(0);

			Collection<E> incidentEdges = tree.getIncidentEdges(newRoot);

			for(E e : incidentEdges) {
				N n = tree.getIncidentNode(newRoot, e);

				if (!marked.get(n)) {
					queue.add(n);
					marked.put(n, true);
					father.setAnnotation(n, newRoot);
				}
			}
		}

		return father;
	}

	/**
	 * Calculates the minimum exchange edge of "tree", subject to the
	 * constraints "in" and "out"
	 *
	 * @param tree
	 *            undirected graph, without cycle
	 * @param tree
	 *            annotates edge directions to "tree"
	 * @param in
	 *            edges, which have to remain in the tree
	 * @param out
	 *            edges, which have to remain out of the tree
	 *
	 * @return minimum exchange edge, subject to "in" and "out"
	 */
	public EdgeExchange exchange(G tree, GraphElementAnnotator<N, N> father,
			LinkedList<E> in, LinkedList<E> out) {

		// Manages minimal exchange edge
		EdgeExchange exEdge = new EdgeExchange();
		// True <=> if no exchange edge has been found so far
		exEdge.first = true;

		TreeUnionFind<N> nodePartition = new TreeUnionFind<>();
		//nodePartition.setPathCompression(false);

		// Create partition of nodes, initially each node has it's own set
		for (N n: graph.getAllNodes()) {
			nodePartition.add(n);
		}

		Collection<E> treeEdgeList = tree.getAllEdges();
		for (E e : orderedEdgeList) {	// iterate over possible exchange candidates (f)
			if (!out.contains(e) && !treeEdgeList.contains(e)) { // edge is not in "out" and edge is not already in the tree

				Iterator<N> incidentNodes = graph.getIncidentNodes(e)
						.iterator();

				N x = incidentNodes.next();
				N y = incidentNodes.next();

				N a = getFirstEligibleCommonAncestor(father, nodePartition, x,
						y);

				Collection<N> xy = graph.getIncidentNodes(e);

				for (N v1 : xy) {
					N v = nodePartition.find(v1);

					// Iterate over candidates for exchange
					while (!v.equals(a) && (father.getAnnotation(v) != null)) {

						// Candidate for exchange with e = (x,y) (exists because father is not root node)
						E ev = tree.getEdge(father.getAnnotation(v), v);

						if (!in.contains(ev)) {		// Skip edge it's in list "in"

							// Calculate exchange weight
							double r1 = weights.getAnnotation(e)
									.getFirstWeight()
									- weights.getAnnotation(ev)
									.getFirstWeight();

							if (!exEdge.first) {
								if (r1 <= exEdge.r) {
									exEdge.e = ev;
									exEdge.f = e;
									exEdge.r = r1;
								}
							} else {	// first exchange pair (e,f) found, thus minimal
								exEdge.e = ev;
								exEdge.f = e;
								exEdge.r = r1;

								exEdge.first = false;
							}

							N u = nodePartition.find(father
									.getAnnotation(v));

							// Node v is now representative
							nodePartition.union(v, u);
							nodePartition.makeRepresentative(u);
							v = u;
						} else {
							v = nodePartition.find(father.getAnnotation(v));
						}
					}
				}
			}
		}

		nodePartition.clear();

		return exEdge;
	}



/**
 *	Calculates the smallest spanning tree in the sets of the partition
 * "treePartition"
 */
	public G generate() {
		PartitionSet first = treePartition.pollFirst();

		if (first != null) {

			// Create new k-best tree, delete edge e
			Collection<E> tmp = first.fatherTree.getAllEdges();
			G newTree = this.graph.getSubgraphWithImpliedNodes(tmp);
			newTree.deleteEdge(first.e);

			// Create new father annotator
			GraphElementHashAnnotator<N, N> father = new GraphElementHashAnnotator<>();

			for (N n : first.fatherTree.getAllNodes()) {
				father.setAnnotation(n, first.father.getAnnotation(n));
			}

			// Delete father annotation of node of edge e
			Iterator<N> nodes = graph.getIncidentNodes(first.e).iterator();
			N e1 = nodes.next();
			N e2 = nodes.next();
			N t = null;

			N e1Father = first.father.getAnnotation(e1);
			if ((e1Father != null) && (e1Father.equals(e2))) {
				father.setAnnotation(e1, null);
				t = e1;
			} else if (first.father.getAnnotation(e2).equals(e1)) {
				father.setAnnotation(e2, null);
				t = e2;
			}

			nodes = graph.getIncidentNodes(first.f).iterator();

			N s1 = nodes.next();
			N s2 = nodes.next();
			N s1Tmp = s1;
			N s2Tmp = s2;

			N s1Father;
			N s2Father;

			while (!s1Tmp.equals(t) && !s2Tmp.equals(t)) {
				s1Father = father.getAnnotation(s1Tmp);
				s2Father = father.getAnnotation(s2Tmp);

				if (s1Father != null) {
					s1Tmp = s1Father;
				}

				if (s2Father != null) {
					s2Tmp = s2Father;
				}
			}

			// Create new father annotator
			GraphElementHashAnnotator<N, N> oldFather = new GraphElementHashAnnotator<>();

			for (N n : first.fatherTree.getAllNodes()) {
				oldFather.setAnnotation(n, father.getAnnotation(n));
			}

			if (s1Tmp.equals(t)) {
				s1Tmp = s1;

				// revert edges between t and s1
				while (!s1Tmp.equals(t)) {
					s1Father = oldFather.getAnnotation(s1Tmp);

					father.setAnnotation(s1Father, s1Tmp);
					//father.setAnnotation(s1Tmp, null);
					s1Tmp = s1Father;
				}

				nodes = graph.getIncidentNodes(first.f).iterator();
				newTree.addEdge(nodes.next(), nodes.next());
				father.setAnnotation(s1, s2);
			} else if (s2Tmp.equals(t)) {
				s2Tmp = s2;

				// revert edges between t and s1
				while (!s2Tmp.equals(t)) {
					s2Father = oldFather.getAnnotation(s2Tmp);

					father.setAnnotation(s2Father, s2Tmp);
					//father.setAnnotation(s2Tmp, null);
					s2Tmp = s2Father;
				}

				nodes = graph.getIncidentNodes(first.f).iterator();
				newTree.addEdge(nodes.next(), nodes.next());
				father.setAnnotation(s2, s1);
			}

			// Calculate weight of to be returned tree
			double oldWeight = first.smallestWeight
					- weights.getAnnotation(first.f).getFirstWeight()
					+ weights.getAnnotation(first.e).getFirstWeight();

			// Branch partition into two partitions
			LinkedList<E> inI = new LinkedList<>();
			for (E e : first.in) {
				inI.add(e);
			}
			inI.add(first.e);

			LinkedList<E> outJ = new LinkedList<>();
			for (E e : first.out) {
				outJ.add(e);
			}
			outJ.add(first.e);

			EdgeExchange smallestExchangeI = this.exchange(first.fatherTree,
					first.father, inI, first.out);

			EdgeExchange smallestExchangeJ = this.exchange(newTree, father,
					first.in, outJ);

			if (smallestExchangeI.e != null) {
					treePartition.add(new PartitionSet(oldWeight
							+ smallestExchangeI.r, smallestExchangeI.e,
							smallestExchangeI.f, first.fatherTree, first.father,
							inI, first.out));

			}
			if (smallestExchangeJ.e != null) {
				treePartition.add(new PartitionSet(first.smallestWeight
						+ smallestExchangeJ.r, smallestExchangeJ.e,
						smallestExchangeJ.f, newTree, father, first.in, outJ));
			}

			//father.clear();
			//father = null;
			oldFather.clear();
			oldFather = null;

			first = null;

			return newTree;
		} else {

			treePartition.clear();

			return null;
		}
	}



/**
 * Computes the first eligible ancestor of two nodes
 *
 * @param father annotates directions to edges
 * @param nodePartition
 * @param x node in the tree
 * @param y node in the tree
 * @return  first eligible ancestor of nodes x and y
 */
	public N getFirstEligibleCommonAncestor(GraphElementAnnotator<N, N> father,
			UnionFind<N> nodePartition, N x, N y) {

		LinkedList<N> xs = new LinkedList<>();
		LinkedList<N> ys = new LinkedList<>();

		xs.add(x);
		ys.add(y);

		N x_current = x;
		N y_current = y;

		N x_father;
		N y_father;

		while (true) {

			x_father = father.getAnnotation(x_current);
			y_father = father.getAnnotation(y_current);


			if (x_father != null) {	// x_current is not root node
				x_current = x_father;
				xs.add(x_current);
			}

			if (y_father != null) { // y_current is not root node
				y_current = y_father;
				ys.add(y_current);
			}

			if (xs.contains(y_current)) {	// First common node found
				return nodePartition.find(y_current);	// Return first eligible predecessor of first common node
			}

			if (ys.contains(x_current)) {	// First common node found
				return nodePartition.find(x_current);	// Return first eligible predecessor of first common node
			}
		}
	}


/**
 * Resets data structures, call before re-using object
 */
	public void reset() {
		this.treePartition.clear();

		this.init();
	}

/**
 * Simple field for managing exchange edges.
 *
 * @author Christopher Morris
 */
public class EdgeExchange {
	public boolean first;
	public E e;
	public E f;

	public double r;
}

/**
 * Models an element of a partition of the set of trees.
 *
 * @author Christopher Morris
 */
public class PartitionSet {

	private double smallestWeight;
	public E e;
	public E f;
	public G fatherTree;
	public GraphElementHashAnnotator<N, N> father;

	public LinkedList<E> in;
	public LinkedList<E> out;

	public PartitionSet(double smallestWeight, E e, E f, G fatherTree,
			GraphElementHashAnnotator<N, N> father, LinkedList<E> in, LinkedList<E> out) {
		this.smallestWeight = smallestWeight;
		this.e = e;
		this.f = f;
		this.fatherTree = fatherTree;
		this.father = father;

		this.in = in;
		this.out = out;
	}
}

public void addPartition(double smallestWeight, E e, E f, G fatherTree,
		GraphElementHashAnnotator<N, N> father, LinkedList<E> in, LinkedList<E> out) {
	PartitionSet set = new PartitionSet(smallestWeight, e, f, fatherTree,
			father, in, out);

	treePartition.add(set);
}

/**
 * Compares two elements of a partition of the set of trees.
 *
 * @author Christopher Morris
 *
 */
private class PartitionComparator implements Comparator<PartitionSet> {

	@Override
	public int compare(PartitionSet p1, PartitionSet p2) {

		if (p1.smallestWeight < p2.smallestWeight) {
			return -1;
		} else if (p1.smallestWeight > p2.smallestWeight) {
			return 1;
		} else {
			// FIX: Force TreeSet to manage duplicate elements
			return -1;
		}
	}

}
}
