package com.github.monet.graph;

import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

import com.github.monet.algorithms.PriorityWrapper;
import com.github.monet.algorithms.PriorityWrapperComparator;
import com.github.monet.graph.interfaces.FancyPriorityQueue;

/**
 * Implements the interface FancyPriorityQueue using java.util.PriorityQueue .
 *
 * @author Christopher Morris
 *
 */
public class SimplePriorityQueue<T> implements
		FancyPriorityQueue<T> {

	private PriorityQueue<PriorityWrapper<T>> priorityQueue;
	private HashMap<T, Double> elementPriorityMap;

	public SimplePriorityQueue(int initialNumElements) {
		this.priorityQueue = new PriorityQueue<>(initialNumElements,
				new PriorityWrapperComparator<T>());

		this.elementPriorityMap = new HashMap<>();
	}

	@Override
	public boolean add(T e, double p) {
		if (!this.elementPriorityMap.containsKey(e)) {
			boolean success = true;

			this.elementPriorityMap.put(e, p);
			success &= this.priorityQueue.add(new PriorityWrapper<>(e, p));

			return success;
		} else {
			return false;
		}
	}

	@Override
	public boolean update(T e, double p) {
		if (this.elementPriorityMap.containsKey(e)) {
			boolean success = true;

			success &= this.priorityQueue.remove(new PriorityWrapper<>(e,
					this.elementPriorityMap.get(e)));
			success &= this.priorityQueue.add(new PriorityWrapper<>(e, p));
			this.elementPriorityMap.remove(e);
			this.elementPriorityMap.put(e, p);

			return success;
		} else {
			return false;
		}
	}

	@Override
	public T poll() {
		if (!this.priorityQueue.isEmpty()) {
			T polled = this.priorityQueue.poll().e;
			this.elementPriorityMap.remove(polled);
			return polled;
		} else {
			return null;
		}
	}

	@Override
	public boolean remove(T e) {
		if (this.elementPriorityMap.containsKey(e)) {
			boolean success = true;

			success &= this.priorityQueue.remove(new PriorityWrapper<>(e,
					this.elementPriorityMap.get(e)));
			elementPriorityMap.remove(e);

			return success;
		} else {
			return false;
		}

	}

	@Override
	public int getSize() {
		return this.priorityQueue.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof PriorityQueue)) {
			return false;
		} else {
			SimplePriorityQueue<T> compareTo = (SimplePriorityQueue<T>) o;
			return this.priorityQueue.equals(compareTo.priorityQueue);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.priorityQueue);
		return hash;
	}
}
