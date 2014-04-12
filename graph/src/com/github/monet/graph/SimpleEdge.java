package com.github.monet.graph;

import java.util.Objects;

import com.github.monet.graph.interfaces.*;

/**
 * Implements the interface Edge.
 *
 * @author Christopher Morris
 *
 */
public class SimpleEdge implements DirectedEdge, UndirectedEdge {

	/**
	 * Incident nodes of edge
	 */
	public SimpleNode u, v;

	public SimpleEdge(SimpleNode u, SimpleNode v) {
		this.u = u;
		this.v = v;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleEdge) {
			SimpleEdge e = (SimpleEdge) obj;
			return (e.u.equals(u) && e.v.equals(v));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + Objects.hashCode(this.u);
		hash = 41 * hash + Objects.hashCode(this.v);
		return hash;
	}

	@Override
	public String toString() {
		return "{" + this.u.toString() + ", " + this.v.toString() + "}";
	}

}
