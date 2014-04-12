package com.github.monet.algorithms.ea.impl.individual;

import java.util.ArrayList;
import java.util.List;

import com.github.monet.algorithms.ea.individual.Genotype;


/**
 * Pruefer-Genotype encoding Minimum-Spanning-Trees.
 *
 * @author Sven Selmke
 *
 */
public class PrueferGenotype extends Genotype {
	private List<Integer> value; // Pruefer-Number as List of node-indices


	public PrueferGenotype() {
		this.value = new ArrayList<Integer>();
	}


	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PrueferGenotype)) {
			return false;
		}
		PrueferGenotype other = (PrueferGenotype)o;
		if (this.value.size() != other.getValue().size()) {
			return false;
		}
		for (int i = 0; i < this.value.size(); i++) {
			if (this.value.get(i) != other.getValue().get(i)) { // FIXED
				return false;
			}
		}
		return true;
	}


	@Override
	public String toString() {
		return this.value.toString();
	}


	@Override
	public Genotype copy() {
		PrueferGenotype copy = new PrueferGenotype();
		List<Integer> prueferNumber = new ArrayList<Integer>(this.value);
		copy.setValue(prueferNumber);
		copy.setEncoding(this.getEncoding());
		return copy;
	}


	public List<Integer> getValue() {
		return value;
	}
	public void setValue(List<Integer> value) {
		this.value = value;
	}
	public void setSymbol(int index, Integer symbol) {
		this.value.set(index, symbol);
	}
	public Integer getSymbol(int index) {
		return this.value.get(index);
	}
	public int getLength() {
		return this.value.size();
	}

}
