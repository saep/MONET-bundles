package com.github.monet.algorithms.ea.operator;

import java.util.ArrayList;
import java.util.List;

import com.github.monet.algorithms.ea.individual.Individual;

/**
 * Abstract class for an evaluation algorithm.
 *
 * @author Sven Selmke
 *
 */
public abstract class Evaluator extends Operator {

	/**
	 * Evaluate given population and sets the fitness of the individuals. Post:
	 * Fitness values of given individuals are set
	 *
	 * @param population
	 *            population to be evaluated
	 */
	public abstract void evaluateFitness(List<Individual> population);


	/**
	 * Evaluate given populations and sets the fitness of the individuals. Post:
	 * Fitness values of given individuals are set
	 *
	 * @param population
	 *            population to be evaluated
	 * @param archive
	 *            second set of individuals
	 */
	public void evaluateFitness(List<Individual> population, List<Individual> archive) {
		List<Individual> all = new ArrayList<Individual>(population);
		all.addAll(archive);
		this.evaluateFitness(all);
	}

}
