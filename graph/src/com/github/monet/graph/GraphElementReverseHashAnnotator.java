/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.monet.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.github.monet.graph.interfaces.GraphElement;
import com.github.monet.graph.interfaces.GraphElementReverseAnnotator;

/**
 * Implementation of the interface GraphElementReverseAnnotator.
 *
 *
 *
 * @param <GE> type of graph element, upper bounded by type graph element
 * @param <GA> generic type, which models the type of annotation
 */
public class GraphElementReverseHashAnnotator<GE extends GraphElement, GA>
		extends GraphElementHashAnnotator<GE, GA>
		implements GraphElementReverseAnnotator<GE, GA> {

	protected HashMap<GA, HashSet<GE>> reverseMap;

	public GraphElementReverseHashAnnotator()
	{
		super();
		reverseMap = new HashMap<>();
	}

	@Override
	public void setAnnotation(GE e, GA a) {
		GA previousAnnotation = super.getAnnotation(e);
		if (previousAnnotation != null) {
			HashSet<GE> previousSet = reverseMap.get(previousAnnotation);
			previousSet.remove(e);
			if (previousSet.isEmpty()) {
				reverseMap.remove(previousAnnotation);
			}
		}
		super.setAnnotation(e, a);
		if (reverseMap.get(a) == null) {
			reverseMap.put(a, new HashSet<GE>());
		}
		reverseMap.get(a).add(e);
	}

	@Override
	public Collection<GE> getElements(GA annotation) {
		Collection<GE> mapping = reverseMap.get(annotation);
		if (mapping != null) {
			return new HashSet<>(mapping);
		} else {
			return new HashSet<>();
		}
	}
}
