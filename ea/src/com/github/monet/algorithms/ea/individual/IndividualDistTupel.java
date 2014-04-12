package com.github.monet.algorithms.ea.individual;


/**
 * Simple class used to sort Individual based on the given double values
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class IndividualDistTupel implements Comparable<IndividualDistTupel> {
	public Individual ind;
	public double dist;

	public IndividualDistTupel(Individual ind, double dist) {
		this.ind = ind;
		this.dist = dist;
	}

	@Override
	public int compareTo(IndividualDistTupel o) {
		if (this.dist  < o.dist) return -1;
		if (this.dist >= o.dist) return  1;
		return 0; // UNREACHABLE (Never return 0 so that individuals having the same distance are added to a tree set)
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

}
