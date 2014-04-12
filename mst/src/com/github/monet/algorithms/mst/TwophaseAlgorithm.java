package com.github.monet.algorithms.mst;

import java.util.ArrayList;
import java.util.Map;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.FirstPhaseAlgorithm;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.interfaces.SecondPhaseAlgorithm;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.interfaces.UndirectedGraph;
import com.github.monet.graph.interfaces.UniobjectiveAlgorithm;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.Algorithm;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;

import org.apache.logging.log4j.Logger;

public class TwophaseAlgorithm<N extends Node, E extends UndirectedEdge, G extends UndirectedGraph<N, E, G>>
		implements Algorithm {

	protected AnnotatedGraph<N, E, G> annotatedGraph;
	protected FirstPhaseAlgorithm<N, E, G> firstPhase;

	public TwophaseAlgorithm(
			UniobjectiveAlgorithm<N, E, G, E> uniobjectiveAlgorithm) {
		this.firstPhase = new FirstPhase2d<>(uniobjectiveAlgorithm);
	}

	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir)
			throws Exception {

		Logger log = job.getLogger();
		Map<String, Object> parameters = job.getParameters();

		// Parameter and input validation
		job.setState("validating");

		// Parameter: Weight annotator name
		String paraWeightString = "edges";
		if (parameters.containsKey("weightAnnotationName")) {
			paraWeightString = String.valueOf(parameters.get(
					"weightAnnotationName"));
		} else {
			log.info(
					"Parameter 'weightAnnotationName' not set. Default value is '"
					+ paraWeightString + "'.");
		}

		this.annotatedGraph = (AnnotatedGraph<N, E, G>) job.getInputGraph();
		GraphElementAnnotator<E, Weight> boxed_weights = this.annotatedGraph.
				getAnnotator(paraWeightString, GraphElementAnnotator.class);
		if (boxed_weights == null) {
			log.error("Annotator '" + paraWeightString
					+ "' (edge weights) not found.");
			throw new Exception("Input validation failed");
		}
		GraphElementWeightAnnotator weights = new GraphElementWeightAnnotator(
				boxed_weights);
		if (weights.getDimension() < 2) {
			log.error("Input weight dimension " + String.valueOf(weights.
					getDimension()) + " is invalid.");
			throw new Exception("Input validation failed");
		}
		if (weights.getDimension() > 2) {
			log.error(
					"This algorithm is not able to process weights with dimension > 2. "
					+ "Input has dimension " + String.valueOf(weights.
					getDimension()) + ".");
			throw new Exception("Input validation failed");
		}

		// Parameter: Second phase
		String paraSecondPhaseString = "kbest";
		if (parameters.containsKey("secondPhase")) {
			paraSecondPhaseString = String.
					valueOf(parameters.get("secondPhase"));
		} else {
			log.info("Parameter 'secondPhase' not set. Default value is '"
					+ paraSecondPhaseString + "'.");
		}
		if (!paraSecondPhaseString.equals("kbest") && !paraSecondPhaseString.
				equals("branchbound")) {
			log.error(
					"Value of parameter secondPhase is invalid. Valid values are: 'kbest', 'branchbound'");
			throw new Exception("Input validation failed");
		}
		SecondPhaseAlgorithm<N, E, G> secondPhase;
		switch (paraSecondPhaseString) {
			case "branchbound":
				secondPhase = new BranchBound<>();
				break;
			default:
			case "kbest":
				secondPhase = new KBestSecondPhase<>();
		}

		// Execution of first phase
		job.setState("executing: first phase");
		meter.startTimer("firstPhase");
		ParetoSet<N, E, G> firstPhaseResult = this.firstPhase.firstPhase(
				this.annotatedGraph, paraWeightString);
		meter.stopTimer("firstPhase");
		log.info("Found " + firstPhaseResult.size()
				+ " extreme efficient solutions in first phase.");

		// Execution of second phase
		job.setState("executing: second phase (" + paraSecondPhaseString + ")");
		meter.startTimer("secondPhase");
		ParetoSet<N, E, G> secondPhaseResult = secondPhase.secondPhase(
				this.annotatedGraph, paraWeightString, firstPhaseResult);
		meter.stopTimer("secondPhase");
		log.info("Found " + secondPhaseResult.size()
				+ " non-extreme efficient solutions in second phase.");

		job.setState("writing output");
		{
			log.info("measuring pareto");
			ArrayList<G> allSolutions = new ArrayList<>(firstPhaseResult);
			allSolutions.addAll(secondPhaseResult);
			for (G g : allSolutions)
			{
				double[] objectiveArray = weights.sum(g.getAllEdges()).getWeights();
				ArrayList<String> edgeList = new ArrayList<>();
				for(E e : g.getAllEdges())
				{
					edgeList.add(e.toString());
				}
				meter.measurePareto(objectiveArray, edgeList);
			}
		}
		{
			log.info("printing extreme efficient (legacy)");
			int i = 0;
			for (G g : firstPhaseResult) {
				meter.measureString("/solutions/extreme_efficient/" + String.
						valueOf(i) + "/graph", g.toString());
				meter.measureDouble("/solutions/extreme_efficient/" + String.
						valueOf(i) + "/objective_value", weights.sum(g.
						getAllEdges()).getWeights());
				String s = String.valueOf(weights.sum(g.getAllEdges()).getWeight(0))
						+ "," + String.valueOf(weights.sum(g.getAllEdges()).getWeight(1));
				log.info(s);
				i++;
			}
		}
		{
			log.info("printing non-extreme efficient (legacy)");
			int i = 0;
			for (G g : secondPhaseResult) {
				meter.measureString("/solutions/extreme_efficient/" + String.
						valueOf(i) + "/graph", g.toString());
				meter.measureDouble("/solutions/non_extreme_efficient/"
						+ String.valueOf(i) + "/objective_value", weights.sum(g.
						getAllEdges()).getWeights());
				String s = String.valueOf(weights.sum(g.getAllEdges()).getWeight(0))
						+ "," + String.valueOf(weights.sum(g.getAllEdges()).getWeight(1));
				log.info(s);
				i++;
			}
		}
	}
}
