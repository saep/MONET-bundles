package com.github.monet.generator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.github.monet.algorithms.Prim;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

/**
 * Class which offers methods to generate random graphs with multiple edge weights.
 *
 * @author Jakob Bossek
 *
 * @param <G>
 *            type of graph, upper bounded by type Graph
 */
public class MonetGraphGenerator<G extends Graph<SimpleNode, SimpleEdge, G>>
implements NetworkGenerator<SimpleNode, SimpleEdge, G> {
	/**
	 * String which contains the type of the generated graph (either directed or undirected).
	 */
	private String type;

	/**
	 * Number of nodes.
	 */
	private int numNodes;

	/**
	 * Number of objectives respectively edge weights.
	 */
	private int numObjectives;

	/**
	 * This value controls the denseness of the graph. Low values lead to sparse graphs, higher values
	 * to dense graphs.
	 */
	private double alpha;

	/**
	 * Maximum weight that will be created (default: 1)
	 */
	private int maxWeight;

	/**
	 * This value is computed once and serves as an upper bound. The first edge weight (distance computed by
	 * the distanceStrategy metric) of the edges in the generated graph is lower than for all but the spanning
	 * tree edges.
	 */
	private final double distanceLimit;

	/**
	 * The distances between the initially placed points in the 2D layer form the first edge weight of the edges.
	 * This distances are computed by the distance strategy (for example euclidean distance).
	 */
	private Metric distanceStrategy;

	/**
	 * Generator for random generation of edge weights.
	 */
	private Random randomGenerator;

	/**
	 * Full edge weighted graph which serves as the point of origin for the generation process.
	 */
	private G fullGraph = null;

	/**
	 * Finally constructed edge weighted graph.
	 */
	private G generatedGraph = null;

	/**
	 * Annotator for edge weights of the full graph.
	 */
	private GraphElementWeightAnnotator<SimpleEdge> fullGraphWeights;

	/**
	 * Annotator for the edge weights of the constructed graph.
	 */
	private GraphElementWeightAnnotator<SimpleEdge> generatedGraphWeights;

	/**
	 * Container for graph and edge annotator.
	 */
	private AnnotatedGraph<SimpleNode, SimpleEdge, G> generatedAnnotatedGraph = null;

	private ArrayList<SimpleNode> nodes = new ArrayList<SimpleNode>();
	private HashMap<SimpleNode, Point2D.Double> points = new HashMap<>();

	/**
	 * Creates an NetworkGenerator instance.
	 *
	 * @param type
	 * 			String representing the type of graph. Either 'directed' or 'undirected.
	 * @param numNodes
	 * 			The desired number of nodes.
	 * @param numObjectives
	 * 			The desired number of objectives, i.e., the number of edge weights.
	 * @param alpha
	 * 			A parameter for denseness control. High values lead to dense graphs. Low values
	 * 			to rather sparse graphs.
	 */
	public MonetGraphGenerator(
			String type,
			int numNodes,
			int numObjectives,
			double alpha) {
		this(type, numNodes, numObjectives, alpha, new EuclideanMetric());
		this.maxWeight = 1;
	}

	/**
	 * Creates an NetworkGenerator instance.
	 *
	 * @param type
	 * 			String representing the type of graph. Either 'directed' or 'undirected.
	 * @param numNodes
	 * 			The desired number of nodes.
	 * @param numObjectives
	 * 			The desired number of objectives, i.e., the number of edge weights.
	 * @param alpha
	 * 			A parameter for denseness control. High values lead to dense graphs. Low values
	 * 			to rather sparse graphs.
	 * @param distanceStrategy
	 * 			Strategy which states the means to compute pairwise distances between points
	 * 			in the euclidean layer.
	 */
	public MonetGraphGenerator(
			String type,
			int numNodes,
			int numObjectives,
			double alpha,
			Metric distanceStrategy) {
		this.type = type;
		this.numNodes = numNodes;
		this.numObjectives = numObjectives;
		this.alpha = alpha;
		this.distanceLimit = (1.6 * this.alpha) / Math.sqrt(this.numNodes);
		this.distanceStrategy = distanceStrategy;
		this.randomGenerator = new Random();

	}

	/**
	 * Generates a network.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void generate() {
		if (this.type.equals("directed")) {
			this.fullGraph = (G) new SimpleDirectedGraph();
		} else {
			this. fullGraph = (G) new SimpleUndirectedGraph();
		}

		// init edge annotator
		GraphElementHashAnnotator<SimpleEdge, Weight> fullGraphRawWeights =
				new GraphElementHashAnnotator<>();
		this.fullGraphWeights =
				new GraphElementWeightAnnotator<>(fullGraphRawWeights);

		// initialize nodes
		for (int i = 0; i < this.numNodes; i++) {
			SimpleNode node = this.fullGraph.addNode();
			this.nodes.add(node);
			/*
			 * create nodes by uniformly placing points in the 2D layer
			 */
			this.points.put(node, new Point2D.Double(
					this.randomGenerator.nextDouble(),
					this.randomGenerator.nextDouble()));
		}

		/*
		 *  compute pairwise distances according to metric
		 */
		System.out.println("Generator: Create distances according to metric.");
		for (SimpleNode nodeA:nodes) {
			for (SimpleNode nodeB:nodes) {
				if (nodeA.equals(nodeB)) {
					continue;
				}
				SimpleEdge edge = fullGraph.addEdge(nodeA, nodeB);
				fullGraphWeights.setAnnotation(edge, new Weight(new double[] {
						this.distanceStrategy.d(points.get(nodeA), points.get(nodeB))}));
			}
		}

		//this.printGraph(fullGraph, fullGraphWeights);

		/*
		 *  compute minimum weight spanning tree
		 */
		System.out.println("Generator: Compute MST (uniobjective).");
		Prim<SimpleNode, SimpleEdge, G> prim = new Prim<>();
		List<SimpleEdge> spanningTree = (ArrayList<SimpleEdge>)
				prim.computeUniobjectiveOptimum(fullGraph, fullGraphWeights);

		/*
		 * Create MST and copy weights into dedicated annotator
		 */
		System.out.println("Generator: Create MST and copy weights into dedicated annotator.");
		generatedGraph = fullGraph.getSubgraphWithImpliedNodes(spanningTree);
		GraphElementHashAnnotator<SimpleEdge, Weight> generatedGraphRawWeights =
				new GraphElementHashAnnotator<>();
		this.generatedGraphWeights =
				new GraphElementWeightAnnotator<>(generatedGraphRawWeights);

		for (SimpleEdge edge:generatedGraph.getAllEdges()) {
			Weight w = this.generateRandomWeights(fullGraphWeights.getAnnotation(edge).getFirstWeight());
			generatedGraphWeights.setAnnotation(edge, w);
		}

		System.out.println("AFTER PRIM!");
		//this.printGraph(generatedGraph, generatedGraphWeights);

		/*
		 * Now extend MST with edges with length <= 1.6*alpha/sqrt(numNodes)
		 */
		System.out.println("Generator: Extend MST with edges.");
		int added = 0;
		int i = 0;
		long t = System.currentTimeMillis();
		for (SimpleNode nodeA:nodes) {
			for (SimpleNode nodeB:nodes) {
				if (nodeA.equals(nodeB)) {
					continue;
				}
				//System.out.println("Checking nodes (" + nodeA + ", " + nodeB + ")");
				SimpleEdge edge = generatedGraph.getEdge(nodeA, nodeB);
				/*
				 * Skip if edge already exists
				 */
				if (edge != null) {
					//System.out.println("Edge does exist!");
					continue;
				}
				/*
				 * Otherwise check if distance is lower than limit and
				 * eventually include edge
				 */
				Weight firstWeight = fullGraphWeights.getAnnotation(fullGraph.getEdge(nodeA, nodeB));
				if (firstWeight.getFirstWeight() <= this.distanceLimit) {
					SimpleEdge includedEdge = generatedGraph.addEdge(nodeA, nodeB);
					Weight w = this.generateRandomWeights(firstWeight.getFirstWeight());
					generatedGraphWeights.setAnnotation(includedEdge, w);
					//System.out.println("ADDING!");
					added++;
				}
			}
			// Test-output
			i++;
			if (i % 10 == 0) {
				System.out.println("Currently at Node " + i + " (time since last output: " + (System.currentTimeMillis() - t) + ")...");
				t = System.currentTimeMillis();
			}
		}
		//this.printGraph(generatedGraph, generatedGraphWeights);
		System.out.println("Setup:");
		System.out.println("Limit: " + this.distanceLimit);
		System.out.println("Added: " + added);

		//HeterogeneousHashAnnotatorContainer annotatorMap = new HeterogeneousHashAnnotatorContainer();
		this.generatedAnnotatedGraph =
				new AnnotatedGraph<SimpleNode, SimpleEdge, G>(generatedGraph);
		this.generatedAnnotatedGraph.addAnnotator("WEIGHTS", generatedGraphWeights);
	}

	/**
	 * Helper method for generating a Weight object. The only parameter firstWeight
	 * serves as the first weight.
	 *
	 * @param firstWeight
	 * 				First edge weight.
	 * @return Weight Object
	 */
	private Weight generateRandomWeights(double firstWeight) {
		double[] w = new double[this.numObjectives];
		w[0] = firstWeight;
		for (int i = 1; i < this.numObjectives; i++) {
			w[i] = this.randomGenerator.nextDouble() * this.maxWeight;
		}
		return new Weight(w);
	}

	/**
	 * Return generated annotated graph.
	 *
	 * @return Annotated graph containing the graph object and an edge annotator.
	 */
	public AnnotatedGraph<SimpleNode, SimpleEdge, G> getGraph() {
		if (this.generatedAnnotatedGraph == null) {
			this.generate();
		}
		return this.generatedAnnotatedGraph;
	}

	/**
	 * Return the euclidean coordinates of the graph for plotting purposes.
	 *
	 * @return Coordinates
	 */
	public HashMap<SimpleNode, Point2D.Double> getCoordinates() {
		if (this.generatedAnnotatedGraph == null) {
			this.generate();
		}
		return this.points;
	}

	/**
	 * Helper method for printing the graph to stdout.
	 *
	 * @param graph
	 * 			Graph object.
	 * @param annotator
	 * 			Edge annotator.
	 */
	public void printGraph(
			Graph<SimpleNode, SimpleEdge, G> graph,
			GraphElementWeightAnnotator<SimpleEdge> annotator) {
		// DEBUG: output graph in textual form to stdout
		for (SimpleNode node:graph.getAllNodes()) {
			System.out.println("NODE " + node);
			for (SimpleEdge edge:graph.getIncidentEdges(node)) {
				System.out.println("(" + graph.getIncidentNode(node, edge) + ", " + annotator.getAnnotation(edge) + ")");
			}
			System.out.println();
		}
	}

	/**
	 * Get the maximum weight
	 */
	public int getMaxWeight() {
		return maxWeight;
	}

	/**
	 * Set the maximum weight
	 */
	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	/**
	 * MAIN
	 * Generate an undirected graph.
	 */
	public static void main(String[] args) {
		MonetGraphGenerator<SimpleDirectedGraph> gn =
				new MonetGraphGenerator<>("directed", 5, 2, 0.9);
		@SuppressWarnings("unused")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gn.getGraph();
	}

}
