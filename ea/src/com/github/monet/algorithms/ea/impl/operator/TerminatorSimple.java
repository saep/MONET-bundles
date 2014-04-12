package com.github.monet.algorithms.ea.impl.operator;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Terminator;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Class to manage termination criteria and to estimate the progress of the
 * current algorithm given some limited information
 *
 * @author Sven Selmke
 *
 */
public class TerminatorSimple extends Terminator {
	// Settings
	private int maxGenerations;
	private Long maxTime; // in ms
	private Double fitnessThreshold;
	private boolean maximization;
	private double[] dominationValues;
	private int constantFitnessGenLimit;

	// Variables
	private double lastFitness = 0;
	private int constantFor = -1; // number of generations the fitness has been constant

	public TerminatorSimple() {
		this.maxGenerations = 100;
		this.maxTime = new Long(1000*30);
		this.fitnessThreshold = new Double(1);
		this.maximization = false;
		this.dominationValues = null;

	}

	public TerminatorSimple(int maxGenerations, long maxTime, double fitnessThreshold) {
		this.maxGenerations = maxGenerations;
		this.maxTime = maxTime;
		this.fitnessThreshold = fitnessThreshold;
		this.maximization = false;
		this.dominationValues = null;

	}


	@Override
	public boolean configure(Map<String,Object> params) {
		this.maxGenerations          = Functions.getParam(params, "maxGenerations", Integer.class, 50);
		this.maxTime                 = Functions.getParam(params, "maxTime", Long.class, 60000l);
		this.fitnessThreshold        = Functions.getParam(params, "fitnessThreshold", Double.class, null);
		this.maximization            = Functions.getParam(params, "fitnessMaximization", Boolean.class, false);
		this.constantFitnessGenLimit = Functions.getParam(params, "constantFitnessGenLimit", Integer.class, -1);
		this.dominationValues        = null;
		return true;
	}


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
	public boolean terminate(int generation, long currentTime, Individual best, List<Individual> front) {

		// Cancel at specific generation
		if (generation > this.maxGenerations) {
			Functions.log("Termination criterion: Last generation reached.");
			return true;
		}

		// Check for time limit
		if (this.maxTime != null) {
			if (currentTime > this.maxTime) {
				Functions.log("Termination criterion: Time limit reached.");
				return true;
			}
		}

		// Good fitness reached
		if (best != null && this.fitnessThreshold != null) {
			if (best.getFitness() < fitnessThreshold && !this.maximization ||
				best.getFitness() > fitnessThreshold &&  this.maximization) {
				Functions.log("Termination criterion: Fitness threshold reached.");
				return true;
			}
		}

		// Domination of given values
		if (best != null && this.dominationValues != null) {
			if (Individual.dominates(best.getObjectiveValues(), this.dominationValues)) {
				Functions.log("Termination criterion: Individual dominating the given vector found.");
				return true;
			}
		}

		// Fitness converged
		if (this.constantFitnessGenLimit > 1) {
			// Get fitness
			double newFitness = 0;
			for (Individual i : front) newFitness += i.getFitness();
			// Check if it changed (first call: constantFor == -1, no last fitness available!)
			double eps = 1e-10;
			if (Math.abs(this.lastFitness - newFitness) < eps && this.constantFor != -1) {
				this.constantFor++;
			} else {
				this.constantFor = 1;
			}
			// constant fitness?
			if (this.constantFor >= this.constantFitnessGenLimit) {
				Functions.log("Termination criterion: Constant sum of fitness values for " + this.constantFitnessGenLimit + " generations.");
				return true;
			}
			// Set new fitness
			lastFitness = newFitness;
		}

		// Continue execution
		return false;
	}
	public boolean terminate(int generation, long currentTime, Individual best) {
		return this.terminate(generation, currentTime, best, null);
	}


	/**
	 * Tries to estimate the progress of the algorithm given some basic
	 * information
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
		// Return 1 at termination
		if (this.terminate(generation, currentTime, best))
			return 1;
		// Get progress
		double generationProgress = ((double)generation / this.maxGenerations);
		double timeProgress = ((double)currentTime / this.maxTime);
		return Math.max(generationProgress, timeProgress);
	}


	@Override
	public String getName() {
		return "Simple-Terminator";
	}

}
