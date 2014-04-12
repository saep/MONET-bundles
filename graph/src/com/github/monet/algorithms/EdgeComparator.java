package com.github.monet.algorithms;

import java.util.Comparator;

import com.github.monet.graph.interfaces.*;
import com.github.monet.graph.weighted.*;

/**
 * Implements the interface Comperator for comparing edges using an on object of
 * the type GraphElementWeightAnnotator.
 *
 * @author Christopher Morris
 *
 */
public class EdgeComparator<E extends Edge> implements Comparator<E> {
	private GraphElementWeightAnnotator<E> annotator;

	public EdgeComparator(GraphElementWeightAnnotator<E> annotator) {
		this.annotator = annotator;
	}

	@Override
	public int compare(E e1, E e2) {
		double weightGe1 = this.annotator.getAnnotation(e1).getFirstWeight();
		double weightGe2 = this.annotator.getAnnotation(e2).getFirstWeight();

		if (weightGe1 < weightGe2) {
			return -1;
		} else if (weightGe1 > weightGe2)
			return 1;
		else {
			return 0;
		}
	}

	public void setAnnotator(GraphElementWeightAnnotator<E> annotator) {
		this.annotator = annotator;

	}

	public GraphElementWeightAnnotator<E> getAnnotator() {
		return this.annotator;
	}
}
