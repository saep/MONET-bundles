package com.github.monet.graph.weighted;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class GraphElementWeightAnnotatorTest {

	@Test
	public void testAnnotator() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikExample();

		GraphElementHashAnnotator<SimpleNode, Weight> an = new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<SimpleNode> wan = new GraphElementWeightAnnotator<>(
				an);

		assertTrue(an == wan.getAdapted());
		assertTrue(wan.getAnnotatedElements().isEmpty());

		an.setAnnotation(lg.node_ids.get("1"), new Weight(new double[] { 3.0,
				7.0, 4.0 }));
		wan.setAnnotation(lg.node_ids.get("2"), new Weight(new double[] { 6.0,
				5.0, 5.0 }));
		an.setAnnotation(lg.node_ids.get("3"), new Weight(new double[] { 4.0,
				2.0, 2.0 }));
		wan.setAnnotation(lg.node_ids.get("4"), new Weight(new double[] { 3.0,
				6.0, 6.0 }));
		an.setAnnotation(lg.node_ids.get("5"), new Weight(new double[] { 99.0,
				99.0, 99.0 }));
		an.setAnnotation(lg.node_ids.get("5"), new Weight(new double[] { 2.0,
				7.0, 2.0 }));
		assertEquals(an.getAnnotation(lg.node_ids.get("5")), new Weight(
				new double[] { 2.0, 7.0, 2.0 }));

		// getAnnotatedElements(), getAnnotation()
		assertEquals(wan.getAnnotatedElements(), an.getAnnotatedElements());
		assertEquals(wan.getAnnotation(lg.node_ids.get("3")),
				an.getAnnotation(lg.node_ids.get("3")));

		// scalarize()
		GraphElementWeightAnnotator<SimpleNode> scal = wan
				.scalarize(new double[] { 2.0, 4.0, 6.0 });
		assertEquals(scal.getAnnotation(lg.node_ids.get("4")), new Weight(66.0));

		// sum()
		ArrayList<SimpleNode> someNodes = new ArrayList<>();
		someNodes.add(lg.node_ids.get("1"));
		someNodes.add(lg.node_ids.get("2"));
		someNodes.add(lg.node_ids.get("5"));
		assertEquals(wan.sum(someNodes), new Weight(new double[] { 11.0, 19.0,
				11.0 }));
	}
}
