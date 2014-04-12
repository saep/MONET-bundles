/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.monet.datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.interfaces.UnionFind;

/**
 * UnionFind data structure as proposed in Cormen et al.: "Introdruction to
 * algorithms" with union-by-rank and optional path compression (enabled by
 * default)
 *
 *
 */
public class TreeUnionFind<T> implements UnionFind<T> {

	HashMap<T, SimpleNode> elementToNode;
	SimpleDirectedGraph tree;
	GraphElementHashAnnotator<SimpleNode, T> nodeToElement;
	GraphElementHashAnnotator<SimpleNode, Integer> nodeRanks;
	private boolean pathCompression;
	private boolean safeMode;

	public TreeUnionFind() {
		tree = new SimpleDirectedGraph();
		tree.setSafeMode(false);
		elementToNode = new HashMap<>();
		nodeToElement = new GraphElementHashAnnotator<>();
		nodeRanks = new GraphElementHashAnnotator<>();
		pathCompression = false;
		safeMode = true;
	}

	public void clear() {
		elementToNode.clear();
		elementToNode = null;
		tree = null;
		nodeToElement.clear();
		nodeToElement= null;
		nodeRanks.clear();
		nodeRanks = null;
	}

	@Override
	public void add(T e) {
		if (!this.safeMode || !elementToNode.containsKey(e)) {
			SimpleNode n = tree.addNode();
			elementToNode.put(e, n);
			nodeToElement.setAnnotation(n, e);
			nodeRanks.setAnnotation(n, 1);
		} else {
			assert false : "Element exists already";
		}
	}

	@Override
	public T find(T e) {
		SimpleNode root = findNode(e);
		if (root == null) {
			assert false : "Element does not exist";
			return null;
		}
		return nodeToElement.getAnnotation(root);
	}

	@Override
	public T union(T u, T v) {
		if (!this.safeMode || (elementToNode.containsKey(u) && elementToNode.containsKey(v))) {
			SimpleNode uParent = findNode(u);
			SimpleNode vParent = findNode(v);
			if (uParent.equals(vParent)) {
				return nodeToElement.getAnnotation(vParent);
			}
			Integer uParentRank = nodeRanks.getAnnotation(uParent);
			Integer vParentRank = nodeRanks.getAnnotation(vParent);
			if (uParentRank > vParentRank) {
				moveSubtree(vParent, uParent);
				return nodeToElement.getAnnotation(uParent);
			} else {
				moveSubtree(uParent, vParent);
				if (uParentRank == vParentRank) {
					nodeRanks.setAnnotation(vParent, vParentRank + 1);
				}
				return nodeToElement.getAnnotation(vParent);
			}
		} else {
			assert false : "Element u does not exist";
			assert false : "Element v does not exist";
			return null;
		}
	}

	@Override
	public boolean makeRepresentative(T newRepresentative) {
		if (!this.safeMode || elementToNode.containsKey(newRepresentative)) {
			SimpleNode innerNode = elementToNode.get(newRepresentative);
			SimpleNode root = findNode(newRepresentative);
			if (innerNode.equals(root)) {
				return true;
			}
			T oldRepresentative = nodeToElement.getAnnotation(root);

			elementToNode.put(oldRepresentative, innerNode);
			elementToNode.put(newRepresentative, root);
			nodeToElement.setAnnotation(root, newRepresentative);
			nodeToElement.setAnnotation(innerNode, oldRepresentative);

			return true;
		} else {
			assert false : "Element does not exist";
			return false;
		}
	}

	private SimpleNode findNode(T e) {
		if (!this.safeMode || elementToNode.containsKey(e)) {
			SimpleNode currentNode = elementToNode.get(e);
			Collection<SimpleEdge> incomingEdges = tree.getIncomingEdges(
					currentNode);
			List<SimpleNode> visitedNodes = new LinkedList<>();
			while (!incomingEdges.isEmpty()) {
				if (pathCompression) {
					visitedNodes.add(currentNode);
				}
				SimpleEdge parentEdge = incomingEdges.iterator().next();
				currentNode = tree.getSource(parentEdge);
				incomingEdges = tree.getIncomingEdges(currentNode);
			}
			if (isPathCompression()) {
				for (SimpleNode node : visitedNodes) {
					moveSubtree(node, currentNode);
				}
			}
			return currentNode;
		} else {
			assert false : "Element does not exist";
			return null;
		}
	}

	private void moveSubtree(SimpleNode subtreeRoot, SimpleNode newParent) {
		if (!tree.getIncomingEdges(subtreeRoot).isEmpty()) {
			SimpleEdge edge = tree.getIncomingEdges(subtreeRoot).iterator().
					next();
			tree.deleteEdge(edge);
		}
		tree.addEdge(newParent, subtreeRoot);
	}

	/**
	 * Path compression improves the running time
	 *
	 * @return Path compression activation
	 */
	public boolean isPathCompression() {
		return pathCompression;
	}

	/**
	 * Path compression improves the running time.
	 *
	 * @param Activation flag
	 */
	public void setPathCompression(boolean pathCompression) {
		this.pathCompression = pathCompression;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TreeUnionFind) {
			TreeUnionFind<T> other = (TreeUnionFind) o;
			boolean isEqual = true;
			isEqual &= elementToNode.keySet().equals(other.elementToNode.
					keySet());
			if (isEqual) {
				HashMap<T, T> representativeMap = new HashMap<>();
				for (T elem : elementToNode.keySet()) {
					T thisRep = find(elem);
					T otherRep = other.find(elem);
					if (!representativeMap.containsKey(otherRep)
							&& !representativeMap.containsValue(thisRep)) {
						representativeMap.put(otherRep, thisRep);
					}
					isEqual &= thisRep.equals(representativeMap.get(otherRep));
				}
			}
			return isEqual;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.elementToNode.keySet());
		return hash;
	}

	/**
	 * @return the safeMode
	 */
	public boolean isSafeMode() {
		return safeMode;
	}

	/**
	 * @param safeMode the safeMode to set
	 */
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
}
