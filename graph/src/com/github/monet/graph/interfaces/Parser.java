package com.github.monet.graph.interfaces;

import com.github.monet.graph.AnnotatedGraph;

/**
 * Models an parser to parse a stream and convert them to objects upper bounded
 * by type Graph.
 *
 * @author Jakob Bossek
 */
public interface Parser<N extends Node, E extends Edge, G extends Graph<N, E, G>> {
    public AnnotatedGraph<N, E, G> parse(String fileName);
}
