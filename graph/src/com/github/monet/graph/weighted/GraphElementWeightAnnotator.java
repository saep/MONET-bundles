package com.github.monet.graph.weighted;

import java.util.Collection;

import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.interfaces.*;

/**
 * Implemention of the interface GraphElementAnnotator, annotates graph elements
 * with weight vectors.
 *
 * @author Christopher Morris
 *
 * @param <GE>
 */
public class GraphElementWeightAnnotator<GE extends GraphElement> implements
		GraphElementAnnotator<GE, Weight> {

	private GraphElementAnnotator<GE, Weight> adapted;

	public GraphElementWeightAnnotator(GraphElementAnnotator<GE, Weight> adapted) {
		assert adapted != null : "adapted must not be null";

		this.adapted = adapted;
	}

	@Override
	public Collection<GE> getAnnotatedElements() {
		return this.getAdapted().getAnnotatedElements();
	}

	@Override
	public Weight getAnnotation(GE e) {
		assert e != null : "e must not be null";

		return this.getAdapted().getAnnotation(e);
	}

	@Override
	public void setAnnotation(GE e, Weight a) {
		assert e != null : "e must not be null";
		assert a != null : "a must not be null";
		this.getAdapted().setAnnotation(e, a);
	}

	/**
	 * Returns the dimension of a random Weight in the annotation colletion.
	 *
	 * @return Dimension if annotation collection contains at least one element,
	 * 0 otherwise
	 */
	public int getDimension() {
		if (adapted.getAnnotatedElements().isEmpty()) {
			return 0;
		} else {
			return adapted.getAnnotation(adapted.getAnnotatedElements().
					iterator().next()).getDimension();
		}
	}

	public Weight sum(Iterable<GE> elements) {
		assert elements.iterator().hasNext() : "elements must not be empty";

		int dim = adapted.getAnnotation(elements.iterator().next()).
				getDimension();

		Weight sum = new Weight(new double[dim]);
		for (GE element : elements) {
			sum.add(adapted.getAnnotation(element));
		}
		return sum;
	}

	/**
	 * Scalarizes all weight vector annotations to scalar values.
	 *
	 * @param weight coefficients
	 *            coefficients
	 * @return scalar product of weight vector and parameter coefficients
	 */
	public GraphElementWeightAnnotator<GE> scalarize(double[] coefficients) {
		assert coefficients.length == getDimension() :
				"dimension of coefficients does not match dimension of weights";

		GraphElementAnnotator<GE, Weight> newAnnotator =
				new GraphElementHashAnnotator<>();
		GraphElementWeightAnnotator<GE> scalarized =
				new GraphElementWeightAnnotator<>(newAnnotator);
		for (GE element : this.getAdapted().getAnnotatedElements()) {
			Weight weight = this.getAdapted().getAnnotation(element);
			Weight scalarWeight = weight.scalarize(coefficients);
			scalarized.setAnnotation(element, scalarWeight);
		}
		return scalarized;
	}

	/**
	 * @return annotator object
	 */
	public GraphElementAnnotator<GE, Weight> getAdapted() {
		return adapted;
	}
}
