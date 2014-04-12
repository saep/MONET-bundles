package com.github.monet.algorithms.ea.impl.algorithm;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.util.Functions;

public class RandomSearch extends EvolutionaryAlgorithm {

	// Parameters
	private int popSize;
	private PhenotypeMapping mapping;
	private Creator creator;

	/**
	 * Configuration of the Random-Search
	 */
	@Override
	public boolean configure(Map<String,Object> params) {
		this.popSize       = Functions.getParam(params, "popSize", Integer.class, 50);
		this.mapping       = Functions.getParam(params, "mapping", PhenotypeMapping.class, null);
		this.creator       = Functions.getParam(params, "creator", Creator.class, null);
		if (this.mapping == null || this.creator == null) {
			Functions.log("Configuration of RandomSearch failed. Not all parameters set.", Functions.LOG_ERROR);
			return false;
		}

		return true;
	}

	@Override
	public String getName() {
		return "RandomSearch";
	}

	@Override
	public List<Individual> execute() {
		// Create the population
		Functions.log("RandomSearch: Creating random solutions...");
		List<Individual> result = this.creator.createPopulation(this.popSize);
		Functions.log("RandomSearch: Population created! Now creating solutions (phenotypes)...");
		this.mapping.createPhenotypes(result);
		Functions.log("RandomSearch: Solutions created! Now extracting non-dominated individuals...");
		Individual.logGeneration(result, this.getOpConfig().getExpName(), "initial", false);

		// Extract non-dominated individuals
		result = Individual.getNondominatedSolutions(result);
		Functions.log("RandomSearch: Finished! " + result.size() + " non-dominated individuals found.");
		Individual.logGeneration(result, this.getOpConfig().getExpName(), "result", false);

		return result;
	}

}
