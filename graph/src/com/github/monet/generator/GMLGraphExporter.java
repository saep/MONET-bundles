package com.github.monet.generator;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
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
public class GMLGraphExporter<N extends Node, E extends Edge, G extends Graph<N, E, G>> implements GraphExporter<N, E, G> {
	/**
	 * Saves an annotated graph in the gml graph format.
	 *
	 * @param savePath
	 * 			Path to graph file.
	 * @param fileName
	 * 			File name of graph file. *.gml is addad automatically.
	 * @param annotatedGraph
	 * 			AnnotatedGraph object to save.
	 * @param coordinates
	 * 			HashMap of 2D-coordinates of the graph.
	 * @return boolean value. True if exported successfully, otherwise false.
	 */
	@Override
	public boolean export(
			String savePath,
			String fileName,
			AnnotatedGraph<N, E, G> annotatedGraph,
			HashMap<N, Point2D.Double> coordinates) {
		Graph<N, E, G> graph = annotatedGraph.getGraph();
		@SuppressWarnings("unchecked")
		GraphElementWeightAnnotator<E> graphWeights =
				annotatedGraph.getAnnotator("edges", GraphElementWeightAnnotator.class);
		String output = "graph [\n";
		output += "  creator \"PG573 MONet\"\n";
		output += "  comment \"This graph was exported by " + GMLGraphExporter.class + "\"\n";
		output += "  directed 0\n";
		ArrayList<N> nodes = (ArrayList<N>) graph.getAllNodes();

		for (N node:nodes) {
			/*
			 * Get node ID (without "node" prefix)
			 * FIXME: every node should have an ID, so getId() should be in the Node Interface
			 */
			int nodeId = this.extractNodeId(node);
			output += "  node [\n";
			output += "    id " + nodeId + "\n";
			output += "    label \"" + nodeId + "\"\n";
			output += "    graphics [\n";
			output += "      center [ x " + coordinates.get(node).getX() + " y " + coordinates.get(node).getY() + " ]\n";
			output += "      fill \"#cfcfcf\"\n";
			output += "    ]\n";
			output += "  ]\n";
		}

		for (N nodeA:nodes) {
			int nodeAId = this.extractNodeId(nodeA);
			for (E edge:graph.getIncidentEdges(nodeA)) {
				N nodeB = graph.getIncidentNode(nodeA, edge);
				int nodeBId = this.extractNodeId(nodeB);
				output += "  edge [\n";
				output += "    source " + nodeAId + "\n";
				output += "    target " + nodeBId + "\n";
				output += "    label \"" + Arrays.toString(graphWeights.getAnnotation(edge).getWeights()) + "\"\n";
				output += "  ]\n";
			}
		}
		output += "]";

		/*
		 * FIXME: catch Exception or throw it
		 */
		FileWriter fileWriter = null;
		try {
			File file = new File("/Users/jboss/monet.gml");
			fileWriter = new FileWriter(file);
			fileWriter.write(output);
		} catch (IOException e) {
			System.out.println("HAE???");
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

		System.out.println(output);

		return false;
	}

	/**
	 * Extracts node Id from String represenation.
	 *
	 * @param node
	 * 			Node upper bounded by type Node.
	 * @return int
	 */
	private int extractNodeId(N node) {
		/*
		 * Get node ID (without "node" prefix)
		 * FIXME: every node should have an ID, so getId() should be in the Node Interface
		 */
		return Integer.parseInt(node.toString().replaceAll("[\\D]", ""));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MonetGraphGenerator<SimpleDirectedGraph> gn = new MonetGraphGenerator<>("undirected", 5, 1, 1.2);
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gn.getGraph();
		HashMap<SimpleNode, Point2D.Double> coordinates = gn.getCoordinates();
		GMLGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph> exporter =
				new GMLGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph>();
		exporter.export("examplegraph/graphs/dense/", "dense_directed_graph", graph, coordinates);
	}

}
