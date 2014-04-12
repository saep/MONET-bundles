package com.github.monet.graph;

import java.util.HashMap;

/**
 * Simple wrapper around HashMap, which is able to store objects of different
 * types.
 *
 * @author Christopher Morris
 *
 */
public class HeterogeneousHashAnnotatorContainer extends
		HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public HeterogeneousHashAnnotatorContainer() {
		super();
	}

	/**
	 * Returns object mapped to string s.
	 *
	 * @param s
	 *            string
	 * @param clazz
	 *            type of to be returned object, e.g Integer
	 * @return
	 */
	public <T> T get(String s, Class<T> clazz) {
		return clazz.cast(this.get(s));
	}

	@Deprecated
	@Override
	public Object get(Object o) {
		return super.get(o);
	}
}
