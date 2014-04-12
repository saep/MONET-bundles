package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleNode;

public class GraphElementReverseHashAnnotatorTest {

	@Test
	public void testEquals() {
		GraphElementReverseHashAnnotator<SimpleNode, String> a1 =
				new GraphElementReverseHashAnnotator<>();

		SimpleNode u = new SimpleNode(1);
		SimpleNode v = new SimpleNode(2);
		SimpleNode w = new SimpleNode(3);

		String s = "group1";
		String t = "group2";

		assertEquals(a1.getElements("group1").size(), 0);
		assertEquals(a1.getElements("group2").size(), 0);
		a1.setAnnotation(u, s);
		a1.setAnnotation(v, s);
		assertEquals(a1.getElements("group1").size(), 2);
		assertEquals(a1.getElements("group2").size(), 0);
		a1.setAnnotation(w, t);
		assertEquals(a1.getElements("group1").size(), 2);
		assertEquals(a1.getElements("group2").size(), 1);
		assertEquals(a1.getElements("group2").iterator().next(), w);
		a1.setAnnotation(u, t);
		assertEquals(a1.getElements("group1").size(), 1);
		assertEquals(a1.getElements("group2").size(), 2);
		assertEquals(a1.getElements("group1").iterator().next(), v);
		a1.setAnnotation(v, null);
		assertEquals(a1.getElements("group1").size(), 0);
		assertEquals(a1.getElements("group2").size(), 2);
	}
}
