package com.github.monet.algorithms.ea.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.interfaces.GraphParser;
import com.github.monet.parser.MonetParser;
import com.github.monet.worker.Job;

import org.apache.logging.log4j.Logger;

import com.github.monet.algorithms.ea.main.Evolution;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Class for testing EAs
 */
public class EaTestProblem {
	public String graphFileName;
	public boolean directed;
	public Long seed;
	public String preset;
	public HashMap<String, Object> params;
	public boolean setRandomStartEndNodes;
	public String expName;

	/**
	 * Constructor.
	 * The given HashMap will be cloned.
	 */
	@SuppressWarnings("unchecked")
	public EaTestProblem(String expName, String graphFileName, boolean directed, Long seed, String preset, HashMap<String, Object> params, boolean setRandomStartEndNodes) {
		this.expName = expName;
		this.graphFileName = graphFileName;
		this.directed = directed;
		this.seed = seed;
		this.preset = preset;
		this.params = params;
		this.setRandomStartEndNodes = setRandomStartEndNodes;
		if (this.params == null) {
			this.params = new HashMap<String, Object>();
		} else {
			this.params = (HashMap<String, Object>)this.params.clone();
		}
	}

	/**
	 * Execute the Preset for given graph with given random seed. "seed" and
	 * "preset" (if not null) will overwrite existing parameters.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startTest() {
		EaRandom.setNewSeed(seed);
		if (params == null)
			params = new HashMap<String, Object>();

		// Get Graph (this might set the startNode and endNode to some values even if no IDs are specified)
		Functions.setNewParam(params, Functions.PARAM_GRAPHDIRECTED, directed);
		Job job = EaTestProblem.createDummyJob(params);
		GraphParser gp = new MonetParser();
		AnnotatedGraph g = (AnnotatedGraph) gp.parse(Functions.TEST_GRAPHDIR + graphFileName, job);

		// Select start and end
		if (this.setRandomStartEndNodes) {
			Node startNode = (Node) EaRandom.getRandomElement(g.getGraph().getAllNodes());
			Node endNode = (Node) EaRandom.getRandomElement(g.getGraph().getAllNodes());
			while (startNode == endNode) {
				endNode = (Node) EaRandom.getRandomElement(g.getGraph().getAllNodes());
			}
			Functions.log("Selecting random Start '" + startNode + "' and End '" + endNode + "'.", Functions.LOG_TEST);
			Functions.setParam(params, "startNode", startNode);
			Functions.setParam(params, "endNode", endNode);
		}

		// Set Parameters
		Functions.setParam(params, "preset", preset);
		Functions.setParam(params, "seed", seed.toString());
		Functions.setParam(params, "experimentName", expName);

		// Execute Algorithm
		if (g != null) {
			try {
				Evolution evo = new Evolution();
				evo.execute(g, params, null, null, null);
			} catch (Exception e) {
				e.printStackTrace();
				assert (false);
			}
		}

	}

	/**
	 * Execute multiple tests at once
	 */
	public static void testProblems(List<EaTestProblem> problems) {
		// Test each problem (store some logging results in "results")
		String results = "Short Logs:\n";
		while(problems.size() > 0){
			EaTestProblem problem = problems.get(0);
			// Test Algorithm
			long t = System.currentTimeMillis();
			problem.startTest();
			// Output
			String output = "Finished running preset '" + problem.preset + "' on graph '" + problem.graphFileName + "'. Time: " + (System.currentTimeMillis() - t)/1000 + "[s].";
			results += output + "\n";
			Functions.log(output, Functions.LOG_TEST);
			problems.remove(0);
		}

		// Print all results (again)
		Functions.log(results, Functions.LOG_TEST);
	}

	/**
	 * Creates a dummy job which only consists of algorithm parameters
	 */
	public static Job createDummyJob(final Map<String,Object> params) {
		Job job = new Job() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void clean() {
			}
			@Override
			public String getID() {
				return null;
			}
			@Override
			public Logger getLogger() {
				return null;
			}
			@Override
			public String getParserDescriptor() {
				return "DummyJobParser";
			}
			@Override
			public Map<String, Object> getParameters() {
				return params;
			}
			@Override
			public Object getInputGraph() {
				return null;
			}
			@Override
			public Map<String, Object> getParserParameters() {
				return params;
			}
		};
		return job;
	}

}
