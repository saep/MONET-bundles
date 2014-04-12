package com.github.monet.generator;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

public interface NetworkGenerator<N extends Node, E extends Edge, G extends Graph<N, E, G>> {
	public void generate();
	public AnnotatedGraph<N, E, G> getGraph();
}
