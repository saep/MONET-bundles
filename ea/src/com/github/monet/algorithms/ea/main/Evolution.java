package com.github.monet.algorithms.ea.main;

import java.util.Map;

import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.interfaces.Algorithm;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;

/*
 * ############################################################################
 * #           EVOLUTIONARY ALGORITHM FRAMEWORK FOR MONET                     #
 * ############################################################################
 *
 *
 * Requires:
 * 		Graph classes
 *
 *
 * Overview:
 * 		Evolution: Entry. Used to configure the EA
 * 		EaConfigurator: Uses given parameters to configure EAs.
 * 			--> NEW ALGORITHMS HAVE TO BE REGISTERED THERE! <--
 *
 * 		EvolutionaryAlgorithm: Super-Class for all EAs
 * 		Individual: Consists of a Genotype and a Phenotype
 * 		SearchOperator: Classes such as Creators, Mutators, Recombinators that create or modify genotypes
 * 		PhenotypeMapping: Classes for creating phenotypes from given genotypes (Genotype-Phenotype-Mapping GPM)
 * 		Evaluator: Evaluate the fitness of individuals (Based on the phenotypes of individuals)
 * 		Selector: Selects individuals with high fitness
 *
 *
 * Course of actions
 * 		1. Evolution.execute() is called using some parameters
 * 		2. An EaConfigurator instance is created
 * 		3. The EaConfigurator creates an evolutionary algorithm object depending on specified parameters
 * 		4. Depending on the algorithm, parameters will be preprocessed (e.g. creation and configuration of Operators)
 * 		5. Parameters will be passed to the algorithm using a configuration method
 * 		6. The EA is executed by the EaConfigurator
 *
 *
 * Parameters
 * 		// User Parameters
 * 		preset, eaName, autoGenerateSeed, makeGraphComplete, seed, creatorName,
 * 		mutatorName, recombinatorName, selectorName, mappingName,
 * 		evaluatorName, terminatorName, popSize, offspringSize,
 * 		archiveSize, mutatorProbability, recombinator_probability,
 * 		numTournamentRounds, maxGenerations, maxTime, fitnessThreshold,
 * 		maxDegree
 *
 * 		// Parameters that are available if certain conditions are met (created by EaConfigurator)
 * 		startNode, Node, first node of the path
 * 		endNode, Node, last node of the path
 *
 *		// Parameters that are always available (automatically) (created by EaConfigurator)
 *		numNodes, Integer, number of nodes in the graph
 *		numObjectives, Integer, dimension of objective-values
 *		problemGraph, AnnotatedGraph<N, E, G>, input graph
 *		idNodeMap, HashMap<Integer, Node>, unique ID for each node
 *		nodeIdMap, HashMap<Node, Integer>, unique ID for each Node
 *		fitnessMaximization, Boolean, might be set by the Evaluator if fitness is to be maximized (default: false/minimization)
 *
 * 		// Note: Genotype and Encoding is implicitly specified via Creator, Phenotype is implicitly specified via GPM
 *
 *
 * Simple Graph Format
 * 		// //, # lines ignored by parser
 * 		1. line: Number of nodes
 * 		2. line: Number of edges
 * 		3. line: Number of objectives
 * 		...: Edges (format: "from to w1 w2 ... wn" e.g. "1 2 5.4 3.2")
 *
 *
 * Meter, Logger, Job
 * 		Throw exceptions or set job state on error (if (this.job != null) this.job.setState(Job.STATE_FAILED);)
 * 		Usage of the meter: meter.measureInt("measureTest", 7);
 * 		Usage of the logger: Functions.log("Message\n");
 *
 *
 * GIT
 * 		git commit -a -m "Algorithm: ..."
 *
 *
 * TO.DO EaFramework
 * 		/
 *
 */

/**
 * Main class / entry point for executing an evolutionary algorithm. This class
 * creates a new EaConfigurator in order to configure and execute the algorithm
 * specified by the given parameters.
 *
 * @author Sven Selmke
 *
 */
public class Evolution implements Algorithm {

    // ########################################################################
    // MAIN
    // ########################################################################

    /**
     * Main execution method. Creates an EaConfigurator and starts the execution of the ea.
     *
     * @param inputGraph
     * @param job
     * @param meter
     * @param serviceDir
     * @throws Exception
     */
    public void execute(Object inputGraph, Map<String,Object> params, Job job, Meter meter, ServiceDirectory serviceDir) throws Exception {
    	// Set logger in Functions
    	if (job != null) {
    		Functions.setLogger(job.getLogger());
    	}

		// Create EA Configurator and EXECUTE the EA
		EaConfigurator configurator = new EaConfigurator();
		configurator.configure(inputGraph, params, job, meter, serviceDir);
		configurator.executeEa();
    }

    // ########################################################################
    // MAIN END
    // ########################################################################


	/**
	 * Entry point for the execution of the evolutionary algorithm
	 */
	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir) throws Exception {
		assert(job != null);
    	Object inputGraph = job.getInputGraph();
    	Map<String,Object> params = job.getParameters();
    	this.execute(inputGraph, params, job, meter, serviceDir);
	}

}
