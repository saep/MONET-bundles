package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.PrueferEncoding;
import com.github.monet.algorithms.ea.impl.individual.PrueferGenotype;
import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedGraph;

/**
 * Creator for randomly creating Pruefer-Genotypes.
 *
 * @author Sven Selmke
 *
 */
public class PrueferCreator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Creator {

	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
	private boolean createFromSpanningTree;
	private HashMap<Node, Integer> nodeIdMap;


	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		this.createFromSpanningTree = Functions.getParam(params, "createInitialPopFromST", Boolean.class,true);
		this.nodeIdMap = Functions.getParam(params, "nodeIdMap", HashMap.class, null);
		Encoding enc = Functions.getConfiguredOperatorByName("Pruefer-Encoding", Encoding.class, params);
		if (enc == null) {
			return false;
		}
		this.setEncoding(enc);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		if (!(this.problemAnnotatedGraph.getGraph() instanceof UndirectedGraph)) {
			Functions.log("Error! Cannot run Pruefer-Creator on a directed graph! Configuration failed.", Functions.LOG_ERROR);
			return false;
		}
		return true;
	}


	@Override
	public Genotype create() {
		// Create new Genotype
		PrueferEncoding prueferEnc = (PrueferEncoding)this.encoding;
		PrueferGenotype prueferGenotype = new PrueferGenotype();
		prueferGenotype.setEncoding(prueferEnc);

		// Create random pruefer-number
		// 1. Create a random number (only for complete graphs!) or
		// 2. Create a random spanning tree and convert it into a number
		List<Integer> prueferNumber;
		if (!this.createFromSpanningTree) {
			prueferNumber = new ArrayList<Integer>();
			for (int i = 0; i < prueferEnc.getLength(); i++) {
				Integer nextNumber = EaRandom.getRandomElement( prueferEnc.getValidSymbols() );
				prueferNumber.add(nextNumber);
			}
		} else {
			//List<Edge> edges = Functions.createRandomMST(this.problemAnnotatedGraph.getGraph());
			List<Edge> edges = Functions.createRandomMST_Kruskal(this.problemAnnotatedGraph.getGraph(), nodeIdMap);
			prueferNumber = prueferEnc.tree2Pruefer(this.problemAnnotatedGraph.getGraph(), edges);
		}

		// Configure and return genotype
		prueferGenotype.setValue(prueferNumber);
		return prueferGenotype;
	}


	@Override
	public String getName() {
		return "Pruefer-Creator";
	}


	public boolean isCreateFromSpanningTree() {
		return createFromSpanningTree;
	}
	public void setCreateFromSpanningTree(boolean createFromSpanningTree) {
		this.createFromSpanningTree = createFromSpanningTree;
	}

}
