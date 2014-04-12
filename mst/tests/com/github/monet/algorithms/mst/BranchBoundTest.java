/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.monet.algorithms.mst;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

import org.junit.Test;

import com.github.monet.algorithms.mst.BranchBound;

/**
 *
 *
 */
public class BranchBoundTest {

	@Test
	public void testBranchBound() {
		// Get example graph
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		SimpleUndirectedGraph g = ag.getGraph();

		// Construct extreme efficient solutions
		ArrayList<SimpleEdge> s1edges = new ArrayList<>();
		s1edges.add(lg.edge_ids.get("1,2"));
		s1edges.add(lg.edge_ids.get("1,3"));
		s1edges.add(lg.edge_ids.get("2,4"));
		s1edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph s1 = g.getSubgraph(g.getAllNodes(), s1edges);
		ArrayList<SimpleEdge> s2edges = new ArrayList<>();
		s2edges.add(lg.edge_ids.get("1,3"));
		s2edges.add(lg.edge_ids.get("1,4"));
		s2edges.add(lg.edge_ids.get("2,3"));
		s2edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph s2 = g.getSubgraph(g.getAllNodes(), s2edges);
		ArrayList<SimpleEdge> s3edges = new ArrayList<>();
		s3edges.add(lg.edge_ids.get("1,2"));
		s3edges.add(lg.edge_ids.get("2,3"));
		s3edges.add(lg.edge_ids.get("2,4"));
		s3edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph s3 = g.getSubgraph(g.getAllNodes(), s3edges);
		ArrayList<SimpleEdge> s4edges = new ArrayList<>();
		s4edges.add(lg.edge_ids.get("1,2"));
		s4edges.add(lg.edge_ids.get("1,4"));
		s4edges.add(lg.edge_ids.get("2,3"));
		s4edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph s4 = g.getSubgraph(g.getAllNodes(), s4edges);

		GraphElementAnnotator<SimpleEdge, Weight> annotator = ag.getAnnotator(
				"WEIGHTS", GraphElementAnnotator.class);
		GraphElementWeightAnnotator<SimpleEdge> weightAnnotator =
				new GraphElementWeightAnnotator<>(annotator);
		WeightedEdgesCalculator<SimpleNode, SimpleEdge, SimpleUndirectedGraph> calc =
				new WeightedEdgesCalculator<>(weightAnnotator);
		ParetoFront<SimpleNode, SimpleEdge, SimpleUndirectedGraph> extremeEfficient =
				new ParetoFront<>(calc);
		extremeEfficient.add(s1);
		extremeEfficient.add(s2);
		extremeEfficient.add(s3);
		extremeEfficient.add(s4);

		// Execute k-best second phase
		BranchBound<SimpleNode, SimpleEdge, SimpleUndirectedGraph> bb =
				new BranchBound<>();
		ParetoFront<SimpleNode, SimpleEdge, SimpleUndirectedGraph> efficient =
				bb.secondPhase(ag, "WEIGHTS", extremeEfficient);

		// Construct non-extreme efficient solutions
		ArrayList<SimpleEdge> e3edges = new ArrayList<>();
		e3edges.add(lg.edge_ids.get("1,3"));
		e3edges.add(lg.edge_ids.get("2,3"));
		e3edges.add(lg.edge_ids.get("2,4"));
		e3edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph e3 = g.getSubgraph(g.getAllNodes(), e3edges);
		ArrayList<SimpleEdge> e5edges = new ArrayList<>();
		e5edges.add(lg.edge_ids.get("1,2"));
		e5edges.add(lg.edge_ids.get("1,3"));
		e5edges.add(lg.edge_ids.get("1,4"));
		e5edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph e5 = g.getSubgraph(g.getAllNodes(), e5edges);
		ArrayList<SimpleEdge> e8edges = new ArrayList<>();
		e8edges.add(lg.edge_ids.get("1,3"));
		e8edges.add(lg.edge_ids.get("1,4"));
		e8edges.add(lg.edge_ids.get("2,4"));
		e8edges.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph e8 = g.getSubgraph(g.getAllNodes(), e8edges);

		// Test
		assertTrue("Found " + String.valueOf(efficient.size())
				+ " efficient solutions instead of 3", efficient.size() == 3);
		assertTrue(efficient.contains(e3));
		assertTrue(efficient.contains(e5));
		assertTrue(efficient.contains(e8));
	}
}
