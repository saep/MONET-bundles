package com.github.monet.algorithms.ea.individual;

import com.github.monet.algorithms.ea.operator.PhenotypeMapping;


/**
 * Super class for all phenotypes. A phenotype is the result of the
 * {@link PhenotypeMapping} executed for a given {@link Genotype}. It represents
 * a possible solution which can be simulated / evaluated in order to receive
 * objective values.
 *
 * @author Sven Selmke
 *
 */
public abstract class Phenotype {
	private boolean  isValid;
	private double[] objectiveValues;
	private double[] normalizedValues;
	private double[] invertedValues;

	/**
	 * Checks if two genotypes are equal. Since it is possible to create two
	 * equal phenotypes from two different genotypes, a custom euqlas method
	 * should always be available.
	 *
	 * @return boolean
	 */
	@Override
	public abstract boolean equals(Object o);


	/**
	 * Computes the distance between this phenotype and another phenotype. Note
	 * that the distance does not need to be symmetric, i.e. there might be some
	 * phenotypes a and b for which a.distance(b) != b.distance(a) holds.
	 *
	 * @param p
	 *            the other phenotype
	 *
	 * @return a value representing the distance between this Phenotype and the
	 *         given other phenotype
	 */
	public abstract double distance(Phenotype p);


	public double[] getObjectiveValues() {
		return objectiveValues;
	}
	public void setObjectiveValues(double[] objectiveValues) {
		this.objectiveValues = objectiveValues;
	}
	public double[] getNormalizedValues() {
		return normalizedValues;
	}
	public void setNormalizedValues(double[] normalizedValues) {
		this.normalizedValues = normalizedValues;
	}
	public double[] getInvertedValues() {
		return invertedValues;
	}
	public void setInvertedValues(double[] invertedValues) {
		this.invertedValues = invertedValues;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
