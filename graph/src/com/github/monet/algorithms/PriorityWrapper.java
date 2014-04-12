package com.github.monet.algorithms;

import java.util.Objects;

/**
 * Wraps an Object of generic type T and a priority of type double together.
 *
 * @author Christopher Morris
 *
 * @param <T> generic type
 */
public class PriorityWrapper<T> {

	public T e;
	public double p;

	public PriorityWrapper(T e, double p) {
		this.e = e;
		this.p = p;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PriorityWrapper) {
			PriorityWrapper<?> other = (PriorityWrapper<?>)o;
			return e.equals(other.e) && p == other.p;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.e);
		hash =
				53 * hash +
				(int) (Double.doubleToLongBits(this.p) ^
				(Double.doubleToLongBits(this.p) >>> 32));
		return hash;
	}
}
