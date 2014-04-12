package com.github.monet.generator;

import java.awt.geom.Point2D;
import java.util.HashMap;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;

/**
 * Interface for graph export classes.
 *
 * @author Jakob Bossek
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 * 			  type of graph, upper bounded by type UndirectedEdge
 */
public interface GraphExporter<N extends Node, E extends Edge, G extends Graph<N, E, G>> {
	public boolean export(
			String savePath,
			String fileName,
			AnnotatedGraph<N, E, G> annotatedGraph,
			HashMap<N, Point2D.Double> coordinates);
}
