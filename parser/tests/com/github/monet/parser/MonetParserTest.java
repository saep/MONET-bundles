package com.github.monet.parser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Parser;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.GraphParser;

/**
 * Testcase for MonetParser.
 *
 * @author Jakob Bossek
 * @see MonetParser
 * @see Parser
 */
public class MonetParserTest {

	@Test
	public void testParsing() {
		// initialize parser and parse graph test file
		GraphParser parser = new MonetParser();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> annotatedGraph = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) parser
				.parse("src/monet/parser/graph.txt", null);

		/*
		 * For testing purposes construct matrix of weights similar to these
		 * defined in graph.txt file.
		 */
		double[][][] distMatrix = new double[4][4][2];
		distMatrix[0][1] = distMatrix[1][0] = new double[] { 5.4, 3.2 };
		distMatrix[0][2] = distMatrix[2][0] = new double[] { 4.6, 2.2 };
		distMatrix[0][3] = distMatrix[3][0] = new double[] { 3.5, 2.1 };
		distMatrix[1][2] = distMatrix[2][1] = new double[] { 9.4, 1.4 };
		distMatrix[1][3] = distMatrix[3][1] = new double[] { 10.3, 8.9 };
		distMatrix[2][3] = distMatrix[3][2] = new double[] { 4.8, 1.2 };

		SimpleUndirectedGraph graph = annotatedGraph.getGraph();
		GraphElementAnnotator<Edge, Weight> annotator = annotatedGraph
				.getAnnotator("edges", GraphElementAnnotator.class);

		// check number of nodes and edges
		assertEquals("number of nodes must be equal to 4", graph.getNumNodes(),
				4);
		assertEquals("number of edges must be equal to 6", graph.getNumEdges(),
				6);
		// System.out.println(annotatedGraph);

		// check if all edges exist and are annotated with the correct weights
		Iterator<SimpleNode> nodeIterator = graph.getAllNodes().iterator();
		int nodeAID = 0;
		while (nodeIterator.hasNext()) {
			SimpleNode nodeA = nodeIterator.next();
			int nodeBID = 0;
			Iterator<SimpleNode> nodeIterator2 = graph.getAllNodes().iterator();
			while (nodeIterator2.hasNext()) {
				SimpleNode nodeB = nodeIterator2.next();
				if (nodeA != nodeB) {
					Edge edge = graph.getEdge(nodeA, nodeB);
					Weight weight = annotator.getAnnotation(edge);
					assertTrue(
							"weight of edge ("
									+ nodeAID
									+ ", "
									+ nodeBID
									+ ") "
									+ "should be "
									+ Arrays.toString(distMatrix[nodeAID][nodeBID])
									+ ", not "
									+ Arrays.toString(weight.getWeights()),
							Arrays.equals(distMatrix[nodeAID][nodeBID],
									weight.getWeights()));
				}
				++nodeBID;
			}
			++nodeAID;
		}

	}
}
