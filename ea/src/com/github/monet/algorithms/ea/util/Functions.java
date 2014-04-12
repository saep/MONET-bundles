package com.github.monet.algorithms.ea.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.DirectedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.Weight;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Misc functions available to the whole EA-Framework.
 *
 * @author Sven Selmke
 *
 */
public class Functions {

	// GENERAL CONSTANTS
	// ########################################################################

	/**
	 * Name of parameters
	 */
	public static final String PARAM_EDGEANNOTATOR   = "edges"; // Name of the annotator used to store edge weights
	public static final String PARAM_SDANNOTATOR     = "sdAnnotator"; // Name of the annootator used to store the start and end node (SSSP)
	public static final String PARAM_SOURCENODE      = "startNode";
	public static final String PARAM_DESTNODE        = "endNode";
	public static final String PARAM_GRAPHDIRECTED   = "directed";

	public static final String TEST_GRAPHDIR         = "../graph_instances/"; // Directory containing the test graphs

	public static boolean IGNORESYSOUT               = false; // can be set to true in order to ignore "System.out."
	public static boolean PRINTLOGFILE               = false; // Print all log messages to a log file?
	public static int LOGGENERATIONS                 = 0; // Can be used by algorithms to save results (0=disabled, 1=results only, 2=everything)
	public static final String LOGPATH               = "./logs/"; // Path where all log files are created
	public static final String LOGFILE               = "./logs/ealog.txt";  // Path to the general log file



	// MISC FUNCTIONS
	// ########################################################################

	/**
	 * Simple join function on double arrays
	 */
	public static String join(double[] arr, String sep) {
		String result = "";
		for (int i = 0; i < arr.length; i++)
			result += (i < arr.length-1) ? (arr[i]+sep) : (arr[i]);
		return result;
	}

	/**
	 * Simple join function on double arrays
	 */
	public static <T> String join(List<T> list, String sep) {
		String result = "";
		for (int i = 0; i < list.size(); i++)
			result += (i < list.size()-1) ? (list.get(i).toString()+sep) : (list.get(i).toString());
		return result;
	}

	/**
	 * Return a double-array containing the given value at each position
	 */
	public static double[] createDoubleArray(int size, double value) {
		double[] arr = new double[size];
		Arrays.fill(arr, value);
		return arr;
	}


	/**
	 * Create a Double-List from given double-String
	 */
	public static List<Double> createDoubleList(double[] arr) {
		List<Double> result = new ArrayList<Double>(arr.length);
		for (int i = 0; i < arr.length; i++)
			result.add(arr[i]);
		return result;
	}


	/**
	 * Gets the i-th element from given collection
	 * @param col collection
	 * @param i index
	 * @return
	 */
	public static <T> T getElement(Collection<T> col, int index) {
		if (col.size() <= index) {
			return null;
		} else {
			Iterator<T> iter = col.iterator();
			for (int i = index; i > 0; i--)
				iter.next();
			return iter.next();
		}
	}


	/**
	 * Calculate euclidean distance between the two given points
	 *
	 * @param pointA
	 * @param pointB
	 * @return
	 */
	public static double getDistance(double[] pointA, double[] pointB){
		assert (pointA.length == pointB.length);
		double sum = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			sum += Math.pow(pointA[i] - pointB[i], 2);
		}
		return Math.sqrt(sum);
	}



	// SPANNING TREE FUNCTIONS
	// ########################################################################

	/**
	 * Returns number of possible spanning trees of a graph with n nodes
	 */
	public static long getPossibleMstNum(int n) {
		return (long)Math.pow(n, n-2);
	}


	/**
	 * Create adjacency List for an UNDIRECTED graph
	 * @param problemAnnotatedGraph
	 * @param edges
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> HashMap<Node, List<Node>> createAdjacencyList(AnnotatedGraph<N, E, G> problemAnnotatedGraph,
			List<Edge> edges) {
		HashMap<Node, List<Node>> adjL = new HashMap<>();

		for (Edge ed : edges) {
			E e = (E) ed;
			Object[] incNodes = problemAnnotatedGraph.getGraph()
					.getIncidentNodes(e).toArray();

			if (adjL.get((Node) incNodes[0]) == null) {
				List<Node> tmp = new ArrayList<>();
				tmp.add((Node) incNodes[1]);
				adjL.put((Node) incNodes[0], tmp);
			} else {
				adjL.get((Node) incNodes[0]).add((Node) incNodes[1]);
			}
			if (adjL.get((Node) incNodes[1]) == null) {
				List<Node> tmp = new ArrayList<>();
				tmp.add((Node) incNodes[0]);
				adjL.put((Node) incNodes[1], tmp);
			} else {
				adjL.get((Node) incNodes[1]).add((Node) incNodes[0]);
			}
		}

		return adjL;
	}


	/**
	 * DFS for a path from cur to dst in Tree represented by given adjacency list.
	 * Post: Stack will contain the nodes on the way from cur to dst.
	 */
	public static boolean getPathDFS(Node cur, Node dst, HashMap<Node, List<Node>> adjL, Stack<Node> stack, Node parent) {
		// Destination reached?
		if (cur.equals(dst)) {
			stack.push(dst);
			return true;
		}
		// Check children
		for (Node n : adjL.get(cur)) {
			// adjL also includes the "parent"! We want to skip that one!
			if (n.equals(parent))
				continue;
			// Recursion
			if (getPathDFS(n, dst, adjL, stack, cur)) {
				stack.push(cur);
				return true;
			}
		}
		// Destination not found in current branch (cur Node)
		return false;
	}
	public static boolean getPathDFS(Node cur, Node dst, HashMap<Node, List<Node>> adjL, Stack<Node> stack) {
		return Functions.getPathDFS(cur, dst, adjL, stack, null);
	}


	/**
	 * DFS for a path from startNode to endNode in Tree represented by given adjacency list.
	 * Post: Stack will contain the nodes on the way from startNode to endNode.
	 */
	public static void getPathDFS_iterative(Node startNode, Node endNode, HashMap<Node, List<Node>> adjL, Stack<Node> path) {

		// Initialize DFS
		HashMap<Node, Boolean> nodesVisited = new HashMap<>();
		Node curNode = startNode;
		nodesVisited.put(curNode, true);
		path.push(curNode);

		// Search (DFS)
		while (!curNode.equals(endNode)) {
			boolean nodeFound = false;

			// Try to find an edge from current node to an unmarked node
			for (Node n : adjL.get(curNode)) {

				if (!nodesVisited.containsKey(n)) {
					path.push(n);
					curNode = n;
					nodesVisited.put(curNode, true);
					nodeFound = true;
					break;
				}
			}

			// No node unmarked node found, trace back
			if (!nodeFound) {
				if (path.size() > 1) {
					path.pop();
				} else {
					Functions.log("DFS in DirectMSTMutator failed.", Functions.LOG_ERROR);
				}
				curNode = path.peek();
			}
		}
	}


	/**
	 * Create a random spanning tree for an undirected graph.
	 *
	 * 1. Start with a random root
	 * 2. For each node curNode:
	 *    a. Follow random path until the current spanning tree is found
	 *    b. Add edges/nodes from the path to the spanning tree
	 *
	 * Implementation of the algorithm specified in
	 * "Generating Random Spanning Trees More Quickly than the Cover Time" by
	 * David Bruce Wilson.
	 */
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Edge> createRandomMST(G g) {
		List<Edge> edges = new ArrayList<Edge>();
		List<N> allNodes = new ArrayList<N>( g.getAllNodes() );

		// HashMap specifying which nodes are reached by the spanning tree
		HashMap<N,Boolean> inTree = new HashMap<N,Boolean>();

		// HashMap specifying a path (used to save the path from a node to the spanning tree)
		HashMap<N,N> nextNodes = new HashMap<N,N>();

		// Select random starting node
		N r = EaRandom.getRandomElement(allNodes);
		inTree.put(r, true);

		// Random Walk
		for (N curNode : allNodes) {
			// Start walking through the graph until we reach the tree
			// Note that cycles are eliminated automatically by resetting nextArr[u]
			N u = curNode;
			while (inTree.get(u) == null) { // FIXED
				N next = g.getIncidentNode(u, EaRandom.getRandomElement(g.getIncidentEdges(u)) );
				nextNodes.put(u, next);
				u = next;
			}
			// Follow the path found in the previous loop and add the nodes/edges to the tree
			u = curNode;
			while (inTree.get(u) == null) {// FIXED
				N next = nextNodes.get(u);
				inTree.put(u, true);
				edges.add( g.getEdge(u, next) );
				u = next;
			}
		}

		return edges;
	}


	/**
	 * Create a random spanning tree for an undirected graph.
	 *
	 * Implementation of the algorithm specified in
	 * "An Efficient EA for the Degree-Constrained MST Problem" by Günther R
	 * Raidl.
	 *
	 * @param g
	 *            graph the ST is created for
	 * @param nodeIdMap
	 *            a map containing a unique id for each node. IMPORTANT: The ids
	 *            have to be numbered from 0 to i-1!
	 */
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Edge> createRandomMST_Kruskal(
					G g,
					HashMap<Node,Integer> nodeIdMap,
					String mode,
					GraphElementAnnotator<E, Weight> annotator,
					HashMap<Node,Integer> degreeCounter,
					int maxDegree
	) {
		List<Edge> edges = new ArrayList<Edge>();
		int nodeCount = g.getAllNodes().size();

		// Shuffle edges (PERFORMANCE: there's probably no need to copy the list)
		List<E> allEdges;
		if (mode.equalsIgnoreCase("shuffle")) {
			allEdges = new ArrayList<E>(g.getAllEdges());
			Collections.shuffle(allEdges, EaRandom.getRand());
		} else if (mode.equalsIgnoreCase("sort") && annotator != null) {
			allEdges = Functions.sortEdgesGeneric(annotator);
		} else {
			allEdges = new ArrayList<E>(g.getAllEdges());
		}

		// Store components of the graph
		SimpleUnionFind uf= new SimpleUnionFind(nodeIdMap.size());

		// Initiate degreeCounter
		if (maxDegree > 0 && degreeCounter != null) {
			for (N n : g.getAllNodes()) {
				degreeCounter.put(n, 0);
			}
		}

		// Create ST
		for (E e : allEdges) {

			// Get nodes of given Edge
			Collection<N> incidentNodes = g.getIncidentNodes(e);
			Iterator<N> nodeIter = incidentNodes.iterator();
			N n1    = nodeIter.next();
			int id1 = nodeIdMap.get(n1);
			N n2    = nodeIter.next();
			int id2 = nodeIdMap.get(n2);

			// Add new edge if components of vertices are not connected yet
			// Add degree check
			if (!uf.connected(id1, id2)) {
				if (maxDegree > 0 && degreeCounter != null) {
					if ((degreeCounter.get(n1) < maxDegree) && (degreeCounter.get(n2) < maxDegree)) {
						edges.add(e);
						uf.union(id1, id2);
						int tmp = degreeCounter.get(n1) + 1;
						degreeCounter.put(n1, tmp);
						tmp = degreeCounter.get(n2) + 1;
						degreeCounter.put(n2, tmp);
					}
				} else {
					edges.add(e);
					uf.union(id1, id2);
				}
			}

			// Finished
			if (edges.size() == nodeCount - 1) {
				break;
			}

		}
		return edges;
	}
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Edge> createRandomMST_Kruskal(G g, HashMap<Node,Integer> nodeIdMap, HashMap<Node,Integer> degreeCounter, int maxDegree) {
		return Functions.createRandomMST_Kruskal(g, nodeIdMap, "shuffle", null, degreeCounter, maxDegree);
	}
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Edge> createRandomMST_Kruskal(G g, HashMap<Node,Integer> nodeIdMap) {
		return Functions.createRandomMST_Kruskal(g, nodeIdMap, "shuffle", null, null, 0);
	}


	/**
	 * Transform given graph into a complete graph by adding edges weighted with infinity
	 * works only on undirected graphs
	 *
	 * @param problemGraph
	 * @param weightAnnotator
	 * @param numObjectives
	 */
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> boolean makeGraphComplete(
			Graph<N, E, G> problemGraph,
			GraphElementAnnotator<E, Weight> weightAnnotator,
			int numObjectives
	) {
		// Check if there's anything to do
		if (Functions.isGraphComplete(problemGraph)) {
			return false;
		}

		// Make graph complete
		Weight infty = new Weight(Functions.createDoubleArray(numObjectives,Double.MAX_VALUE));
		int numEdgesOld = problemGraph.getNumEdges();
		for (N n1 : problemGraph.getAllNodes()) {
			for (N n2 : problemGraph.getAllNodes()) {
				if (problemGraph.getEdge((N)n1, (N)n2) == null && !n1.equals(n2)) {
					E e = problemGraph.addEdge((N)n1, (N)n2);
					weightAnnotator.setAnnotation(e, infty);
				}
			}
		}

		// Logging
		if (numEdgesOld != problemGraph.getNumEdges()) {
			Functions.log(
					"Note: Given input graph not complete! " + (problemGraph.getNumEdges()-numEdgesOld) + " new edges added." +
					" The graph has " + problemGraph.getNumNodes() + " Nodes and " + problemGraph.getNumEdges() + " Edges." +
					" There are " + Functions.getPossibleMstNum(problemGraph.getNumNodes()) + " possible spanning trees."
			);
			return true;
		}
		return false;
	}


	/**
	 * Checks if given graph is a complete graph
	 *
	 * @param problemGraph
	 */
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> boolean isGraphComplete(Graph<N, E, G> problemGraph) {
		int nodes = problemGraph.getNumNodes();
		int edges = problemGraph.getNumEdges();
		if (problemGraph instanceof DirectedGraph) {
			return (edges == nodes*nodes - nodes);
		} else {
			return (edges == (nodes*nodes - nodes)/2);
		}
		//for (N n1 : problemGraph.getAllNodes())
		//	for (N n2 : problemGraph.getAllNodes())
		//		if (problemGraph.getEdge((N)n1, (N)n2) == null && !n1.equals(n2))
		//			return false;
		//return true;
	}


	/**
	 * Sort edges according to the annotator (uses first weight only)
	 */
	public static List<Edge> sortEdges(GraphElementAnnotator<Edge, Weight> annotator) {
		// Helper-Class in order to compare annotators
		class AnnotatorComparator implements Comparator<Edge> {
			private GraphElementAnnotator<Edge, Weight> annotator;
			// Constructor
			AnnotatorComparator(GraphElementAnnotator<Edge, Weight> annotator) {
				this.annotator = annotator;
			}
			// Compare
			@Override
			public int compare(Edge e1, Edge e2) {
				double w1 = this.annotator.getAnnotation(e1).getFirstWeight();
				double w2 = this.annotator.getAnnotation(e2).getFirstWeight();
				return Double.compare(w1, w2);
			}
		}
		// Sorting
		List<Edge> sortedEdges = new ArrayList<Edge>(annotator.getAnnotatedElements());
		Comparator<Edge> comparator = new AnnotatorComparator(annotator);
		Collections.sort(sortedEdges, comparator);
		return sortedEdges;
	}


	/**
	 * Sort edges according to the annotator (uses first weight only)
	 */
	public static <E extends Edge> List<E> sortEdgesGeneric(GraphElementAnnotator<E, Weight> annotator) {
		// Helper-Class in order to compare annotators
		class AnnotatorComparator<Ed extends Edge> implements Comparator<Ed> {
			private GraphElementAnnotator<Ed, Weight> annotator;
			// Constructor
			AnnotatorComparator(GraphElementAnnotator<Ed, Weight> annotator) {
				this.annotator = annotator;
			}
			// Compare
			@Override
			public int compare(Ed e1, Ed e2) {
				double w1 = this.annotator.getAnnotation(e1).getFirstWeight();
				double w2 = this.annotator.getAnnotation(e2).getFirstWeight();
				return Double.compare(w1, w2);
			}
		}
		// Sorting
		List<E> sortedEdges = new ArrayList<E>(annotator.getAnnotatedElements());
		Comparator<E> comparator = new AnnotatorComparator<E>(annotator);
		Collections.sort(sortedEdges, comparator);
		return sortedEdges;
	}





	// SINGLE SOURCE SHORTEST PATH FUNCTIONS
	// ########################################################################

	/**
	 * Calculates a random path from startNode to endNode.
	 * This method works for directed and undirected graphs.
	 *
	 * Algorithm:
	 * 		1. Find an edge from current node to an unmarked node
	 * 			exists: continue there
	 * 			does not exist: trace back
	 *
	 * Alternatives:
	 * 		1. Random weights and Dijkstra or
	 * 		2. Random ST and remove edges
	 *
	 * @param g
	 * @param startNode
	 * @param endNode
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> List<Node> createRandomPath(Graph<N, E, G> g, N startNode, N endNode) {
		List<Node> path = new ArrayList<Node>();
		if (startNode.equals(endNode)) return path;
		N curNode, prevNode;

		// Keep track of visited nodes
		HashMap<N, Boolean> visitedNodes = new HashMap<N, Boolean>();
		for (N n : g.getAllNodes()) {
			visitedNodes.put(n, false); // remove (check for null instead)
		}

		// Visit startNode
		visitedNodes.put(startNode, true);
		path.add(startNode);
		curNode = startNode;
		prevNode = null; // remove

		// Till endNode is visit
		while (!curNode.equals(endNode)) {
			boolean nodeFound = false;

			// Get edges from curNode to other nodes
			Collection<E> incEdges;
			if (g instanceof DirectedGraph) {
				incEdges = new ArrayList<E>(((DirectedGraph)g).getOutgoingEdges(curNode));
				//System.out.println("Getting " + incEdges.size() + " directed edges.");
			} else {
				incEdges = new ArrayList<E>(g.getIncidentEdges(curNode));
				//System.out.println("Getting " + incEdges.size() + " undirected edges.");
			}

			// Try to find an edge from current node to an unmarked node
			while (incEdges.size() > 0) {
				E e = EaRandom.getRandomElement(incEdges);
				N u = g.getIncidentNode(curNode, e);
				//System.out.println("Trying " + u);
				if (!visitedNodes.get(u)) {
					path.add(u);
					visitedNodes.put(u, true);
					prevNode = curNode;
					curNode = u;
					nodeFound = true;
					//System.out.println("Found " + u);
					break;
				}
				incEdges.remove(e);
			}

			// Not found? Trace back!
			if (!nodeFound) {
				//System.out.println("Failed. Traceback to " + prevNode);
				path.remove(curNode);
				curNode = prevNode;
				// If curNode is null then we can't trace back any further. There isn't a path to target node!
				if (curNode == null) {
					Functions.log("Error! There's no way from given starting node " + startNode.toString() + " to " + endNode.toString() + "!");
					return null;
				}
				// Trace back the edge (note: not possible for starting node!)
				if (path.size() > 1) {
					prevNode = (N)path.get(path.size()-2);
				} else {
					prevNode = null;
				}
			}

		} // end while

		return path;
	}


	/**
	 * Checks whether a path from given start to given end node exists.
	 *
	 * @param g
	 * @param startNode
	 * @param endNode
	 * @return true if a path exists
	 */
	public static <G extends Graph<N, E, G>, N extends Node, E extends Edge> boolean pathExists(Graph<N, E, G> g, N startNode, N endNode) {
		return (Functions.createRandomPath(g, startNode, endNode) != null);
	}



	// WEIGHT FUNCTIONS (GRAPH WEIGHT)
	// ########################################################################

	/**
	 * Create double[] array from given weight.
	 *
	 * @param w
	 *            weight to extract the values from
	 *
	 * @return array representing the weight
	 */
	public static double[] weightToDoubleArray(Weight w) {
		double[] objectiveValues = new double[w.getDimension()];
		for (int i = 0; i < w.getDimension(); i++) {
			objectiveValues[i] = w.getWeight(i);
		}
		return objectiveValues;
	}


	/**
	 * Calculate the Nadir-Point
	 */
	public static double[] getNadirPoint(List<Weight> weights, boolean minimization) {
		if (weights.size() == 0) {
			return null;
		}
		// Initialize
		int dim = weights.get(0).getDimension();
		double[] nadir = new double[dim];
		for (int i = 0; i < dim; i++) {
			nadir[i] = weights.get(0).getWeight(i);
		}
		// Get nadir point
		if(minimization){ //FIXED
			for (Weight w : weights) {
				for (int i = 0; i < dim; i++) {
					if (nadir[i] < w.getWeight(i)) {
						nadir[i] = w.getWeight(i);
					}
				}
			}
		} else{
			for (Weight w : weights) {
				for (int i = 0; i < dim; i++) {
					if (nadir[i] > w.getWeight(i)) {
						nadir[i] = w.getWeight(i);
					}
				}
			}
		}

		return nadir;
	}


	/**
	 * Calculate sum of all weights as a double array
	 */
	public static double[] sumWeights(List<Weight> weights) {
		if (weights.size() == 0) {
			return null;
		}
		// Initialize
		int dim = weights.get(0).getDimension();
		double[] sum = new double[dim];
		for (int i = 0; i < dim; i++) {
			sum[i] = 0;
		}
		// Calculate sum
		for (Weight w : weights) {
			for (int i = 0; i < dim; i++) {
				sum[i] += w.getWeight(i);
			}
		}
		return sum;
	}



	// LOGGING
	// ########################################################################

	private static Logger logger = null;

	public static final int LOG_ERROR   = 0;
	public static final int LOG_WARNING = 1;
	public static final int LOG_DEBUG   = 2;
	public static final int LOG_PRINT   = 3; // Can be used to not spam the logger
	public static final int LOG_TEST    = 4; // Output of tests (JUnit)


	/**
	 * Log the given String to logger, console, and/or file.
	 * The given log message should not end with a newline.
	 */
	public static void log(String msg, int level) {
		// Log using console or logger
		if (Functions.logger != null) {
			switch (level) {
				case LOG_ERROR:   Functions.logger.log(Level.FATAL, msg);               break;
				case LOG_WARNING: Functions.logger.log(Level.WARN,  msg);               break;
				case LOG_DEBUG:   Functions.logger.log(Level.DEBUG, msg);               break;
				case LOG_PRINT:   if (!Functions.IGNORESYSOUT) System.out.println(msg); break;
				case LOG_TEST:    if (!Functions.IGNORESYSOUT) System.out.println(msg); break;
			}
		} else {
			if (!Functions.IGNORESYSOUT) System.out.println(msg);
		}
		// Save to log file as well?
		if (Functions.PRINTLOGFILE && Functions.LOGFILE != null)  {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(Functions.LOGFILE, true));
				writer.write(msg + "\n");
			} catch (IOException e) {
				System.out.println("Error while writing the log file: " + e.getMessage());
			} finally {
				try {
					if (writer != null)
						writer.close();
				} catch (IOException e) {
				}
			}
		}
	}


	/**
	 * Log the given message as a Debug message
	 */
	public static void log(String msg) {
		Functions.log(msg, Functions.LOG_DEBUG);
	}


	/**
	 * Clear the log file
	 */
	public static void clearLogFile() {
		if (Functions.LOGFILE != null) {
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(Functions.LOGFILE);
			} catch (FileNotFoundException e1) {
			} finally {
				if (pw != null) pw.close();
			}
		}
	}

	public static void setLogger(Logger logger) {
		Functions.logger = logger;
	}
	public static Logger getLogger() {
		return Functions.logger;
	}



	// NAMEABLE INTERFACE
	// ########################################################################

	/**
	 * An array of all available Classes implementing the Nameable-Interface
	 */
	public static List<Class<?>> availableOperators;


	/**
	 * Add an available operator
	 */
	public static void addOperator(Class<?> c) {
		if (availableOperators == null) {
			availableOperators = new ArrayList<Class<?>>();
		}
		if (Nameable.class.isAssignableFrom(c)) {
			Functions.log("Registering new operator " + c.getName() + ".");
			availableOperators.add(c);
		} else {
			Functions.log("Cannot register operator " + c.getName() + " because it is not nameable.", Functions.LOG_WARNING);
		}
	}


	/**
	 * Get a List of all available operator names
	 */
	public static List<String> getOperatorNames() {
		List<String> result = new ArrayList<String>();
		if (availableOperators != null) {
			for (Class<?> c : availableOperators) {
				try {
					result.add(((Nameable)c.newInstance()).getName());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}


	/**
	 * Get an instance of the operator corresponding to the given name.
	 */
	public static Nameable getOperatorByName(String name) {
		if (availableOperators == null) {
			Functions.log("Warning! No operators initialized! Operator '" + name + "' not found.", Functions.LOG_WARNING);
			return null;
		}

		// Search the operator and return it (if found)
		for (Class<?> c : availableOperators) {
			try {
				if (((Nameable)c.newInstance()).getName().equals(name)) {
					return (Nameable)c.newInstance();
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// Operator not found
		Functions.log("Warning! Operator '" + name + "' not found in getOperatorByName!", Functions.LOG_WARNING);
		return null;
	}


	/**
	 * Get an instance of the operator corresponding to the given name and casts
	 * the result to the given class
	 */
	public static <T extends Nameable> T getOperatorByName(String name, Class<T> castAs) {
		Nameable op = Functions.getOperatorByName(name);

		// Catch errors (operator not found or of wrong type)
		if (op == null) {
			return null;
		}
		if (!castAs.isInstance(op)) {
			Functions.log("Error! Operator " + name + " is not of required type " + castAs + "!", Functions.LOG_ERROR);
			return null;
		}

		// Cast and return
		T obj = castAs.cast(op);
		return obj;
	}


	/**
	 * Get an instance of the operator corresponding to the given name and casts
	 * the result to the given class. If the operator is found, its
	 * configuration method will be called using the given parameter map (Same
	 * as getOperatorByName, but also configures the operator).
	 */
	public static <T extends Nameable> T getConfiguredOperatorByName(String name, Class<T> castAs, Map<String,Object> params) {
		// Get name of operator we are looking for and actually look for it
		T op = Functions.getOperatorByName(name, castAs);

		// Configure the operator and return it (if configuration was successfull)
		if (op != null) {
			if (!op.configure(params)) {
				Functions.log("Error! Operator " + name + " was not configured successful! Returning null.", Functions.LOG_ERROR);
				return null;
			}
		}
		return op;
	}


	/**
	 * Extract the parameter of given name (paraName) from the parameter map
	 * (params) and search for the operator.
	 *
	 * NOTE: THE PARAMETER paramName SPECIFIES A KEY OF THE PARAMETERMAP. THE
	 * VALUE FOUND FOR THAT KEY THEN IS USED AS NAME OF THE OPERATOR.
	 *
	 * e.g. using paraName = "Mutator" the entry "Mutator"->"PrueferMutator"
	 * might be found in the param map. The method then searches for the
	 * "PrueferMutator", configures it and returns the result.
	 */
	public static <T extends Nameable> T getConfiguredOperatorByParamName(Map<String,Object> params, String paramName, Class<T> castAs) {
		// Get name of operator we are looking for and actually look for it
		String opName = Functions.getParam(params, paramName, String.class, "");
		return Functions.getConfiguredOperatorByName(opName, castAs, params);
	}



	// PARAMETER FUNCTIONS
	// ########################################################################

	/**
	 * Sets a new parameter in the given map ONLY IF the key doesn't have a
	 * value yet (or if it's null). I.e. this method behaves like
	 * "put(key,value)" except that it doesn't overwrite existing values.
	 *
	 * @param parameters
	 *            map to add the value to
	 * @param key
	 *            key of the value
	 * @param value
	 *            value to insert
	 *
	 * @return true if the parameter has been set
	 */
	public static boolean setNewParam(Map<String,Object> parameters, String key, Object value) {
		if (parameters.get(key) == null) {
			parameters.put(key, value);
			return true;
		}
		return false;
	}


	/**
	 * Same as parameters.put(key, value).
	 *
	 * @param parameters
	 *            map to add the value to
	 * @param key
	 *            key of the value
	 * @param value
	 *            value to insert
	 *
	 */
	public static Object setParam(Map<String,Object> parameters, String key, Object value) {
		return parameters.put(key, value);
	}


	/**
	 * Get a parameter from the given parameter map. If no parameter with given
	 * name exists, the name will be trimmed and the method will then try again,
	 * ignoring upper/lower case. If still no parameter is found, the default
	 * value will be returned.
	 *
	 * @param parameters
	 *            parameter map containing the desired parameter
	 * @param name
	 *            name of the parameter to get
	 * @param castAs
	 *            type of the parameter
	 * @param def
	 *            default value
	 *
	 * @return parameter 'name' casted to the given type
	 */
	public static <T> T getParam(Map<String,Object> parameters, String name, Class<T> castAs, T def) {
		if (parameters == null) {
			Functions.log("Parameter map is null! Using default value '" + def + "'.");
			return def;
		}

		// Get the parameter
		// if not found: try to trim the name
		// if not found: try to ignore case
		Object param = parameters.get(name);
		if (param == null) {
			name  = name.trim();
			param = parameters.get(name);
		}
		if (param == null) {
			for (String key : parameters.keySet()) {
				if (key.equalsIgnoreCase(name)) {
					name  = key;
					param = parameters.get(name);
					break;
				}
			}
		}

		// Check the parameter
		if (param == null) {
			Functions.log("Parameter '" + name + "' not found. Using default value '" + def + "'.");
			return def;
		}


		// Number to Number
		if (castAs.equals(Long.class) && Number.class.isInstance(param)) {
			return castAs.cast(((Number) param).longValue());
		}
		if (castAs.equals(Double.class) && Number.class.isInstance(param)) {
			return castAs.cast(((Number) param).doubleValue());
		}


		// Anything to String
		if (castAs.equals(String.class)) {
			return castAs.cast(param.toString());
		}


		// String to Anything
		// If found type is string but expected type is not, try to parse the string
		if (!castAs.isInstance(param) && param.getClass().equals(String.class)) {
			String str = (String)param;
			str = str.trim();

			// Cast to Integer
			if (castAs.equals(Integer.class)) {
				try {
					param = Integer.parseInt(str);
				} catch (NumberFormatException e) {
					Functions.log("Failed to parse string parameter '" + str + "' as Integer.", Functions.LOG_WARNING);
				}
			}

			// Cast to Long
			if (castAs.equals(Long.class)) {
				try {
					if (str.endsWith("l")) {
						str = str.substring(0, str.length()-1);
					}
					param = Long.parseLong(str);
				} catch (NumberFormatException e) {
					Functions.log("Failed to parse string parameter '" + str + "' as Long.", Functions.LOG_WARNING);
				}
			}

			// Cast to Double
			if (castAs.equals(Double.class)) {
				try {
					if (str.endsWith("d")) {
						str = str.substring(0, str.length()-1);
					}
					param = Double.parseDouble(str);
				} catch (NumberFormatException e) {
					Functions.log("Failed to parse string parameter '" + str + "' as Double.", Functions.LOG_WARNING);
				}
			}

			// Cast to Boolean
			if (castAs.equals(Boolean.class)) {
				try {
					param = Boolean.parseBoolean(str);
				} catch (Exception e) {
					// Try to parse yes/no as well
					if (str.equalsIgnoreCase("yes")) {
						param = true;
					} else if (str.equalsIgnoreCase("no")) {
						param = false;
					} else {
						Functions.log("Failed to parse string parameter '" + str + "' as Boolean.", Functions.LOG_WARNING);
					}
				}
			}

		}


		// Wrong type => return default value
		if (!castAs.isInstance(param)) {
			Functions.log("Error! Parameter " + name + " is not of required type " + castAs + " (found: " + param.getClass().getName() + ")! Trying to use default value '" + def + "'.", Functions.LOG_ERROR);
			return def;
		}

		// Cast value and return
		return castAs.cast(param);
	}

	/**
	 * Get a parameter from the given parameter map. Default value is null.
	 *
	 * @param parameters
	 *            parameter map containing the desired parameter
	 * @param name
	 *            name of the parameter to get
	 * @param castAs
	 *            type of the parameter
	 *
	 * @return parameter 'name' casted to the given type
	 */
	public static <T> T getParam(Map<String,Object> parameters, String name, Class<T> castAs) {
		return Functions.getParam(parameters, name, castAs, null);
	}

	/**
	 * Check if given parameter exists.
	 *
	 * @param parameters
	 *            parameter map containing the desired parameter
	 * @param name
	 *            name of the parameter to get
	 *
	 * @return parameter 'name' casted to the given type
	 */
	public static boolean isParam(Map<String,Object> parameters, String name) {
		return Functions.getParam(parameters, name, Object.class, null) != null;
	}

}
