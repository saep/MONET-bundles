package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.STSPGenotype;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.interfaces.Node;

/**
 * Implementation of a simple recombination algorithm.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class SPEA2Recombinator extends Recombinator {

	private double probability;

	@Override
	public boolean configure(Map<String,Object> params) {
		this.probability = Functions.getParam(params, "recombinatorProbability", Double.class, 0.5d);
		return true;
	}

	@Override
	public Individual recombinateIndividuals(Individual parentA, Individual parentB, List<Individual> pool) {

		// Get parent genotypes
		STSPGenotype g1 = (STSPGenotype) parentA.getGenotype();
		STSPGenotype g2 = (STSPGenotype) parentB.getGenotype();

		// Create new genotype
		STSPGenotype newGenotype = (STSPGenotype)g1.copy();
		Individual newIndividual = new Individual(newGenotype);

		// if no recombination, simply return first parent
		if (!EaRandom.nextBoolean(this.probability)) {
			return newIndividual;
		}

		// Check if crossover is possible
		// If not then try again with new individual selected from mating pool (ONLY IF NOT NULL!)
		int g1Index, g2Index;
		int tries    = 0;
		int maxTries = 10;
		do {
			tries++;
			g1Index = EaRandom.getRandomNumber(0, g1.getNodes().size());
			g2Index = g2.getNodes().indexOf( g1.getNodes().get(g1Index) );
			if (g2Index == -1 && pool != null) {
				g2 = (STSPGenotype) EaRandom.getRandomElement(pool).getGenotype();
			} else {
				break;
			}
		} while (tries < maxTries);

		// Failed to do crossover
		if (g2Index == -1) {
			//if (pool != null) {
			//	Functions.log("Skipping SPEA2 recombination after " + tries + " tries.", Functions.LOG_PRINT);
			//} else {
			//	Functions.log("Skipping SPEA2 recombination after " + tries + " try (no pool for retries available).", Functions.LOG_PRINT);
			//}
			return parentA;
		}

		List<Node> nodeIds = new ArrayList<Node>();
		// Take first part of g1 (excluding the crossover node)
		for (int i = 0; i < g1Index; i++) {
			nodeIds.add(g1.getNodes().get(i));
		}
		// Take second part of g2
		for (int i = g2Index; i < g2.getNodes().size(); i++) {
			nodeIds.add(g2.getNodes().get(i));
		}
		// Set new IDs
		newGenotype.setNodes(nodeIds);

		// Return new individual
		return newIndividual;
	}

	@Override
	public String getName() {
		return "SPEA2-Recombinator";
	}

}
