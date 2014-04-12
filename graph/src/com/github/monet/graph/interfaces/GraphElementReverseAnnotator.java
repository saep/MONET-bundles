package com.github.monet.graph.interfaces;

import java.util.Collection;

/**
 * Models annotator to annotate graph elements with query support for
 * inverse images (annotation -> set of graph elements).
 *
 *
 *
 * @param <GE> type of graph element, upper bounded by type graph element
 * @param <GA> generic type, which models the type of annotation
 */
public interface GraphElementReverseAnnotator<GE extends GraphElement, GA>
		extends GraphElementAnnotator<GE, GA> {

	public Collection<GE> getElements(GA annotation);
}
