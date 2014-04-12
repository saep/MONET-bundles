package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.STSPEncoding;
import com.github.monet.algorithms.ea.impl.individual.STSPGenotype;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;


/**
 * Implementation of a simple mutation algorithm. Chooses a random node of the
 * given path and replaces the second half of the path with a new path.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class SPEA2Mutator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Mutator {

	private double probability;
	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;

	@Override
	public String getName() {
		return "SPEA2-Mutator";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		this.probability = Functions.getParam(params, "mutatorProbability", Double.class, 0.5d);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		return true;
	}

	@Override
	public Genotype mutate(Genotype genotype) {

		if (genotype instanceof STSPGenotype) {
			STSPGenotype newGenotype = (STSPGenotype) genotype.copy();
			STSPEncoding enc         = (STSPEncoding) newGenotype.getEncoding();

			// if no recombination, simply return first parent
			if (!EaRandom.nextBoolean(this.probability)) {
				return newGenotype;
			}

			// Generate a new path from a random node g1 (of the given genotype) to the end-node
			int g1Index = EaRandom.getRandomNumber(0, newGenotype.getNodes().size());
			@SuppressWarnings("unchecked")
			List<Node> nodes = Functions.createRandomPath(problemAnnotatedGraph.getGraph(), (N)newGenotype.getNodes().get(g1Index), (N)enc.getEndNode());
			List<Node> nodeIds = new ArrayList<Node>();
			// Take first part of g1 (excluding the crossover node)
			for (int i = 0; i < g1Index; i++) {
				nodeIds.add(newGenotype.getNodes().get(i));
			}
			// Take second part of g2
			for (Node n : nodes) {
				nodeIds.add(n);
			}
			// Set new IDs
			newGenotype.setEncoding(enc);
			newGenotype.setNodes(nodeIds);
			return newGenotype;
		}

		return null;
	}

}
