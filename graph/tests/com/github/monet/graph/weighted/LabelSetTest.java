package com.github.monet.graph.weighted;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.weighted.LabelSet;
import com.github.monet.graph.weighted.Weight;

public class LabelSetTest {

	@Test
	public void testEquals() {

		double[] weights1 = { 2.0, 3.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 2.0, 3.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 3.0, 4.0 };
		Weight weight3 = new Weight(weights3);
		double[] weights4 = { 3.0, 4.0 };
		Weight weight4 = new Weight(weights4);
		double[] weights5 = { 4.0, 5.0 };
		Weight weight5 = new Weight(weights5);
		double[] weights6 = { 4.0, 5.0 };
		Weight weight6 = new Weight(weights6);

		LabelSet l1 = new LabelSet();
		LabelSet l2 = new LabelSet();

		l1.insertLabel(weight1);
		l2.insertLabel(weight2);
		l1.insertLabel(weight3);
		l2.insertLabel(weight4);
		l1.insertLabel(weight5);

		assertFalse(l1.equals(l2));

		l2.insertLabel(weight6);

		assertTrue(l1.equals(l2));
	}

	@Test
	public void testAdd() {

		double[] weights1 = { 2.0, 3.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 3.0, 4.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 3.0, 4.0 };
		Weight weight3 = new Weight(weights3);
		double[] weights4 = { 4.0, 5.0 };
		Weight weight4 = new Weight(weights4);
		double[] weights5 = { 4.0, 5.0 };
		Weight weight5 = new Weight(weights5);
		double[] weights6 = { 5.0, 6.0 };
		Weight weight6 = new Weight(weights6);

		LabelSet l1 = new LabelSet();
		LabelSet l2 = new LabelSet();

		l1.insertLabel(weight1);
		l2.insertLabel(weight2);
		l1.insertLabel(weight3);
		l2.insertLabel(weight4);
		l1.insertLabel(weight5);
		l2.insertLabel(weight6);

		double[] weightsAdd = { 1.0, 1.0 };
		Weight weightAdd = new Weight(weightsAdd);

		l1.add(weightAdd);

		assertTrue(l1.equals(l2));
	}

}
