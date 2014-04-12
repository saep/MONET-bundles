package com.github.monet.graph.interfaces;

public interface UnionFind<T> {

	public void add(T e);

	public T find(T e);

	public T union(T u, T v);

	public boolean makeRepresentative(T e);
}
