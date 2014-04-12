package com.github.monet.graph;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class SimpleUndirectedGraphTest {

	@Test
	public void testEquals() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikExample();
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lh = ExampleGraphs
				.getSteinerRadzikExample();
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> li = ExampleGraphs
				.getSteinerRadzikExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag = lg.graph;
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ah = lh.graph;
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ai = li.graph;
		SimpleUndirectedGraph g = ag.getGraph();
		SimpleUndirectedGraph h = ah.getGraph();
		SimpleUndirectedGraph i = ai.getGraph();

		assertTrue(g.equals(h));
		assertTrue(g.equals(i));
		assertTrue(h.equals(g));
		assertTrue(i.equals(g));

		i.deleteNode(i.getAllNodes().iterator().next());
		assertTrue(g.equals(h));
		assertTrue(!g.equals(i));
		assertTrue(h.equals(g));
		assertTrue(!i.equals(g));

		SimpleEdge deleted_edge_h = i.getAllEdges().iterator().next();
		Collection<SimpleNode> deleted_edge_nodes = h
				.getIncidentNodes(deleted_edge_h);
		h.deleteEdge(deleted_edge_h);
		assertTrue(!g.equals(h));
		assertTrue(!g.equals(i));
		assertTrue(!h.equals(g));
		assertTrue(!i.equals(g));

		i.addNode();
		Iterator<SimpleNode> it = deleted_edge_nodes.iterator();
		SimpleNode u = it.next();
		SimpleNode v = it.next();
		h.addEdge(u, v);
		assertTrue(g.equals(h));
		assertTrue(!g.equals(i));
		assertTrue(h.equals(g));
		assertTrue(!i.equals(g));
	}

	@Test
	public void testNodeManagement() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikExample();
		com.github.monet.graph.SimpleUndirectedGraph g = (com.github.monet.graph.SimpleUndirectedGraph) lg.graph
				.getGraph();

		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getAdjacentNodes(lg.node_ids.get("4")).size() == 4);
		assertTrue(g.getAdjacentNodes(lg.node_ids.get("2")).contains(lg.node_ids.get("4")));

		g.deleteNode(lg.node_ids.get("4"));
		assertTrue(g.getNumNodes() == 4);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getNumEdges() == 3);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getIncidentEdges(lg.node_ids.get("4")) == null);
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("4,5")) == null);
		assertTrue(g.getIncidentEdges(lg.node_ids.get("1")).size() == 2);
		assertTrue(g.getIncidentEdges(lg.node_ids.get("5")).isEmpty());

		g.deleteNode(lg.node_ids.get("1"));
		g.deleteNode(lg.node_ids.get("2"));
		g.deleteNode(lg.node_ids.get("3"));
		g.deleteNode(lg.node_ids.get("5"));
		assertTrue(g.getNumNodes() == 0);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getNumEdges() == 0);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());

		SimpleNode u = g.addNode();
		assertTrue(g.getNumNodes() == 1);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getAllNodes().contains(u));
	}

	@Test
	public void testEdgeManagement() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikExample();
		com.github.monet.graph.SimpleUndirectedGraph g = (com.github.monet.graph.SimpleUndirectedGraph) lg.graph
				.getGraph();

		assertTrue(g.getNumEdges() == 7);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getIncidentEdges(lg.node_ids.get("1")).size() == 3);
		assertTrue(g.getIncidentEdges(lg.node_ids.get("3")).size() == 3);
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("1,3")).contains(
				lg.node_ids.get("1")));
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("1,3")).contains(
				lg.node_ids.get("3")));
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("1,3")).size() == 2);
		assertTrue(g.getIncidentNode(lg.node_ids.get("1"),
				lg.edge_ids.get("1,3")) == lg.node_ids.get("3"));
		assertTrue(g.getIncidentNode(lg.node_ids.get("3"),
				lg.edge_ids.get("1,3")) == lg.node_ids.get("1"));

		g.deleteEdge(lg.edge_ids.get("1,3"));
		assertTrue(g.getNumEdges() == 6);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getIncidentEdges(lg.node_ids.get("1")).size() == 2);
		assertTrue(g.getIncidentEdges(lg.node_ids.get("3")).size() == 2);
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("1,3")) == null);
		assertTrue(g.getIncidentNode(lg.node_ids.get("1"),
				lg.edge_ids.get("1,3")) == null);
		assertTrue(g.getIncidentNode(lg.node_ids.get("3"),
				lg.edge_ids.get("1,3")) == null);

		g.deleteEdge(lg.edge_ids.get("1,2"));
		g.deleteEdge(lg.edge_ids.get("1,4"));
		g.deleteEdge(lg.edge_ids.get("2,3"));
		g.deleteEdge(lg.edge_ids.get("3,4"));
		g.deleteEdge(lg.edge_ids.get("2,4"));
		g.deleteEdge(lg.edge_ids.get("4,5"));
		assertTrue(g.getNumEdges() == 0);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());

		SimpleEdge e = g.addEdge(lg.node_ids.get("1"), lg.node_ids.get("5"));
		assertTrue(g.getNumEdges() == 1);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getAllEdges().contains(e));
		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
	}
}
