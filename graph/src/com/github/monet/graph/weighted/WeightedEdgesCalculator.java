package com.github.monet.graph.weighted;

import java.util.Objects;

import com.github.monet.graph.interfaces.CostCalculator;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

/**
 *
 *
 */
public class WeightedEdgesCalculator<N extends Node, E extends Edge, G extends Graph<N, E, G>>
		implements CostCalculator<N, E, G, Weight> {

	GraphElementWeightAnnotator<E> weights;

	public WeightedEdgesCalculator(GraphElementWeightAnnotator<E> weights) {
		this.weights = weights;
	}

	@Override
	public Weight calculateCosts(G graph) {
		return weights.sum(graph.getAllEdges());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WeightedEdgesCalculator) {
			return this.weights.equals(((WeightedEdgesCalculator) o).weights);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.weights);
		return hash;
	}
}
