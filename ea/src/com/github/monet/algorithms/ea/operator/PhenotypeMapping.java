package com.github.monet.algorithms.ea.operator;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.individual.Phenotype;

/**
 * Genotype-Phenotype-Mapping.
 *
 * @author Sven Selmke
 *
 */
public abstract class PhenotypeMapping extends Operator {

	/**
	 * Creates a phenotype from given genotype
	 *
	 * @param g
	 *            genotype to create a phenotype from
	 *
	 * @return a phenotype corresponding to the given genotype
	 */
	public abstract Phenotype createPhenotype(Genotype g);


	/**
	 * Set parameters for the algorithm.
	 *
	 * @param parameters
	 *            parameters used to configure the mapping
	 */
	public boolean configure(Map<String,Object> parameters) {
		return true;
	}


	/**
	 * Creates phenotypes for given individuals
	 *
	 * @param individuals
	 *            phenotypes will be created for (and stored in!) given
	 *            individuals
	 *
	 */
	public void createPhenotypes(List<Individual> individuals) {
		for (Individual ind : individuals) {
			Phenotype p = this.createPhenotype(ind.getGenotype());
			ind.setPhenotype(p);
		}
	}


	/**
	 * Creates phenotypes for a single individual
	 *
	 * @param individual
	 *            phenotypes will be created for (and stored in!) given
	 *            individuals
	 *
	 */
	public void createPhenotypes(Individual ind) {
		Phenotype p = this.createPhenotype(ind.getGenotype());
		ind.setPhenotype(p);
	}

}
