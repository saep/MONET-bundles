package com.github.monet.algorithms.mst;

import java.util.Iterator;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.interfaces.SecondPhaseAlgorithm;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.interfaces.UndirectedGraph;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;

/**
 * Implementation of Steiner's and Radzik's second phase for the BMST problem.
 * Uses Gabow's k-best algorithm. Steiner, Radzik: Solving the Biobjective
 * Minimum Spanning Tree problem using a k-best algorithm
 *
 *
 */
public class KBestSecondPhase<N extends Node, E extends UndirectedEdge, G extends UndirectedGraph<N, E, G>>
		implements SecondPhaseAlgorithm<N, E, G> {

	@Override
	public ParetoFront<N, E, G> secondPhase(
			AnnotatedGraph<N, E, G> annotatedGraph,
			String weightAnnotationName, ParetoSet<N, E, G> extremeEfficient) {

		int c = 0;
		// Unbox annotated graph
		G graph = annotatedGraph.getGraph();
		GraphElementAnnotator annotator = annotatedGraph.getAnnotator(
				weightAnnotationName, GraphElementAnnotator.class);
		GraphElementWeightAnnotator weightAnnotator
				= new GraphElementWeightAnnotator(annotator);

		// Prepare to iterate over pareto front neighbours
		Iterator<G> iterator = extremeEfficient.iterator();
		G first = iterator.next();
		Weight costsFirst = weightAnnotator.sum(first.getAllEdges());

		WeightedEdgesCalculator<N, E, G> weightedEdgeCalculator
				= new WeightedEdgesCalculator<>(weightAnnotator);
		ParetoFront<N, E, G> allSolutions = new ParetoFront<>(
				weightedEdgeCalculator);

		// Iterator over pareto front neighbours
		while (iterator.hasNext()) {
			G second = iterator.next();
			Weight costsSecond = weightAnnotator.sum(second.getAllEdges());
			// Scalarize
			double[] coefficients = new double[]{costsFirst.getWeight(1)
				- costsSecond.getWeight(1), costsSecond.getWeight(
				0) - costsFirst.getWeight(0)};
			GraphElementWeightAnnotator<E> scalarization = weightAnnotator.
					scalarize(coefficients);
			double maximumCost = coefficients[0] * costsSecond.getWeight(0)
					+ coefficients[1] * costsFirst.getWeight(1);
			// Iterate over k-best MSTs
			ParetoFront<N, E, G> currentSolutions = new ParetoFront<>(
					weightedEdgeCalculator);
			currentSolutions.add(first);
			currentSolutions.add(second);
			Gabow<N, E, G> kBest = new Gabow<>(graph, scalarization);
			while (true) {
				G kMst = kBest.generate();
				c++;
				if (kMst == null) {
					// No more solutions
					break;
				} else if (kMst.equals(first) || kMst.equals(second)) {
					continue;
				} else {
					Weight costskMst = weightAnnotator.sum(kMst.getAllEdges());
					double scalarizedCostskMst = costskMst.scalarize(
							coefficients).getFirstWeight();
					// Determine if solution is within viable region
					if (costskMst.getWeight(0) >= costsSecond.getWeight(0)
							|| costskMst.getWeight(1) >= costsFirst.getWeight(1)) {
						continue;
					}
					// Determine if costskMst is dominated by prevoiusly found solutions
					boolean isDominated = false;
					for (Weight key : currentSolutions.keySet()) {
						isDominated |= key.dominates(costskMst)
								== Weight.DominationRelation.PARETO_SMALLER;
					}
					if (scalarizedCostskMst >= maximumCost) {
						// Beyond maximum cost line
						break;
					} else if (!isDominated) {
						// New efficient solutions found
						currentSolutions.add(kMst);
						// Iterate over neighbours on current pareto front
						Iterator<G> neighbours = currentSolutions.iterator();
						G firstNeighbour = neighbours.next();
						Weight costsFirstNeighbour = weightAnnotator.sum(
								firstNeighbour.getAllEdges());
						maximumCost = 0;
						while (neighbours.hasNext()) {
							// Get next pair of neighbours
							G secondNeighbour = neighbours.next();
							Weight costsSecondNeighbour = weightAnnotator.sum(
									secondNeighbour.getAllEdges());
							// Compute local nadir point
							Weight localNadir = new Weight(new double[]{
								costsSecondNeighbour.getWeight(0),
								costsFirstNeighbour.getWeight(1)});
							// Compute scalarized costs of points of intersection
							double scalarizedCostsLocalNadir = localNadir.
									scalarize(coefficients).getFirstWeight();
							// Choose greater scalarized cost as new maximum cost
							if (maximumCost < scalarizedCostsLocalNadir) {
								maximumCost = scalarizedCostsLocalNadir;
							}

							firstNeighbour = secondNeighbour;
							costsFirstNeighbour = costsSecondNeighbour;
						}
					}
				}
			}

			// Add found solutions to output
			for (G solution : currentSolutions) {
				allSolutions.add(solution);
			}

			first = second;
			costsFirst = costsSecond;
		}

		System.out.println("Number of generated spanning trees: " + c);
		return allSolutions;
	}
}
