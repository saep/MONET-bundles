package com.github.monet.algorithms.ea.impl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
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

public class SPEA2 extends EvolutionaryAlgorithm {

	// Generation variables
	private List<Individual> population;
	private List<Individual> nonDominatedPopulation;
	private List<Individual> archive;
	private List<Individual> nonDominatedArchive;
	private int currentGeneration;

	// Parameters
	private int popSize;
	private int archiveSize;
	private PhenotypeMapping mapping;
	private Creator creator;
	private Mutator mutator;
	private Recombinator recombinator;
	private TerminatorSimple terminator;
	private Evaluator evaluator;
	private Selector selector;

	@Override
	public String getName() {
		return "SPEA-2";
	}

	@Override
	public boolean configure(Map<String, Object> params) {
		this.popSize = Functions.getParam(params, "popSize", Integer.class, 50);
		this.archiveSize = Functions.getParam(params, "archiveSize", Integer.class, 50);
		this.mapping = Functions.getParam(params, "mapping", PhenotypeMapping.class, null);
		this.creator = Functions.getParam(params, "creator", Creator.class, null);
		this.mutator = Functions.getParam(params, "mutator", Mutator.class, null);
		this.recombinator = Functions.getParam(params, "recombinator", Recombinator.class, null);
		this.terminator = Functions.getParam(params, "terminator", TerminatorSimple.class, null);
		this.evaluator = Functions.getParam(params, "evaluator", Evaluator.class, null);
		this.selector = Functions.getParam(params, "selector", Selector.class, null);
		this.archive = new ArrayList<Individual>(this.archiveSize);
		this.nonDominatedArchive = new ArrayList<Individual>();
		if (this.terminator == null || this.mutator == null || this.creator == null || this.mapping == null || this.archive == null
				|| this.recombinator == null || this.evaluator == null || this.selector == null) {
			Functions.log("Configuration of SPEA-2 failed. Not all " + "parameters are initialized.", Functions.LOG_ERROR);
			return false;
		}
		return true;
	}

	@Override
	public List<Individual> execute() {
		Functions.log("Starting execution of SPEA-2.");
		long startingTime = System.currentTimeMillis();

		// Create initial population
		Functions.log("Creating initial population...");
		this.population = this.creator.createPopulation(this.popSize);
		this.mapping.createPhenotypes(this.population);
		this.evaluator.evaluateFitness(this.population);
		Functions.log("Initial population has been created.");
		Individual.logGeneration(this.population, this.getOpConfig().getExpName(), "initial", false);

		// Start main loop
		this.currentGeneration = 0;
		while (!(this.terminator.terminate(this.currentGeneration, System.currentTimeMillis() - startingTime, null, this.archive))) {
			this.currentGeneration++;

			// Log
			if (this.meter != null) {
				this.meter.startTimer("generation/" + this.currentGeneration + "/duration");
			}
			if (this.currentGeneration % 25 == 0) {
				Functions.log("Starting generation " + this.currentGeneration + " (" + this.nonDominatedArchive.size() + " non-dominated invdividuals).");
			}

			// Environmental Selection
			// Calculation of the archive for the next generation
			this.nonDominatedPopulation = Individual.getNondominatedSolutions(this.population);
			this.nonDominatedArchive    = Individual.getNondominatedSolutions(this.archive);

			// Add non dominated solutions to archive
			List<Individual> nextArchive = new ArrayList<Individual>(this.archiveSize);
			nextArchive.addAll(this.nonDominatedPopulation);
			nextArchive.addAll(this.nonDominatedArchive);

			// actual archiveSize != this.archiveSize?
			if (nextArchive.size() < this.archiveSize) {
				// Get dominated solutions
				List<Individual> dominated = new ArrayList<Individual>(this.population);
				dominated.addAll(this.archive);
				dominated.removeAll(this.nonDominatedPopulation); // PERFORMANCE
				dominated.removeAll(this.nonDominatedArchive);
				//System.out.println("NextArchive size: " + nextArchive.size() + " ("+this.nonDominatedPopulation.size()+"+"+this.nonDominatedArchive.size()+"), wanted: " + this.archiveSize);
				//System.out.println("Dominated size: " + dominated.size());
				// Add best to archive
				this.fillArchive(dominated, nextArchive);
			}

			if (nextArchive.size() > this.archiveSize) {
				this.truncateArchive(nextArchive);
			}

			this.archive = nextArchive;

			// Mating Selection
			List<Individual> matingPool = this.selector.select(this.archive, null, this.popSize);

			// Variation of mating pool
			for (int i = 0; i < this.popSize; i++) {
				Individual indi = matingPool.get(i);
				Individual mate = EaRandom.getRandomElement(matingPool);
				Individual tmp = this.recombinator.recombinateIndividuals(indi, mate, matingPool);
				tmp = this.mutator.mutateIndividual(tmp);
				this.population.set(i, tmp); // FIXED
			}

			// Calculate Fitness for population and archive
			this.mapping.createPhenotypes(this.archive);
			this.mapping.createPhenotypes(this.population);
			this.evaluator.evaluateFitness(this.population, this.archive);

			// Log
			if (this.meter != null) {
				this.meter.stopTimer("generation/"+this.currentGeneration+"/duration");
			}
			Individual.logGeneration(this.archive, this.getOpConfig().getExpName(), "gen"+this.currentGeneration, false);

		}

		List<Individual> nonDominated = Individual.getNondominatedSolutions(this.archive);
		nonDominated = Individual.getUniquePhenotypeIndividuals(nonDominated);
		Functions.log("Execution of SPEA finished!");
		Functions.log("Number of created Individuals: " + Individual.getIdCounter() + ".");
		Functions.log("Size of nondominated set: " + nonDominated.size() + ".");
		Individual.logGeneration(this.archive, this.getOpConfig().getExpName(), "result", false);

		// Log results
		//Individual.exportObjectiveValuesToCSV(nonDominated, "./logs/test.csv");
		//for (int i = 0; i < Math.min(nonDominated.size(),20); i++) Functions.log(nonDominated.get(i).toString());
		/*
		for (Individual ind : nonDominated) {
			Functions.log("Result-Individual " + ind.getId() + " (Fitness: " + ind.getFitness() + ")");
			SPEA2Genotype g = (SPEA2Genotype) ind.getGenotype();
			for (Node n : g.getNodes()) {
				Functions.log(n.toString());
			}
		}
		*/

		// Measure results and return
		this.measureObjectiveValues(nonDominated);
		return nonDominated;
	}

	/**
	 * Called when archive is too small.
	 * Add best individuals from given population and archive (Using fitness).
	 */
	private void fillArchive(List<Individual> dominatedIndividuals, List<Individual> archive) {
		int count = this.archiveSize - archive.size();
		// Get comparator
		Individual.IndividualBestFitnessComparator comp = new Individual.IndividualBestFitnessComparator();
		// Add best dominated(!) individuals
		if (dominatedIndividuals.size() > 0) {
			Collections.sort(dominatedIndividuals, comp); // FIXED
			for (int i = 0; i < count; i++) {
				archive.add(dominatedIndividuals.get(i % dominatedIndividuals.size()));
			}
		// No dominated individuals given?
		} else {
			Functions.log("No dominated individuals to fill the archive. Copying individuals instead.");
			Collections.sort(archive, comp); // FIXED
			for (int i = 0; i < count; i++) {
				archive.add(archive.get(i));
			}
		}
	}

	/**
	 * Called when archive is too large.
	 * Remove individuals that are close to other individuals.
	 */
	private void truncateArchive(List<Individual> archive) {
		while (archive.size() > this.archiveSize) {
			// Which is the Individual with the lowest distance
			int indiceDel = 0;
			for (int i = 1; i < archive.size(); i++) {
				if (archive.get(i).getSortedDistance().first().dist <= archive.get(indiceDel).getSortedDistance().first().dist) {
					indiceDel = i;
				}
			}
			archive.remove(indiceDel);
		}
	}

}
