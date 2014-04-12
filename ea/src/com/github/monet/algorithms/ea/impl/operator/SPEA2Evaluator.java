package com.github.monet.algorithms.ea.impl.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.individual.IndividualDistTupel;
import com.github.monet.algorithms.ea.operator.Evaluator;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Evaluator corresponding to the strategy of the SSSP SPEA2 algorithm.
 * 	1. Calculate Strength of all individuals
 *  2. Calculate raw fitness R(i)
 *  3. Calculate Density D(i)
 *  4. Calculate Fitness F(i) = D(i) + R(i)
 *  (smaller fitness is better)
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class SPEA2Evaluator extends Evaluator {

	@Override
	public String getName() {
		return "SPEA2-Evaluator";
	}

	@Override
	public boolean configure(Map<String,Object> params) {
		// This Evaluator creates small fitness values for "better" individuals
		Functions.setNewParam(params, "fitnessMaximization", false);
		Individual.minimizationOfFitness = true;
		return true;
	}

	@Override
	public void evaluateFitness(List<Individual> population) {
		Functions.log("Starting fitness-evaluation for " + population.size() + " individuals...", Functions.LOG_PRINT);

		// Calculate Strength
		Individual.calculateStrength(population);

		// Calculate Raw Fitness (temporary save in "Result" variable)
		// Raw Fitness = Strength of all individuals that dominate the current one
		for (Individual i1 : population) {
			double rawFitness = 0;
			for (Individual i2 : population) {
				if (i2.dominates(i1)) {
					rawFitness += i2.getStrength();
				}
			}
			i1.setResult(rawFitness);
		}

		// Calculate all distances
		// For each individual: save sorted distances to all other individuals
		// 1. HashMap: "Individual -> TreeSet" with distances
		// 2. TreeSet: Contains tupels (Distance, other Individual)
		HashMap<Individual, TreeSet<IndividualDistTupel>> distanceMap = new HashMap<Individual, TreeSet<IndividualDistTupel>>();
		for (Individual i1 : population) {
			TreeSet<IndividualDistTupel> sortedDistances = new TreeSet<IndividualDistTupel>();
			for (Individual i2 : population) {
				//double dist = i1.getPhenotype().distance(i2.getPhenotype());
				double dist = Functions.getDistance(i1.getObjectiveValues(), i2.getObjectiveValues());
				// NOTE: IF THERE'S ALREADY AN INDIVIDUAL IN THE SET FOR WHICH THE COMPARATOR RETURNS 0, THEN IT WILL _NOT_ BE ADDED!
				sortedDistances.add( new IndividualDistTupel(i2,dist) );
			}
			distanceMap.put(i1, sortedDistances);
			// Save it also inside the individual
			i1.setSortedDistance(sortedDistances);
		}

		// Calculate Density
		// Calculate Fitness
		int k = (int) Math.floor( Math.sqrt(2 * population.size()) ); // population.size() == archive.size()
		for (Individual i1 : population) {
			// Get the k-th element
			IndividualDistTupel kth = null;
			TreeSet<IndividualDistTupel> sortedDistances = distanceMap.get(i1);
			int counter = 0;
			for (IndividualDistTupel tupel : sortedDistances) {
				counter++;
				if (counter == k) {
					kth = tupel;
					break;
				}
			}
			// Calculate Density
			i1.setSharing( 1 / (kth.dist + 2) );
			// Calculate Fitness
			i1.setFitness( i1.getResult() + i1.getSharing() );
		}

		Functions.log("Finished fitness-evaluation.", Functions.LOG_PRINT);
	}

}
