package com.github.monet.graph.interfaces;

import com.github.monet.graph.AnnotatedGraph;

public interface SecondPhaseAlgorithm<N extends Node, E extends Edge, G extends Graph<N, E, G>> {
	public ParetoSet<N, E, G> secondPhase(AnnotatedGraph<N,E,G> graph, String weightAnnotationName, ParetoSet<N, E, G> paretoSet);
}
