package com.github.monet.algorithms.ea.operator;

import com.github.monet.algorithms.ea.individual.Individual;

public abstract class Terminator extends Operator {

	/**
	 * Checks if the termination criterion represented by this object has been
	 * reached. The parameters are available information about the current state
	 * of the algorithm.
	 *
	 * @param generation
	 *            current generation the algorithm arrived at
	 * @param currentTime
	 *            how long the algorithm has been running already
	 * @param best
	 *            best individual found so far (Note: individual with best
	 *            FITNESS value! NOT a list of non-dominated individuals)
	 *
	 * @return true if the algorithm is supposed to terminate now
	 */
	abstract public boolean terminate(int generation, long currentTime, Individual best);

	/**
	 * Tries to estimate the progress of the algorithm given some basic
	 * information.
	 *
	 * @param generation
	 *            current generation the algorithm arrived at
	 * @param currentTime
	 *            how long the algorithm has been running already
	 * @param best
	 *            best individual found so far (Note: individual with best
	 *            FITNESS value! NOT a list of non-dominated individuals)
	 *
	 * @return value in [0,1] representing the current progress
	 */
	public double getProgress(int generation, long currentTime, Individual best) {
		return 0;
	}

}
