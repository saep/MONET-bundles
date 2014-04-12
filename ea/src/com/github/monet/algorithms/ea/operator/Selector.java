package com.github.monet.algorithms.ea.operator;

import java.util.List;

import com.github.monet.algorithms.ea.individual.Individual;

/**
 * abstract class for a selection algorithms.
 *
 * @author Sven Selmke
 *
 */
public abstract class Selector extends Operator {

	/**
	 * Specifies whether to maximize or minimize the fitness
	 */
	protected boolean fitnessMaximization = false;

	/**
	 * Select individuals according to their fitness
	 *
	 * @param population
	 *            current population to select individuals from. It is up to the
	 *            algorithm whether individuals are selected from the offspring,
	 *            or from offspring and population
	 * @param offspring
	 *            list of offspring to select individuals from. It is up to the
	 *            algorithm whether individuals are selected from the offspring,
	 *            or from offspring and population
	 *
	 * @return list of selected individuals
	 */
	public abstract List<Individual> select(List<Individual> population, List<Individual> offspring, int amount);

	public boolean isFitnessMaximization() {
		return fitnessMaximization;
	}
	public void setFitnessMaximization(boolean fitnessMaximization) {
		this.fitnessMaximization = fitnessMaximization;
	}

}
