package com.github.monet.algorithms.ea.util;

import java.util.Map;

/**
 * Nameable interface used to name algorithms, operators and strategies.
 *
 * @author Sven Selmke
 *
 */
public interface Nameable {

	/**
	 * Return the name of the class the object belongs to as a readable string
	 * e.g. "Pr�fer-Nummer" instead of "PrueferNumber"
	 */
	public String getName();


	/**
	 * Set parameters for the operator. (This way no parameters have to be used
	 * in the mutation/recombination/... methods)
	 *
	 * @param parameters
	 *            parameters used to configure the operator
	 */
	public boolean configure(Map<String,Object> parameters);

}
