package com.github.monet.algorithms.ea.impl.individual;

import java.util.List;

import com.github.monet.algorithms.ea.individual.Phenotype;

import com.github.monet.graph.interfaces.Edge;

/**
 * Implementation of a Minimum-Spanning-Tree Phenotype. Given that the original
 * graph and edge weights are stored somewhere else, it is sufficient to
 * represent the MST using a list of edges.
 *
 * @author Sven Selmke
 *
 */
public class MSTPhenotype extends Phenotype {

	// Phenotype-Graph as a list of Edges (MST!)
	private List<Edge> edges;
	private int missingEdges = 0;


	@Override
	public double distance(Phenotype p) {
		// Distance to other Phenotype-Types is quite large
		if (!(p instanceof MSTPhenotype) || this.edges == null) {
			return 1;
		}

		MSTPhenotype other = (MSTPhenotype)p;

		// Special case: If there are no edges in this phenotype
		if (this.edges.size() == 0) {
			return (other.getEdges().size() == 0) ? 0 : 1;
		}

		// Simple distance:
		// for each edge in this graph check if it is contained in the other graph as well.
		double distance = 0;
		for (Edge e1 : this.getEdges()) {
			if (!other.getEdges().contains(e1)) { // FIXED
				distance += 1;
			}
		}

		// map distance to [0,1]
		distance = distance / this.edges.size();
		assert(distance >= 0 && distance <= 1);

		return distance;
	}


	@Override
	public String toString() {
		if (this.edges == null) return "";
		String result = "";
		for (int i = 0; i < this.edges.size(); i++) {
			result += this.edges.get(i).toString();
			if (i < this.edges.size()-1) {
				result += ", ";
			}
		}
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		// Check type
		if (!(obj instanceof MSTPhenotype)) {
			return false;
		}
		MSTPhenotype other = (MSTPhenotype)obj;
		// Check if object is the same
		// (better performance than always checking all edges!)
		if (this == other) {
			return true;
		}
		// Check all edges
		// (Both MSTs contain the same amount of edges!)
		for (Edge e : this.edges) {
			if (!other.getEdges().contains(e)) {
				return false;
			}
		}
		return true;
	}


	public List<Edge> getEdges() {
		return edges;
	}
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	public int getMissingEdges() {
		return missingEdges;
	}
	public void setMissingEdges(int missingEdges) {
		this.missingEdges = missingEdges;
	}

}
