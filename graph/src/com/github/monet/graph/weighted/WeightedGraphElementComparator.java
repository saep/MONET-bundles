package com.github.monet.graph.weighted;

import java.util.Comparator;

import com.github.monet.graph.interfaces.GraphElement;

public class WeightedGraphElementComparator<GE extends GraphElement> implements Comparator<GE> {

	private GraphElementWeightAnnotator<GE> annotator;
	private LexSortComparator weightComparator;

	public WeightedGraphElementComparator(GraphElementWeightAnnotator<GE> annotator) {
		this.annotator = annotator;
		this.weightComparator = new LexSortComparator();
	}

	public int compare(GE ge1, GE ge2) {

		Weight w_ge1 = annotator.getAnnotation(ge1);
		Weight w_ge2 = annotator.getAnnotation(ge2);

		return weightComparator.compare(w_ge1, w_ge2);
	}
}
