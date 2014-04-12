package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class AnnotatedGraphTest {


	@Test
	@SuppressWarnings("unchecked")
	public void testEquals() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg = ExampleGraphs.getSteinerRadzikExample();


		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag = new AnnotatedGraph<>(
				lg.graph.getGraph());

		ag.addAnnotator("WEIGHTS", lg.graph.getAnnotator("WEIGHTS", GraphElementAnnotator.class));

		assertEquals(ag, lg.graph);
	}
}
