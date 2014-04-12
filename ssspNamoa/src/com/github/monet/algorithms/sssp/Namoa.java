package com.github.monet.algorithms.sssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import com.github.monet.graph.*;
import com.github.monet.graph.interfaces.*;
import com.github.monet.graph.weighted.*;
import com.github.monet.graph.weighted.Weight.DominationRelation;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class Namoa<G extends DirectedGraph<N, E, G>, N extends Node, E extends DirectedEdge>
		implements com.github.monet.interfaces.Algorithm {

	private G graph;
	private GraphElementWeightAnnotator<E> weights;
	private GraphElementHashAnnotator<N, LabelSet> openLabels, closedLabels, h;
	private LinkedList<OLE<N>> open;
	private N source, destination;
	private Meter meter;
	private ArrayList<LinkedList<E>> solutions;
	private int dimension;
	private Job job;
	private Logger logger;
	private int pathsExtended;

	// private ServiceDirectory serviceDir;

	@SuppressWarnings("unchecked")
	public void execute(Object input, Map<String, Object> parameters,
			Meter meter, ServiceDirectory serviceDir, Job job) {
		this.graph = (G) ((AnnotatedGraph<N, E, G>) input).getGraph();
		this.weights = new GraphElementWeightAnnotator<E>(((AnnotatedGraph<N, E, G>) input).getAnnotator("edges",
				GraphElementHashAnnotator.class));
		openLabels = new GraphElementHashAnnotator<N, LabelSet>();
		closedLabels = new GraphElementHashAnnotator<N, LabelSet>();
		solutions = new ArrayList<LinkedList<E>>();
		GraphElementReverseHashAnnotator<N, String> sdAnnotator = ((AnnotatedGraph<N, E, G>) input)
				.getAnnotator("sdAnnotator",
						GraphElementReverseHashAnnotator.class);
		this.source = sdAnnotator.getElements("startNode").iterator().next();
		this.destination = sdAnnotator.getElements("endNode").iterator().next();
		dimension = weights.getAnnotation(
				graph.getOutgoingEdges(source).iterator().next())
				.getDimension();
		h = ((AnnotatedGraph<N, E, G>) input).getAnnotator("HEURISTIC",
				GraphElementHashAnnotator.class);
		if (parameters != null
				&& (GraphElementHashAnnotator<N, LabelSet>) parameters
						.get("HEURISTIC") != null)
			h = (GraphElementHashAnnotator<N, LabelSet>) parameters
					.get("HEURISTIC");
		// if no heuristic is passed, set the outgoing edges' weights as
		// heuristic
		if (h == null) {
			h = new GraphElementHashAnnotator<N, LabelSet>();
			for (N n : graph.getAllNodes()) {
				LabelSet ls = new LabelSet();
				for (E e : graph.getOutgoingEdges(n)) {
					ls.insertLabel(weights.getAnnotation(e));
				}
				h.setAnnotation(n, ls);
			}

			double[] nullVector = new double[dimension];
			for (int i = 0; i < nullVector.length; i++)
				nullVector[i] = 0.0;
			Weight nullWeight = new Weight(nullVector);
			h.getAnnotation(destination).insertLabel(nullWeight);

		}
		open = new LinkedList<OLE<N>>();
		// this.source = (N) parameters.get("SOURCE");
		// this.destination = (N) parameters.get("DESTINATION");
		this.job = job;
		if (job != null)
			this.logger = job.getLogger();
		this.meter = meter;
		// this.serviceDir = serviceDir;

		run();
	}

	void run() {
		meter.startExperiment();
		if (logger != null)
			logger.log(Level.DEBUG, "Algorithm started");
		meter.startTimer("ALGORITHM");
		// Initialize.
		for (N n : graph.getAllNodes()) {
			openLabels.setAnnotation(n, new LabelSet());
			closedLabels.setAnnotation(n, new LabelSet());
		}
		double[] nullVector = new double[dimension];
		for (int i = 0; i < nullVector.length; i++)
			nullVector[i] = 0.0;
		Weight nullWeight = new Weight(nullVector);
		open.add(new OLE<N>(source, nullWeight, h.getAnnotation(source)));
		openLabels.getAnnotation(source).getLabels().add(nullWeight);

		// Extend open paths.
		while (!open.isEmpty()) {

			OLE<N> act = open.removeFirst();
			System.out.println(act.getN().toString());
			Weight actLabel = act.getG();

			// Close path.
			openLabels.getAnnotation(act.getN()).getLabels().remove(actLabel);
			closedLabels.getAnnotation(act.getN()).getLabels().add(actLabel);
			// If the active node is the destination
			if (act.getN().equals(destination)) {
				// Remove all paths from open, that are dominated by actLabel
				for (int i = 0; i < open.size(); i++) {
					LabelSet o = open.get(i).getF();
					for (int j = 0; j < o.getLabels().size(); j++) {
						if (actLabel.dominates(o.getLabels().get(j)) == Weight.DominationRelation.PARETO_SMALLER) {
							o.getLabels().remove(j);
							j--;
						}
					}
					if (o.getLabels().isEmpty()) {
						open.remove(i);
						i--;
					}
				}

			} else {
				// Expand path.
				for (E e : graph.getOutgoingEdges(act.getN())) {
					// Add test if e creates a circle
					N direction = graph.getTarget(e);
					Weight g = Weight.add(actLabel, weights.getAnnotation(e));

					// If direction has not been visited before
					if (closedLabels.getAnnotation(direction).getLabels()
							.isEmpty()
							&& openLabels.getAnnotation(direction).getLabels()
									.isEmpty()) {
						LabelSet f = LabelSet
								.add(h.getAnnotation(direction), g);
						// Check if a label in f is dominated by a path to
						// destination
						for (int i = 0; i < f.getLabels().size(); i++) {
							for (int j = 0; j < closedLabels
									.getAnnotation(destination).getLabels()
									.size(); j++) {
								if (closedLabels.getAnnotation(destination)
										.getLabels().get(j)
										.dominates(f.getLabels().get(i)) == Weight.DominationRelation.PARETO_SMALLER) {
									f.getLabels().remove(i);
									i--;
									break;
								}

							}
						}
						// If there are undominated function values in f, add
						// the new path to open
						if (!f.getLabels().isEmpty()) {
							pathsExtended++;
							open.add(new OLE<N>(direction, g, f));
							openLabels.getAnnotation(direction).getLabels()
									.add(g);
						}

					} else {
						// Check if g is dominated by any label in open/closed
						// of direction and remove labels dominated by g
						boolean dominated = false;
						for (int i = 0; i < closedLabels
								.getAnnotation(direction).getLabels().size(); i++) {
							Weight w = closedLabels.getAnnotation(direction)
									.getLabels().get(i);
							DominationRelation d = w.dominates(g);
							if (d == Weight.DominationRelation.PARETO_SMALLER) {
								dominated = true;
							} else if (d == Weight.DominationRelation.PARETO_GREATER) {
								closedLabels.getAnnotation(direction)
										.getLabels().remove(i);
							}
						}

						for (int i = 0; i < openLabels.getAnnotation(direction)
								.getLabels().size(); i++) {
							Weight w = openLabels.getAnnotation(direction)
									.getLabels().get(i);
							DominationRelation d = w.dominates(g);
							if (d == Weight.DominationRelation.PARETO_SMALLER) {
								dominated = true;
							} else if (d == Weight.DominationRelation.PARETO_GREATER) {
								openLabels.getAnnotation(direction).getLabels()
										.remove(i);
								// Remove OLE corresponding to w from open
								for (int j = 0; j < open.size(); j++) {
									if (open.get(j).getG().equals(w))
										open.remove(j);
								}
							}
						}

						// If g is undominated
						if (!dominated) {
							LabelSet f = LabelSet.add(
									h.getAnnotation(direction), g);

							// Check if a label in f is dominated by a path to
							// destination

							for (int i = 0; i < f.getLabels().size(); i++) {
								for (int j = 0; j < closedLabels
										.getAnnotation(destination).getLabels()
										.size(); j++) {
									if (closedLabels.getAnnotation(destination)
											.getLabels().get(j)
											.dominates(f.getLabels().get(i)) == Weight.DominationRelation.PARETO_SMALLER) {
										f.getLabels().remove(i);
										i--;
										break;
									}

								}
							}

							// If there are undominated function values in f,
							// add
							// the new path to open
							if (!f.getLabels().isEmpty()) {
								pathsExtended++;
								open.add(new OLE<N>(direction, g, f));
								openLabels.getAnnotation(direction).getLabels()
										.add(g);
							}
						}
					}
				}
			}
		}

		meter.stopTimer("ALGORITHM");
		if (logger != null) {
			logger.log(Level.DEBUG, "Algorithm finished. "
					+ closedLabels.getAnnotation(destination).getLabels()
							.size() + " solutions found.");
			logger.log(Level.DEBUG, "Backtracking started");
		}
		meter.startTimer("BACKTRACKING");
		meter.measureInt("PARETO_SIZE", closedLabels.getAnnotation(destination).getLabels().size());
		// backtrack each label of the source node separately

		for (Weight w : closedLabels.getAnnotation(destination).getLabels()) {
			ArrayList<LinkedList<E>> tempsol = recBacktrackLabel(destination,
					w, new LinkedList<E>());
			solutions.addAll(tempsol);
			for (LinkedList<E> sol : tempsol) {
				ArrayList<String> edgestring = new ArrayList<String>();
				for (E e : sol) {
					edgestring.add(e.toString());
				}
				meter.measurePareto(w.getWeights(), edgestring);
			}

		}

		meter.stopTimer("BACKTRACKING");

		WeightedEdgesCalculator<N, E, G> calc = new WeightedEdgesCalculator<N, E, G>(
				weights);
		ParetoFront<N, E, G> front = new ParetoFront<N, E, G>(calc);
		for (LinkedList<E> solution : solutions) {
			front.add(graph.getSubgraphWithImpliedNodes(solution));
			if (logger != null)
				logger.log(Level.DEBUG, solution.toString());
		}

		meter.measureInt("PATHS_EXTENDED", pathsExtended);
		// meter.endExperiment();
	}

	/**
	 * Recursive procedure to find the path that corresponds to a given label.
	 *
	 * @param n
	 *            The node at which the path will end
	 * @param w
	 *            The label whose corresponding path is to be found.
	 * @param subPath
	 *            A path from the destination node to node n
	 * @return
	 */
	ArrayList<LinkedList<E>> recBacktrackLabel(N n, Weight w,
			LinkedList<E> subPath) {
		ArrayList<LinkedList<E>> retval = new ArrayList<LinkedList<E>>();

		if (n.equals(source))
			retval.add(subPath);
		else {
			for (E e : this.graph.getIncomingEdges(n)) {
				Weight tempWeight = Weight
						.add(w,
								Weight.scalarProduct(-1,
										this.weights.getAnnotation(e)));
				if (closedLabels.getAnnotation(this.graph.getSource(e))
						.getLabels().contains(tempWeight)) {

					@SuppressWarnings("unchecked")
					LinkedList<E> extSubPath = (LinkedList<E>) subPath.clone();
					extSubPath.addFirst(e);
					retval.addAll(recBacktrackLabel(this.graph.getSource(e),
							tempWeight, extSubPath));
				}
			}
		}
		return retval;
	}

	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir) {
		assert (job != null);
		// Get AnnotatedGraph from Job
		Object inputGraph = job.getInputGraph();
		assert (inputGraph != null);
		// Get Parameters
		Map<String, Object> params = job.getParameters();
		assert (params != null);
		// Start execution
		this.execute(inputGraph, params, meter, serviceDir, job);
	}

	public GraphElementHashAnnotator<N, LabelSet> getClosedLabels() {
		return closedLabels;
	}

	public ArrayList<LinkedList<E>> getSolutions() {
		return solutions;
	}
}
