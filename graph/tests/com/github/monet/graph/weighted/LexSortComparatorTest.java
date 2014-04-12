package com.github.monet.graph.weighted;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.weighted.LexSortComparator;
import com.github.monet.graph.weighted.Weight;

public class LexSortComparatorTest {

	@Test
	public void testComparator() {
		double[] weights1 = { 1.0, 1.0 };
		Weight weight1 = new Weight(weights1);
		double[] weights2 = { 2.0, 2.0 };
		Weight weight2 = new Weight(weights2);
		double[] weights3 = { 2.0, 3.0 };
		Weight weight3 = new Weight(weights3);
		double[] weights4 = { 3.0, 2.0 };
		Weight weight4 = new Weight(weights4);
		double[] weights5 = { 4.0, 1.0 };
		Weight weight5 = new Weight(weights5);
		double[] weights5a = { 4.0, 1.0 };
		Weight weight5a = new Weight(weights5a);

		LexSortComparator lsc = new LexSortComparator();
		assertTrue(lsc.compare(weight1, weight2) < 0);
		assertTrue(lsc.compare(weight2, weight3) < 0);
		assertTrue(lsc.compare(weight3, weight4) < 0);
		assertTrue(lsc.compare(weight4, weight5) < 0);
		assertTrue(lsc.compare(weight5, weight1) > 0);

		assertTrue(lsc.compare(weight2, weight1) > 0);
		assertTrue(lsc.compare(weight3, weight2) > 0);
		assertTrue(lsc.compare(weight4, weight3) > 0);
		assertTrue(lsc.compare(weight5, weight4) > 0);
		assertTrue(lsc.compare(weight1, weight5) < 0);

		assertTrue(lsc.compare(weight5, weight5a) == 0);
	}
}
