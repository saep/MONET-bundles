package com.github.monet.algorithms;

import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import com.github.monet.algorithms.Prim;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.test.ExampleGraphs;

public class PrimTest {

	@Test
	public void testPrim() {
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag = ExampleGraphs
				.getTinyUndirectedSingleCriterionGraph();
		Graph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = ag.getGraph();
		GraphElementAnnotator<SimpleEdge, Weight> annotator = ag.getAnnotator(
				"WEIGHTS", GraphElementAnnotator.class);

		GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(
				annotator);

		Prim prim = new Prim();
		Iterable<SimpleEdge> mst = prim.computeUniobjectiveOptimum(g, weights);

		int size = 0;
		Iterator<SimpleEdge> it = mst.iterator();
		while (it.hasNext()) {
			it.next();
			size++;
		}
		assertTrue(size == 3);
		assertTrue(weights.sum(mst).getFirstWeight() == 12);
	}
}
