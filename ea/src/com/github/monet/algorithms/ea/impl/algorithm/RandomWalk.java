package com.github.monet.algorithms.ea.impl.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.util.Functions;

public class RandomWalk extends EvolutionaryAlgorithm {

	// Parameters
	private PhenotypeMapping mapping;
	private Creator creator;
	private Mutator mutator;
	private int popSize;

	/**
	 * Configuration of the Random-Search
	 */
	@Override
	public boolean configure(Map<String,Object> params) {
		this.mapping       = Functions.getParam(params, "mapping", PhenotypeMapping.class, null);
		this.creator       = Functions.getParam(params, "creator", Creator.class, null);
		this.mutator       = Functions.getParam(params, "mutator", Mutator.class, null);
		this.popSize       = Functions.getParam(params, "popSize", Integer.class, 50);
		if (this.mapping == null || this.creator == null || this.mutator == null) {
			Functions.log("Configuration of RandomWalk failed. Not all parameters set.", Functions.LOG_ERROR);
			return false;
		}

		return true;
	}

	@Override
	public String getName() {
		return "RandomWalk";
	}

	@Override
	public List<Individual> execute() {
		List<Individual> result = new ArrayList<Individual>(this.popSize);

		// Create initial individual
		Functions.log("RandomWalk: Creating random initial solution...");
		Individual individual = this.creator.createPopulation(1).get(0);
		result.add(individual);
		this.mapping.createPhenotypes(result);
		Individual.logGeneration(result, this.getOpConfig().getExpName(), "initial", false);

		// Mutate individual
		Functions.log("RandomWalk: Initial solution created! Now creating mutations...");
		for (int i = 0; i < this.popSize; i++) {
			Individual mutant = this.mutator.mutateIndividual(individual);
			this.mapping.createPhenotypes(mutant);
			Individual.logGeneration(mutant, this.getOpConfig().getExpName(), "gen1", true);
			result.add(mutant);
		}

		// Extract non-dominated individuals
		result = Individual.getNondominatedSolutions(result);
		Functions.log("RandomWalk: Finished! " + result.size() + " non-dominated individuals found.");
		Individual.logGeneration(result, this.getOpConfig().getExpName(), "result", false);

		return result;
	}

}
