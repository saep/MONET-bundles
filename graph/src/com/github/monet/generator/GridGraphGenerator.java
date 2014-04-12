package com.github.monet.generator;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.DirectedEdge;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

/**
 * Simple class to generate Grid Graphs and export it to monet graph file
 * format.
 *
 * @author David Mezlaf
 *
 */
public class GridGraphGenerator {
	public static void main(String[] args) {
		String dir = System.getProperty("user.dir") + "/";
	/*	GridGraphGenerator.generateGridGraph(4, 300, 2, dir,
				"grid_dir_4_2.txt");
		GridGraphGenerator.generateGridGraph(60, 300, 2, dir,
				"grid_dir_60_2.txt");
		GridGraphGenerator.generateGridGraph(10, 300, 2, dir,
				"grid_dir_10_2.txt");
		GridGraphGenerator.generateGridGraph(10, 300, 3, dir,
				"grid_dir_10_3.txt");
		GridGraphGenerator.generateGridGraph(10, 300, 5, dir,
				"grid_dir_10_5.txt");
		GridGraphGenerator.generateGridGraph(10, 300, 7, dir,
				"grid_dir_10_7.txt");
		System.out.println("generated small graphs.");

		GridGraphGenerator.generateGridGraph(15, 300, 2, dir,
				"grid_dir_15_2.txt");
		GridGraphGenerator.generateGridGraph(15, 300, 3, dir,
				"grid_dir_15_3.txt");
		GridGraphGenerator.generateGridGraph(15, 300, 5, dir,
				"grid_dir_15_5.txt");
		GridGraphGenerator.generateGridGraph(15, 300, 7, dir,
				"grid_dir_15_7.txt");
		System.out.println("generated bigger graphs.");

		GridGraphGenerator.generateGridGraph(25, 300, 2, dir,
				"grid_dir_25_2.txt");
		GridGraphGenerator.generateGridGraph(25, 300, 3, dir,
				"grid_dir_25_3.txt");
		GridGraphGenerator.generateGridGraph(25, 300, 5, dir,
				"grid_dir_25_5.txt");
		GridGraphGenerator.generateGridGraph(25, 300, 7, dir,
				"grid_dir_25_7.txt");
		System.out.println("generated even bigger graphs.");

		GridGraphGenerator.generateGridGraph(40, 300, 2, dir,
				"grid_dir_40_2.txt");
		GridGraphGenerator.generateGridGraph(40, 300, 3, dir,
				"grid_dir_40_3.txt");
		GridGraphGenerator.generateGridGraph(40, 300, 5, dir,
				"grid_dir_40_5.txt");
		*/

		for(int i=1; i < 4; i++){
		GridGraphGenerator.generateGridGraph(40, 300, 2, dir,
				"st_sp_grid_dir_40_2_" + i + ".txt");

		GridGraphGenerator.generateGridGraph(40, 300, 3, dir,
				"st_sp_grid_dir_40_3_" + i + ".txt");

		GridGraphGenerator.generateGridGraph(20, 300, 2, dir,
				"st_sp_grid_dir_20_2_" + i + ".txt");

		GridGraphGenerator.generateGridGraph(20, 300, 3, dir,
				"st_sp_grid_dir_20_3_" + i + ".txt");
		GridGraphGenerator.generateGridGraph(10, 300, 2, dir,
				"st_sp_grid_dir_10_2_" + i + ".txt");
		GridGraphGenerator.generateGridGraph(10, 300, 3, dir,
				"st_sp_grid_dir_10_3_" + i + ".txt");

		GridGraphGenerator.generateGridGraph(4, 300, 2, dir,
				"grid_undir_4_2_" + i + ".txt");

		GridGraphGenerator.generateGridGraph(5, 300, 2, dir,
				"grid_undir_5_2_" + i + ".txt");

		}


		System.out.println("generated really big graphs.");
		System.out.println("program ended.");
	}

	public static void generateGridGraph() {
		int width = 50;
		int maxWeight = 600;
		int dimension = 2;
		String dir = System.getProperty("user.dir") + "/";
		String fileName = "grid_dir_50_2.txt";
		GridGraphGenerator.generateGridGraph(width, maxWeight, dimension, dir,
				fileName);
	}




	public static void generateGridGraph(int width, int maxWeight,
			int dimension, String dir, String fileName) {
		SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
		SimpleNode[][] nodes = new SimpleNode[width][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				nodes[i][j] = graph.addNode();
			}
		}
		SimpleEdge[][] hEdges = new SimpleEdge[width][width - 1];
		SimpleEdge[][] vEdges = new SimpleEdge[width - 1][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width - 1; j++) {
				hEdges[i][j] = graph.addEdge(nodes[i][j], nodes[i][j + 1]);
				vEdges[j][i] = graph.addEdge(nodes[j][i], nodes[j + 1][i]);
			}
		}

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
				adapted);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>(
				graph);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width - 1; j++) {
				weights.setAnnotation(hEdges[i][j], GridGraphGenerator
						.createRandomWeight(dimension, maxWeight));
				weights.setAnnotation(vEdges[j][i], GridGraphGenerator
						.createRandomWeight(dimension, maxWeight));
			}
		}

		aGraph.addAnnotator("edges", weights);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(nodes[width - 1][width - 1], "endNode");
		sdAnnotator.setAnnotation(nodes[0][0], "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		MonetGraphExporter<SimpleNode, SimpleEdge, SimpleUndirectedGraph> exporter = new MonetGraphExporter<SimpleNode, SimpleEdge, SimpleUndirectedGraph>();
		exporter.export(dir, fileName, aGraph);
	}

	public AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> generateGridGraph(int width, int maxWeight, int dimension) {
		SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
		SimpleNode[][] nodes = new SimpleNode[width][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				nodes[i][j] = graph.addNode();
			}
		}
		SimpleEdge[][] hEdges = new SimpleEdge[width][width - 1];
		SimpleEdge[][] vEdges = new SimpleEdge[width - 1][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width - 1; j++) {
				hEdges[i][j] = graph.addEdge(nodes[i][j], nodes[i][j + 1]);
				vEdges[j][i] = graph.addEdge(nodes[j][i], nodes[j + 1][i]);
			}
		}

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
				adapted);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>(
				graph);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width - 1; j++) {
				weights.setAnnotation(hEdges[i][j], GridGraphGenerator
						.createRandomWeight(dimension, maxWeight));
				weights.setAnnotation(vEdges[j][i], GridGraphGenerator
						.createRandomWeight(dimension, maxWeight));
			}
		}

		aGraph.addAnnotator("WEIGHTS", weights);

		return aGraph;
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
