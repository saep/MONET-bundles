package com.github.monet.algorithms.ea.impl.operator;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.STSPEncoding;
import com.github.monet.algorithms.ea.impl.individual.STSPGenotype;
import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

public class SPEA2Creator<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends Creator {

	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		Encoding enc = Functions.getConfiguredOperatorByName("SSSP-Encoding", Encoding.class, params);
		if (enc == null) {
			return false;
		}
		this.setEncoding(enc);
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		return true;
	}

	@Override
	public String getName() {
		return "SPEA2-Creator";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Genotype create() {
		STSPEncoding enc = (STSPEncoding)this.encoding;

		// Create a random path
		List<Node> nodes = Functions.createRandomPath(problemAnnotatedGraph.getGraph(), (N)enc.getStartNode(), (N)enc.getEndNode());
		if (nodes == null || nodes.size() == 0) {
			Functions.log("Error! No path found from start node " + (N)enc.getStartNode() + " to node " + (N)enc.getEndNode() + " in SPEA2 Creator", Functions.LOG_ERROR);
		}

		// Create genotype
		STSPGenotype genotype = new STSPGenotype();
		genotype.setEncoding(enc);
		genotype.setNodes(nodes);

		return genotype;
	}

}
