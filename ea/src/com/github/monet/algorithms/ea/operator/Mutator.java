package com.github.monet.algorithms.ea.operator;

import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.individual.Individual;

/**
 * 1-ary search Operator.
 *
 * @author Sven Selmke
 *
 */
public abstract class Mutator extends SearchOperator {

	/**
	 * Mutate the given Genotype. THIS METHOD SHOULD ALWAYS WORK ON A COPY OF
	 * THE GIVEN GENOTYPE.
	 *
	 * @param genotype
	 *            genotype to mutate
	 *
	 * @return a new genotype
	 */
	public abstract Genotype mutate(Genotype genotype);


	/**
	 * Mutate the genotype of the given Individual and create a NEW individual
	 *
	 * @param individual
	 *            individual whose genotype will be mutated
	 *
	 * @return a NEW individual containing the modified genotype
	 */
	public Individual mutateIndividual(Individual individual) {
		Genotype newGenotype = this.mutate( individual.getGenotype() );
		Individual newIndividual = new Individual(newGenotype);
		return newIndividual;
	}

}
