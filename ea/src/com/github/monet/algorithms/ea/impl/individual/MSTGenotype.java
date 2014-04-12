package com.github.monet.algorithms.ea.impl.individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import com.github.monet.algorithms.ea.individual.Genotype;

import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Node;

/**
 * MST genotype consisting of an edge-list
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class MSTGenotype extends Genotype {
	private List<Edge> edges;
	private HashMap<Node, List<Node>> adjacencyList;
	private HashMap<Node, Integer> degreeCounter;

	public MSTGenotype(){
		this.edges = new ArrayList<Edge>();
		this.degreeCounter = new HashMap<>();
	}

	@Override
	public Genotype copy() {
		MSTGenotype copy = new MSTGenotype();
		List<Edge> copied = new ArrayList<Edge>(this.edges);
		copy.setEdges(copied);
		copy.setEncoding(this.getEncoding());
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MSTGenotype)) {
			return false;
		}
		MSTGenotype other = (MSTGenotype)o;
		for (Edge i : this.edges) {
			if (!other.getEdges().contains(i)) {
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

	public HashMap<Node, List<Node>> getAdjacencyList() {
		return this.adjacencyList;
	}
	public void setAdjacencyList(HashMap<Node, List<Node>> adjL) {
		this.adjacencyList = adjL;
	}

	public HashMap<Node, Integer> getDegreeCounter() {
		return this.degreeCounter;
	}
	public void setDegreeCounter(HashMap<Node, Integer> degreeCounter) {
		this.degreeCounter = degreeCounter;
	}

	@Override
	public String toString() {
		return "Direct MST-Genotype: Number of Edges = " + edges.size();
	}

}
