package com.github.monet.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<T> {

	ArrayList<T> elements;
	Comparator<T> comp;

	public SortedList(Comparator<T> comp) {
		this.elements = new ArrayList<>();
		this.comp = comp;
	}

	public void add(T e) {
		elements.add(e);
		Collections.sort(elements,
				comp);
	}

	public T first() {
		T r;

		try {
			r = elements.get(0);
			return r;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public T pollFirst() {
		T r;

		try {
			r = elements.get(0);
			elements.remove(r);
			return r;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void clear() {
		elements.clear();

	}

	public int size() {
		return elements.size();
	}
}
