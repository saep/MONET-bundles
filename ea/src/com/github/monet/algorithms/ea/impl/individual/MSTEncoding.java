package com.github.monet.algorithms.ea.impl.individual;

import java.util.Map;

import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.util.Functions;

/**
 * Simple encoding for a MST. A genotype of this encoding is a list of valid
 * edges.
 *
 * The encoding uses the unique node IDs as symbols.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class MSTEncoding extends Encoding {
	private int numNodes;
	private int size;
	private int maxDegree;

	@Override
	public boolean configure(Map<String,Object> params) {
		this.numNodes  = Functions.getParam(params, "numNodes", Integer.class, null);
		this.size      = this.numNodes - 1;
		this.maxDegree = Functions.getParam(params, "maxDegree", Integer.class, 0);
		return true;
	}

	@Override
	public String getName() {
		return "MST-Encoding";
	}

	public int getNumNodes() {
		return numNodes;
	}
	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public int getDegree(){
		return this.maxDegree;
	}

	public void setDegree(int maxDegree){
		this.maxDegree = maxDegree;
	}


}
