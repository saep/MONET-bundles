package com.github.monet.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.HeterogeneousHashAnnotatorContainer;
import com.github.monet.graph.SimpleAbstractGraph;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.weighted.LabelSet;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.GraphParser;
import com.github.monet.worker.Job;

/**
 * Parser for reading in graphs with multiple edge weights based on the simple
 * text based MONet graph format.
 *
 * @author Jakob Bossek
 * @version 0.1
 */
public class MonetHeurParser implements GraphParser {
	/**
	 * @param fileName
	 *            the filename of the file containing the multi weighted graph
	 *            in MONet graph format
	 * @param job
	 *            The associated {@link Job}
	 *
	 * @return Object
	 * @see SimpleUndirectedGraph
	 * @see GraphElementHashAnnotator
	 */

	public MonetHeurParser() {
	}

	@Override
	public Object parse(String inputFile, Job job) {
		// initialize graph, annotator and some more stuff
		BufferedReader in = null;
		Map<String, Object> params = null;
		if (job != null) {
			params = job.getParserParameters();
			if (params == null) {
				params = job.getParameters();
			}
		}
		SimpleDirectedGraph g = new SimpleDirectedGraph();
		GraphElementHashAnnotator<SimpleEdge, Weight> annotation;
		annotation = new GraphElementHashAnnotator<>();
		GraphElementHashAnnotator<SimpleNode, LabelSet> heuristic = new GraphElementHashAnnotator<>();
		int startNodeid = 0;
		int endNodeid = 0;
		SimpleNode startNode = null;
		SimpleNode endNode = null;
		try {
			in = new BufferedReader(new FileReader(inputFile));

			// read number of nodes
			ArrayList<SimpleNode> nodes = new ArrayList<>();
			String line = in.readLine();
			int numNodes = Integer.parseInt(line);

			// Set id for start and end nodes
			// is the parameter map passed?
			if (params != null) {
				// If index is out of bound, set startNode to 0
				if (params.get("startNodeid") != null) {
					startNodeid = (int) params.get("startNodeid");
					if (startNodeid < 0 || startNodeid > numNodes - 1) {
						startNodeid = 0;
					}
				}
				// If index is out of bounce, set endNode to numNodes
				if (params.get("endNodeid") != null) {
					endNodeid = (int) params.get("endNodeid");
					if (endNodeid < 0 || endNodeid > numNodes - 1) {
						endNodeid = numNodes - 1;
					}
				}
			}

			// create node objects
			for (int i = 0; i < numNodes; i++) {
				nodes.add(g.addNode());
				heuristic.setAnnotation(nodes.get(i), new LabelSet());

				// Check if the Node has to be added as startNode or endNode
				if (params != null) {
					if (i == startNodeid) {
						params.put("startNode", nodes.get(i));
						startNode = nodes.get(i);
					}
					if (i == endNodeid) {
						params.put("endNode", nodes.get(i));
						endNode = nodes.get(i);
					}
				}

			}

			// read number of edges
			line = in.readLine();
			int numEdges = Integer.parseInt(line);

			// read number of objectives
			line = in.readLine();
			int numObjectives = Integer.parseInt(line);

			while ((line = in.readLine()) != "START_HEURISTIC") {

				// split each line by single space
				String[] tokens = line.split(" ");
				int n = tokens.length;

				// first two tokens represent nodes
				SimpleNode source = nodes.get(Integer.parseInt(tokens[0]) - 1);
				SimpleNode dest = nodes.get(Integer.parseInt(tokens[1]) - 1);

				// the remainder represents all the edge weights
				double w[] = new double[numObjectives];
				for (int i = 2; i < n; i++) {
					w[i - 2] = Double.parseDouble(tokens[i]);
				}

				// encapsulate edge weights and set annotation
				Weight weight = new Weight(w);
				SimpleEdge edge = g.addEdge(source, dest);
				annotation.setAnnotation(edge, weight);
			}

			// read heuristic
			while ((line = in.readLine()) != null) {
				String[] tokens = line.split(" ");
				int n = tokens.length;
				// first token represents node
				SimpleNode node = nodes.get(Integer.parseInt(tokens[0]) - 1);
				// the remainder represents the heuristic vector
				double w[] = new double[numObjectives];
				for (int i = 2; i < n; i++) {
					w[i - 2] = Double.parseDouble(tokens[i]);
				}
				// encapsulate edge weights and set annotation
				Weight weight = new Weight(w);
				heuristic.getAnnotation(node).insertLabel(weight);
			}

		} catch (IOException e) {
			System.err.println("ERROR OCCURED: " + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// HeterogeneousHashAnnotatorContainer annotatorMap = new
		// HeterogeneousHashAnnotatorContainer();
		// annotatorMap.put("edges", annotation);
		AnnotatedGraph annotatedGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(
					(SimpleDirectedGraph) g);
		annotatedGraph.addAnnotator("edges", annotation);
		annotatedGraph.addAnnotator("HEURISTIC", heuristic);

		if (startNode != null && endNode != null) {
			GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();
			sdAnnotator.setAnnotation(startNode, "startNode");
			sdAnnotator.setAnnotation(endNode, "endNode");
			annotatedGraph.addAnnotator("sdAnnotator", sdAnnotator);
		}
		return annotatedGraph;
	}

	public static void main(String[] args) {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		try {
			GraphParser gp = new MonetHeurParser();
			AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp
					.parse("src/monet/graph/parser/graph.txt", null);
			System.out.println(g.getGraph());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
