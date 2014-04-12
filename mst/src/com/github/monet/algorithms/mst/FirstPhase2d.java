package com.github.monet.algorithms.mst;

import java.util.LinkedList;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.interfaces.*;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;

public class FirstPhase2d<N extends Node, E extends Edge, G extends Graph<N, E, G>> implements FirstPhaseAlgorithm<N,E,G> {

	UniobjectiveAlgorithm<N, E, G, E> firstPhaseAlgorithm;

	public FirstPhase2d(UniobjectiveAlgorithm<N, E, G, E> firstPhaseAlgorithm) {
		this.firstPhaseAlgorithm = firstPhaseAlgorithm;
	}

	@Override
	public ParetoFront<N, E, G> firstPhase(AnnotatedGraph<N, E, G> graph,
			String weightAnnotationName) {

		G g = graph.getGraph();
		GraphElementAnnotator<E, Weight> boxed_weights = graph.getAnnotator(
				weightAnnotationName, GraphElementAnnotator.class);
		GraphElementWeightAnnotator weights = new GraphElementWeightAnnotator(
				boxed_weights);
		WeightedEdgesCalculator<N,E,G> wec = new WeightedEdgesCalculator<>(weights);
		ParetoFront<N, E, G> paretoFront = new ParetoFront<>(wec);

		/* Create an LinkedList for the first two extreme efficient solutions */
		LinkedList<G> extreme_supported = new LinkedList<>();

		/* Compute the first two extreme solutions with respect to only one dimension */
		for (int i = 0; i < 2; i++) {
			double[] coefficients = new double[]{
				i == 0 ? 1.0 : 0.0, i == 1 ? 1.0 : 0.0
			};
			GraphElementWeightAnnotator<E> scalarization = weights.
					scalarize(coefficients);
			Iterable<E> edges = firstPhaseAlgorithm.
					computeUniobjectiveOptimum(g, scalarization);
			extreme_supported.add(g.getSubgraph(g.getAllNodes(), edges));
		}

		/* Ideal point found, |parteoFront| = 1 */
		Weight extreme_value_1 = weights.sum(extreme_supported.get(0).
				getAllEdges());
		Weight extreme_value_2 = weights.sum(extreme_supported.get(1).
				getAllEdges());
		/* TODO Use EPS */
		if (extreme_value_1.getWeight(0) == extreme_value_2.getWeight(0)
				|| extreme_value_1.getWeight(1) == extreme_value_2.getWeight(1)) {
			paretoFront.add(extreme_supported.get(0));
			return paretoFront;
		}

		/* Recursive expansion */
		/*if (computeColinearSolutions) {
		 computeSupportedPointsColinear(graph, extreme_supported.get(0),
		 extreme_supported.get(1), weights, paretoFront,
		 firstPhaseAlgorithm);
		 } else*/ {
			computeSupportedPoints(g, extreme_supported.get(0),
					extreme_supported.get(1), weights, paretoFront,
					firstPhaseAlgorithm);
		}

		/* Insert extreme supported solutions into paretoFront if not dominated */
		if (paretoFront.isEmpty() || weights.sum(paretoFront.first().
				getAllEdges()).dominates(
				extreme_value_1)
				!= Weight.DominationRelation.PARETO_SMALLER) {
			paretoFront.add(extreme_supported.get(0));
		}
		if (paretoFront.isEmpty() || weights.sum(paretoFront.last().
				getAllEdges()).dominates(
				extreme_value_2)
				!= Weight.DominationRelation.PARETO_SMALLER) {
			paretoFront.add(extreme_supported.get(1));
		}

		return paretoFront;
	}

	private void computeSupportedPoints(
			G original, G leftNeighbor,
			G rightNeighbor,
			GraphElementWeightAnnotator<E> annotator,
			ParetoFront<N, E, G> paretoFront,
			UniobjectiveAlgorithm<N, E, G, E> firstPhaseAlgorithm) {

		/* Compute the costs of the two neighbors */
		Weight costsLeftNeighbor = annotator.sum(leftNeighbor.getAllEdges());
		Weight costsRightNeighbor = annotator.sum(rightNeighbor.getAllEdges());

		/* Compute the scalarization */
		double[] coefficients = new double[]{costsLeftNeighbor.getWeight(1)
			- costsRightNeighbor.getWeight(1), costsRightNeighbor.getWeight(0)
			- costsLeftNeighbor.getWeight(0)};
		GraphElementWeightAnnotator<E> scalarization = annotator.scalarize(
				coefficients);

		/* Compute new extreme efficient solution */
		Iterable<E> o_mst_edges = firstPhaseAlgorithm.
				computeUniobjectiveOptimum(original, scalarization);
		G o_mst = original.getSubgraph(original.getAllNodes(),
				o_mst_edges);

		/* End of recursion? */
		if (o_mst.equals(leftNeighbor) || o_mst.equals(rightNeighbor)) {
			return;
		} else {
			paretoFront.add(o_mst);
		}

		/* Recursive expansion */
		computeSupportedPoints(original, leftNeighbor, o_mst, annotator,
				paretoFront,
				firstPhaseAlgorithm);
		computeSupportedPoints(original, o_mst, rightNeighbor, annotator,
				paretoFront,
				firstPhaseAlgorithm);
	}
//    private void computeSupportedPointsColinear(
//            G original, G leftNeighbor,
//            G rightNeighbor,
//            GraphElementWeightAnnotator<E> annotator,
//            ParetoFront<N,E,G> paretoFront,
//            UniobjectiveAllAlgorithm<N,E,G,E> firstPhaseAlgorithm) {
//        List<double> g = computeCosts(G, A);
//        List<double> h = computeCosts(G, A);
//
//        // g_y-g_h, h_x - g_x
//        EdgeAnnotator<?> scal = scalarization();
//
//        List<Graph> l_mst = mst.allMst(o, scal);
//        l_mst.lexSort(A);
//
//        int l_mst_last = l_mst.size() - 1;
//        if ((l_mst.size() == 1) || (!l_mst.get(0).equals(g) && !l_mst.get(
//                l_mst_last).equals(h))) {
//            pf.insert(l_mst.get(0));
//            computeSupportedPointsColinear(l_mst.get(l_mst_last), h, A, pf);
//            computeSupportedPointsColinear(g, l_mst.get(0), A, pf);
//            return;
//        }
//
//        if (g_mst.equals(g) || g_mst.equals(h)) {
//            return;
//        } else {
//            pf.insert(g_mst);
//        }
//        computeSupportedPointsColinear(g, g_mst, A, pf, mst);
//        computeSupportedPointsColinear(g_mst, h, A, pf, mst);*/
}
