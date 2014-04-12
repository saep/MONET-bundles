package com.github.monet.test;

import java.util.HashMap;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.test.LabeledGraph;

public class ExampleGraphs {

	public static AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> getTinyUndirectedSingleCriterionGraph() {
		SimpleUndirectedGraph g = new SimpleUndirectedGraph();
		SimpleNode abe = g.addNode();
		SimpleNode bc = g.addNode();
		SimpleNode cde = g.addNode();
		SimpleNode ad = g.addNode();
		SimpleEdge a = g.addEdge(abe, ad);
		SimpleEdge b = g.addEdge(abe, bc);
		SimpleEdge c = g.addEdge(bc, cde);
		SimpleEdge d = g.addEdge(cde, ad);
		SimpleEdge e = g.addEdge(abe, cde);
		GraphElementHashAnnotator<SimpleEdge, Weight> raw_weights =
				new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<SimpleEdge> weights =
				new GraphElementWeightAnnotator<>(
				raw_weights);
		weights.setAnnotation(a, new Weight(new double[]{9.0}));
		weights.setAnnotation(b, new Weight(new double[]{5.0}));
		weights.setAnnotation(c, new Weight(new double[]{2.0}));
		weights.setAnnotation(d, new Weight(new double[]{7.0}));
		weights.setAnnotation(e, new Weight(new double[]{3.0}));

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag;
		ag = new AnnotatedGraph<>(g);
		ag.addAnnotator("WEIGHTS", weights);

		return ag;
	}

	/**
	 * Example from Steiner, Radzik: "Solving the Biojective Minimum Spanning
	 * Tree problem using a k-best algorithm".
	 *
	 * @return Weight annotated Graph with node and edge labels
	 */
	public static LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> getSteinerRadzikExample() {
		SimpleUndirectedGraph g = new SimpleUndirectedGraph();
		SimpleNode n1 = g.addNode();
		SimpleNode n2 = g.addNode();
		SimpleNode n3 = g.addNode();
		SimpleNode n4 = g.addNode();
		SimpleNode n5 = g.addNode();
		SimpleEdge e12 = g.addEdge(n1, n2);
		SimpleEdge e13 = g.addEdge(n1, n3);
		SimpleEdge e14 = g.addEdge(n1, n4);
		SimpleEdge e23 = g.addEdge(n2, n3);
		SimpleEdge e24 = g.addEdge(n2, n4);
		SimpleEdge e34 = g.addEdge(n3, n4);
		SimpleEdge e45 = g.addEdge(n4, n5);
		GraphElementHashAnnotator<SimpleEdge, Weight> annotator =
				new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<SimpleEdge> weights =
				new GraphElementWeightAnnotator<>(
				annotator);
		weights.setAnnotation(e12, new Weight(new double[]{2.0, 10.0}));
		weights.setAnnotation(e13, new Weight(new double[]{5.0, 9.0}));
		weights.setAnnotation(e14, new Weight(new double[]{7.0, 9.0}));
		weights.setAnnotation(e23, new Weight(new double[]{13.0, 1.0}));
		weights.setAnnotation(e24, new Weight(new double[]{1.0, 13.0}));
		weights.setAnnotation(e34, new Weight(new double[]{5.0, 15.0}));
		weights.setAnnotation(e45, new Weight(new double[]{9.0, 5.0}));

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ag;
		ag = new AnnotatedGraph<>(g);
		ag.addAnnotator("WEIGHTS", weights);


		HashMap<String, SimpleNode> node_ids = new HashMap<>();
		node_ids.put("1", n1);
		node_ids.put("2", n2);
		node_ids.put("3", n3);
		node_ids.put("4", n4);
		node_ids.put("5", n5);

		HashMap<String, SimpleEdge> edge_ids = new HashMap<>();
		edge_ids.put("1,2", e12);
		edge_ids.put("1,3", e13);
		edge_ids.put("1,4", e14);
		edge_ids.put("2,3", e23);
		edge_ids.put("2,4", e24);
		edge_ids.put("3,4", e34);
		edge_ids.put("4,5", e45);

		LabeledGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> lg =
				new LabeledGraph<>(
				ag, node_ids, edge_ids);

		return lg;
	}

	/**
	 * Example from Steiner, Radzik: "Solving the Biojective Minimum Spanning
	 * Tree problem using a k-best algorithm" with directed edges: lower node
	 * (id) = source
	 *
	 * @return Weight annotated Graph with node and edge labels
	 */
	public static LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> getSteinerRadzikDirectedExample() {
		SimpleDirectedGraph g = new SimpleDirectedGraph();
		SimpleNode n1 = g.addNode();
		SimpleNode n2 = g.addNode();
		SimpleNode n3 = g.addNode();
		SimpleNode n4 = g.addNode();
		SimpleNode n5 = g.addNode();
		SimpleEdge e12 = g.addEdge(n1, n2);
		SimpleEdge e13 = g.addEdge(n1, n3);
		SimpleEdge e14 = g.addEdge(n1, n4);
		SimpleEdge e23 = g.addEdge(n2, n3);
		SimpleEdge e24 = g.addEdge(n2, n4);
		SimpleEdge e34 = g.addEdge(n3, n4);
		SimpleEdge e45 = g.addEdge(n4, n5);
		GraphElementHashAnnotator<SimpleEdge, Weight> annotator =
				new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<SimpleEdge> weights =
				new GraphElementWeightAnnotator<>(
				annotator);
		weights.setAnnotation(e12, new Weight(new double[]{2.0, 10.0}));
		weights.setAnnotation(e13, new Weight(new double[]{5.0, 9.0}));
		weights.setAnnotation(e14, new Weight(new double[]{7.0, 9.0}));
		weights.setAnnotation(e23, new Weight(new double[]{13.0, 1.0}));
		weights.setAnnotation(e24, new Weight(new double[]{1.0, 13.0}));
		weights.setAnnotation(e34, new Weight(new double[]{5.0, 15.0}));
		weights.setAnnotation(e45, new Weight(new double[]{9.0, 5.0}));

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> ag;
		ag = new AnnotatedGraph<>(g);
		ag.addAnnotator("WEIGHTS", weights);


		HashMap<String, SimpleNode> node_ids = new HashMap<>();
		node_ids.put("1", n1);
		node_ids.put("2", n2);
		node_ids.put("3", n3);
		node_ids.put("4", n4);
		node_ids.put("5", n5);

		HashMap<String, SimpleEdge> edge_ids = new HashMap<>();
		edge_ids.put("1,2", e12);
		edge_ids.put("1,3", e13);
		edge_ids.put("1,4", e14);
		edge_ids.put("2,3", e23);
		edge_ids.put("2,4", e24);
		edge_ids.put("3,4", e34);
		edge_ids.put("4,5", e45);

		LabeledGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> lg =
				new LabeledGraph<>(
				ag, node_ids, edge_ids);

		return lg;
	}
}
