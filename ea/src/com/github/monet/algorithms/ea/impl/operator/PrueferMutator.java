package com.github.monet.algorithms.ea.impl.operator;

import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.PrueferEncoding;
import com.github.monet.algorithms.ea.impl.individual.PrueferGenotype;
import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Implementation of a simple uniform mutation algorithm.
 *
 * @author Sven Selmke
 *
 */
public class PrueferMutator extends Mutator {

	private double probability;

	@Override
	public String getName() {
		return "Uniform-Mutation";
	}

	@Override
	public boolean configure(Map<String,Object> params) {
		this.probability = Functions.getParam(params, "mutatorProbability", Double.class, 0.5d);
		return true;
	}

	@Override
	public Genotype mutate(Genotype genotype) {
		Genotype copy = genotype.copy();
		Encoding enc  = copy.getEncoding();

		if (copy instanceof PrueferGenotype) {
			PrueferGenotype newGenotype = (PrueferGenotype)copy;
			PrueferEncoding prueferEncoding = (PrueferEncoding)enc;
			// Random mutation at each index
			for (int i = 0; i < newGenotype.getLength(); i++) {
				if (EaRandom.nextBoolean(this.probability)) {
					Integer mutatedSymbol = EaRandom.getRandomElement(prueferEncoding.getValidSymbols());
					newGenotype.setSymbol(i, mutatedSymbol);
				}
			}
			return newGenotype;
		}

		return null;
	}

}
