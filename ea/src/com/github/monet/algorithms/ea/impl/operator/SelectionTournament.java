package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

public class SelectionTournament extends Selector {

	private int rounds;

	@Override
	public String getName() {
		return "Tournament-Selection";
	}

	@Override
	public boolean configure(Map<String,Object> params) {
		this.fitnessMaximization = Functions.getParam(params, "fitnessMaximization", Boolean.class, false);
		this.rounds = Functions.getParam(params, "numTournamentRounds", Integer.class, 1);
		return true;
	}

	@Override
	public List<Individual> select(List<Individual> population, List<Individual> offspring, int amount) {
		//boolean fitnessMaximization = false;

		// Create list containing population and offspring
		List<Individual> individuals = new ArrayList<Individual>(population);
		if (offspring != null)
			individuals.addAll(offspring);
		if (individuals.size() == 0)
			return new ArrayList<Individual>();

		// Select individuals
		List<Individual> selectedIndividuals = new ArrayList<Individual>();
		for (int i = 0; i < amount; i++) {
			// Execute a single tournament
			Individual winner = EaRandom.getRandomElement(individuals);
			for (int j = 0; j < this.rounds; j++) {
				Individual contestant = EaRandom.getRandomElement(individuals);
				if (contestant.isBetterThan(winner)) { // FIXED
					winner = contestant;
				}
			}
			selectedIndividuals.add(winner);
		}

		assert(selectedIndividuals.size() == amount) : ""+selectedIndividuals.size()+"!="+amount+".";
		return selectedIndividuals;
	}

}
