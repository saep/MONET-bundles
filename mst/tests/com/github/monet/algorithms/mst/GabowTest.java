package com.github.monet.algorithms.mst;

import java.util.HashMap;
import java.util.Map;

import com.github.monet.algorithms.Kruskal;
import com.github.monet.generator.CompleteGraphGenerator;
import com.github.monet.generator.GridGraphGenerator;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.parser.MonetParser;
import com.github.monet.worker.Job;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.github.monet.algorithms.mst.FirstPhase2d;
import com.github.monet.algorithms.mst.Gabow;
import com.github.monet.algorithms.mst.KBestSecondPhase;

public class GabowTest {

	@Test
	public void GabowTest() {

		MonetParser parser = new MonetParser();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph
		    = (AnnotatedGraph) parser.parse(
		      "/Users/chris/git/pg573repository/monet/graph_instances/experiments/experiments_4h/mst/grid_undir_4_2_1.txt",
		      createDummyJob(new HashMap<String, Object>()));

		GraphElementWeightAnnotator<SimpleEdge> annotator = new GraphElementWeightAnnotator<>(graph.getAnnotator("edges",  GraphElementAnnotator.class));

		double[] coefficients = new double[]{ 2206.1799, 2342.1691 };
		GraphElementWeightAnnotator<SimpleEdge> scalarization = annotator.scalarize(
					coefficients);

		//GridGraphGenerator gen = new GridGraphGenerator();
		//CompleteGraphGenerator gen = new CompleteGraphGenerator();

		//AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph =gen.generateGridGraph(4, 10, 2);
		//AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph =gen.generateCompleteGraph(50, 10, 2);

		Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph.getGraph(), scalarization);

		SimpleUndirectedGraph ret = gabow.generate();
		int i= 1;

		while (ret != null) {
			ret = gabow.generate();

			Weight w = annotator.sum(ret.getAllEdges());

			if ((w.getWeight(0) == 2309.0) && (w.getWeight(1) == 1792.0)) {
				System.out.println("### SUCCESS ###");
			}

			i++;
		}

		System.out.println(i);


	}

	/**
	 * Creates a dummy job which only consists of algorithm parameters
	 */
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


	/*
	@Test
	public void GabowTest() {
/*
	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	ArrayList<SimpleEdge> in = new ArrayList<>();
	//in.add(ac);
	ArrayList<SimpleEdge> out = new ArrayList<>();
	//out.add(bd);

	ArrayList<SimpleEdge> ret = gabow.init(in, out);
	SimpleEdge r_e = ret.get(0);
	SimpleEdge r_f = ret.get(1);

	System.out.println(r_e + " " + weights.getAnnotation(r_e).getFirstWeight());
	System.out.println(r_f + " " + weights.getAnnotation(r_f).getFirstWeight());
*/
	/*
	// K_4
	SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
	SimpleNode a = graph.addNode();
	SimpleNode b = graph.addNode();
	SimpleNode c = graph.addNode();
	SimpleNode d = graph.addNode();

	SimpleEdge ab = graph.addEdge(a,b);
	SimpleEdge ac = graph.addEdge(a,c);
	SimpleEdge ad = graph.addEdge(a,d);
	SimpleEdge bc = graph.addEdge(b,c);
	SimpleEdge bd = graph.addEdge(b,d);
	SimpleEdge cd = graph.addEdge(c,d);

	GraphElementHashAnnotator<SimpleEdge, Weight> an = new GraphElementHashAnnotator<>();
	an.setAnnotation(ab, new Weight(1.));
	an.setAnnotation(ac, new Weight(2.));
	an.setAnnotation(ad, new Weight(3.));
	an.setAnnotation(bc, new Weight(4.));
	an.setAnnotation(bd, new Weight(5.));
	an.setAnnotation(cd, new Weight(6.));
	GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(an);

	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	SimpleUndirectedGraph ret = gabow.generate();
	int i= 1;

	while (ret != null) {
		ret = gabow.generate();
		i++;

	}

	System.out.println(i);
	*/
	/*
	// K_5
	SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
	SimpleNode a = graph.addNode();
	SimpleNode b = graph.addNode();
	SimpleNode c = graph.addNode();
	SimpleNode d = graph.addNode();
	SimpleNode e = graph.addNode();

	SimpleEdge ab = graph.addEdge(a,b);
	SimpleEdge ac = graph.addEdge(a,c);
	SimpleEdge ad = graph.addEdge(a,d);
	SimpleEdge ae = graph.addEdge(a,e);

	SimpleEdge bc = graph.addEdge(b,c);
	SimpleEdge bd = graph.addEdge(b,d);
	SimpleEdge be = graph.addEdge(b,e);

	SimpleEdge cd = graph.addEdge(c,d);
	SimpleEdge ce = graph.addEdge(c,e);

	SimpleEdge de = graph.addEdge(d,e);

	GraphElementHashAnnotator<SimpleEdge, Weight> an = new GraphElementHashAnnotator<>();
	an.setAnnotation(ab, new Weight(1.));
	an.setAnnotation(ac, new Weight(2.));
	an.setAnnotation(ad, new Weight(3.));
	an.setAnnotation(ae, new Weight(4.));
	an.setAnnotation(bc, new Weight(5.));
	an.setAnnotation(bd, new Weight(6.));
	an.setAnnotation(be, new Weight(7.));
	an.setAnnotation(cd, new Weight(8.));
	an.setAnnotation(ce, new Weight(9.));
	an.setAnnotation(de, new Weight(10.));

	GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(an);

	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	SimpleUndirectedGraph ret = gabow.generate();
	int i= 1;

	while (ret != null) {
		ret = gabow.generate();
		i++;
	}

	System.out.println(i);
	*/
	/*
	// K_6
	SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
	SimpleNode a = graph.addNode();
	SimpleNode b = graph.addNode();
	SimpleNode c = graph.addNode();
	SimpleNode d = graph.addNode();
	SimpleNode e = graph.addNode();
	SimpleNode f = graph.addNode();

	SimpleEdge ab = graph.addEdge(a,b);
	SimpleEdge ac = graph.addEdge(a,c);
	SimpleEdge ad = graph.addEdge(a,d);
	SimpleEdge ae = graph.addEdge(a,e);
	SimpleEdge af = graph.addEdge(a,f);

	SimpleEdge bc = graph.addEdge(b,c);
	SimpleEdge bd = graph.addEdge(b,d);
	SimpleEdge be = graph.addEdge(b,e);
	SimpleEdge bf = graph.addEdge(b,f);

	SimpleEdge cd = graph.addEdge(c,d);
	SimpleEdge ce = graph.addEdge(c,e);
	SimpleEdge cf = graph.addEdge(c,f);

	SimpleEdge de = graph.addEdge(d,e);
	SimpleEdge df = graph.addEdge(d,f);

	SimpleEdge ef = graph.addEdge(e,f);

	GraphElementHashAnnotator<SimpleEdge, Weight> an = new GraphElementHashAnnotator<>();
	an.setAnnotation(ab, new Weight(1.));
	an.setAnnotation(ac, new Weight(2.));
	an.setAnnotation(ad, new Weight(3.));
	an.setAnnotation(ae, new Weight(4.));
	an.setAnnotation(af, new Weight(4.));
	an.setAnnotation(bc, new Weight(5.));
	an.setAnnotation(bd, new Weight(6.));
	an.setAnnotation(be, new Weight(7.));
	an.setAnnotation(bf, new Weight(7.));

	an.setAnnotation(cd, new Weight(8.));
	an.setAnnotation(ce, new Weight(9.));
	an.setAnnotation(cf, new Weight(9.));

	an.setAnnotation(de, new Weight(10.));
	an.setAnnotation(df, new Weight(10.));

	an.setAnnotation(ef, new Weight(11.));

	GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(an);

	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	SimpleUndirectedGraph ret = gabow.generate();
	int i= 1;

	while (ret != null) {
		ret = gabow.generate();
		i++;
	}

	System.out.println(i);

	*/

	// K_7
/*	SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
	SimpleNode a = graph.addNode();
	SimpleNode b = graph.addNode();
	SimpleNode c = graph.addNode();
	SimpleNode d = graph.addNode();
	SimpleNode e = graph.addNode();
	SimpleNode f = graph.addNode();
	SimpleNode g = graph.addNode();

	SimpleEdge ab = graph.addEdge(a,b);
	SimpleEdge ac = graph.addEdge(a,c);
	SimpleEdge ad = graph.addEdge(a,d);
	SimpleEdge ae = graph.addEdge(a,e);
	SimpleEdge af = graph.addEdge(a,f);
	SimpleEdge ag = graph.addEdge(a,g);

	SimpleEdge bc = graph.addEdge(b,c);
	SimpleEdge bd = graph.addEdge(b,d);
	SimpleEdge be = graph.addEdge(b,e);
	SimpleEdge bf = graph.addEdge(b,f);
	SimpleEdge bg = graph.addEdge(b,g);

	SimpleEdge cd = graph.addEdge(c,d);
	SimpleEdge ce = graph.addEdge(c,e);
	SimpleEdge cf = graph.addEdge(c,f);
	SimpleEdge cg = graph.addEdge(c,g);

	SimpleEdge de = graph.addEdge(d,e);
	SimpleEdge df = graph.addEdge(d,f);
	SimpleEdge dg = graph.addEdge(d,g);

	SimpleEdge ef = graph.addEdge(e,f);
	SimpleEdge eg = graph.addEdge(e,g);

	SimpleEdge fg = graph.addEdge(f,g);

	GraphElementHashAnnotator<SimpleEdge, Weight> an = new GraphElementHashAnnotator<>();
	an.setAnnotation(ab, new Weight(1.));
	an.setAnnotation(ac, new Weight(1.));
	an.setAnnotation(ad, new Weight(1.));
	an.setAnnotation(ae, new Weight(1.));
	an.setAnnotation(af, new Weight(1.));
	an.setAnnotation(ag, new Weight(1.));

	an.setAnnotation(bc, new Weight(1.));
	an.setAnnotation(bd, new Weight(1.));
	an.setAnnotation(be, new Weight(1.));
	an.setAnnotation(bf, new Weight(1.));
	an.setAnnotation(bg, new Weight(1.));

	an.setAnnotation(cd, new Weight(1.));
	an.setAnnotation(ce, new Weight(1.));
	an.setAnnotation(cf, new Weight(1.));
	an.setAnnotation(cg, new Weight(1.));

	an.setAnnotation(de, new Weight(1.));
	an.setAnnotation(df, new Weight(1.));
	an.setAnnotation(dg, new Weight(1.));

	an.setAnnotation(ef, new Weight(1.));
	an.setAnnotation(eg, new Weight(1.));

	an.setAnnotation(fg, new Weight(1.));

	GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(an);

	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	SimpleUndirectedGraph ret = gabow.generate();
	int i= 1;

	while (ret != null) {
		ret = gabow.generate();
		i++;
	}

	System.out.println(i);*/

	/*/*
	SimpleUndirectedGraph graph = new SimpleUndirectedGraph();
	SimpleNode a = graph.addNode();
	SimpleNode b = graph.addNode();
	SimpleNode c = graph.addNode();
	SimpleNode d = graph.addNode();
	SimpleNode e = graph.addNode();
	SimpleNode f = graph.addNode();
	SimpleNode g = graph.addNode();
	SimpleNode h = graph.addNode();

	SimpleEdge ab = graph.addEdge(a,b);
	SimpleEdge ac = graph.addEdge(a,c);
	SimpleEdge ad = graph.addEdge(a,d);
	SimpleEdge ae = graph.addEdge(a,e);
	SimpleEdge af = graph.addEdge(a,f);
	SimpleEdge ag = graph.addEdge(a,g);
	SimpleEdge ah = graph.addEdge(a,h);

	SimpleEdge bc = graph.addEdge(b,c);
	SimpleEdge bd = graph.addEdge(b,d);
	SimpleEdge be = graph.addEdge(b,e);
	SimpleEdge bf = graph.addEdge(b,f);
	SimpleEdge bg = graph.addEdge(b,g);
	SimpleEdge bh = graph.addEdge(b,h);

	SimpleEdge cd = graph.addEdge(c,d);
	SimpleEdge ce = graph.addEdge(c,e);
	SimpleEdge cf = graph.addEdge(c,f);
	SimpleEdge cg = graph.addEdge(c,g);
	SimpleEdge ch = graph.addEdge(c,h);

	SimpleEdge de = graph.addEdge(d,e);
	SimpleEdge df = graph.addEdge(d,f);
	SimpleEdge dg = graph.addEdge(d,g);
	SimpleEdge dh = graph.addEdge(d,h);

	SimpleEdge ef = graph.addEdge(e,f);
	SimpleEdge eg = graph.addEdge(e,g);
	SimpleEdge eh = graph.addEdge(e,h);

	SimpleEdge fg = graph.addEdge(f,g);
	SimpleEdge fh = graph.addEdge(f,h);

	SimpleEdge gh = graph.addEdge(g,h);

	GraphElementHashAnnotator<SimpleEdge, Weight> an = new GraphElementHashAnnotator<>();
	an.setAnnotation(ab, new Weight(1.));
	an.setAnnotation(ac, new Weight(2.));
	an.setAnnotation(ad, new Weight(3.));
	an.setAnnotation(ae, new Weight(4.));
	an.setAnnotation(af, new Weight(5.));
	an.setAnnotation(ag, new Weight(6.));
	an.setAnnotation(ah, new Weight(7.));

	an.setAnnotation(bc, new Weight(8.));
	an.setAnnotation(bd, new Weight(9.));
	an.setAnnotation(be, new Weight(10.));
	an.setAnnotation(bf, new Weight(11.));
	an.setAnnotation(bg, new Weight(12.));
	an.setAnnotation(bh, new Weight(13.));

	an.setAnnotation(cd, new Weight(14.));
	an.setAnnotation(ce, new Weight(15.));
	an.setAnnotation(cf, new Weight(16.));
	an.setAnnotation(cg, new Weight(17.));
	an.setAnnotation(ch, new Weight(18.));

	an.setAnnotation(de, new Weight(19.));
	an.setAnnotation(df, new Weight(20.));
	an.setAnnotation(dg, new Weight(21.));
	an.setAnnotation(dh, new Weight(22.));

	an.setAnnotation(ef, new Weight(23.));
	an.setAnnotation(eg, new Weight(24.));
	an.setAnnotation(eh, new Weight(25.));

	an.setAnnotation(fg, new Weight(26.));
	an.setAnnotation(fh, new Weight(27.));

	an.setAnnotation(gh, new Weight(28.));

	GraphElementWeightAnnotator<SimpleEdge> weights = new GraphElementWeightAnnotator<>(an);

	Gabow<SimpleNode, SimpleEdge, SimpleUndirectedGraph> gabow = new Gabow<>(graph, weights);

	SimpleUndirectedGraph ret = gabow.generate();
	int i= 1;

	while (ret != null) {
		ret = gabow.generate();
		i++;
	}


	System.out.println(i);
	/*
	SimpleUndirectedGraph spanningTree = new SimpleUndirectedGraph();
	SimpleNode az = spanningTree.addNode();
	SimpleNode bz = spanningTree.addNode();
	SimpleNode cz = spanningTree.addNode();
	SimpleNode dz = spanningTree.addNode();

	SimpleEdge azdz = spanningTree.addEdge(az,bz);
	SimpleEdge czdz = spanningTree.addEdge(az,cz);
	SimpleEdge azbz = spanningTree.addEdge(az,dz);

	assertTrue(gabow.isSpanningTree(spanningTree, graph));
	*/
	/*ArrayList<SimpleEdge> in = new ArrayList<>();
	//in.add(ad);
	ArrayList<SimpleEdge> out = new ArrayList<>();
	//out.add(bc);



	ArrayList<SimpleEdge> ret = gabow.init(in, out);
	System.out.println(ret);
	SimpleEdge e = ret.get(0);
	SimpleEdge f = ret.get(1);

	System.out.println(e + " " + weights.getAnnotation(e).getFirstWeight());
	System.out.println(f + " " + weights.getAnnotation(f).getFirstWeight());
	*/



}

