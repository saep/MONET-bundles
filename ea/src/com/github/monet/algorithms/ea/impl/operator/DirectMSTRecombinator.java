package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.monet.algorithms.ea.impl.individual.MSTGenotype;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;
import com.github.monet.algorithms.ea.util.SimpleUnionFind;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedGraph;

/**
 * Implementation of a simple recombination algorithm.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class DirectMSTRecombinator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Recombinator {

	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
	private double probability;
	private HashMap<Node, Integer> nodeIdMap;
	private HashMap<Integer, Node> idNodeMap;
	private int maxDegree;

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String, Object> params) {
		this.nodeIdMap = Functions.getParam(params, "nodeIdMap", HashMap.class, null);
		this.idNodeMap = Functions.getParam(params, "idNodeMap", HashMap.class, null);
		this.probability = Functions.getParam(params, "recombinatorProbability", Double.class, 0.5d);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		this.maxDegree = Functions.getParam(params, "maxDegree", Integer.class, 0);
		if (!(this.problemAnnotatedGraph.getGraph() instanceof UndirectedGraph)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Individual recombinateIndividuals(Individual parentA, Individual parentB, List<Individual> pool) {

		// Get parent genotypes
		MSTGenotype g1 = (MSTGenotype) parentA.getGenotype();
		MSTGenotype g2 = (MSTGenotype) parentB.getGenotype();
		List<Edge> edges1 = g1.getEdges();
		List<Edge> edges2 = g2.getEdges();

		// Create new genotype
		// if no recombination, simply return first parent
		MSTGenotype newGenotype = (MSTGenotype)g1.copy();
		if (!EaRandom.nextBoolean(this.probability)) {
			return new Individual(newGenotype);
		}
		List<Edge> resultEdges = new ArrayList<Edge>();
		HashMap<Node, Integer> degreeCounter = new HashMap<>();

		// Main crossover:
		// ##################

		int nodeCount = this.problemAnnotatedGraph.getGraph().getAllNodes().size();

		// Store components of the graph
		SimpleUnionFind uf= new SimpleUnionFind(this.nodeIdMap.size());

		// 1. get edges that are in both parents
		for (Edge e : edges1) {
			if (edges2.contains(e)) {
				constraintCheck(e, degreeCounter, true); // FIXED
				// add edge
				resultEdges.add(e);
				// update union find
				Collection<N> incidentNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E)e);
				Iterator<N> nodeIter = incidentNodes.iterator();
				N n1    = nodeIter.next();
				int id1 = nodeIdMap.get(n1);
				N n2    = nodeIter.next();
				int id2 = nodeIdMap.get(n2);
				uf.union(id1, id2);
			}
		}

		// 2. ST edges that are either only in E1 or only in E2 (PERFORMANCE:
		// create in previous loop)
		List<Edge> singleEdges = new ArrayList<Edge>(edges1);
		singleEdges.addAll(edges2);
		singleEdges.removeAll(resultEdges);
		Collections.shuffle(singleEdges, EaRandom.getRand());

		// 3. Add random edges from singleEdges
		for (Edge e : singleEdges) {
			// Get nodes of given Edge
			Collection<N> incidentNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E)e);
			Iterator<N> nodeIter = incidentNodes.iterator();
			N n1    = nodeIter.next();
			int id1 = this.nodeIdMap.get(n1);
			N n2    = nodeIter.next();
			int id2 = this.nodeIdMap.get(n2);
			// Add new edge if components of vertices are not connected yet
			if (!uf.connected(id1, id2)) {
				if ((this.maxDegree > 0 && constraintCheck(e, degreeCounter, true)) || this.maxDegree == 0) {
					resultEdges.add(e);
					uf.union(id1, id2);
				}
			}
			// Finished
			if (resultEdges.size() == nodeCount - 1) {
				break;
			}
		}

		// 4. Not finished yet (This probably won't happen without a degree
		// constraint)?
		// Determine all unconnected components and connect these components by
		// repeatedly choosing two random vertices (from different components)
		// with a degree smaller than the constraint.
		if (resultEdges.size() != nodeCount - 1) {
			Map<Integer, List<Integer>> comps = uf.getComponents();
			Set<Integer> compRepresentatives = comps.keySet(); // changes affect the map!

			// Repeatedly connect two components
			while (comps.size() > 1) {
				// select random(?) components
				Iterator<Integer> iter          = compRepresentatives.iterator();
				Integer firstCompRepr           = iter.next();
				List<Integer> firstCompNodeids  = comps.get(firstCompRepr);
				Integer secondCompRepr          = iter.next();
				List<Integer> secondCompNodeIds = comps.get(secondCompRepr);

				// Select nodes from both components that don't violate the degree constraint
				Node firstNode = this.idNodeMap.get(  EaRandom.getRandomElement(firstCompNodeids)  );
				while (!nodeCheck(firstNode, degreeCounter)) {
					firstNode = this.idNodeMap.get(  EaRandom.getRandomElement(firstCompNodeids)  );
				}
				Node secondNode = this.idNodeMap.get(  EaRandom.getRandomElement(secondCompNodeIds)  );
				while (!nodeCheck(secondNode, degreeCounter)) {
					secondNode = this.idNodeMap.get(  EaRandom.getRandomElement(secondCompNodeIds)  );
				}

				// Put edge between these nodes (connecting the firstComp and secondComp)
				Edge newEdge = this.problemAnnotatedGraph.getGraph().getEdge((N) firstNode, (N) secondNode);
				resultEdges.add(newEdge);
				this.constraintCheck(newEdge, degreeCounter, true); // increase degreeCounter

				// Merge lists
				secondCompNodeIds.addAll(firstCompNodeids);
				compRepresentatives.remove(firstCompRepr); // reduces comps.size() by 1!
			}

		}

		// ##################

		// Set new edges
		newGenotype.setEdges(resultEdges);
		if (this.maxDegree > 0) {
			newGenotype.setDegreeCounter(degreeCounter);
		}

		// Return new individual
		Individual newIndividual = new Individual(newGenotype);
		return newIndividual;
	}

	/**
	 * Checks if the degree constraint is met by both nodes of the given edge.
	 * If the conditions are met, the degree of both nodes is increased by 1
	 * (only if increase is set to true).
	 */
	@SuppressWarnings("unchecked")
	private boolean constraintCheck(Edge e, HashMap<Node, Integer> degreeCounter, boolean increase) {
		if (degreeCounter == null) return false;
		Collection<N> incNodes = this.problemAnnotatedGraph.getGraph().getIncidentNodes((E) e);
		Iterator<N> myIter = incNodes.iterator();
		Node i = myIter.next();
		Node j = myIter.next();
		if (this.nodeCheck(i, degreeCounter) && this.nodeCheck(j, degreeCounter)) {
			if (increase) {
				int tmp = degreeCounter.get(i) + 1;
				degreeCounter.put(i, tmp);
				tmp = degreeCounter.get(j) + 1;
				degreeCounter.put(j, tmp);
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks if the degree constraint is met by the given node.
	 */
	private boolean nodeCheck(Node n, HashMap<Node, Integer> degreeCounter) {
		if (degreeCounter.get(n) == null) {
			degreeCounter.put(n, 0);
		}
		return (degreeCounter.get(n) < this.maxDegree);
	}

	@Override
	public String getName() {
		return "MST-Recombinator";
	}

}
