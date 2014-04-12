package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Evaluator;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Evaluator corresponding to Strategy II of the Pruefer-Algorithm. Each
 * successive Pareto-front will receive a certain dummy-fitness, lowered in each
 * iteration. Sharing is considered as well.
 *
 * Note that this algorithm does NOT have to be used with the Pruefer-Encoding.
 * It is named PrueferEvaluator because it was introduced/used in the same
 * Paper.
 *
 * @author Sven Selmke
 *
 */
public class PrueferEvaluator2 extends Evaluator {

	@Override
	public boolean configure(Map<String,Object> params) {
		// This Evaluator creates LARGER fitness values for "better" individuals
		// This has to be saved in the parameter-map for the selector-algorithm!
		Functions.setNewParam(params, "fitnessMaximization", true);
		Individual.minimizationOfFitness = false;
		return true;
	}

	@Override
	public void evaluateFitness(List<Individual> population) {
		Functions.log("Starting fitness-evaluation for " + population.size() + " individuals...", Functions.LOG_PRINT);
		double sigmaShare = 1;
		double dummyDecreaseFactor = 0.5;
		double dummyFitness = population.size() * 100;
		boolean useSharing = true;

		List<Individual> tmpPopulation = new ArrayList<Individual>(population);
		while (tmpPopulation.size() > 0) {
			// Get non-dominated set from current population and set a dummy fitness
			List<Individual> nonDominatedSet = Individual.getNondominatedSolutions(tmpPopulation);
			tmpPopulation.removeAll(nonDominatedSet);
			// Sharing (Consider entire population instead of non-dominated set? Not clear from paper)
			for (Individual ind1 : nonDominatedSet) {
				double nicheCount = 0;
				if (useSharing) {
					for (Individual ind2 : nonDominatedSet) {
						double dist = ind1.getPhenotype().distance(ind2.getPhenotype());
						nicheCount += (dist < sigmaShare) ? (1 - Math.pow(dist/sigmaShare, 2)) : 0; // FIXED
					}
					ind1.setSharing(nicheCount);
				}
				if (nicheCount > 0) {
					ind1.setFitness(dummyFitness / (nicheCount));
				} else {
					ind1.setFitness(dummyFitness);
				}
			}
			// Decrease dummy Fitness for next front
			dummyFitness = dummyFitness * dummyDecreaseFactor;
		}

		Functions.log("Finished fitness-evaluation.", Functions.LOG_PRINT);
	}

	@Override
	public String getName() {
		return "Pruefer-Evaluator-2";
	}

}

