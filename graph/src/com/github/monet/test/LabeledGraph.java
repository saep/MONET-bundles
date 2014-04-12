package com.github.monet.test;

import java.util.HashMap;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

public class LabeledGraph<N extends Node, E extends Edge, G extends Graph<N, E, G>> {

	public LabeledGraph(AnnotatedGraph<N, E, G> graph,
			HashMap<String, N> node_ids, HashMap<String, E> edge_ids) {
		this.graph = graph;
		this.node_ids = node_ids;
		this.edge_ids = edge_ids;
	}

	public AnnotatedGraph<N, E, G> graph;
	public HashMap<String, N> node_ids;
	public HashMap<String, E> edge_ids;
}
