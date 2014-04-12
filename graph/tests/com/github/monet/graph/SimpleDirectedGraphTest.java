package com.github.monet.graph;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.test.ExampleGraphs;
import com.github.monet.test.LabeledGraph;

public class SimpleDirectedGraphTest {

	@Test
	public void testEquals() {
		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikDirectedExample();
		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> lh = ExampleGraphs
				.getSteinerRadzikDirectedExample();
		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> li = ExampleGraphs
				.getSteinerRadzikDirectedExample();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> ag = lg.graph;
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> ah = lh.graph;
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> ai = li.graph;
		SimpleDirectedGraph g = ag.getGraph();
		SimpleDirectedGraph h = ah.getGraph();
		SimpleDirectedGraph i = ai.getGraph();

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
		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikDirectedExample();
		com.github.monet.graph.SimpleDirectedGraph g = (com.github.monet.graph.SimpleDirectedGraph) lg.graph
				.getGraph();

		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getAdjacentNodes(lg.node_ids.get("4")).size() == 4);
		assertTrue(g.getAdjacentNodes(lg.node_ids.get("2")).contains(lg.node_ids.get("4")));
		assertTrue(g.getPrecedingNodes(lg.node_ids.get("4")).size() == 3);
		assertTrue(g.getPrecedingNodes(lg.node_ids.get("2")).contains(lg.node_ids.get("1")));
		assertTrue(g.getSucceedingNodes(lg.node_ids.get("4")).size() == 1);
		assertTrue(g.getSucceedingNodes(lg.node_ids.get("2")).contains(lg.node_ids.get("4")));

		g.deleteNode(lg.node_ids.get("4"));
		assertTrue(g.getNumNodes() == 4);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getNumEdges() == 3);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("4")) == null);
		assertTrue(g.getIncidentNodes(lg.edge_ids.get("4,5")) == null);
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("1")).size() == 2);
		assertTrue(g.getIncomingEdges(lg.node_ids.get("3")).size() == 2);
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("5")).isEmpty());
		assertTrue(g.getIncomingEdges(lg.node_ids.get("5")).isEmpty());

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
		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> lg = ExampleGraphs
				.getSteinerRadzikDirectedExample();
		com.github.monet.graph.SimpleDirectedGraph g = (com.github.monet.graph.SimpleDirectedGraph) lg.graph
				.getGraph();

		assertTrue(g.getNumEdges() == 7);
		assertTrue(g.getNumEdges() == g.getAllEdges().size());
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("1")).size() == 3);
		assertTrue(g.getIncomingEdges(lg.node_ids.get("1")).isEmpty());
		assertTrue(g.getIncidentEdges(lg.node_ids.get("1")).size() == 3);
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("3")).size() == 1);
		assertTrue(g.getIncomingEdges(lg.node_ids.get("3")).size() == 2);
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
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("1")).size() == 2);
		assertTrue(g.getIncomingEdges(lg.node_ids.get("1")).isEmpty());
		assertTrue(g.getIncidentEdges(lg.node_ids.get("1")).size() == 2);
		assertTrue(g.getOutgoingEdges(lg.node_ids.get("3")).size() == 1);
		assertTrue(g.getIncomingEdges(lg.node_ids.get("3")).size() == 1);
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
		assertTrue(g.getNumNodes() == 5);
		assertTrue(g.getNumNodes() == g.getAllNodes().size());
		assertTrue(g.getAllEdges().contains(e));
		assertTrue(g.getOutgoingEdges(
				g.getIncidentNode(lg.node_ids.get("5"), e)).size() == 1);
		assertTrue(g.getIncomingEdges(
				g.getIncidentNode(lg.node_ids.get("5"), e)).isEmpty());
		assertTrue(g.getOutgoingEdges(
				g.getIncidentNode(lg.node_ids.get("1"), e)).isEmpty());
		assertTrue(g.getIncomingEdges(
				g.getIncidentNode(lg.node_ids.get("1"), e)).size() == 1);
	}
}
