package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;


/**
 * Roulette-Wheel-Selection, also known as Fitness-Proportionate-Selection.
 *
 * @author Sven Selmke
 *
 */
public class SelectorRouletteWheel extends Selector {

	@Override
	public boolean configure(Map<String,Object> params) {
		this.fitnessMaximization = Functions.getParam(params, "fitnessMaximization", Boolean.class, false);
		return true;
	}

	@Override
	public List<Individual> select(List<Individual> population, List<Individual> offspring, int amount) {

		// Create list containing population and offspring
		List<Individual> individuals = new ArrayList<Individual>(population);
		if (offspring != null)
			individuals.addAll(offspring);
		if (individuals.size() == 0)
			return new ArrayList<Individual>();

		// Maximum fitness value (so we do not generate infinity while adding up the fitness values)
		double maxAllowedFitness = (Double.MAX_VALUE / amount);

		// Collect fitness values and get max and min fitness values
		double[] fitnessArr = new double[individuals.size()];
		double maxFitness = individuals.get(0).getFitness();
		double minFitness = individuals.get(0).getFitness();
		double totalFitness = 0;
		for (int i = 0; i < fitnessArr.length; i++) {
			// Get fitness (includes an upper bound)
			double fit = individuals.get(i).getFitness();
			if (Double.isNaN(fit) || Double.isInfinite(fit) || fit > maxAllowedFitness) {
				fit = maxAllowedFitness;
			}
			// Set fitness values
			fitnessArr[i] = fit;
			totalFitness += fit;
			if (fit > maxFitness) maxFitness = fit;
			if (fit < minFitness) minFitness = fit;
		}
		if (maxFitness == minFitness) maxFitness += 0.1;

		// Transform the array into a cumulative distribution function
		// (The i-th element is the sum of all the previous elements and itself)
		double cumulativeProb = 0;
		for (int i = 0; i < fitnessArr.length; i++) {
			// In case of fitness minimization: take an inverse value as selection probability
			if (this.isFitnessMaximization()) {
				cumulativeProb += fitnessArr[i];
			} else {
				cumulativeProb += (totalFitness - fitnessArr[i]) / (maxFitness - minFitness); // (maxFitness - fitnessArr[i]) would lead to 0% chance for the worst individual
			}
			fitnessArr[i] = cumulativeProb;
		}

		// Select individuals
		List<Individual> selectedIndividuals = new ArrayList<Individual>();
		for (int i = 0; i < amount; i++) {
			double r = EaRandom.getRandomDouble(0, cumulativeProb);
			for (int j = 0; j < fitnessArr.length; j++) {
				if (j == 0) {
					if (fitnessArr[j] >= r)
						selectedIndividuals.add(individuals.get(j));
				} else {
					if (fitnessArr[j-1] < r && fitnessArr[j] >= r)
						selectedIndividuals.add(individuals.get(j));
				}
			}
		}

		assert(selectedIndividuals.size() == amount) : ""+selectedIndividuals.size()+"!="+amount+".";
		return selectedIndividuals;
	}


	@Override
	public String getName() {
		return "Roulette-Wheel-Selection";
	}

}
