package com.github.monet.algorithms.sssp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.interfaces.DirectedEdge;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.Weight.DominationRelation;
import com.github.monet.interfaces.Meter;
import com.github.monet.parser.MonetParser;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;
import com.github.monet.worker.TestMeter;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class LabelCorrectingTest {

	@Test
	public void test() {

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

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("SOURCE", a);
		// paramMap.put("DESTINATION", f);
		paramMap.put("MERGE_MODE", true);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(f, "endNode");
		sdAnnotator.setAnnotation(a, "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		Meter meter = new TestMeter();
		ServiceDirectory serviceDirectory = null;

		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();

		lc.execute(aGraph, paramMap, meter, serviceDirectory, null);

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

		assertTrue(lc.getLabels().getAnnotation(f).getLabels()
				.containsAll(compareTo));

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

		assertTrue(solutions.containsAll((lc.getSolutions())));
	}


	@Test
	public void test2(){
		MonetParser parser = new MonetParser();
		HashMap<String, Object> params =new HashMap<String, Object>();
		params.put("directed", true);
		params.put("startNodeid", -1);
		params.put("endNodeid", -1);
		params.put("MERGE_MODE", true);
		Job job = createDummyJob(params);
		String instanceName = "grid_dir_15_2.txt";
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> annotatedGraph = (AnnotatedGraph)parser.parse("../graph_instances/directed_integer_grid_graphs/" + instanceName, job);
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		lc.execute(annotatedGraph, params, new TestMeter(), null, job);
	}



	@Test
	public void mergeSimpleVSIntelligent() {
		int layerSize = 5;
		int dimension = 2;
		int max = 100;
		SimpleDirectedGraph graph = new SimpleDirectedGraph();

		SimpleNode source = graph.addNode();
		SimpleNode dest = graph.addNode();
		SimpleNode[] layer1 = new SimpleNode[layerSize];
		SimpleNode[] layer2 = new SimpleNode[layerSize];
		SimpleNode[] layer3 = new SimpleNode[layerSize];
		SimpleNode[] layer4 = new SimpleNode[layerSize];

		for (int i = 0; i < layerSize; i++) {
			layer1[i] = graph.addNode();
			layer2[i] = graph.addNode();
			layer3[i] = graph.addNode();
			layer4[i] = graph.addNode();
		}

		SimpleEdge[][] matchingEdges1 = new SimpleEdge[layerSize][layerSize];
		SimpleEdge[][] matchingEdges2 = new SimpleEdge[layerSize][layerSize];
		SimpleEdge[][] matchingEdges3 = new SimpleEdge[layerSize][layerSize];
		SimpleEdge[] sourceEdges = new SimpleEdge[layerSize];
		SimpleEdge[] destEdges = new SimpleEdge[layerSize];
		for (int i = 0; i < layerSize; i++) {
			sourceEdges[i] = graph.addEdge(source, layer1[i]);
			destEdges[i] = graph.addEdge(layer4[i], dest);
			for (int j = 0; j < layerSize; j++) {
				matchingEdges1[i][j] = graph.addEdge(layer1[i], layer2[j]);
				matchingEdges2[i][j] = graph.addEdge(layer2[i], layer3[j]);
				matchingEdges3[i][j] = graph.addEdge(layer3[i], layer4[j]);
			}
		}

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		//GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(
		//		adapted);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(
				graph);

		for (int i = 0; i < layerSize; i++) {
			adapted.setAnnotation(sourceEdges[i],
					this.createRandomWeight(dimension, max));
			adapted.setAnnotation(destEdges[i],
					this.createRandomWeight(dimension, max));
			for (int j = 0; j < layerSize; j++) {
				adapted.setAnnotation(matchingEdges1[i][j],
						this.createRandomWeight(dimension, max));
				adapted.setAnnotation(matchingEdges2[i][j],
						this.createRandomWeight(dimension, max));
				adapted.setAnnotation(matchingEdges3[i][j],
						this.createRandomWeight(dimension, max));
			}
		}

		aGraph.addAnnotator("edges", adapted);

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("SOURCE", source);
		// paramMap.put("DESTINATION", dest);
		paramMap.put("MERGE_MODE", true);

		GraphElementReverseHashAnnotator<SimpleNode, String> sdAnnotator = new GraphElementReverseHashAnnotator<>();

		sdAnnotator.setAnnotation(dest, "endNode");
		sdAnnotator.setAnnotation(source, "startNode");

		aGraph.addAnnotator("sdAnnotator", sdAnnotator);

		Meter meter = new TestMeter();
		ServiceDirectory serviceDirectory = null;

		System.out.println("== intelligent merging ==");
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lcInt = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		lcInt.execute(aGraph, paramMap, meter, serviceDirectory, null);

		paramMap = new HashMap<String, Object>();
		// paramMap.put("SOURCE", source);
		// paramMap.put("DESTINATION", dest);
		paramMap.put("MERGE_MODE", "SIMPLE");
		meter = new TestMeter();

		System.out.println("== simple merging ==");
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lcSimp = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		lcSimp.execute(aGraph, paramMap, meter, serviceDirectory, null);

		for (SimpleNode n : layer1) {
			assertTrue(lcInt
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(
							lcSimp.getLabels().getAnnotation(n).getLabels()));
			assertTrue(lcSimp
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(lcInt.getLabels().getAnnotation(n).getLabels()));
		}
		for (SimpleNode n : layer2) {
			assertTrue(lcInt
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(
							lcSimp.getLabels().getAnnotation(n).getLabels()));
			assertTrue(lcSimp
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(lcInt.getLabels().getAnnotation(n).getLabels()));
		}
		for (SimpleNode n : layer3) {
			assertTrue(lcInt
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(
							lcSimp.getLabels().getAnnotation(n).getLabels()));
			assertTrue(lcSimp
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(lcInt.getLabels().getAnnotation(n).getLabels()));
		}
		for (SimpleNode n : layer4) {
			assertTrue(lcInt
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(
							lcSimp.getLabels().getAnnotation(n).getLabels()));
			assertTrue(lcSimp
					.getLabels()
					.getAnnotation(n)
					.getLabels()
					.containsAll(lcInt.getLabels().getAnnotation(n).getLabels()));
		}

		boolean complete = lcInt.getSolutions().containsAll(
				lcSimp.getSolutions());
		boolean correct = lcSimp.getSolutions().containsAll(
				lcInt.getSolutions());

		System.out.println("== comparison complete ==");
		assertTrue(correct && complete);
	}

	@Test
	public void testRecFindMinima6d() {
		int pathes = 400;
		int dimension = 6;
		List<Weight> allLabels = new ArrayList<Weight>();
		for (int i = 0; i < pathes; i++) {
			double[] weightE = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				weightE[j] = (int) (Math.random() * 100000);
			}
			Weight w2 = new Weight(weightE);
			allLabels.add(w2);
		}

		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		allLabels = lc.sortByDimension(allLabels, allLabels.get(0)
				.getDimension() - 1);
		List<Weight> lcSolutions = lc.recFindMinima(allLabels);
		List<Weight> compareTo = this.getSolutionsByStupid(allLabels);
		boolean correct = compareTo.containsAll(lcSolutions);
		boolean complete = lcSolutions.containsAll(compareTo);
		boolean containsDuplicates = false;
		for (int i = 0; i < lcSolutions.size(); i++) {
			for (int j = i + 1; j < lcSolutions.size(); j++) {
				if (lcSolutions.get(i).equals(lcSolutions.get(j))) {
					containsDuplicates = true;
				}
			}
		}
		assertTrue(correct && complete && !containsDuplicates);
	}

	@Test
	public void testRecFindMinima3d() {
		int pathes = 400;
		int dimension = 3;
		List<Weight> allLabels = new ArrayList<Weight>();
		for (int i = 0; i < pathes; i++) {
			double[] weightE = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				weightE[j] = (int) (Math.random() * 100000);
			}
			Weight w2 = new Weight(weightE);
			allLabels.add(w2);
		}

		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		allLabels = lc.sortByDimension(allLabels, allLabels.get(0)
				.getDimension() - 1);
		List<Weight> lcSolutions = lc.recFindMinima(allLabels);
		List<Weight> compareTo = this.getSolutionsByStupid(allLabels);
		boolean correct = compareTo.containsAll(lcSolutions);
		boolean complete = lcSolutions.containsAll(compareTo);
		boolean containsDuplicates = false;
		for (int i = 0; i < lcSolutions.size(); i++) {
			for (int j = i + 1; j < lcSolutions.size(); j++) {
				if (lcSolutions.get(i).equals(lcSolutions.get(j))) {
					containsDuplicates = true;
				}
			}
		}
		assertTrue(correct && complete && !containsDuplicates);
	}

	@Test
	public void testSortByDimension() {
		int pathes = 300;
		int dimension = 3;
		ArrayList<Weight> allLabels = new ArrayList<Weight>();
		for (int i = 0; i < pathes; i++) {
			double[] weightE = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				weightE[j] = (int) (Math.random() * 100000);
			}
			Weight w2 = new Weight(weightE);
			allLabels.add(w2);
		}

		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		List<Weight> lcSolutions = lc.sortByDimension(allLabels, 0);
		boolean isSorted = true;
		int index = 0;
		double lastWeight = 0;
		while (index < lcSolutions.size()) {
			if (lastWeight > lcSolutions.get(index).getWeight(0)) {
				isSorted = false;
			}
			lastWeight = lcSolutions.get(index).getWeight(0);
			index++;
		}
		boolean correct = allLabels.containsAll(lcSolutions);
		boolean complete = lcSolutions.containsAll(allLabels);
		assertTrue(correct && complete && isSorted && this.isSortedByDimension(lcSolutions, 0));
	}

	@Test
	public void testInsertIntoList() {
		int size = 50;
		int dimension = 2;
		List<Weight> allLabels = new ArrayList<Weight>();
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		for (int i = 0; i < size; i++) {
			Weight w = this.createRandomWeight(dimension, 100000);
			lc.insertIntoList(allLabels, w, 0);
		}
		assertTrue(this.isSortedByDimension(allLabels, 0));
		int b=2;
		int c=3;
	}

	@Test
	public void testFindWeightInList() {
		int size = 100;
		int dimension = 3;
		int threshold = 40000;
		List<Weight> list = new ArrayList<Weight>();
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		for (int i = 0; i < size; i++) {
			Weight w = this.createRandomWeight(dimension, 100000);
			list.add(w);
		}
		list = lc.sortByDimension(list, 0);

		// find the right value
		int correct = 0;
		while(correct < list.size() && list.get(correct).getWeight(0) < threshold) {
			correct++;
		}
		assertTrue(correct == lc.findWeightInList(list, threshold, 0));
	}

	public boolean isSortedByDimension(List<Weight>list, int dimension) {
		boolean isSorted = true;
		int index = 0;
		double lastWeight = 0;
		while (index < list.size()) {
			if (lastWeight > list.get(index).getWeight(dimension)) {
				isSorted = false;
			}
			lastWeight = list.get(index).getWeight(dimension);
			index++;
		}
		return isSorted;
	}

	/**
	 * Method simply and slowly iterates through source and separately checks
	 * all weights for being a minimum. Makes testing easier because now i don't
	 * have to make up input instances.
	 *
	 * @param source
	 *            the set of Labels to check
	 * @return all pareto-minima in source
	 */
	public ArrayList<Weight> getSolutionsByStupid(List<Weight> source) {
		ArrayList<Weight> retval = new ArrayList<Weight>();
		for (Weight i : source) {
			boolean isMinimum = true;
			for (Weight j : source) {
				if (!i.equals(j)) {
					if (j.dominates(i)
							.equals(DominationRelation.PARETO_SMALLER)) {
						isMinimum = false;
					}
				}
			}
			if (isMinimum) {
				retval.add(i);
			}
		}
		return retval;
	}

	public Weight createRandomWeight(int dimension, int max) {
		double[] w = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			w[i] = (int) (Math.random() * max);
		}
		Weight retval = new Weight(w);
		return retval;
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
