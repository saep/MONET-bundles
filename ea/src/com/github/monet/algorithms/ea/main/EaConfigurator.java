package com.github.monet.algorithms.ea.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.impl.algorithm.PrueferEA;
import com.github.monet.algorithms.ea.impl.algorithm.RandomSearch;
import com.github.monet.algorithms.ea.impl.algorithm.RandomWalk;
import com.github.monet.algorithms.ea.impl.algorithm.SMSEMOA;
import com.github.monet.algorithms.ea.impl.algorithm.SPEA2;
import com.github.monet.algorithms.ea.impl.individual.MSTEncoding;
import com.github.monet.algorithms.ea.impl.individual.PrueferEncoding;
import com.github.monet.algorithms.ea.impl.individual.STSPEncoding;
import com.github.monet.algorithms.ea.impl.operator.DirectMSTCreator;
import com.github.monet.algorithms.ea.impl.operator.DirectMSTMapping;
import com.github.monet.algorithms.ea.impl.operator.DirectMSTMutator;
import com.github.monet.algorithms.ea.impl.operator.DirectMSTRecombinator;
import com.github.monet.algorithms.ea.impl.operator.PrueferCreator;
import com.github.monet.algorithms.ea.impl.operator.PrueferEvaluator2;
import com.github.monet.algorithms.ea.impl.operator.PrueferMapping;
import com.github.monet.algorithms.ea.impl.operator.PrueferMutator;
import com.github.monet.algorithms.ea.impl.operator.PrueferRecombinator;
import com.github.monet.algorithms.ea.impl.operator.SPEA2Creator;
import com.github.monet.algorithms.ea.impl.operator.SPEA2Evaluator;
import com.github.monet.algorithms.ea.impl.operator.SPEA2Mapping;
import com.github.monet.algorithms.ea.impl.operator.SPEA2Mutator;
import com.github.monet.algorithms.ea.impl.operator.SPEA2Recombinator;
import com.github.monet.algorithms.ea.impl.operator.SelectionTournament;
import com.github.monet.algorithms.ea.impl.operator.SelectorRouletteWheel;
import com.github.monet.algorithms.ea.impl.operator.TerminatorSimple;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.Evaluator;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.operator.Terminator;
import com.github.monet.algorithms.ea.util.Configuration;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleAbstractGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;

/**
 * Creates, configures and executes an evolutionary algorithm.
 *
 * @author Sven Selmke
 *
 * @param <N> Node type of the nodes used in the input graph
 * @param <E> Edge type of the edges used in the input graph
 * @param <G> Graph type of the input graph
 */
public class EaConfigurator {

	private Configuration config;
	private Job job;
	private EvolutionaryAlgorithm alg;
	private boolean configSuccess = false;
	private static boolean initialized = false;



	// OPERATOR SPECIFIC CONFIGURATION METHODS
	// ########################################################################

	/**
	 * This method has to be called before running a EA.
	 */
	public static void initizalize() {
		// Only execute this method once
		if (EaConfigurator.initialized) return;
		EaConfigurator.initialized = true;
		// Add available operators
		Functions.addOperator(TerminatorSimple.class);
		Functions.addOperator(PrueferEA.class);
		Functions.addOperator(SMSEMOA.class);
		Functions.addOperator(SPEA2.class);
		Functions.addOperator(RandomSearch.class);
		Functions.addOperator(RandomWalk.class);
		Functions.addOperator(PrueferEncoding.class);
		Functions.addOperator(PrueferCreator.class);
		Functions.addOperator(PrueferEvaluator2.class);
		Functions.addOperator(PrueferMutator.class);
		Functions.addOperator(PrueferRecombinator.class);
		Functions.addOperator(PrueferMapping.class);
		Functions.addOperator(SelectorRouletteWheel.class);
		Functions.addOperator(SelectionTournament.class);
		Functions.addOperator(STSPEncoding.class);
		Functions.addOperator(SPEA2Creator.class);
		Functions.addOperator(SPEA2Evaluator.class);
		Functions.addOperator(SPEA2Mutator.class);
		Functions.addOperator(SPEA2Recombinator.class);
		Functions.addOperator(SPEA2Mapping.class);
		Functions.addOperator(MSTEncoding.class);
		Functions.addOperator(DirectMSTCreator.class);
		Functions.addOperator(DirectMSTMutator.class);
		Functions.addOperator(DirectMSTRecombinator.class);
		Functions.addOperator(DirectMSTMapping.class);
	}

	/**
	 * Validates the overall configuration after everything has been set up by
	 * the EaConfigurator.
	 *
	 * Validation:
	 *
	 * 1. Some operators are only compatible with certain other operators (e.g.
	 * Mutators, Recombinators and Mappings require a certain Creator;
	 * Evaluators require a certain GPM).
	 *
	 * 2. Some operators require certain data (e.g. PrueferCreator requires an
	 * undirected graph)
	 *
	 * 3. Misc validations (minimum number of edges for MST problems; some
	 * problems require a startNode and endNode)
	 *
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static boolean validateConfiguration(Configuration config) {
		Class<?>[] classes = {PrueferMutator.class, PrueferCreator.class};

		// CREATOR VALIDATION
		if (config.getCreator() != null) {
			Creator op = config.getCreator();
		}

		// MUTATOR VALIDATION
		if (config.getMutator() != null) {
			Mutator op = config.getMutator();

			// Checks for PrueferMutator
			if (op instanceof PrueferMutator) {
				if (!(config.getCreator() instanceof PrueferCreator)) {
					Functions.log("Error! PrueferMutator is only compatible with the PrueferCreator.", Functions.LOG_ERROR);
					return false;
				}
			}

			// Checks for DirectMSTMutator
			if (op instanceof DirectMSTMutator) {
				if (!(config.getCreator() instanceof DirectMSTCreator)) {
					Functions.log("Error! PrueferMutator is only compatible with the DirectMSTCreator.", Functions.LOG_ERROR);
					return false;
				}
			}

			// Checks for SPEA2Mutator
			if (op instanceof SPEA2Mutator) {
				if (!(config.getCreator() instanceof SPEA2Creator)) {
					Functions.log("Error! SPEA2Mutator is only compatible with the SPEA2Creator.", Functions.LOG_ERROR);
					return false;
				}
			}

		}

		// RECOMBINATOR VALIDATION
		if (config.getRecombinator() != null) {
			Recombinator op = config.getRecombinator();

			// Checks for PrueferRecombinator
			if (op instanceof PrueferRecombinator) {
				if (!(config.getCreator() instanceof PrueferCreator)) {
					Functions.log("Error! PrueferRecombinator is only compatible with the PrueferCreator.", Functions.LOG_ERROR);
					return false;
				}
			}

			// Checks for DirectMSTRecombinator
			if (op instanceof DirectMSTRecombinator) {
				if (!(config.getCreator() instanceof DirectMSTCreator)) {
					Functions.log("Error! DirectMSTRecombinator is only compatible with the DirectMSTCreator.", Functions.LOG_ERROR);
					return false;
				}
			}

			// Checks for PrueferRecombinator
			if (op instanceof SPEA2Recombinator) {
				if (!(config.getCreator() instanceof SPEA2Creator)) {
					Functions.log("Error! SPEA2Recombinator is only compatible with the SPEA2Creator.", Functions.LOG_ERROR);
					return false;
				}
			}

		}

		// MAPPING VALIDATION
		if (config.getMapping() != null) {
			PhenotypeMapping op = config.getMapping();

		}

		// EVALUATOR VALIDATION
		if (config.getEvaluator() != null) {
			Evaluator op = config.getEvaluator();

		}

		// SELECTOR VALIDATION
		if (config.getSelector() != null) {
			Selector op = config.getSelector();

		}

		// TERMINATOR VALIDATION
		if (config.getTerminator() != null) {
			Terminator op = config.getTerminator();

		}

		// OTHER PARAMETERS

		// using maxDegree requires a complete graph
		int maxDegree = Functions.getParam(config.getParams(), "maxDegree", Integer.class, 0);
		if (maxDegree >= 2) {
			Functions.log("Using the maxDegree parameter requires a complete graph. Checking now...", Functions.LOG_DEBUG);
			if (config.isGraphComplete()) {
				Functions.log("Graph is already complete! No changes were made.", Functions.LOG_DEBUG);
			} else {
				Functions.makeGraphComplete(((AnnotatedGraph)config.getAnnotatedGraph()).getGraph(), config.getWeightAnnotator(), config.getDim());
			}
		}

		// minimum required nodes, edges and dimension
		if (config.getNumNodes() < 2) {
			Functions.log("The given graph has less than two nodes! Will NOT execute algorithm.", Functions.LOG_ERROR);
			return false;
		}
		if (config.getNumEdges() < 1) {
			Functions.log("The given graph has less than one edge! Will NOT execute algorithm.", Functions.LOG_ERROR);
			return false;
		}
		if (config.getDim() < 1) {
			Functions.log("The given graph problem has no dimension! Will NOT execute algorithm.", Functions.LOG_ERROR);
			return false;
		}

		return true;
	}



	// CONFIGURATION METHODS
	// ########################################################################

	/**
	 * Create and configure an evolutionary algorithm according to the given
	 * parameters. The given parameter map will be updated.
	 *
	 * Job-Parameters:
	 * 		See Evolution class
	 *
	 * @param inputGraph
	 *            the weight-annotated graph given as a "problem"
	 * @param job
	 *            job containing additional parameters for the algorithm
	 * @param meter
	 *            used by the algorithm to measure results
	 * @param serviceDir
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void configure(Object inputGraph, Map<String,Object> origParams, Job job, Meter meter, ServiceDirectory serviceDir) throws Exception {
    	this.job = job;

    	// STEP 0
    	// Initialization and general parameters

    	// Create a copy of given parameters because we change the map!
    	Map<String,Object> params = null;
    	if (origParams == null) {
			Functions.log("No parameters given! Cannot execute algorithm.", Functions.LOG_ERROR);
			this.configSuccess = false;
			return;
    	} else {
    		params = new HashMap(origParams);
		}

    	// create a name for this experiment if non was given
    	Functions.setNewParam(params, "experimentName", "exp");

    	// Create Configuration object
    	this.config = new Configuration();
    	config.setJob(job);
    	config.setMeter(meter);
    	config.setServiceDir(serviceDir);
    	config.setParams(params);
    	config.setExpName( Functions.getParam(params, "experimentName", String.class, "exp") );

    	// Initialize Configurator
    	EaConfigurator.initizalize();

    	// Parameter Presets
    	// Does not overwrite existing parameter-values!
    	String presetName = Functions.getParam(params, "preset", String.class, "");
    	if (presetName.equalsIgnoreCase("PrueferMST-PrueferEA")) {
    		Functions.setNewParam(params, "eaName", "Pruefer-EA");
    		Functions.setNewParam(params, "creatorName", "Pruefer-Creator");
    		Functions.setNewParam(params, "mutatorName", "Uniform-Mutation");
    		Functions.setNewParam(params, "recombinatorName", "Pruefer-Recombinator");
    		Functions.setNewParam(params, "selectorName", "Roulette-Wheel-Selection");
    		Functions.setNewParam(params, "evaluatorName", "Pruefer-Evaluator-2");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "mappingName", "Pruefer-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "offspringSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "400");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectMST-PrueferEA")) { // final report
    		Functions.setNewParam(params, "eaName", "Pruefer-EA");
    		Functions.setNewParam(params, "creatorName", "MST-Creator");
    		Functions.setNewParam(params, "mutatorName", "MST-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "MST-Recombinator");
    		Functions.setNewParam(params, "selectorName", "Roulette-Wheel-Selection");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "evaluatorName", "Pruefer-Evaluator-2");
    		Functions.setNewParam(params, "mappingName", "MST-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "offspringSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "400");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectMST-SMSEMOA")) { // final report
    		Functions.setNewParam(params, "eaName", "SMS-EMOA");
    		Functions.setNewParam(params, "creatorName", "MST-Creator");
    		Functions.setNewParam(params, "mutatorName", "MST-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "MST-Recombinator");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "mappingName", "MST-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "4000");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectMST-SPEA2")) {
    		Functions.setNewParam(params, "eaName", "SPEA-2");
    		Functions.setNewParam(params, "creatorName", "MST-Creator");
    		Functions.setNewParam(params, "mutatorName", "MST-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "MST-Recombinator");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "selectorName", "Tournament-Selection");
    		Functions.setNewParam(params, "evaluatorName", "Pruefer-Evaluator-2");
    		Functions.setNewParam(params, "mappingName", "MST-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "200");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectSSSP-PrueferEA")) {
    		Functions.setNewParam(params, "eaName", "Pruefer-EA");
    		Functions.setNewParam(params, "creatorName", "SPEA2-Creator");
    		Functions.setNewParam(params, "mutatorName", "SPEA2-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "SPEA2-Recombinator");
    		Functions.setNewParam(params, "selectorName", "Roulette-Wheel-Selection");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "evaluatorName", "SPEA2-Evaluator");
    		Functions.setNewParam(params, "mappingName", "SPEA2-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "offspringSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "200");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectSSSP-SMSEMOA")) {
    		Functions.setNewParam(params, "eaName", "SMS-EMOA");
    		Functions.setNewParam(params, "creatorName", "SPEA2-Creator");
    		Functions.setNewParam(params, "mutatorName", "SPEA2-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "SPEA2-Recombinator");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "mappingName", "SPEA2-GPM");
    		Functions.setNewParam(params, "popSize", "250");
    		Functions.setNewParam(params, "maxGenerations", "4000");
    		Functions.setNewParam(params, "maxTime", 2*60*60*1000);
    		Functions.setNewParam(params, "fitnessThreshold", null);
    		Functions.setNewParam(params, "minimization", "true");

    	} else if (presetName.equalsIgnoreCase("DirectSSSP-SPEA2"))  { // final report
    		Functions.setNewParam(params, "eaName", "SPEA-2");
    		Functions.setNewParam(params, "creatorName", "SPEA2-Creator");
    		Functions.setNewParam(params, "mutatorName", "SPEA2-Mutator");
    		Functions.setNewParam(params, "recombinatorName", "SPEA2-Recombinator");
    		Functions.setNewParam(params, "terminatorName", "Simple-Terminator");
    		Functions.setNewParam(params, "selectorName", "Tournament-Selection");
    		Functions.setNewParam(params, "evaluatorName", "SPEA2-Evaluator");
    		Functions.setNewParam(params, "mappingName", "SPEA2-GPM");
        	Functions.setNewParam(params, "popSize", "250");
        	Functions.setNewParam(params, "archiveSize", "250");
        	Functions.setNewParam(params, "maxGenerations", "400");
        	Functions.setNewParam(params, "maxTime", 2*60*60*1000);
        	Functions.setNewParam(params, "fitnessThreshold", null);
        	Functions.setNewParam(params, "minimization", "true");
    	}


    	// STEP 1
    	// Get Graph and update parameters

    	// Get graph
    	if (inputGraph == null) {
			Functions.log("No graph given! Cannot execute algorithm.", Functions.LOG_ERROR);
			this.configSuccess = false;
			return;
    	}
    	AnnotatedGraph<?, ?, ?> g = (AnnotatedGraph<?, ?, ?>) inputGraph;
    	int numNodes = g.getGraph().getNumNodes();
    	int numEdges = g.getGraph().getNumEdges();
    	boolean isGraphComplete = Functions.isGraphComplete(g.getGraph());

    	// Disable safe mode
    	if (g.getGraph() instanceof SimpleAbstractGraph) {
    		SimpleAbstractGraph abstractGraph = (SimpleAbstractGraph)(g.getGraph());
    		abstractGraph.setSafeMode(false);
    		Functions.log("Disabling safe-mode for given graph.", Functions.LOG_DEBUG);
    	}

    	// Create a map for Node IDs -> Node objects
    	HashMap<Integer, Node> idNodeMap = new HashMap<Integer, Node>();
    	HashMap<Node, Integer> nodeIdMap = new HashMap<Node, Integer>();
    	int n = 0;
    	for (Node node : g.getGraph().getAllNodes()) {
    		idNodeMap.put(n, node);
    		nodeIdMap.put(node, n);
    		n++;
    	}

    	// Make sure that an annotator is given
    	GraphElementAnnotator<Edge, Weight> annotator = g.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class);
    	if (annotator == null) {
			Functions.log("No Annotator " + Functions.PARAM_EDGEANNOTATOR + " given! Cannot execute algorithm.", Functions.LOG_ERROR);
			this.configSuccess = false;
			return;
    	}
    	if (!annotator.getAnnotatedElements().iterator().hasNext()) {
    		Functions.log("Given Annotator " + Functions.PARAM_EDGEANNOTATOR + " does not have any elements! Cannot execute algorithm.", Functions.LOG_ERROR);
    		this.configSuccess = false;
    		return;
    	}

    	// Check for start and end node
		GraphElementReverseHashAnnotator<Node, String> sdAnnotator = g.getAnnotator(Functions.PARAM_SDANNOTATOR, GraphElementReverseHashAnnotator.class);
		if (sdAnnotator != null) {
			Iterator<Node> iter = sdAnnotator.getElements(Functions.PARAM_SOURCENODE).iterator();
			if (iter.hasNext()) {
				Node source = iter.next();
				Functions.setNewParam(params, Functions.PARAM_SOURCENODE, source);
				config.setStartNode((Node)params.get(Functions.PARAM_SOURCENODE));
			}
			iter = sdAnnotator.getElements(Functions.PARAM_DESTNODE).iterator();
			if (iter.hasNext()) {
				Node destination = iter.next();
				Functions.setNewParam(params, Functions.PARAM_DESTNODE, destination);
				config.setEndNode((Node)params.get(Functions.PARAM_DESTNODE));
			}
		}

    	// Get dimension
    	int numObjectives = ((Weight)annotator.getAnnotation(annotator.getAnnotatedElements().iterator().next())).getDimension();

    	params.put("numObjectives", numObjectives);
    	params.put("numNodes", numNodes);
    	params.put("numEdges", numEdges);
    	params.put("isGraphComplete", isGraphComplete);
    	params.put("problemGraph", g);
    	params.put("idNodeMap", idNodeMap);
    	params.put("nodeIdMap", nodeIdMap);
    	config.setDim(numObjectives);
    	config.setNumNodes(numNodes);
    	config.setNumEdges(numEdges);
    	config.setGraphComplete(isGraphComplete);
    	config.setIdNodeMap(idNodeMap);
    	config.setNodeIdMap(nodeIdMap);
    	config.setAnnotatedGraph(g);
    	config.setWeightAnnotator(annotator);


    	// STEP 2
    	// Global Settings

    	// Log random seed
    	Long seed = Functions.getParam(params, "seed", Long.class, null);
    	boolean autoGenerateRandom = Functions.getParam(params, "autoGenerateSeed", Boolean.class, false);
    	if (autoGenerateRandom) {
    		seed = EaRandom.getNewRandomSeed();
    	} else {
    		seed = Functions.getParam(params, "seed", Long.class, null);
    		if (seed == null) {
    			seed = EaRandom.getNewRandomSeed();
    		} else {
    			Functions.log("Parameter autoGenerateSeed is not set to true, but no seed is specified!", Functions.LOG_WARNING);
    			EaRandom.setNewSeed(seed);
    		}
    	}
    	if (meter != null) meter.measureLong("randomSeed", seed);

    	// Create a complete graph from given instance?
    	boolean makeGraphComplete = Functions.getParam(params, "makeGraphComplete", Boolean.class, false);
    	if (makeGraphComplete) {
    		Functions.log("Creating a complete graph!", Functions.LOG_DEBUG);
			GraphElementAnnotator<Edge, Weight> weightAnnotator = g.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class);
    		int numObjs = ((Weight)weightAnnotator.getAnnotation(weightAnnotator.getAnnotatedElements().iterator().next())).getDimension();
    		// Functions.makeGraphComplete(problemGraph, weightAnnotator, numObjectives);
    		Functions.makeGraphComplete(g.getGraph(), g.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class), numObjs);
    	}


		// STEP 3
		// Create Parameters for the EA (Creator, Mutator,
		// Recombinator, Mapping, Evaluator, Selector, Terminator).
    	//
		// Note: The order of configuration is important, because some operators
		// may add parameters to the map that are important for other operators
		// that are configured later! E.g. the evaluator may specify a
		// maximization or minimization parameter for the selector.
    	//
		// If configuration of an operator is not successful, null is returned
		// by as operator. The execution continues, but will most likely fail to
		// configure the main algorithm and therefore terminate with an error.

    	// Create Creator
    	if (Functions.isParam(params, "creatorName")) {
    		Functions.log("Evaluating creatorName parameter.");
    		Creator op = Functions.getConfiguredOperatorByParamName(params, "creatorName", Creator.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("creator", op);
    		config.setCreator(op);
    	}

		// Create Mutator
    	if (Functions.isParam(params, "mutatorName")) {
    		Functions.log("Evaluating mutatorName parameter.");
    		Mutator op = Functions.getConfiguredOperatorByParamName(params, "mutatorName", Mutator.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("mutator", op);
    		config.setMutator(op);
    	}

		// Create Recombinator
    	if (Functions.isParam(params, "recombinatorName")) {
    		Functions.log("Evaluating recombinatorName parameter.");
    		Recombinator op = Functions.getConfiguredOperatorByParamName(params, "recombinatorName", Recombinator.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("recombinator", op);
    		config.setRecombinator(op);
    	}

		// Create GPM
    	if (Functions.isParam(params, "mappingName")) {
    		Functions.log("Evaluating mappingName parameter.");
    		PhenotypeMapping op = Functions.getConfiguredOperatorByParamName(params, "mappingName", PhenotypeMapping.class);
    		if (op == null) { this.configSuccess = false; return; }
			params.put("mapping", op);
			config.setMapping(op);
    	}

		// Create Evaluator
    	if (Functions.isParam(params, "evaluatorName")) {
    		Functions.log("Evaluating evaluatorName parameter.");
    		Evaluator op = Functions.getConfiguredOperatorByParamName(params, "evaluatorName", Evaluator.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("evaluator", op);
    		config.setEvaluator(op);
    	}

		// Create Selector
    	if (Functions.isParam(params, "selectorName")) {
    		Functions.log("Evaluating selectorName parameter.");
    		Selector op = Functions.getConfiguredOperatorByParamName(params, "selectorName", Selector.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("selector", op);
    		config.setSelector(op);
    	}

		// Create Terminator
    	if (Functions.isParam(params, "terminatorName")) {
    		Functions.log("Evaluating terminatorName parameter.");
    		Terminator op = Functions.getConfiguredOperatorByParamName(params, "terminatorName", Terminator.class);
    		if (op == null) { this.configSuccess = false; return; }
    		params.put("terminator", op);
    		config.setTerminator(op);
    	}



		// STEP 4
		// Create and configure the EA

		// Create EA
    	String algName = Functions.getParam(params, "eaName", String.class, null);
    	if (algName != null) {
	    	this.alg = Functions.getOperatorByName(algName, EvolutionaryAlgorithm.class);
	    	if (this.alg != null) {
		    	this.alg.setMeter(meter);
		    	this.configSuccess = this.alg.configure(params);
		    	params.put("ea", this.alg);
		    	config.setAlgorithm(this.alg);
		    	this.alg.setOpConfig(config);
	    	} else {
	    		Functions.log("Given EA '" + algName + "' not found! Nothing to execute.", Functions.LOG_ERROR);
	    		this.configSuccess = false;
	    	}
    	} else {
    		Functions.log("No eaName specified! Cannot execute algorithm.", Functions.LOG_ERROR);
    		this.configSuccess = false;
    	}



		// STEP 5
		// Validate the configuration

    	if (!EaConfigurator.validateConfiguration(config)) {
    		Functions.log("Validation of the given configuration failed.", Functions.LOG_ERROR);
    		this.configSuccess = false;
    	}

    	if (this.configSuccess) {
    		Functions.log("Configurion sucessful.");
    		Functions.log(config.toString());
    	}

    }


    /**
     * Start/Execute the algorithm
     *
     * @return the Individuals found by the algorithm.
     */
    public List<Individual> executeEa() {
    	if (this.configSuccess) {
    		List<Individual> result = this.alg.execute();
    		if (result == null) {
        		Functions.log("Error: Execution of the algorithm returned null instead of a set of solutions!", Functions.LOG_ERROR);
        		if (this.job != null) this.job.setState(Job.State.FAILED);
        		return null;
    		}
    		return result;
    	} else {
    		Functions.log("Cannot execute algorithm because the configuration failed or wasn't executed.", Functions.LOG_ERROR);
    		if (this.job != null) this.job.setState(Job.State.FAILED);
    		return null;
    	}
    }

}
