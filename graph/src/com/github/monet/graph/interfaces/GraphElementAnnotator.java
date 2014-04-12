package com.github.monet.graph.interfaces;

import java.util.Collection;

/**
 * Models annotator to annotate graph elements.
 *
 * @author Christopher Morris
 *
 * @param <GE>
 *            type of graph element, upper bounded by type graph element
 * @param <GA>
 *            generic type, which models type of annotation
 */
public interface GraphElementAnnotator<GE extends GraphElement, GA> {

	public Collection<GE> getAnnotatedElements();

	public GA getAnnotation(GE e);

	public void setAnnotation(GE e, GA a);
}
