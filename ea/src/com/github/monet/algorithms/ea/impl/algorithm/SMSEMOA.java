package com.github.monet.algorithms.ea.impl.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.impl.operator.TerminatorSimple;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.common.ParetoPoint;


/**
 * Implementation of the SMSEOMO Algorithm designed for using the S-Metric. See
 * "SMS-EMOA - Effektive evolutionäre Mehrzieloptimierung" by Nicola Beume,
 * Boris Naujoks, Günter Rudolph.
 *
 * @author Sven Selmke
 *
 */
public class SMSEMOA extends EvolutionaryAlgorithm {

	// Generation variables
	private List<Individual> population;
	private List<Individual> nonDominatedSet;
	private int currentGeneration;

	// Parameters
	private int popSize;
	private PhenotypeMapping mapping;
	private Creator creator;
	private Mutator mutator;
	private Recombinator recombinator;
	private TerminatorSimple terminator;


	/**
	 * Configuration of the SMSEMOA. Parameters needed are: popSize
	 */
	@Override
	public boolean configure(Map<String,Object> params) {
		this.popSize       = Functions.getParam(params, "popSize", Integer.class, 50);
		this.mapping       = Functions.getParam(params, "mapping", PhenotypeMapping.class, null);
		this.creator       = Functions.getParam(params, "creator", Creator.class, null);
		this.mutator       = Functions.getParam(params, "mutator", Mutator.class, null);
		this.recombinator  = Functions.getParam(params, "recombinator", Recombinator.class, null);
		this.terminator    = Functions.getParam(params, "terminator", TerminatorSimple.class, null);

		if (this.terminator == null || this.mutator == null ||
				this.creator == null || this.mapping == null
			) {
			Functions.log("Configuration of SMS-EMOA failed. Not all parameters set.", Functions.LOG_ERROR);
			return false;
		}

		return true;
	}


	/**
	 * Implements the SMS-EMOA Algorithm.
	 *
	 * @return unique non-dominated individuals found by the algorithm
	 */
	@Override
	public List<Individual> execute() {
		Functions.log("Starting execution of SMS-EMOA.");
		long startingTime = System.currentTimeMillis();

		// Create initial population
		Functions.log("Creating initial population...");
		this.population = this.creator.createPopulation(this.popSize);
		this.mapping.createPhenotypes(this.population);
		this.nonDominatedSet = Individual.getNondominatedSolutions(this.population);
		this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet);
		Functions.log("Initial population has been created.");
		Individual.logGeneration(this.population, this.getOpConfig().getExpName(), "initial", false);

		// Start main loop
		this.currentGeneration = 0;
		while (!(this.terminator.terminate(this.currentGeneration, System.currentTimeMillis()-startingTime, null))) {
			this.currentGeneration++;

			// Log (remember: a SMSEMOA generation creates only one individual. We don't want to log 4000 log entries!)
			//if (this.meter != null) {
			//	this.meter.startTimer("generation/"+this.currentGeneration+"/duration");
			//}
			//Functions.log("Current set of nondominated Individuals:\n ");
			//for (int i = 0; i < nonDominatedSet.size(); i++) Functions.log(nonDominatedSet.get(i).toString());
			if (this.currentGeneration % 100 == 0) {
				Functions.log("Starting generation " + this.currentGeneration + " (" + nonDominatedSet.size() + " non-dominated invdividuals).");
			}

			// create a single offspring
			Individual child = null;
			if (this.recombinator != null) {
				Individual parentA = EaRandom.getRandomElement( this.population );
				Individual parentB = EaRandom.getRandomElement( this.population );
				child = this.recombinator.recombinateIndividuals(parentA, parentB, null);
				if (this.mutator != null) {
					child = this.mutator.mutateIndividual(child);
				}
			} else {
				if (this.mutator != null) {
					Individual parent = EaRandom.getRandomElement( this.population );
					child = this.mutator.mutateIndividual(parent);
					assert(parent != child); // child has to be a new object!
				}
			}
			assert(child != null);

			// add new child to population
			this.population.add(child);

			// evaluate fitness
			List<Individual> offspring = new ArrayList<Individual>();
			offspring.add(child);
			this.mapping.createPhenotypes(offspring);

			// remove worst individual:
			//    1. calculate rank
			//    2. remove individual with maximum rank or minimum contribution
			// Faster method:
			//    calculate changes caused by new individual only
			Individual worst = null;
			// Alternative calculation:
			//	List<Individual> dominatedSolutions = Individual.getDominatedSolutions(this.population);
			//	if (dominatedSolutions.size() > 0) {
			if (Individual.containsDominatedSolution(population)) {
				Individual.calculateRank(this.population); // FIXED
				worst = Individual.getMaxRankIndividual(this.population);
			} else {
				int dim = this.population.get(0).getObjectiveValues().length;
				// for minimization: get individual with MAX contribution (which is the worst one!)
				// for maximization: get the individual with MIN contribution (which is the worst one)
				worst = ParetoPoint.calcSMetricContrib(this.population, null, dim, Individual.minimizationOfObjectives);
			}
			this.population.remove(worst);

			// Update set of nondominated solutions
			this.nonDominatedSet = Individual.updateNondominatedSolutions(this.nonDominatedSet, this.population);
			this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet);

			// Log
			//if (this.meter != null) {
			//	this.meter.stopTimer("generation/"+this.currentGeneration+"/duration");
			//}
			Individual.logGeneration(child, this.getOpConfig().getExpName(), "gen1", true);
		}

		// Get Unique results and create log information
		this.nonDominatedSet = Individual.getUniquePhenotypeIndividuals(this.nonDominatedSet);
		Functions.log("Execution of SMS-EMOA finished!");
		Functions.log("Number of created Individuals: " + Individual.getIdCounter() + ".");
		Functions.log("Size of nondominated set: " + this.nonDominatedSet.size() + ".");
		Individual.logGeneration(this.nonDominatedSet, this.getOpConfig().getExpName(), "result", false);
		//Individual.exportObjectiveValuesToCSV(this.nonDominatedSet, "./test.csv");
		//for (int i = 0; i < Math.min(nonDominatedSet.size(),20); i++) Functions.log(nonDominatedSet.get(i).toString());

		// Finish (measure and return)
		this.measureObjectiveValues(this.nonDominatedSet);
		return this.nonDominatedSet;
	}


	@Override
	public String getName() {
		return "SMS-EMOA";
	}

}
