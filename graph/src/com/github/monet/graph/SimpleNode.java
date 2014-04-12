package com.github.monet.graph;

import com.github.monet.graph.interfaces.*;

/**
 * Implements the interface Node.
 *
 * @author Christopher Morris
 *
 */
public class SimpleNode implements Node {

	private int id;

	public SimpleNode(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) throws IllegalArgumentException {
		if (obj instanceof SimpleNode) {
			return ((SimpleNode) obj).getId() == getId();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + this.id;
		return hash;
	}

	@Override
	public String toString() {
		return "Node " + String.valueOf(this.getId());
	}

	/**
	 * Returns the node id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
