package com.github.monet.algorithms.ea.operator;

import java.util.List;

import com.github.monet.algorithms.ea.individual.Individual;

/**
 * 2-ary search Operator.
 *
 * @author Sven Selmke
 *
 */
public abstract class Recombinator extends SearchOperator {

	/**
	 * Creates a new individual given two parents
	 *
	 * @param parentA
	 *            first parent
	 * @param parentB
	 *            second parent
	 * @param pool
	 *            some algorithms might need other individuals from a mating
	 *            pool as well
	 *
	 * @return new individual containing a genotype created using the
	 *         recombination algorithm
	 */
	public abstract Individual recombinateIndividuals(Individual parentA, Individual parentB, List<Individual> pool);

}
