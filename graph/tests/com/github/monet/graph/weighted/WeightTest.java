package com.github.monet.graph.weighted;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.monet.graph.*;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.UndirectedEdge;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;

public class WeightTest extends TestCase {

	@Test
	public void test() {
		double[] weights1 = { 2.0, 3.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 2.0, 3.0 };
		Weight weight2 = new Weight(weights2);

		double[] weights3 = { 4.0, 6.0 };
		Weight weight3 = new Weight(weights3);

		assertEquals(weight1, weight2);

		weight1.add(weight2);

		assertEquals(weight3, weight1);

		double[] weights4 = { 6.0, 9.0 };
		weight3 = new Weight(weights4);
		assertEquals(Weight.add(weight1, weight2), weight3);
	}

	@Test
	public void testScalarization() {
		SimpleNode node1 = new SimpleNode(1);
		SimpleNode node2 = new SimpleNode(2);
		SimpleNode node3 = new SimpleNode(3);
		SimpleEdge edge1 = new SimpleEdge(node1, node2);
		SimpleEdge edge2 = new SimpleEdge(node2, node3);
		double[] weights1 = { 2.0, 3.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 2.0, 4.0 };
		Weight weight2 = new Weight(weights2);
		GraphElementHashAnnotator<UndirectedEdge, Weight> annotation = new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<UndirectedEdge> weightAnnotation = new GraphElementWeightAnnotator<>(
				annotation);
		annotation.setAnnotation(edge1, weight1);
		annotation.setAnnotation(edge2, weight2);
		double[] scalars = { 0.5, 2.0 };
		GraphElementAnnotator<UndirectedEdge, Weight> scalarAnnotation = weightAnnotation
				.scalarize(scalars);

		assertTrue(scalarAnnotation.getAnnotation(edge1).getFirstWeight() == 7.0);
		assertTrue(scalarAnnotation.getAnnotation(edge2).getFirstWeight() == 9.0);
	}

	@Test
	public void testDomination() {
		double[] weights1 = { 2.0, 3.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 2.0, 4.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 3.0, 4.0 };
		Weight weight3 = new Weight(weights3);
		double[] weights4 = { 2.5, 3.5 };
		Weight weight4 = new Weight(weights4);
		double[] weights5 = { 2.5, 3.5 };
		Weight weight5 = new Weight(weights5);

		assertTrue(weight1.dominates(weight2) == Weight.DominationRelation.PARETO_SMALLER);
		assertTrue(weight1.dominates(weight3) == Weight.DominationRelation.PARETO_SMALLER);
		assertTrue(weight2.dominates(weight1) == Weight.DominationRelation.PARETO_GREATER);
		assertTrue(weight2.dominates(weight3) == Weight.DominationRelation.PARETO_SMALLER);
		assertTrue(weight3.dominates(weight1) == Weight.DominationRelation.PARETO_GREATER);
		assertTrue(weight3.dominates(weight2) == Weight.DominationRelation.PARETO_GREATER);
		assertTrue(weight4.dominates(weight2) == Weight.DominationRelation.UNCOMPARABLE);
		assertTrue(weight2.dominates(weight4) == Weight.DominationRelation.UNCOMPARABLE);
		assertTrue(weight4.dominates(weight5) == Weight.DominationRelation.EQUAL);
	}
}
