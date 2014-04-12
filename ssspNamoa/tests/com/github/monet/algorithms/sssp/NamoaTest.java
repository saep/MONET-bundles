package com.github.monet.algorithms.sssp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.interfaces.DirectedEdge;
import com.github.monet.graph.weighted.LabelSet;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.Meter;
import com.github.monet.parser.MonetHeurGridParser;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;
import com.github.monet.worker.TestMeter;

public class NamoaTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test1() {


		// Run Namoa on a general graph and compare its solution to the right solution

		SimpleDirectedGraph graph = new SimpleDirectedGraph();
		SimpleNode a = graph.addNode();
		SimpleNode b = graph.addNode();
		SimpleNode c = graph.addNode();
		SimpleNode d = graph.addNode();
		SimpleNode e = graph.addNode();
		SimpleNode f = graph.addNode();
		SimpleEdge ab = graph.addEdge(a, b);
		SimpleEdge ac = graph.addEdge(a, c);
		SimpleEdge bd = graph.addEdge(b, d);
		SimpleEdge be = graph.addEdge(b, e);
		SimpleEdge cb = graph.addEdge(c, b);
		SimpleEdge ce = graph.addEdge(c, e);
		SimpleEdge de = graph.addEdge(d, e);
		SimpleEdge df = graph.addEdge(d, f);
		SimpleEdge eb = graph.addEdge(e, b);
		SimpleEdge ef = graph.addEdge(e, f);

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		//GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
		//		adapted);

		double[] abWeightD = { 3, 12 };
		Weight abWeight = new Weight(abWeightD);
		adapted.setAnnotation(ab, abWeight);

		double[] acWeightD = { 4, 6 };
		Weight acWeight = new Weight(acWeightD);
		adapted.setAnnotation(ac, acWeight);

		double[] bdWeightD = { 8, 1 };
		Weight bdWeight = new Weight(bdWeightD);
		adapted.setAnnotation(bd, bdWeight);

		double[] beWeightD = { 1, 5 };
		Weight beWeight = new Weight(beWeightD);
		adapted.setAnnotation(be, beWeight);

		double[] cbWeightD = { 5, 6 };
		Weight cbWeight = new Weight(cbWeightD);
		adapted.setAnnotation(cb, cbWeight);

		double[] ceWeightD = { 6, 4 };
		Weight ceWeight = new Weight(ceWeightD);
		adapted.setAnnotation(ce, ceWeight);

		double[] deWeightD = { 7, 5 };
		Weight deWeight = new Weight(deWeightD);
		adapted.setAnnotation(de, deWeight);

		double[] dfWeightD = { 9, 2 };
		Weight dfWeight = new Weight(dfWeightD);
		adapted.setAnnotation(df, dfWeight);

		double[] ebWeightD = { 1, 1 };
		Weight ebWeight = new Weight(ebWeightD);
		adapted.setAnnotation(eb, ebWeight);

		double[] efWeightD = { 9, 11 };
		Weight efWeight = new Weight(efWeightD);
		adapted.setAnnotation(ef, efWeight);



		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(
				graph);

		aGraph.addAnnotator("edges", adapted);

		GraphElementHashAnnotator<SimpleNode, LabelSet> h = new GraphElementHashAnnotator<SimpleNode, LabelSet>();

		for(SimpleNode n: graph.getAllNodes()){
			h.setAnnotation(n, new LabelSet());
			for(SimpleEdge edge: graph.getOutgoingEdges(n)){
				h.getAnnotation(n).getLabels().add(((GraphElementHashAnnotator<SimpleEdge, Weight>)aGraph.getAnnotator("edges", GraphElementHashAnnotator.class)).getAnnotation(edge));
			}
		}

		double[] nullVector = { 0.0, 0.0 };
		Weight nullWeight = new Weight(nullVector);
		h.getAnnotation(f).getLabels().add(nullWeight);

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		//paramMap.put("SOURCE", a);
		//paramMap.put("DESTINATION", f);
		paramMap.put("HEURISTIC", h);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(f, "endNode");
		sdAnnotator.setAnnotation(a, "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		Meter meter = new TestMeter();
		ServiceDirectory serviceDirectory = null;

		Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge> na = new Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge>();

		na.execute(aGraph, paramMap, meter, serviceDirectory, null);


		double[] weights1 = { 13.0, 28.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 19.0, 21.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 20.0, 15.0 };
		Weight weight3 = new Weight(weights3);
		double[] weights4 = { 28.0, 14.0 };
		Weight weight4 = new Weight(weights4);

		ArrayList<Weight> compareTo = new ArrayList<Weight>();
		compareTo.add(weight1);
		compareTo.add(weight2);
		compareTo.add(weight3);
		compareTo.add(weight4);

		assertTrue(na.getClosedLabels().getAnnotation(f).getLabels()
				.containsAll(compareTo));
		assertTrue(compareTo.containsAll(na.getClosedLabels().getAnnotation(f).getLabels()));

		ArrayList<LinkedList<SimpleEdge>> solutions = new ArrayList<LinkedList<SimpleEdge>>();
		LinkedList<SimpleEdge> solution1 = new LinkedList<SimpleEdge>();
		LinkedList<SimpleEdge> solution2 = new LinkedList<SimpleEdge>();
		LinkedList<SimpleEdge> solution3 = new LinkedList<SimpleEdge>();
		LinkedList<SimpleEdge> solution4 = new LinkedList<SimpleEdge>();

		solution1.add(ab);
		solution1.add(be);
		solution1.add(ef);

		solution2.add(ac);
		solution2.add(ce);
		solution2.add(ef);

		solution3.add(ab);
		solution3.add(bd);
		solution3.add(df);

		solution4.add(ac);
		solution4.add(ce);
		solution4.add(eb);
		solution4.add(bd);
		solution4.add(df);

		solutions.add(solution1);
		solutions.add(solution2);
		solutions.add(solution3);
		solutions.add(solution4);

		assertTrue(solutions.containsAll((na.getSolutions())));
		assertTrue(na.getSolutions().containsAll(solutions));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void test2() {

		// Add a single cheap node from SOURCE to DIRECTION to the graph from test1 to test filtering and elimination
		SimpleDirectedGraph graph = new SimpleDirectedGraph();
		SimpleNode a = graph.addNode();
		SimpleNode b = graph.addNode();
		SimpleNode c = graph.addNode();
		SimpleNode d = graph.addNode();
		SimpleNode e = graph.addNode();
		SimpleNode f = graph.addNode();
		SimpleEdge af = graph.addEdge(a, f);
		SimpleEdge ab = graph.addEdge(a, b);
		SimpleEdge ac = graph.addEdge(a, c);
		SimpleEdge bd = graph.addEdge(b, d);
		SimpleEdge be = graph.addEdge(b, e);
		SimpleEdge cb = graph.addEdge(c, b);
		SimpleEdge ce = graph.addEdge(c, e);
		SimpleEdge de = graph.addEdge(d, e);
		SimpleEdge df = graph.addEdge(d, f);
		SimpleEdge eb = graph.addEdge(e, b);
		SimpleEdge ef = graph.addEdge(e, f);

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		//GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
		//		adapted);

		double[] afWeightD = { 6.0, 6.0 };
		Weight afWeight = new Weight(afWeightD);
		adapted.setAnnotation(af, afWeight);

		double[] abWeightD = { 3, 12 };
		Weight abWeight = new Weight(abWeightD);
		adapted.setAnnotation(ab, abWeight);

		double[] acWeightD = { 4, 6 };
		Weight acWeight = new Weight(acWeightD);
		adapted.setAnnotation(ac, acWeight);

		double[] bdWeightD = { 8, 1 };
		Weight bdWeight = new Weight(bdWeightD);
		adapted.setAnnotation(bd, bdWeight);

		double[] beWeightD = { 1, 5 };
		Weight beWeight = new Weight(beWeightD);
		adapted.setAnnotation(be, beWeight);

		double[] cbWeightD = { 5, 6 };
		Weight cbWeight = new Weight(cbWeightD);
		adapted.setAnnotation(cb, cbWeight);

		double[] ceWeightD = { 6, 4 };
		Weight ceWeight = new Weight(ceWeightD);
		adapted.setAnnotation(ce, ceWeight);

		double[] deWeightD = { 7, 5 };
		Weight deWeight = new Weight(deWeightD);
		adapted.setAnnotation(de, deWeight);

		double[] dfWeightD = { 9, 2 };
		Weight dfWeight = new Weight(dfWeightD);
		adapted.setAnnotation(df, dfWeight);

		double[] ebWeightD = { 1, 1 };
		Weight ebWeight = new Weight(ebWeightD);
		adapted.setAnnotation(eb, ebWeight);

		double[] efWeightD = { 9, 11 };
		Weight efWeight = new Weight(efWeightD);
		adapted.setAnnotation(ef, efWeight);



		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(
				graph);

		aGraph.addAnnotator("edges", adapted);

		GraphElementHashAnnotator<SimpleNode, LabelSet> h = new GraphElementHashAnnotator<SimpleNode, LabelSet>();

		for(SimpleNode n: graph.getAllNodes()){
			h.setAnnotation(n, new LabelSet());
			for(SimpleEdge edge: graph.getOutgoingEdges(n)){
				h.getAnnotation(n).getLabels().add(((GraphElementHashAnnotator<SimpleEdge, Weight>)aGraph.getAnnotator("edges", GraphElementHashAnnotator.class)).getAnnotation(edge));
			}
		}

		double[] nullVector = { 0.0, 0.0 };
		Weight nullWeight = new Weight(nullVector);
		h.getAnnotation(f).getLabels().add(nullWeight);

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("SOURCE", a);
		paramMap.put("DESTINATION", f);
		paramMap.put("HEURISTIC", h);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(f, "endNode");
		sdAnnotator.setAnnotation(a, "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		Meter meter = new TestMeter();
		ServiceDirectory serviceDirectory = null;

		Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge> na = new Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge>();

		na.execute(aGraph, paramMap, meter, serviceDirectory, null);

		double[] weights1 = { 6.0, 6.0 };
		Weight weight1 = new Weight(weights1);

		ArrayList<Weight> compareTo = new ArrayList<Weight>();
		compareTo.add(weight1);

		assertTrue(na.getClosedLabels().getAnnotation(f).getLabels()
				.containsAll(compareTo));
		assertTrue(compareTo.containsAll(na.getClosedLabels().getAnnotation(f).getLabels()));
	}




	@SuppressWarnings("unchecked")
	@Test
	public void test3() {


		// Modify graph from test1 to test pruning

		SimpleDirectedGraph graph = new SimpleDirectedGraph();
		SimpleNode a = graph.addNode();
		SimpleNode b = graph.addNode();
		SimpleNode c = graph.addNode();
		SimpleNode d = graph.addNode();
		SimpleNode e = graph.addNode();
		SimpleNode f = graph.addNode();
		SimpleEdge ab = graph.addEdge(a, b);
		SimpleEdge ac = graph.addEdge(a, c);
		SimpleEdge bd = graph.addEdge(b, d);
		SimpleEdge be = graph.addEdge(b, e);
		SimpleEdge cb = graph.addEdge(c, b);
		SimpleEdge ce = graph.addEdge(c, e);
		SimpleEdge de = graph.addEdge(d, e);
		SimpleEdge df = graph.addEdge(d, f);
		SimpleEdge eb = graph.addEdge(e, b);
		SimpleEdge ef = graph.addEdge(e, f);

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		//GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
				//adapted);

		double[] abWeightD = { 10, 13 };
		Weight abWeight = new Weight(abWeightD);
		adapted.setAnnotation(ab, abWeight);

		double[] acWeightD = { 4, 6 };
		Weight acWeight = new Weight(acWeightD);
		adapted.setAnnotation(ac, acWeight);

		double[] bdWeightD = { 8, 1 };
		Weight bdWeight = new Weight(bdWeightD);
		adapted.setAnnotation(bd, bdWeight);

		double[] beWeightD = { 1, 5 };
		Weight beWeight = new Weight(beWeightD);
		adapted.setAnnotation(be, beWeight);

		double[] cbWeightD = { 5, 6 };
		Weight cbWeight = new Weight(cbWeightD);
		adapted.setAnnotation(cb, cbWeight);

		double[] ceWeightD = { 6, 4 };
		Weight ceWeight = new Weight(ceWeightD);
		adapted.setAnnotation(ce, ceWeight);

		double[] deWeightD = { 7, 5 };
		Weight deWeight = new Weight(deWeightD);
		adapted.setAnnotation(de, deWeight);

		double[] dfWeightD = { 9, 2 };
		Weight dfWeight = new Weight(dfWeightD);
		adapted.setAnnotation(df, dfWeight);

		double[] ebWeightD = { 1, 1 };
		Weight ebWeight = new Weight(ebWeightD);
		adapted.setAnnotation(eb, ebWeight);

		double[] efWeightD = { 9, 11 };
		Weight efWeight = new Weight(efWeightD);
		adapted.setAnnotation(ef, efWeight);



		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(
				graph);

		aGraph.addAnnotator("edges", adapted);

		GraphElementHashAnnotator<SimpleNode, LabelSet> h = new GraphElementHashAnnotator<SimpleNode, LabelSet>();

		for(SimpleNode n: graph.getAllNodes()){
			h.setAnnotation(n, new LabelSet());
			for(SimpleEdge edge: graph.getOutgoingEdges(n)){
				h.getAnnotation(n).getLabels().add(((GraphElementHashAnnotator<SimpleEdge, Weight>)aGraph.getAnnotator("edges", GraphElementHashAnnotator.class)).getAnnotation(edge));
			}
		}

		double[] nullVector = { 0.0, 0.0 };
		Weight nullWeight = new Weight(nullVector);
		h.getAnnotation(f).getLabels().add(nullWeight);

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("HEURISTIC", h);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(f, "endNode");
		sdAnnotator.setAnnotation(a, "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		Meter meter = new TestMeter();
		ServiceDirectory serviceDirectory = null;

		Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge> na = new Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge>();

		na.execute(aGraph, paramMap, meter, serviceDirectory, null);


		double[] weights1 = { 19.0, 21.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 26.0, 15.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 28.0, 14.0 };
		Weight weight3 = new Weight(weights3);

		ArrayList<Weight> compareTo = new ArrayList<Weight>();
		compareTo.add(weight1);
		compareTo.add(weight2);
		compareTo.add(weight3);

		assertTrue(na.getClosedLabels().getAnnotation(f).getLabels()
				.containsAll(compareTo));
		assertTrue(compareTo.containsAll(na.getClosedLabels().getAnnotation(f).getLabels()));
	}


	@Test
	public void test4(){
		MonetHeurGridParser parser = new MonetHeurGridParser();
		Job job = createDummyJob(new HashMap<String, Object>());
		String instanceName = "grid_dir_15_2.txt";
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> annotatedGraph = (AnnotatedGraph)parser.parse("../graph_instances/directed_integer_grid_graphs/" + instanceName, job);
		Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge> na = new Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		na.execute(annotatedGraph, new HashMap<String, Object>(), new TestMeter(), null, job);
	}


	public static Job createDummyJob(final Map<String,Object> params) {
		Job job = new Job() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void clean() {
			}
			@Override
			public String getID() {
				return null;
			}
			@Override
			public Logger getLogger() {
				return null;
			}
			@Override
			public String getParserDescriptor() {
				return "DummyJobParser";
			}
			@Override
			public Map<String, Object> getParameters() {
				return params;
			}
			@Override
			public Object getInputGraph() {
				return null;
			}
			@Override
			public Map<String, Object> getParserParameters() {
				return params;
			}
		};
		return job;
	}
}
