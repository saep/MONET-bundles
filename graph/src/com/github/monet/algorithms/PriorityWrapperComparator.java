package com.github.monet.algorithms;

import java.util.Comparator;

/**
 * Implements the interface java.util.Comparator to compare objects of type
 * PriorityWrapper, which is used in the implementation of the class
 * SimplePriorityQueue.
 *
 * @author Christopher Morris
 *
 * @param <T>
 */
public class PriorityWrapperComparator<T> implements
		Comparator<PriorityWrapper<T>> {
	public PriorityWrapperComparator() {
		super();
	}

	@Override
	public int compare(PriorityWrapper<T> p1, PriorityWrapper<T> p2) {
		double p1d = p1.p;
		double p2d = p2.p;

		if (p1d > p2d) {
			return 1;
		} else if (p1d < p2d) {
			return -1;
		} else {
			return 0;
		}

	}
}
