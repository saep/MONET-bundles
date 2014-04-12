package com.github.monet.generator;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;

/**
 * NetworkExporter for gml graph format.
 *
 * @author Jakob Bossek
 *
 * @param <N>
 *            type of node, upper bounded by type Node
 * @param <E>
 *            type of edge, upper bounded by type Edge
 * @param <G>
 *            type of graph, upper bounded by type Graph
 */
public class MonetGraphExporter<N extends Node, E extends Edge, G extends Graph<N, E, G>>
		implements GraphExporter<N, E, G> {
	/**
	 * Saves an annotated graph in the MONet graph format.
	 *
	 * @param savePath
	 *            Path to graph file.
	 * @param fileName
	 *            File name of graph file. *.gml is addad automatically.
	 * @param annotatedGraph
	 *            AnnotatedGraph object to save.
	 * @param coordinates
	 *            HashMap of 2D-coordinates of the graph. Irrelavant for the
	 *            MONet graph format. Therefore you may should pass a null
	 *            object or make use of the shorthand function.
	 * @return boolean value. True if exported successfully, otherwise false.
	 */
	@Override
	public boolean export(String savePath, String fileName,
			AnnotatedGraph<N, E, G> annotatedGraph,
			HashMap<N, Point2D.Double> coordinates) {
		Graph<N, E, G> graph = annotatedGraph.getGraph();
		@SuppressWarnings("unchecked")
		GraphElementWeightAnnotator<E> graphWeights = annotatedGraph
				.getAnnotator("edges", GraphElementWeightAnnotator.class);
		String output = graph.getNumNodes() + "\n";
		output += graph.getNumEdges() + "\n";
		output += graphWeights.getDimension() + "\n";
		HashMap<E, Boolean> addedEdges = new HashMap<E, Boolean>();

		List<N> nodes = (List<N>) graph.getAllNodes();
		Collection<E> incidentEdges = null;

		for (N nodeA : nodes) {
			int nodeAId = this.extractNodeId(nodeA);
			if (graph instanceof SimpleUndirectedGraph) {
				incidentEdges = graph.getIncidentEdges(nodeA);
			} else if (graph instanceof SimpleDirectedGraph) {
				incidentEdges = (Collection<E>) ((SimpleDirectedGraph) graph)
						.getOutgoingEdges((SimpleNode) nodeA);
			}
			for (E edge : incidentEdges) {

				if (addedEdges.get(edge) != null) {
					continue;
				} else {
					addedEdges.put(edge, true);

					N nodeB = graph.getIncidentNode(nodeA, edge);
					int nodeBId = this.extractNodeId(nodeB);
					output += nodeAId + " " + nodeBId + " ";

					double w[] = graphWeights.getAnnotation(edge).getWeights();

					for (int i = 0; i < w.length; i++) {
						output += w[i];
						output += (i < w.length - 1) ? " " : "";
					}
					output += "\n";
				}
			}
		}

		/*
		 * Finally write the generated string representation to a file.
		 */
		FileWriter fileWriter = null;
		try {
			File file = new File(savePath + fileName);
			fileWriter = new FileWriter(file);
			fileWriter.write(output);
		} catch (IOException e) {
			return false;
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	/**
	 * Saves an annotated graph in the MONet graph format.
	 *
	 * @param savePath
	 *            Path to graph file.
	 * @param fileName
	 *            File name of graph file. *.gml is addad automatically.
	 * @param annotatedGraph
	 *            AnnotatedGraph object to save.
	 * @return boolean value. True if exported successfully, otherwise false.
	 */
	public boolean export(String savePath, String fileName,
			AnnotatedGraph<N, E, G> annotatedGraph) {
		return export(savePath, fileName, annotatedGraph, null);
	}

	/**
	 * Extracts node Id from String represenation.
	 *
	 * @param node
	 *            Node upper bounded by type Node.
	 * @return int
	 */
	private int extractNodeId(N node) {
		/*
		 * Get node ID (without "node" prefix) FIXME: every node should have an
		 * ID, so getId() should be in the Node Interface
		 */
		return Integer.parseInt(node.toString().replaceAll("[\\D]", "")) + 1;
	}

	public static void main(String[] args) {
		MonetGraphGenerator<SimpleDirectedGraph> gn = new MonetGraphGenerator<>(
				"directed", 10, 2, 500);
		gn.setMaxWeight(10);
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gn
				.getGraph();
		MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph> exporter = new MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph>();
		exporter.export("~/", "graph_1000_2_500.txt", graph);
	}

}
