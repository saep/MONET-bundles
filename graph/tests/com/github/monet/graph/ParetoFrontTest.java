package com.github.monet.graph;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class ParetoFrontTest {

	@Test
	public void testEquals() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs
				.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		GraphElementWeightAnnotator<SimpleEdge> wa =
				new GraphElementWeightAnnotator<>(
				ag.getAnnotator("WEIGHTS", GraphElementAnnotator.class));
		WeightedEdgesCalculator<SimpleNode, SimpleEdge, SimpleUndirectedGraph> wec =
				new WeightedEdgesCalculator<>(wa);
		ParetoFront<SimpleNode, SimpleEdge, SimpleUndirectedGraph> pf1 =
				new ParetoFront<>(wec);
		ParetoFront<SimpleNode, SimpleEdge, SimpleUndirectedGraph> pf2 =
				new ParetoFront<>(wec);

		ArrayList<SimpleEdge> edges1_1 = new ArrayList<>();
		edges1_1.add(lg.edge_ids.get("1,2"));
		edges1_1.add(lg.edge_ids.get("1,3"));
		edges1_1.add(lg.edge_ids.get("2,4"));
		edges1_1.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg1_1 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges1_1);
		ArrayList<SimpleEdge> edges2_1 = new ArrayList<>();
		edges2_1.add(lg.edge_ids.get("1,2"));
		edges2_1.add(lg.edge_ids.get("2,3"));
		edges2_1.add(lg.edge_ids.get("2,4"));
		edges2_1.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg2_1 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges2_1);
		ArrayList<SimpleEdge> edges3_1 = new ArrayList<>();
		edges3_1.add(lg.edge_ids.get("1,2"));
		edges3_1.add(lg.edge_ids.get("2,3"));
		edges3_1.add(lg.edge_ids.get("1,4"));
		edges3_1.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg3_1 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges3_1);
		ArrayList<SimpleEdge> edges4_1 = new ArrayList<>();
		edges4_1.add(lg.edge_ids.get("1,3"));
		edges4_1.add(lg.edge_ids.get("1,4"));
		edges4_1.add(lg.edge_ids.get("2,3"));
		edges4_1.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg4_1 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges4_1);

		assertTrue(pf1.isEmpty());
		pf1.add(sg3_1);
		pf1.add(sg1_1);
		pf1.add(sg4_1);
		pf1.add(sg2_1);
		assertTrue(pf1.size() == 4);

		ArrayList<SimpleEdge> edges1_2 = new ArrayList<>();
		edges1_2.add(lg.edge_ids.get("1,2"));
		edges1_2.add(lg.edge_ids.get("1,3"));
		edges1_2.add(lg.edge_ids.get("2,4"));
		edges1_2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg1_2 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges1_2);
		ArrayList<SimpleEdge> edges2_2 = new ArrayList<>();
		edges2_2.add(lg.edge_ids.get("1,2"));
		edges2_2.add(lg.edge_ids.get("2,3"));
		edges2_2.add(lg.edge_ids.get("2,4"));
		edges2_2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg2_2 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges2_2);
		ArrayList<SimpleEdge> edges3_2 = new ArrayList<>();
		edges3_2.add(lg.edge_ids.get("1,2"));
		edges3_2.add(lg.edge_ids.get("2,3"));
		edges3_2.add(lg.edge_ids.get("1,4"));
		edges3_2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg3_2 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges3_2);
		ArrayList<SimpleEdge> edges4_2 = new ArrayList<>();
		edges4_2.add(lg.edge_ids.get("1,3"));
		edges4_2.add(lg.edge_ids.get("1,4"));
		edges4_2.add(lg.edge_ids.get("2,3"));
		edges4_2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg4_2 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges4_2);

		assertTrue(pf2.isEmpty());
		pf2.add(sg2_2);
		pf2.add(sg4_2);
		pf2.add(sg3_2);
		pf2.add(sg1_2);
		assertTrue(pf2.size() == 4);

		assertEquals(pf1, pf2);
		pf2.remove(sg3_2);
		assertNotEquals(pf1, pf2);
		pf1.remove(sg3_1);
		assertEquals(pf1, pf2);
	}

	@Test
	public void testParetoFront() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		GraphElementWeightAnnotator<SimpleEdge> wa =
				new GraphElementWeightAnnotator<>(
				ag.getAnnotator("WEIGHTS", GraphElementAnnotator.class));
		WeightedEdgesCalculator<SimpleNode, SimpleEdge, SimpleUndirectedGraph> wec =
				new WeightedEdgesCalculator<>(wa);
		ParetoFront<SimpleNode, SimpleEdge, SimpleUndirectedGraph> pf =
				new ParetoFront<>(wec, true);

		ArrayList<SimpleEdge> edges1 = new ArrayList<>();
		edges1.add(lg.edge_ids.get("1,2"));
		edges1.add(lg.edge_ids.get("1,3"));
		edges1.add(lg.edge_ids.get("2,4"));
		edges1.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg1 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges1);
		ArrayList<SimpleEdge> edges2 = new ArrayList<>();
		edges2.add(lg.edge_ids.get("1,2"));
		edges2.add(lg.edge_ids.get("2,3"));
		edges2.add(lg.edge_ids.get("2,4"));
		edges2.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg2 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges2);
		ArrayList<SimpleEdge> edges3 = new ArrayList<>();
		edges3.add(lg.edge_ids.get("1,2"));
		edges3.add(lg.edge_ids.get("2,3"));
		edges3.add(lg.edge_ids.get("1,4"));
		edges3.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg3 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges3);
		ArrayList<SimpleEdge> edges4 = new ArrayList<>();
		edges4.add(lg.edge_ids.get("1,3"));
		edges4.add(lg.edge_ids.get("1,4"));
		edges4.add(lg.edge_ids.get("2,3"));
		edges4.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg4 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges4);


		// Dominates all
		ArrayList<SimpleEdge> edges5 = new ArrayList<>();
		edges5.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg5 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges5);

		// Is dominated
		ArrayList<SimpleEdge> edges6 = new ArrayList<>();
		edges6.add(lg.edge_ids.get("1,2"));
		edges6.add(lg.edge_ids.get("1,3"));
		edges6.add(lg.edge_ids.get("1,4"));
		edges6.add(lg.edge_ids.get("2,3"));
		edges6.add(lg.edge_ids.get("2,4"));
		edges6.add(lg.edge_ids.get("3,4"));
		edges6.add(lg.edge_ids.get("4,5"));
		SimpleUndirectedGraph sg6 = ag.getGraph().getSubgraph(
				ag.getGraph().getAllNodes(), edges6);

		assertTrue(pf.isEmpty());
		assertTrue(pf.add(sg3));
		assertTrue(pf.add(sg1));
		assertTrue(pf.add(sg4));
		assertTrue(pf.add(sg2));
		assertTrue(pf.size() == 4);

		assertEquals(pf.first(), sg1);
		assertEquals(pf.last(), sg4);

		Iterator<SimpleUndirectedGraph> it = pf.iterator();
		assertEquals(it.next(), sg1);
		assertEquals(it.next(), sg2);
		assertEquals(it.next(), sg3);
		assertEquals(it.next(), sg4);

		// Dominance management
		assertFalse(pf.add(sg6));
		assertTrue(pf.size() == 4);
		assertTrue(pf.add(sg5));
		assertTrue(pf.size() == 1);

		// General management
		assertEquals(pf.first(), sg5);
		assertTrue(pf.remove(sg5));
		assertTrue(pf.isEmpty());
	}
}
