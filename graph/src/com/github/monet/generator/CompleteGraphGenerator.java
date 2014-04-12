package com.github.monet.generator;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

/**
 * Simple class to generate Grid Graphs and export it to monet graph file
 * format.
 *
 * @author David Metzlaf, Christopher Morris
 *
 */
public class CompleteGraphGenerator {
	public static void main(String[] args) {
		String dir = System.getProperty("user.dir") + "/";
		CompleteGraphGenerator.generateCompleteGraph(10, 300, 2, dir,
				"complete_dir_10_2.txt");
		CompleteGraphGenerator.generateCompleteGraph(10, 300, 3, dir,
				"complete_dir_10_3.txt");
		CompleteGraphGenerator.generateCompleteGraph(10, 300, 5, dir,
				"complete_dir_10_5.txt");
		CompleteGraphGenerator.generateCompleteGraph(10, 300, 7, dir,
				"complete_dir_10_7.txt");
		System.out.println("generated small graphs.");

		CompleteGraphGenerator.generateCompleteGraph(15, 300, 2, dir,
				"complete_dir_15_2.txt");
		CompleteGraphGenerator.generateCompleteGraph(15, 300, 3, dir,
				"complete_dir_15_3.txt");
		CompleteGraphGenerator.generateCompleteGraph(15, 300, 5, dir,
				"complete_dir_15_5.txt");
		CompleteGraphGenerator.generateCompleteGraph(15, 300, 7, dir,
				"complete_dir_15_7.txt");
		System.out.println("generated bigger graphs.");

		CompleteGraphGenerator.generateCompleteGraph(25, 300, 2, dir,
				"complete_dir_25_2.txt");
		CompleteGraphGenerator.generateCompleteGraph(25, 300, 3, dir,
				"complete_dir_25_3.txt");
		CompleteGraphGenerator.generateCompleteGraph(25, 300, 5, dir,
				"complete_dir_25_5.txt");
		CompleteGraphGenerator.generateCompleteGraph(25, 300, 7, dir,
				"complete_dir_25_7.txt");
		System.out.println("generated even bigger graphs.");

		CompleteGraphGenerator.generateCompleteGraph(40, 300, 2, dir,
				"complete_dir_40_2.txt");
		CompleteGraphGenerator.generateCompleteGraph(40, 300, 3, dir,
				"complete_dir_40_3.txt");
		CompleteGraphGenerator.generateCompleteGraph(40, 300, 5, dir,
				"complete_dir_40_5.txt");
		CompleteGraphGenerator.generateCompleteGraph(40, 300, 7, dir,
				"complete_dir_40_7.txt");
		System.out.println("generated really big graphs.");
		System.out.println("program ended.");
	}

	public static void generateCompleteGraph() {
		int n = 50;
		int maxWeight = 600;
		int dimension = 2;
		String dir = System.getProperty("user.dir") + "/";
		String fileName = "complete_dir_50_2.txt";
		CompleteGraphGenerator.generateCompleteGraph(n, maxWeight, dimension, dir,
				fileName);
	}

	public AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> generateCompleteGraph(int n, int maxWeight,
			int dimension) {
		SimpleUndirectedGraph graph = new SimpleUndirectedGraph();

		SimpleNode[] nodes = new SimpleNode[n];
		for (int i = 0; i < n; i++) {
			nodes[i] = graph.addNode();
		}

		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				graph.addEdge(nodes[i], nodes[j]);
			}
		}

		GraphElementHashAnnotator<SimpleEdge, Weight> adapted = new GraphElementHashAnnotator<SimpleEdge, Weight>();
		GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<SimpleEdge>(
				adapted);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>(
				graph);

		for (SimpleEdge e : graph.getAllEdges()) {
			weights.setAnnotation(e, CompleteGraphGenerator.createRandomWeight(dimension, maxWeight));
		}

		aGraph.addAnnotator("WEIGHTS", weights);

		return aGraph;

	}

	public static void generateCompleteGraph(int n, int maxWeight,
			int dimension, String dir, String fileName) {
		SimpleUndirectedGraph graph = new SimpleUndirectedGraph();

		SimpleNode[] nodes = new SimpleNode[n];
		for (int i = 0; i < n; i++) {
			nodes[i] = graph.addNode();
		}

		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				graph.addEdge(nodes[i], nodes[j]);
			}
		}

		GraphElementHashAnnotator<SimpleEdge, Weight> adapted = new GraphElementHashAnnotator<SimpleEdge, Weight>();
		GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<SimpleEdge>(
				adapted);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>(
				graph);

		for (SimpleEdge e : graph.getAllEdges()) {
			weights.setAnnotation(e, CompleteGraphGenerator.createRandomWeight(dimension, maxWeight));
		}

		aGraph.addAnnotator("edges", weights);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(nodes[n-1], "endNode");
		sdAnnotator.setAnnotation(nodes[0], "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		MonetGraphExporter<SimpleNode, SimpleEdge, SimpleUndirectedGraph> exporter = new MonetGraphExporter<SimpleNode, SimpleEdge, SimpleUndirectedGraph>();
		exporter.export(dir, fileName, aGraph);
	}

	public static Weight createRandomWeight(int dimension, int max) {
		double[] w = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			w[i] = (int) (Math.random() * max);
		}
		Weight retval = new Weight(w);
		return retval;
	}
}
