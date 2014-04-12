package com.github.monet.algorithms.ea.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple Union-Find / Disjoint-set data structure. Disjoint-set forests
 * implementation with path compression and union by rank. See
 * http://en.wikipedia.org/wiki/Disjoint-set_data_structure for more
 * information.
 */
public class SimpleUnionFind {
	private int[] parent; // parent of i (or i if i is the root / representative)
	private int[] rank;   // measure for the depth of the tree
	private int size;     // size of the parent array


	/**
	 * MakeSet operation creating a set for each element.
	 *
	 * @param size
	 *            size of the set
	 */
	public SimpleUnionFind(int size) {
		this.parent = new int[size];
		this.rank   = new int[size];
		this.size   = size;
		for (int i = 0; i < size; i++) {
			parent[i] = i;
		}
	}


	/**
	 * Find given element by following the parents until the root is reached.
	 *
	 * Simple approach: follow the parents until the root is reached.
	 *
	 * Path compression approach: while following the parents, set each parent
	 * of the visited nodes directly to the root node / representative. This
	 * speeds up future operations.
	 *
	 * @param i
	 *            element
	 *
	 * @return representative
	 */
	public int find(int i) {
		int p = parent[i];
		if (i == p) {
			return i;
		}
		return parent[i] = find(p);
	}


	/**
	 * Union operation.
	 *
	 * Simple approach: Make the root of one set a child of the root of the
	 * other set.
	 *
	 * Union by rank approach: Append the root of the smaller (regarding depth)
	 * tree to the root of the larger tree. This way, the depth only increases
	 * (by exactly 1) if both trees have the same depth.
	 *
	 * @param i
	 *            first element
	 * @param j
	 *            second element
	 */
	public void union(int i, int j) {
		// Get representatives
		int r1 = find(i);
		int r2 = find(j);
		if (r2 == r1) {
			return;
		}

		// Merge (Union by rank approach)
		if (rank[r1] > rank[r2]) {
			parent[r2] = r1;
		} else if (rank[r2] > rank[r1]) {
			parent[r1] = r2;
		} else {
			parent[r2] = r1;
			rank[r1]++;
		}
	}


	/**
	 * Checks whether p and q are in the same component
	 * @param p
	 * @param q
	 * @return true if p and q are in the same component
	 */
    public boolean connected(int i, int j) {
        return (find(i) == find(j));
    }


    /**
     * Returns the size of the data structure
     */
    public int size() {
    	return this.size;
    }


	/**
	 * Return components. Map:
	 * "Representative of a component => List of elements"
	 */
    public Map<Integer,List<Integer>> getComponents() {
    	Map<Integer,List<Integer>> components = new TreeMap<Integer,List<Integer>>();

    	for (int i = 0; i < this.size; i++) {
    		// get representative of the component
    		int componentRoot = this.find(i);
    		// add i to the corresponding component-list
    		List<Integer> compList = components.get(componentRoot);
    		if (compList == null) {
    			compList = new LinkedList<Integer>();
    			components.put(componentRoot, compList);
    		}
    		compList.add(i);
    	}
    	return components;
    }

}
