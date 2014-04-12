package com.github.monet.algorithms.ea.impl.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.MSTEncoding;
import com.github.monet.algorithms.ea.impl.individual.MSTGenotype;
import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedGraph;

/**
 * Creator for randomly creating MST-Genotypes.
 *
 * @author Sven Selmke
 *
 */
public class DirectMSTCreator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Creator {

	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
	private HashMap<Node, Integer> nodeIdMap;


	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		Encoding enc = Functions.getConfiguredOperatorByName("MST-Encoding", Encoding.class, params);
		if (enc == null) {
			return false;
		}
		this.setEncoding(enc);
		this.nodeIdMap             = Functions.getParam(params, "nodeIdMap", HashMap.class, null);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		if (!(this.problemAnnotatedGraph.getGraph() instanceof UndirectedGraph)) {
			Functions.log("Error! Cannot run DirectMSTCreator on a directed graph! Configuration failed.", Functions.LOG_ERROR);
			return false;
		}
		return true;
	}


	@Override
	public Genotype create() {
		// Create new Genotype
		MSTEncoding enc = (MSTEncoding)this.encoding;
		MSTGenotype genotype = new MSTGenotype();
		HashMap<Node,Integer> degreeCounter = genotype.getDegreeCounter();
		int maxDegree = enc.getDegree();
		genotype.setEncoding(enc);

		// Create a random spanning tree
		List<Edge> edges = Functions.createRandomMST_Kruskal(this.problemAnnotatedGraph.getGraph(), this.nodeIdMap, degreeCounter, maxDegree);

		// Configure and return genotype
		genotype.setEdges(edges);
		return genotype;
	}


	@Override
	public String getName() {
		return "MST-Creator";
	}

}
