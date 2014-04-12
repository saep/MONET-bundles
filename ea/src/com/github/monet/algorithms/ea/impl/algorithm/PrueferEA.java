package com.github.monet.algorithms.ea.impl.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.impl.operator.TerminatorSimple;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.Evaluator;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Implementation of the Pruefer-Evolutionary-Algorithm by Gengui Zhou and
 * Mitsuo Gen (see "Genetic Algorithm Approach on Multi-criteria Minimum
 * Spanning Tree Problem, 1999). Although it is possible to configure this
 * algorithm using other encoding strategies, it is intended to be configured
 * using the Pruefer-Encoding to find Minimum-Spanning-Trees.
 *
 * @author Sven Selmke
 *
 */
public class PrueferEA extends EvolutionaryAlgorithm {

	// Generation variables
	private List<Individual> population;
	private List<Individual> nonDominatedSet;
	private int currentGeneration;

	// Parameters
	private int popSize;
	private int offspringSize; // lambda
	private PhenotypeMapping mapping;
	private Creator creator;
	private Mutator mutator;
	private Recombinator recombinator;
	private Selector selector;
	private Evaluator evaluator;
	private TerminatorSimple terminator;


	/**
	 * Configuration of the Pruefer-Algorithm. Parameters needed are: popSize,
	 * offspringSize, encoding, mapping, creator, mutator, recombinator,
	 * selector, evaluator, terminator.
	 */
	@Override
	public boolean configure(Map<String,Object> params) {
		this.popSize       = Functions.getParam(params, "popSize", Integer.class, 50);
		this.offspringSize = Functions.getParam(params, "offspringSize", Integer.class, 50);
		this.mapping       = Functions.getParam(params, "mapping", PhenotypeMapping.class, null);
		this.creator       = Functions.getParam(params, "creator", Creator.class, null);
		this.mutator       = Functions.getParam(params, "mutator", Mutator.class, null);
		this.recombinator  = Functions.getParam(params, "recombinator", Recombinator.class, null);
		this.selector      = Functions.getParam(params, "selector", Selector.class, null);
		this.evaluator     = Functions.getParam(params, "evaluator", Evaluator.class, null);
		this.terminator    = Functions.getParam(params, "terminator", TerminatorSimple.class, null);

		if (this.terminator == null || this.evaluator == null ||
				this.selector == null || this.recombinator == null ||
				this.mutator == null || this.creator == null ||
				this.mapping == null) {
			Functions.log("Configuration of Pruefer-EA failed. Not all parameters set.", Functions.LOG_ERROR);
			return false;
		}

		return true;
	}


	/**
	 * Implements the Pruefer-Algorithm. After creating an initial population,
	 * the main loop will recombinate randomly selected parents in order to
	 * create a certain amount of offspring, which are then mutated. After
	 * evaluating the newly generated individuals, the selection algorithm is
	 * executed to form a new generation. At each generation, a set of all
	 * non-dominated individuals found so far is beeing updated.
	 *
	 * @return unique non-dominated individuals found by the algorithm
	 */
	@Override
	public List<Individual> execute() {
		Functions.log("Starting execution of Pruefer-EA.");
		long startingTime = System.currentTimeMillis();

		// Create initial population
		Functions.log("Creating initial population...");
		this.population = this.creator.createPopulation(this.popSize);
		this.mapping.createPhenotypes(this.population);
		this.evaluator.evaluateFitness(this.population);
		this.nonDominatedSet = Individual.getNondominatedSolutions(this.population);
		this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet);
		Functions.log("Initial population has been created.");
		Individual.logGeneration(this.population, this.getOpConfig().getExpName(), "initial", false);

		// Start main loop
		this.currentGeneration = 0;
		while (!(this.terminator.terminate(this.currentGeneration, System.currentTimeMillis()-startingTime, null, this.nonDominatedSet))) {
			this.currentGeneration++;

			// Log
			if (this.meter != null) {
				this.meter.startTimer("generation/"+this.currentGeneration+"/duration");
			}
			if (this.currentGeneration % 25 == 0) {
				Functions.log("Starting generation " + this.currentGeneration + " (" + nonDominatedSet.size() + " non-dominated invdividuals).");
			}

			// create offspring
			List<Individual> offspring = new ArrayList<Individual>();
			for (int i = 0; i < this.offspringSize; i++) {
				Individual parentA = EaRandom.getRandomElement( this.population );
				Individual parentB = EaRandom.getRandomElement( this.population );
				Individual child   = this.recombinator.recombinateIndividuals(parentA, parentB, this.population);
				child = this.mutator.mutateIndividual(child);
				offspring.add(child);
			}

			// evaluate fitness
			this.mapping.createPhenotypes(offspring);
			this.evaluator.evaluateFitness(offspring);

			// select best
			this.population = this.selector.select(this.population, offspring, this.population.size());

			// Update set of nondominated solutions
			this.nonDominatedSet = Individual.updateNondominatedSolutions(this.nonDominatedSet, this.population); // PERFORMANCE
			this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet); // PERFORMANCE combine with update nondominated

			// Log
			if (this.meter != null) {
				this.meter.stopTimer("generation/"+this.currentGeneration+"/duration");
			}
			Individual.logGeneration(this.nonDominatedSet, this.getOpConfig().getExpName(), "gen"+this.currentGeneration, false);
		}

		// Get Unique results and create log information
		this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet);
		Functions.log("Execution of Pruefer-EA finished!");
		Functions.log("Number of created Individuals: " + Individual.getIdCounter() + ".");
		Functions.log("Size of nondominated set: " + this.nonDominatedSet.size() + ".");
		Individual.logGeneration(this.nonDominatedSet, this.getOpConfig().getExpName(), "result", false);

		// Finish (measure and return)
		this.measureObjectiveValues(this.nonDominatedSet);
		return this.nonDominatedSet;
	}


	@Override
	public String getName() {
		return "Pruefer-EA";
	}

}
