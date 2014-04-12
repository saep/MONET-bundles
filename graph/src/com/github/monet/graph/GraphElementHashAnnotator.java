package com.github.monet.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import com.github.monet.graph.interfaces.GraphElement;
import com.github.monet.graph.interfaces.GraphElementAnnotator;

/**
 * Implementation of the interface GraphElementAnnotator.
 *
 * @author Christopher Morris
 *
 * @param <GE>
 *            type of graph element, upper bounded by type graph element
 * @param <GA>
 *            generic type, which models the type of annotation
 */
public class GraphElementHashAnnotator<GE extends GraphElement, GA> implements
		GraphElementAnnotator<GE, GA> {

	private HashMap<GE, GA> annotationMap;

	public GraphElementHashAnnotator() {
		annotationMap = new HashMap<>();
	}

	public GraphElementHashAnnotator(int initialCapacity) {
		annotationMap = new HashMap<>(initialCapacity);
	}

	@Override
	public GA getAnnotation(GE e) {
		return annotationMap.get(e);
	}

	@Override
	public void setAnnotation(GE e, GA a) {
		annotationMap.put(e, a);
	}

	public void clear() {
		annotationMap.clear();
	}

	@Override
	public Collection<GE> getAnnotatedElements() {
		return annotationMap.keySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotationMap == null) ? 0 : annotationMap.hashCode());
		return result;
	}
}
