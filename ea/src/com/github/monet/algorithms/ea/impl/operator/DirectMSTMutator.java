package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import com.github.monet.algorithms.ea.impl.individual.MSTEncoding;
import com.github.monet.algorithms.ea.impl.individual.MSTGenotype;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

/**
 * Implementation of a simple mutation algorithm.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class DirectMSTMutator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Mutator {

	private double probability;
	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
	private int maxDegree;

	@Override
	public String getName() {
		return "MST-Mutator";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		this.probability = Functions.getParam(params, "mutatorProbability", Double.class, 0.5d);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		this.maxDegree = Functions.getParam(params, "maxDegree", Integer.class, 0);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Genotype mutate(Genotype genotype) {

		if (genotype instanceof MSTGenotype) {
			MSTGenotype newGenotype = (MSTGenotype)genotype.copy(); // PERFORMANCE copy AdjacencyList
			MSTEncoding enc         = (MSTEncoding)newGenotype.getEncoding();

			// if no recombination, simply return first parent
			if (!EaRandom.nextBoolean(this.probability)) {
				return newGenotype;
			}

			// Mutation
			//###############
			List<Edge> edges = new ArrayList<Edge>();

			// 1. Select a random edge (i,j) to insert (edge not part of the current ST!).
			Edge e = null;
			Node i = null;
			Node j = null;
			Collection<E> allEdges = this.problemAnnotatedGraph.getGraph().getAllEdges();

			if (maxDegree == 0) {
				while (e == null || newGenotype.getEdges().contains(e)) {
					e = EaRandom.getRandomElement(allEdges);
				}
			} else {
				e = EaRandom.getRandomElement(allEdges);
				Collection<N> incNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) e);
				Iterator<N> myIter = incNodes.iterator();
				i = myIter.next();
				j = myIter.next();
				while (newGenotype.getEdges().contains(e) && newGenotype.getDegreeCounter().get(j) >= this.maxDegree) {
					e = EaRandom.getRandomElement(allEdges);
					incNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) e);
					myIter = incNodes.iterator();
					i = myIter.next();
					j = myIter.next();
				}
			}

			Collection<N> incidentNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E)e);
			Iterator<N> iter = incidentNodes.iterator();
			Node startNode = iter.next();
			Node endNode = iter.next();


			// 2. Get path from i to j in the current ST (path + (i,j) is a circle!).

			// Create adjacency list
			HashMap<Node, List<Node>> adjL = newGenotype.getAdjacencyList();
			if (adjL == null) {
				adjL = Functions.createAdjacencyList(problemAnnotatedGraph, newGenotype.getEdges());
				newGenotype.setAdjacencyList(adjL);
			}

			// Get path using DFS
			Stack<Node> path = new Stack<>();
			Functions.getPathDFS_iterative(startNode, endNode, adjL, path);//getPathDFS(startNode, endNode, adjL, path);

			// Convert path of nodes to path of edges
			ListIterator<Node> myIter = path.listIterator();
			Node n1 = myIter.next();
			Node n2 = null;
			while (myIter.hasNext()) {
				n2 = myIter.next();
				Edge ed = this.problemAnnotatedGraph.getGraph().getEdge((N) n1, (N) n2);
				edges.add(ed);
				n1 = n2;
			}


			// 3. Delete an edge from the circle.

			// Select random edge to delete
			// Check that a new edge on i doesn't violate the degree constraint
			Edge rem = null;
			if (this.maxDegree != 0 && newGenotype.getDegreeCounter().get(i) >= this.maxDegree) {
				for (Edge edg : edges) { // FIXED
					Collection<N> tmpCol = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) edg);
					if (tmpCol.contains(i)) {
						rem = edg;
						break;
					}
				}
			} else {
				rem = EaRandom.getRandomElement(edges);
			}

			// Delete selected edge (AND UPDATE adjL AND degreeCounter!)
			Collection<N> myCol = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) rem);
			iter = myCol.iterator();
			n1 = iter.next();
			n2 = iter.next();
			adjL.get(n1).remove(n2);
			adjL.get(n2).remove(n1);

			if (this.maxDegree > 0) {
				int tmp = newGenotype.getDegreeCounter().get(n1) - 1;
				newGenotype.getDegreeCounter().put(n1, tmp);
				tmp = newGenotype.getDegreeCounter().get(n2) - 1;
				newGenotype.getDegreeCounter().put(n2, tmp);
			}


			// 4. Add new edge
			myCol = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) e);
			iter = myCol.iterator();
			n1 = iter.next();
			n2 = iter.next();

			adjL.get(n1).add(n2);
			adjL.get(n2).add(n1);

			if (this.maxDegree > 0) {
				int tmp = newGenotype.getDegreeCounter().get(n1) + 1;
				newGenotype.getDegreeCounter().put(n1, tmp);
				tmp = newGenotype.getDegreeCounter().get(n2) + 1;
				newGenotype.getDegreeCounter().put(n2, tmp);
			}


			//###############

			// Set new IDs and return genotype
			newGenotype.setEncoding(enc);
			List<Edge> tmpList = newGenotype.getEdges();
			tmpList.remove(rem);
			tmpList.add(e); // FIXED (setEdges removed)


			return newGenotype;
		}

		return null;
	}

}
