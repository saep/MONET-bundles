package com.github.monet.datastructures;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.monet.graph.interfaces.UnionFind;

public class SimpleUnion<T> implements UnionFind<T> {

	HashMap<T,T> map;
	ArrayList<T> elements;

	public SimpleUnion() {
		map = new HashMap<>();
		elements = new ArrayList<>();
	}

	@Override
	public void add(T e) {
		map.put(e,e);
		elements.add(e);
	}

	@Override
	public T find(T e) {
		return map.get(e);

	}

	@Override
	public T union(T u, T v) {

		map.remove(u);
		map.put(u, v);

		return v;
	}

	@Override
	public boolean makeRepresentative(T e) {

		T r = map.get(e);

		map.put(r, e);

		for (T t : elements) {
			if (map.get(t) == r) {
				map.put(t, e);
			}
		}

		return false;
	}

}
