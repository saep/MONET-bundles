package com.github.monet.algorithms.sssp;

import com.github.monet.graph.interfaces.*;
import com.github.monet.graph.weighted.*;

class OLE<N extends Node> {
	private N n;
	private Weight g;
	private LabelSet f;

	OLE(N n, Weight g, LabelSet f) {
		this.n = n;
		this.g = g;
		this.f = f;
	}

	N getN() {
		return n;
	}

	Weight getG() {
		return g;
	}

	LabelSet getF() {
		return f;
	}
}
