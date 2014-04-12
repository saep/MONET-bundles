package com.github.monet.algorithms.ea.impl.individual;

import java.util.ArrayList;
import java.util.List;

import com.github.monet.algorithms.ea.individual.Genotype;

import com.github.monet.graph.interfaces.Node;


/**
 * SSSP genotype consisting of a node-list
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class STSPGenotype extends Genotype {
	private List<Node> nodeIds;

	public STSPGenotype() {
		this.nodeIds = new ArrayList<Node>();
	}

	@Override
	public Genotype copy() {
		STSPGenotype copy = new STSPGenotype();
		List<Node> copiedNodeIds = new ArrayList<Node>(this.nodeIds);
		copy.setNodes(copiedNodeIds);
		copy.setEncoding(this.getEncoding());
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof STSPGenotype)) {
			return false;
		}
		STSPGenotype other = (STSPGenotype)o;
		if (this.nodeIds.size() != other.getNodes().size()) {
			return false;
		}
		for (int i = 0; i < this.nodeIds.size(); i++) {
			if (this.nodeIds.get(i) != other.getNodes().get(i)) { // FIXED
				return false;
			}
		}
		return true;
	}

	public List<Node> getNodes() {
		return this.nodeIds;
	}
	public void setNodes(List<Node> nodes) {
		this.nodeIds = nodes;
	}
	public int getLength() {
		return this.nodeIds.size();
	}

}
