package com.github.monet.algorithms.ea.impl.operator;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.PrueferGenotype;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Implementation of a simple uniform recombination algorithm.
 *
 * @author Sven Selmke
 *
 */
public class PrueferRecombinator extends Recombinator {

	private double probability;

	@Override
	public boolean configure(Map<String,Object> params) {
		this.probability = Functions.getParam(params, "recombinatorProbability", Double.class, 0.5d);
		return true;
	}

	@Override
	public Individual recombinateIndividuals(Individual parentA, Individual parentB, List<Individual> pool) {
		PrueferGenotype g1 = (PrueferGenotype) parentA.getGenotype();
		PrueferGenotype g2 = (PrueferGenotype) parentB.getGenotype();

		// Create new genotype from parents
		PrueferGenotype newGenotype = (PrueferGenotype)g1.copy();
		for (int i = 0; i < newGenotype.getLength(); i++) {
			if (EaRandom.nextBoolean(this.probability)) {
				newGenotype.setSymbol(i, g2.getSymbol(i));
			}
		}

		// Return new individual
		Individual newIndividual = new Individual(newGenotype);
		return newIndividual;
	}


	@Override
	public String getName() {
		return "Pruefer-Recombinator";
	}

}
