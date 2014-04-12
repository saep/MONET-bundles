package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.SimpleNode;

public class SimpleNodeTest {

	@Test
	public void testEquals() {
		SimpleNode u = new SimpleNode(1);
		SimpleNode v = new SimpleNode(2);
		SimpleNode w = new SimpleNode(1);

		assertEquals(u, w);
		assertNotEquals(u, v);
		assertNotEquals(v, w);
	}
}
