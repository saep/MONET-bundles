package com.github.monet.graph;

import java.util.Objects;

import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElement;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;

/**
 * Wrapper class, which wraps a graph and a bunch of annotators together.
 *
 * @author Christopher Morris
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 *            type of graph, upper bounded by type Graph
 */
public class AnnotatedGraph<N extends Node, E extends Edge, G extends Graph<N, E, G>> {

	private G graph;
	private HeterogeneousHashAnnotatorContainer annotatorMap;

	public AnnotatedGraph(G graph) {

		assert graph != null : "graph must not be null";
		this.graph = graph;
		this.annotatorMap = new HeterogeneousHashAnnotatorContainer();
	}

	/**
	 * Adds a new annotator.
	 *
	 * @param annotatorName strings which maps to new annotator
	 * @param annotator new annotator
	 */
	public <GE extends GraphElement, T> void addAnnotator(String annotatorName, GraphElementAnnotator<GE,T> annotator) {
		this.annotatorMap.put(annotatorName, annotator);
	}

	/**
	 * Returns the annotator, which is mapped to string annotatorName.
	 *
	 * @param annotatorName
	 *            string, whose corresponding annotator is to be returned
	 * @param clazz
	 *            type of to be returned annotator object
	 * @return annotator object, which annotatorName maps to
	 */
	public <T> T getAnnotator(String annotatorName, Class<T> clazz) {
		// no assertion in order to preserve intuitive map behavior
		// (return null if annotator doesn't exist)
		//assert this.annotatorMap.containsKey(annotatorName) :
		//		"annotatorName is not an existing annotator";

		T annotator = this.annotatorMap.get(annotatorName, clazz);

		return annotator;
	}

	/**
	 * @return the wrapped graph
	 */
	public G getGraph() {
		return this.graph;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof AnnotatedGraph)) {
			return false;
		} else {
			boolean isEqual = true;
			AnnotatedGraph<N, E, G> compareTo = (AnnotatedGraph<N, E, G>) o;
			isEqual &= this.graph.equals(compareTo.graph);
			isEqual &= this.annotatorMap.equals(compareTo.annotatorMap);
			return isEqual;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.graph);
		hash = 79 * hash + Objects.hashCode(this.annotatorMap);
		return hash;
	}
}
