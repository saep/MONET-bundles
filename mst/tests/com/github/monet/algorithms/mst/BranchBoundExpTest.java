package com.github.monet.algorithms.mst;

import com.github.monet.algorithms.Kruskal;
import com.github.monet.generator.*;
import com.github.monet.graph.*;
import com.github.monet.graph.interfaces.ParetoSet;

import org.junit.Test;

import com.github.monet.algorithms.mst.FirstPhase2d;
import com.github.monet.algorithms.mst.KBestSecondPhase;

/**
 *
 * @author Hendrik
 */
public class BranchBoundExpTest {

	@Test
	public void testExp() {
		GridGraphGenerator gen = new GridGraphGenerator();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph =
				gen.generateGridGraph(5, 100, 2);

//		MonetGraphGenerator<SimpleUndirectedGraph> gen = new MonetGraphGenerator<>("undirected", 16, 2, 1.44);
//		gen.generate();
//		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph = gen.getGraph();
//		MonetGraphIntegerGenerator<SimpleUndirectedGraph> gen = new MonetGraphIntegerGenerator<>("undirected", 16, 2, 1.44);
//		gen.generate();
//		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph = gen.getGraph();

//		CompleteGraphGenerator gen = new CompleteGraphGenerator();
//		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph =
//				gen.generateCompleteGraph(7, 10, 2);
		graph.getGraph().setSafeMode(false);

		Kruskal<SimpleNode, SimpleEdge, SimpleUndirectedGraph> prim =
				new Kruskal<>();
		FirstPhase2d<SimpleNode, SimpleEdge, SimpleUndirectedGraph> firstPhase =
				new FirstPhase2d<>(prim);
		ParetoSet<SimpleNode, SimpleEdge, SimpleUndirectedGraph> ps =
				firstPhase.firstPhase(graph, "WEIGHTS");

		KBestSecondPhase<SimpleNode, SimpleEdge, SimpleUndirectedGraph> secondPhase =
				new KBestSecondPhase<>();
//		BranchBound<SimpleNode, SimpleEdge, SimpleUndirectedGraph> secondPhase2 =
//				new BranchBound<>();
		ParetoSet<SimpleNode, SimpleEdge, SimpleUndirectedGraph> pt =
				secondPhase.secondPhase(graph, "WEIGHTS", ps);
//		ParetoSet<SimpleNode, SimpleEdge, SimpleUndirectedGraph> pt2 =
//				secondPhase2.secondPhase(graph, "WEIGHTS", ps);

		for (SimpleUndirectedGraph g : ps) {
			pt.add(g);
			//pt2.add(g);
		}

		System.out.println(ps.size());
		System.out.println(pt.size());
		//System.out.println(pt2.size());
	}
}
