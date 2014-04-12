package com.github.monet.algorithms.sssp;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.Weight.DominationRelation;
import org.junit.Test;

public class AVLTreeTest {

	@Test
	public void containsDominatorTest() {
		boolean result = true;
		int pathes = 200;
		// Dimension has to be 2, because the avltree only looks for the first 2
		// components.
		int dimension = 2;
		double[] weightC = new double[dimension];
		for (int j = 0; j < dimension; j++) {
			weightC[j] = (int) (Math.random() * 100000);
		}
		Weight comp = new Weight(weightC);
		AVLTree t = new AVLTree();
		ArrayList<Weight> allLabels = new ArrayList<Weight>();
		for (int i = 0; i < pathes; i++) {
			double[] weightE = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				weightE[j] = (int) (Math.random() * 100000);
			}
			Weight w2 = new Weight(weightE);
			allLabels.add(w2);
			t.insert(w2);
			if(this.containsDominator(allLabels, comp) != t
					.containsDominator(comp)) {
				result = false;
			}
		}
		assertTrue(result);
	}

	@Test
	public void recIsBalancedTest() {
		boolean result = true;
		int pathes = 200;
		// Dimension has to be 2, because the avltree only looks for the first 2
		// components.
		int dimension = 2;
		AVLTree t = new AVLTree();
		ArrayList<Weight> allLabels = new ArrayList<Weight>();
		for (int i = 0; i < pathes; i++) {
			double[] weightE = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				weightE[j] = (int) (Math.random() * 100000);
			}
			Weight w2 = new Weight(weightE);
			allLabels.add(w2);
			t.insert(w2);
			if(!t.recIsBalanced()) {
				result = false;
			}
		}
		assertTrue(result);
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
	public boolean containsDominator(ArrayList<Weight> source, Weight comp) {
		boolean retval = false;
		for (Weight i : source) {
			if (i.dominates(comp).equals(DominationRelation.PARETO_SMALLER)) {
				retval = true;
			}
		}
		return retval;
	}

}
