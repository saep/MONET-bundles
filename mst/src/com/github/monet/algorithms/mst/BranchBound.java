package com.github.monet.algorithms.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import com.github.monet.algorithms.MSTBannedForcedEdges;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.GraphElementReverseAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.interfaces.SecondPhaseAlgorithm;
import com.github.monet.graph.tests.VariousGraphTests;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;

/**
 * Implementation of a Branch-and-Bound algorithm for BMST.
 *
 * Sourd, Spanjaard: Multi-objective branch-and-bound. Application to the
 * bi-objective spanning tree problem
 *
 * @author Christopher Morris
 */
public class BranchBound<N extends Node, E extends Edge, G extends Graph<N, E, G>>
		implements SecondPhaseAlgorithm<N, E, G> {

	private VariousGraphTests<N, E, G> graphTests;
	private MSTBannedForcedEdges<N, E, G> mst;
	private boolean doPreprocessing;

	/**
	 * @return the doPreprocessing
	 */
	public boolean isDoPreprocessing() {
		return doPreprocessing;
	}

	/**
	 * @param doPreprocessing the doPreprocessing to set
	 */
	public void setDoPreprocessing(boolean doPreprocessing) {
		this.doPreprocessing = doPreprocessing;
	}

	public enum BranchBoundColoring {

		MANDATORY, FORBIDDEN, AVAILABLE
	}

	private enum Mark {

		VISITED, QUEUED, UNKNOWN
	}

	public BranchBound() {
		graphTests = new VariousGraphTests<>();
		mst = new MSTBannedForcedEdges<>();
		doPreprocessing = true;
	}

	@Override
	public ParetoFront<N, E, G> secondPhase(AnnotatedGraph<N, E, G> graph,
			String weightAnnotationName, ParetoSet<N, E, G> paretoSet) {

		G g = graph.getGraph();

		GraphElementAnnotator<E, Weight> annotator = graph.getAnnotator(
				weightAnnotationName, GraphElementAnnotator.class);
		GraphElementWeightAnnotator<E> weightAnnotator
				= new GraphElementWeightAnnotator<>(annotator);
		WeightedEdgesCalculator<N, E, G> calc = new WeightedEdgesCalculator<>(
				weightAnnotator);
		GraphElementReverseHashAnnotator<E, BranchBound.BranchBoundColoring> coloring
				= new GraphElementReverseHashAnnotator<>();

		for (E e : g.getAllEdges()) {
			coloring.setAnnotation(e, BranchBound.BranchBoundColoring.AVAILABLE);
		}

		while (doPreprocessing) {
			E banEdge = findEdgeToBan(g, weightAnnotator, coloring);
			if (banEdge != null) {
				coloring.setAnnotation(banEdge,
						BranchBoundColoring.FORBIDDEN);
			} else {
				break;
			}
		}

		ParetoFront<N, E, G> paretoFront = new ParetoFront<>(calc, true);
		for (G solution : paretoSet) {
			paretoFront.add(solution);
		}

		branchBound(g, weightAnnotator, calc, coloring, paretoFront, paretoFront);
		for (G solution : paretoSet) {
			paretoFront.remove(solution);
		}

		return paretoFront;
	}

	public void branchBound(G graph,
			GraphElementWeightAnnotator<E> weights,
			WeightedEdgesCalculator<N, E, G> weightCalculator,
			GraphElementReverseAnnotator<E, BranchBoundColoring> coloring,
			ParetoFront<N, E, G> lowerBound, ParetoFront<N, E, G> upperBound) {

		// All edges colored or only one graph with n-1 edges possible
		//   -> try to construct spanning tree
		if (coloring.getElements(BranchBoundColoring.AVAILABLE).isEmpty()
				|| coloring.getElements(BranchBoundColoring.AVAILABLE).size()
				+ coloring.getElements(BranchBoundColoring.MANDATORY).size()
				== graph.getNumNodes() - 1) {
			Collection<E> availableEdges = coloring.getElements(
					BranchBoundColoring.AVAILABLE);
			availableEdges.addAll(coloring.
					getElements(BranchBoundColoring.MANDATORY));
			G spanningTree = graph.getSubgraph(graph.getAllNodes(),
					availableEdges);

			if (graphTests.isSpanningTree(spanningTree)) {
				upperBound.add(spanningTree);
			}
			return;
		}

		// Determine next branching edge using heuristic (choose edge with minimum single criterion weight)
		E minCostEdge = coloring.getElements(BranchBoundColoring.AVAILABLE).
				iterator().next();
		double minCost = Math.min(weights.getAnnotation(minCostEdge).
				getWeight(0), weights.getAnnotation(minCostEdge).
				getWeight(1));

		for (E e : coloring.getElements(BranchBoundColoring.AVAILABLE)) {
			double tmpMinCost = Math.min(weights.getAnnotation(minCostEdge).
					getWeight(0), weights.getAnnotation(minCostEdge).
					getWeight(1));
			if (tmpMinCost <= minCost) {
				minCostEdge = e;
				minCost = tmpMinCost;
			}
		}

		//// Branch left ////
		coloring.setAnnotation(minCostEdge, BranchBoundColoring.MANDATORY);

		// over AVAILABLE + MANDATORY
		Collection<E> availableEdges = coloring.getElements(
				BranchBoundColoring.AVAILABLE);
		availableEdges.addAll(coloring.
				getElements(BranchBoundColoring.MANDATORY));
		G availableGraph = graph.
				getSubgraph(graph.getAllNodes(), availableEdges);

		// Check, if it's possible to construct spanning tree
		if (graphTests.isConnected(availableGraph)) {
			ParetoFront<N, E, G> leftLowerBound = computeLowerBound(graph,
					weights,
					weightCalculator, coloring, lowerBound, minCostEdge,
					BranchBoundColoring.MANDATORY);

			if (!bound(graph, weights, coloring, leftLowerBound, upperBound)) {
				branchBound(graph, weights, weightCalculator, coloring,
						leftLowerBound, upperBound);
			}
		}

		//// Branch right ////
		coloring.setAnnotation(minCostEdge, BranchBoundColoring.FORBIDDEN);

		// over AVAILABLE + MANDATORY
		availableEdges = coloring.getElements(BranchBoundColoring.AVAILABLE);
		availableEdges.addAll(coloring.
				getElements(BranchBoundColoring.MANDATORY));
		availableGraph = graph.getSubgraph(graph.getAllNodes(), availableEdges);

		// Check if it's possible to construct spanning tree
		if (graphTests.isConnected(availableGraph)) {
			ParetoFront<N, E, G> rightLowerBound = computeLowerBound(graph,
					weights,
					weightCalculator, coloring, lowerBound, minCostEdge,
					BranchBoundColoring.FORBIDDEN);

			if (!bound(graph, weights, coloring, rightLowerBound, upperBound)) {
				branchBound(graph, weights, weightCalculator, coloring,
						rightLowerBound, upperBound);
			}
		}

		// Undo coloring
		coloring.setAnnotation(minCostEdge, BranchBoundColoring.AVAILABLE);
	}

	private E findEdgeToBan(G graph, GraphElementWeightAnnotator<E> weights,
			GraphElementReverseAnnotator<E, BranchBoundColoring> coloring) {

		for (E candidate : coloring.getElements(BranchBoundColoring.AVAILABLE)) {
			// Data about candidate
			N candidateFirstNode = graph.getIncidentNodes(candidate).iterator().
					next();
			N candidateSecondNode = graph.getIncidentNode(candidateFirstNode,
					candidate);
			Weight candidateWeight = weights.getAnnotation(candidate);

			// Create checklist to mark visited nodes
			GraphElementReverseHashAnnotator<Node, Mark> checklist
					= new GraphElementReverseHashAnnotator<>();
			for (N u : graph.getAllNodes()) {
				checklist.setAnnotation(u, Mark.UNKNOWN);
			}

			// Create visit queue
			LinkedList<N> queue = new LinkedList<>();
			queue.offer(candidateFirstNode);

			// Visit nodes (BFS)
			while (!queue.isEmpty()) {
				N currentNode = queue.poll();
				checklist.setAnnotation(currentNode, Mark.VISITED);
				for (N neighbour : graph.getAdjacentNodes(currentNode)) {
					E neighbourEdge = graph.getEdge(currentNode, neighbour);
					Weight neighbourEdgeWeight = weights.getAnnotation(
							neighbourEdge);

					// Visit pareto-smaller non-red edges or blue edges only
					if ((coloring.getAnnotation(neighbourEdge)
							!= BranchBoundColoring.FORBIDDEN
							&& neighbourEdgeWeight.dominates(candidateWeight)
							== Weight.DominationRelation.PARETO_SMALLER
							&& !neighbourEdge.equals(candidate)) || coloring.
							getAnnotation(neighbourEdge)
							== BranchBoundColoring.MANDATORY) {

						if (checklist.getAnnotation(neighbour) == Mark.UNKNOWN) {
							queue.add(neighbour);
							checklist.setAnnotation(neighbour, Mark.QUEUED);
							if (neighbour.equals(candidateSecondNode)) {
								return candidate;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private ParetoFront<N, E, G> computeLowerBound(G graph,
			GraphElementWeightAnnotator<E> weights,
			WeightedEdgesCalculator<N, E, G> weightCalculator,
			GraphElementReverseAnnotator<E, BranchBoundColoring> coloring,
			ParetoFront<N, E, G> fatherLowerBound, E newEdge,
			BranchBoundColoring newColor) {

		// Create new pareto front to manage new lowerBound
		ParetoFront<N, E, G> newLowerBound = new ParetoFront<>(
				weightCalculator, true);

		// Compute extreme solution of lower bound
		ArrayList<G> extreme_supported = new ArrayList<>(2);

		// Compute the first two extreme solutions with respect to only one dimension
		// AND edge contraints
		for (int i = 0; i < 2; i++) {
			double[] coefficients = new double[]{
				i == 0 ? 1.0 : 0.0, i == 1 ? 1.0 : 0.0
			};
			GraphElementWeightAnnotator<E> scalarization = weights.
					scalarize(coefficients);

			Collection<E> forcedEdges = coloring.getElements(
					BranchBoundColoring.MANDATORY);
			Collection<E> bannedEdges = coloring.getElements(
					BranchBoundColoring.FORBIDDEN);
			Iterable<E> edges = mst.computeMSTwithForcedEdges(graph,
					scalarization, forcedEdges, bannedEdges);

			extreme_supported.add(graph.getSubgraph(graph.getAllNodes(), edges));
		}

		boolean lastWasUnfeasible = false; // Last solution was unfeasible
		G lastFeasibleBefore = extreme_supported.get(0); // Last feasible solution before unfeasible block
		newLowerBound.add(extreme_supported.get(0));
		newLowerBound.add(extreme_supported.get(1));

		int index = 0;
		for (G fatherGraph : fatherLowerBound) {
			boolean contains = fatherGraph.getAllEdges().contains(newEdge);
			// Check if fatherGraph is feasible
			if (contains && newColor == BranchBoundColoring.FORBIDDEN
					|| !contains && newColor == BranchBoundColoring.MANDATORY) {
				// Unfeasible solution
				lastWasUnfeasible = true;
				// Last element is unfeasible
				if (index == fatherLowerBound.size() - 1) {
					Collection<E> forcedEdges = coloring.getElements(
							BranchBoundColoring.MANDATORY);
					Collection<E> bannedEdges = coloring.getElements(
							BranchBoundColoring.FORBIDDEN);
					computeSupportedPoints(graph, lastFeasibleBefore,
							extreme_supported.get(1), weights, newLowerBound,
							forcedEdges, bannedEdges);
				}
			} else {
				// Feasible solution
				if (lastWasUnfeasible) {
					Collection<E> forcedEdges = coloring.getElements(
							BranchBoundColoring.MANDATORY);
					Collection<E> bannedEdges = coloring.getElements(
							BranchBoundColoring.FORBIDDEN);
					computeSupportedPoints(graph, lastFeasibleBefore,
							fatherGraph, weights, newLowerBound, forcedEdges,
							bannedEdges);
					lastWasUnfeasible = false;
				}
				newLowerBound.add(fatherGraph);
				lastFeasibleBefore = fatherGraph;
			}
			index++;
		}

		return newLowerBound;
	}

	private void computeSupportedPoints(
			G original, G leftNeighbor,
			G rightNeighbor,
			GraphElementWeightAnnotator<E> annotator,
			ParetoSet<N, E, G> paretoFront, Collection<E> forcedEdges,
			Collection<E> bannedEdges) {

		/* Compute the costs of the two neighbors */
		Weight costsLeftNeighbor = annotator.sum(leftNeighbor.getAllEdges());
		Weight costsRightNeighbor = annotator.sum(rightNeighbor.getAllEdges());

		/* Strange things happen if the "neighbors" are identical */
		if (costsLeftNeighbor.dominates(costsRightNeighbor)
				== Weight.DominationRelation.EQUAL) {
			return;
		}

		/* Compute the scalarization */
		double[] coefficients = new double[]{costsLeftNeighbor.getWeight(1)
			- costsRightNeighbor.getWeight(1), costsRightNeighbor.getWeight(0)
			- costsLeftNeighbor.getWeight(0)};
		GraphElementWeightAnnotator<E> scalarization = annotator.scalarize(
				coefficients);

		/* Compute new extreme efficient solution */
		Iterable<E> o_mst_edges = mst.computeMSTwithForcedEdges(original,
				scalarization, forcedEdges, bannedEdges);
		G o_mst = original.getSubgraph(original.getAllNodes(),
				o_mst_edges);
		Weight costsOMst = annotator.sum(o_mst.getAllEdges());

		/* End of recursion? */
		if (costsOMst.dominates(costsLeftNeighbor)
				== Weight.DominationRelation.EQUAL || costsOMst.dominates(
						costsRightNeighbor) == Weight.DominationRelation.EQUAL) {
			return;
		} else {
                        if(!paretoFront.add(o_mst))
                            return;
		}

		/* Recursive expansion */
		computeSupportedPoints(original, leftNeighbor, o_mst, annotator,
				paretoFront, forcedEdges, bannedEdges);
		computeSupportedPoints(original, o_mst, rightNeighbor, annotator,
				paretoFront, forcedEdges, bannedEdges);
	}

	private boolean bound(G graph,
			GraphElementWeightAnnotator<E> weights,
			GraphElementReverseAnnotator<E, BranchBoundColoring> coloring,
			ParetoFront<N, E, G> lowerBound, ParetoFront<N, E, G> upperBound) {

		// Compute set N
		ArrayList<Weight> cornerPoints = new ArrayList<>();
		for (Weight leftWeight : upperBound.keySet()) {
			Weight rightWeight = upperBound.higherKey(leftWeight);
			if (rightWeight == null) {
				break;
			}
			cornerPoints.add(new Weight(new double[]{rightWeight.getWeight(0),
				leftWeight.getWeight(1)}));
		}

		// Check all corner points
		boolean discard = true;
		for (Weight corner : cornerPoints) {
			double x = corner.getWeight(0);
			double y = corner.getWeight(1);
			boolean discardCornerPoint = false;
			for (Weight leftWeight : lowerBound.keySet()) {
				Weight rightWeight = lowerBound.higherKey(leftWeight);
				if (rightWeight == null) {
					break;
				}
				double rx = rightWeight.getWeight(0);
				double ry = rightWeight.getWeight(1);
				double lx = leftWeight.getWeight(0);
				double ly = leftWeight.getWeight(1);
				double slope = (ry - ly) / (rx - lx);
				double position = slope * x + (ly - slope * lx);
				discardCornerPoint |= position - y > 0;
			}
			discard &= discardCornerPoint;
			if (!discard) {
				break;
			}
		}

		return discard;
	}
}
