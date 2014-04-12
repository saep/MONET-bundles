package com.github.monet.graph.weighted;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Models a set of labels.
 *
 * @author Michael Capelle
 *
 */
public class LabelSet {

	private List<Weight> labels;

	public LabelSet() {
		this.labels = new ArrayList<Weight>();
	}

	public LabelSet(List<Weight> labels) {
		this.labels = labels;
	}

	public List<Weight> getLabels() {
		return this.labels;
	}

	public void setLabels(List<Weight> labels) {
		this.labels = labels;
	}

	public double getDimension() {
		if (!this.labels.isEmpty()) {
			return 0;
		} else {
			return this.labels.get(0).getDimension();
		}
	}

	public void insertLabel(Weight label) {
		this.labels.add(label);
	}

	@Override
	public LabelSet clone() {
		LabelSet r = new LabelSet();
		for (Weight w : this.labels) {
			r.insertLabel(w.clone());
		}
		return r;
	}

	public static LabelSet add(LabelSet l, Weight w) {
		/*
		 * This method is a static method to emphasize that it creates a new
		 * Weight object and does no alter any existing object.
		 */
		LabelSet r = l.clone();
		r.add(w);
		return r;
	}

	public void add(Weight w) {
		/*
		 * This method is an instance method to emphasize that it alters the
		 * objects it is called on.
		 */
		for (Weight v : this.labels) {
			v.add(w);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LabelSet)) {
			return false;
		} else {
			LabelSet compareTo = (LabelSet) o;
			boolean isEqual = true;
			isEqual &= this.labels.containsAll(compareTo.labels);
			isEqual &= compareTo.labels.containsAll(this.labels);
			return isEqual;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = (23 * hash) + Objects.hashCode(this.labels);
		return hash;
	}
}
