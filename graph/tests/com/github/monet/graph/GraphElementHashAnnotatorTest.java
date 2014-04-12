package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.weighted.Weight;

public class GraphElementHashAnnotatorTest {

	@Test
	public void testEquals() {
		GraphElementHashAnnotator<SimpleNode, Weight> a1 = new GraphElementHashAnnotator<>();
		GraphElementHashAnnotator<SimpleNode, Weight> a2 = new GraphElementHashAnnotator<>();

		SimpleNode u = new SimpleNode(1);
		SimpleNode v = new SimpleNode(1);
		SimpleNode w = new SimpleNode(2);

		Weight w1 = new Weight(new double[] { 1.0, 2.0 });
		Weight w2 = new Weight(new double[] { 1.0, 2.0 });
		Weight w3 = new Weight(new double[] { 3.0, 2.0 });

		assertEquals(a1, a2);
		a1.setAnnotation(u, w1);
		assertNotEquals(a1, a2);
		a2.setAnnotation(v, w2);
		assertEquals(a1, a2);
		a2.setAnnotation(w, w1);
		assertNotEquals(a1, a2);
		a1.setAnnotation(w, w2);
		assertEquals(a1, a2);
		a1.setAnnotation(u, w3);
		assertNotEquals(a1, a2);
	}
}
