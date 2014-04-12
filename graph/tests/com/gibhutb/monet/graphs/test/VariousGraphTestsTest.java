/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gibhutb.monet.graphs.test;

import static org.junit.Assert.*;

import java.util.ArrayList;



import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.tests.VariousGraphTests;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class VariousGraphTestsTest {

	@Test
	public void testIsConnected() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		SimpleUndirectedGraph g = ag.getGraph();

		VariousGraphTests<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graphTests =
				new VariousGraphTests<>();

		assertTrue(graphTests.isConnected(g));
		g.deleteEdge(lg.edge_ids.get("4,5"));
		assertFalse(graphTests.isConnected(g));
	}

	@Test
	public void testIsSpanningTree() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		SimpleUndirectedGraph g = ag.getGraph();

		VariousGraphTests<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graphTests =
				new VariousGraphTests<>();

		assertFalse(graphTests.isSpanningTree(g));

		ArrayList<SimpleEdge> edges1 = new ArrayList<>();
		edges1.add(lg.edge_ids.get("1,2"));
		edges1.add(lg.edge_ids.get("1,3"));
		edges1.add(lg.edge_ids.get("1,4"));
		edges1.add(lg.edge_ids.get("3,4"));
		SimpleUndirectedGraph g1 = g.getSubgraph(g.getAllNodes(), edges1);
		assertFalse(graphTests.isSpanningTree(g1));

		ArrayList<SimpleEdge> edges2 = new ArrayList<>();
		edges2.add(lg.edge_ids.get("1,2"));
		edges2.add(lg.edge_ids.get("1,3"));
		edges2.add(lg.edge_ids.get("1,4"));
		edges2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph g2 = g.getSubgraph(g.getAllNodes(), edges2);
		assertTrue(graphTests.isSpanningTree(g2));

		ArrayList<SimpleEdge> edges3 = new ArrayList<>();
		edges3.add(lg.edge_ids.get("1,2"));
		edges3.add(lg.edge_ids.get("1,3"));
		edges3.add(lg.edge_ids.get("1,4"));
		edges3.add(lg.edge_ids.get("3,4"));
		SimpleUndirectedGraph g3 = g.getSubgraphWithImpliedNodes(edges3); // Subtle difference compared to test 1
		assertFalse(graphTests.isSpanningTree(g3));
	}
}
