package com.github.monet.algorithms.ea.impl.individual;

import java.util.List;

import com.github.monet.algorithms.ea.individual.Phenotype;

import com.github.monet.graph.interfaces.Edge;


/**
 * SSSP Phenotype consisting of an edge-list
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class STSPPhenotype extends Phenotype {

	// Phenotype-Graph as a list of Edges
	private List<Edge> edges;


	@Override
	public boolean equals(Object o) {
		// Check type
		if (!(o instanceof STSPPhenotype)) {
			return false;
		}
		STSPPhenotype other = (STSPPhenotype)o;

		// Check if object is the same
		// (better performance than always checking all edges!)
		if (this == other) {
			return true;
		}

		// Check all edges
		if (this.edges.size() != other.getEdges().size()) {
			return false;
		}
		for (Edge e : this.edges) {
			if (!other.getEdges().contains(e)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public double distance(Phenotype p) {
		// Distance to other Phenotype-Types is quite large
		if (!(p instanceof STSPPhenotype)) {
			return 1;
		}

		// Simple distance:
		// for each edge in this graph check if it is contained in the other graph as well.
		// NOTE: This implementation is not commutative!
		STSPPhenotype other = (STSPPhenotype)p;
		double distance = 0;
		for (Edge e1 : this.getEdges()) {
			if (!other.getEdges().contains(e1)) {
				distance += 1;
			}
		}

		// map distance to [0,1]
		distance = distance / this.edges.size();

		// penalty for different sizes
		//distance = distance + Math.abs(this.getEdges().size() - other.getEdges().size()) / Math.max(this.edges.size(), other.getEdges().size()); // FIXED
		//distance = Math.min(distance, 1);

		assert(distance >= 0 && distance <= 1);
		return distance;
	}


	public List<Edge> getEdges() {
		return edges;
	}
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

}
