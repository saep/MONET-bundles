package com.github.monet.algorithms.ea.impl.individual;

import java.util.Map;

import com.github.monet.algorithms.ea.individual.Encoding;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Node;


/**
 * Simple encoding for a shortest path (or paths in general). A genotype of this
 * encoding is a list of valid symbols.
 *
 * The encoding uses the unique node IDs as symbols.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 */
public class STSPEncoding extends Encoding {
	private Node startNode;
	private Node endNode;
	private int numNodes;

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {
		this.numNodes  = Functions.getParam(params, "numNodes", Integer.class, null);

		// Source and Target
		this.startNode = Functions.getParam(params, Functions.PARAM_SOURCENODE, Node.class, null);
		this.endNode = Functions.getParam(params, Functions.PARAM_DESTNODE, Node.class, null);
		if (this.startNode == null || this.endNode == null) {
			Functions.log("Error while configurating SSSPEncoding. startNode or endNode is null!", Functions.LOG_ERROR);
			return false;
		}
		if (this.startNode == this.endNode) {
			Functions.log("Error while configurating SSSPEncoding. startNode equals endNode!", Functions.LOG_ERROR);
			return false;
		}

		// Check if a path exists between start and end node
		@SuppressWarnings("rawtypes")
		AnnotatedGraph g = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		if (!Functions.pathExists(g.getGraph(), startNode, endNode)) {
			Functions.log("Error while configurating SSSPEncoding. No path between start and end!", Functions.LOG_ERROR);
			return false;
		}

		// FIXED (removed set valid symbols)
		return true;
	}

	@Override
	public String getName() {
		return "SSSP-Encoding";
	}

	public Node getStartNode() {
		return startNode;
	}
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	public Node getEndNode() {
		return endNode;
	}
	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
	public int getNumNodes() {
		return numNodes;
	}
	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}


}
