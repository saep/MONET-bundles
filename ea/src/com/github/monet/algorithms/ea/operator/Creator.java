package com.github.monet.algorithms.ea.operator;

import java.util.ArrayList;
import java.util.List;

import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.individual.Individual;

/**
 * 0-ary search Operator.
 *
 * @author Sven Selmke
 *
 */
public abstract class Creator extends SearchOperator {

	/**
	 * Encoding object used to create new genotypes. This can be setup in the
	 * configuration method (optional).
	 */
	protected Encoding encoding;


	/**
	 * 0-ary search Operator. Create a new Genotype.
	 *
	 * @param encoding
	 *            creating a new genotype requires an encoding which contains
	 *            information such as the length of the genotype
	 *
	 * @return a new Genotype created using the given encoding
	 */
	public abstract Genotype create();


	/**
	 * Creates a new genotype and adds it to a new individual.
	 *
	 * @param encoding
	 *            encoding used to create the genotype
	 *
	 * @return Individual containing the newly created genotype
	 */
	public Individual createIndividual() {
		return new Individual( this.create() );
	}


	/**
	 * Create a new Population (Individuals only contain a new genotype).
	 *
	 * @param size
	 *            number of individuals (genotypes) to be created
	 * @param encoding
	 *            encoding used to create the genotypes
	 *
	 * @return List of individuals created using the given encoding
	 */
	public List<Individual> createPopulation(int size) {
		List<Individual> population = new ArrayList<Individual>();
		for (int i = 0; i < size; i++) {
			Individual ind = this.createIndividual();
			population.add(ind);
		}
		return population;
	}


	/**
	 * Getter method for the encoding object used to create new genotypes. Can
	 * be null if it's not required by the creator.
	 */
	public Encoding getEncoding() {
		return encoding;
	}


	/**
	 * Set a new encoding for the creator. This usually shouldn't be needed.
	 */
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

}
