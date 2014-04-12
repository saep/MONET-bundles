package com.github.monet.algorithms.mst;

import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

import java.util.HashSet;

import com.github.monet.algorithms.Prim;
import static junit.framework.TestCase.assertTrue;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

import org.junit.Test;

import com.github.monet.algorithms.mst.FirstPhase2d;

public class FirstPhaseTest {

	@Test
	public void testNoColinear() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				ExampleGraphs.
				getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag =
				lg.graph;
		Graph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = ag.getGraph();
		GraphElementAnnotator<SimpleEdge, Weight> annotator = ag.getAnnotator(
				"WEIGHTS", GraphElementAnnotator.class);
		GraphElementWeightAnnotator<SimpleEdge> weights =
				new GraphElementWeightAnnotator<>(
				annotator);

		Prim<SimpleNode, SimpleEdge, SimpleUndirectedGraph> prim = new Prim<>();
		FirstPhase2d firstPhase = new FirstPhase2d(prim);
		ParetoSet<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ps =
				firstPhase.firstPhase(ag, "WEIGHTS");

		HashSet<Weight> costs = new HashSet<>();
		for (Graph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph : ps) {
			costs.add(weights.sum(graph.getAllEdges()));
		}
		assertTrue(costs.size() == 4);
		assertTrue(costs.contains(new Weight(new double[]{17.0, 37.0})));
		assertTrue(costs.contains(new Weight(new double[]{25.0, 29.0})));
		assertTrue(costs.contains(new Weight(new double[]{31.0, 25.0})));
		assertTrue(costs.contains(new Weight(new double[]{34.0, 24.0})));
	}
}
