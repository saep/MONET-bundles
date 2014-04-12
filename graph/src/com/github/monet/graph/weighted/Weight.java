package com.github.monet.graph.weighted;

import java.util.Arrays;

public class Weight {

	private double[] weight;

	public enum DominationRelation {
		UNCOMPARABLE, EQUAL, PARETO_SMALLER, PARETO_GREATER,
	}

	public Weight(double[] weight) {
		this.weight = weight;
	}

	public Weight(double weight) {
		this.weight = new double[1];
		this.weight[0] = weight;
	}

	public double[] getWeights() {
		return weight;
	}

	public void setWeights(double[] weight) {
		this.weight = weight;
	}

	public double getFirstWeight() {
		return this.weight[0];
	}

	public void setFirstWeight(double w) {
		this.weight[0] = w;
	}

	public double getWeight(int i) {
		return this.weight[i];
	}

	public void setWeight(int i, double w) {
		this.weight[i] = w;
	}

	public Weight scalarize(double[] coefficients) {
		if (this.weight.length != coefficients.length) {
			assert false : "Dimensions do not match";
			return null;
		}

		double scalar = 0;
		for (int i = 0; i < coefficients.length; i++) {
			scalar += coefficients[i] * this.weight[i];
		}
		return new Weight(scalar);
	}

	public int getDimension() {
		return this.weight.length;
	}

	public DominationRelation dominates(Weight competitor) {
		boolean thisIsSmallerAtLeastOnce = false;
		boolean otherIsSmallerAtLeastOnce = false;
		boolean equal = true;

		if (this.weight.length != competitor.weight.length) {
			assert false : "Dimensions do not match";
			return null;
		}

		for (int i = 0; i < this.weight.length; i++) {
			if (this.weight[i] < competitor.weight[i]) {
				thisIsSmallerAtLeastOnce = true;
				equal = false;
				if (otherIsSmallerAtLeastOnce) {
					return DominationRelation.UNCOMPARABLE;
				}
			} else if (this.weight[i] > competitor.weight[i]) {
				otherIsSmallerAtLeastOnce = true;
				equal = false;
				if (thisIsSmallerAtLeastOnce) {
					return DominationRelation.UNCOMPARABLE;
				}
			}
		}

		if (thisIsSmallerAtLeastOnce && !equal) {
			return DominationRelation.PARETO_SMALLER;
		} else if (otherIsSmallerAtLeastOnce && !equal) {
			return DominationRelation.PARETO_GREATER;
		} else {
			return DominationRelation.EQUAL;
		}
	}

	public boolean add(Weight summand) {
		/*
		 * This method is an instance method to emphasize that it alters the
		 * objects it is called on.
		 */
		if (this.weight.length != summand.weight.length) {
			assert false : "Dimensions do not match";
			return false;
		}

		for (int i = 0; i < this.weight.length; i++) {
			this.weight[i] += summand.weight[i];
		}
		return true;
	}

	public static Weight add(Weight firstSummand, Weight secondSummand) {
		/*
		 * This method is a static method to emphasize that it creates a new
		 * Weight object and does no alter any existing object.
		 */
		if (firstSummand.weight.length != secondSummand.weight.length) {
			assert false : "Dimensions do not match";
			return null;
		}

		Weight sum = firstSummand.clone();
		for (int i = 0; i < firstSummand.weight.length; i++) {
			sum.weight[i] += secondSummand.weight[i];
		}
		return sum;
	}

	public void scalarProduct(double scalar) {
		/*
		 * This method is an instance method to emphasize that it alters the
		 * objects it is called on.
		 */
		for (int i = 0; i < this.weight.length; i++) {
			this.weight[i] = weight[i] * scalar;
		}
	}

	public static Weight scalarProduct(double scalar, Weight weight) {
		/*
		 * This method is a static method to emphasize that it creates a new
		 * Weight object and does no alter any existing object.
		 */
		Weight result = weight.clone();
		result.scalarProduct(scalar);
		return result;
	}

	@Override
	public Weight clone() {
		double[] r = new double[this.weight.length];
		System.arraycopy(weight, 0, r, 0, this.weight.length);
		return new Weight(r);
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return Arrays.toString(this.weight);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Weight)) {
			return false;
		}
		return Arrays.equals(this.weight, ((Weight) o).weight);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + Arrays.hashCode(this.weight);
		return hash;
	}
}
