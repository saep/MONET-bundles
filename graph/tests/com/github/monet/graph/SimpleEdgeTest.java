package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;

public class SimpleEdgeTest {

	@Test
	public void testEquals() {
		SimpleNode u = new SimpleNode(1);
		SimpleNode v = new SimpleNode(2);
		SimpleNode w = new SimpleNode(1);
		SimpleNode x = new SimpleNode(3);

		SimpleEdge e = new SimpleEdge(u, v);
		SimpleEdge f = new SimpleEdge(w, v);
		SimpleEdge g = new SimpleEdge(v, w);
		SimpleEdge h = new SimpleEdge(v, x);

		assertEquals(e, f);
		assertNotEquals(f, g);
		assertNotEquals(e, h);
		assertNotEquals(f, h);
		assertNotEquals(h, g);
	}
}
