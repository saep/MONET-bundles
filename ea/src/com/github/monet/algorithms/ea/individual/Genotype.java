package com.github.monet.algorithms.ea.individual;


/**
 * Genotype class for genotypes. Genotypes are used in order to search for
 * better solutions. An object of this class represents a single genotype of a
 * single individual. Shared information for all individuals of a population are
 * kept in Encoding objects.
 *
 * @author Sven Selmke
 *
 * @see Encoding
 *
 */
public abstract class Genotype {
	private Encoding encoding;

	/**
	 * Creates a deep copy of the genotype. If a new genotype is created using
	 * this method, modifications should not incluence the old genotype.
	 * Example: An individual is about to be mutated. Therefore, a copy of the
	 * old individual is created and contains a copy(!) of the old genotype. The
	 * new genotype is modified by a mutation algorithm, but both individuals
	 * are kept in the population.
	 *
	 * @return a copy of this genotype
	 */
	public abstract Genotype copy();


	/**
	 * Checks if two genotypes are equal. Since genotypes are usually created
	 * randomly, it is quite likely that two genotypes containing the same data
	 * are generated. Because of that, a more sophisticated equals method might
	 * be needed.
	 *
	 * @return boolean
	 */
	@Override
	public abstract boolean equals(Object o);


	public Encoding getEncoding() {
		return encoding;
	}
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

}
