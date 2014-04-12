package com.github.monet.graph.weighted;

import java.util.Comparator;

public class LexSortComparator implements Comparator<Weight> {

	@Override
	public int compare(Weight first, Weight second) {
		// Can't throw an exception, compare only common dimensions
		int dim = first.getDimension() < second.getDimension() ? first
				.getDimension() : second.getDimension();

		for (int i = 0; i < dim; ++i) {
			// TODO EPS
			if (first.getWeight(i) < second.getWeight(i)) {
				return -1;
			} else if (first.getWeight(i) > second.getWeight(i)) {
				return 1;
			}
		}
		return 0;
	}
}
