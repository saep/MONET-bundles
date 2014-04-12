package com.github.monet.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.monet.graph.interfaces.CostCalculator;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.ParetoSet;
import com.github.monet.graph.weighted.LexSortComparator;
import com.github.monet.graph.weighted.Weight;

/**
 * Implements the interface ParetoSet.
 *
 *
 *
 * @param <N> type of node, upper bounded by type Node
 * @param <E> type of edge, upper bounded by type Edge
 * @param <G> type of graph, upper bounded by type Graph
 */
public class ParetoFront<N extends Node, E extends Edge, G extends Graph<N, E, G>>
		implements ParetoSet<N, E, G> {

	private boolean dominanceManagement;
	private TreeMap<Weight, G> solutions;
	private CostCalculator<N, E, G, Weight> costCalculator;

	/**
	 * @param costCalculator Cost function
	 * @param dominanceManagement Automatic deletion and rejection of dominated
	 * points
	 */
	public ParetoFront(CostCalculator<N, E, G, Weight> costCalculator,
			boolean dominanceManagement) {
		this(costCalculator);
		this.dominanceManagement = dominanceManagement;
	}

	/**
	 * @param costCalculator Cost function
	 */
	public ParetoFront(CostCalculator<N, E, G, Weight> costCalculator) {
		assert costCalculator != null : "costCalculator must not be null";
		LexSortComparator sort = new LexSortComparator();
		solutions = new TreeMap<>(sort);
		this.costCalculator = costCalculator;
		this.dominanceManagement = false;
	}

	/**
	 * @return first element in pareto front according to a lexicographic order,
	 * if exists, otherwise null
	 */
	public G first() {
		return solutions.firstEntry().getValue();
	}

	/**
	 * @return last element in pareto front according to a lexicographic order,
	 * if exists, otherwise null
	 */
	public G last() {
		return solutions.lastEntry().getValue();
	}

	@Override
	public boolean add(G g) {
		assert g != null : "g must not be null";

		Weight cost = costCalculator.calculateCosts(g);

		if (isDominanceManagement()) {
			// Check if g is dominated by existing element and vice versa
			ArrayList<Weight> toBeDeleted = new ArrayList<>();
			for (Weight w : solutions.keySet()) {
				switch(w.dominates(cost))
				{
					case PARETO_SMALLER:
						return false;
					case PARETO_GREATER:
						toBeDeleted.add(w);
				}
			}
			// Remove all dominated elements
			solutions.keySet().removeAll(toBeDeleted);
		}

		solutions.put(cost, g);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		assert o != null : "o must not be null";

		int removed = 0;
		while (solutions.values().remove(o)) {
			removed++;
		}
		return removed > 0;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		assert !c.isEmpty() : "c must not be empty";

		int removed = 0;
		while (solutions.values().removeAll(c)) {
			removed++;
		}
		return removed > 0;
	}

	/* Stupid wrappers */
	@Override
	public int size() {
		return solutions.size();
	}

	@Override
	public boolean isEmpty() {
		return solutions.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return solutions.containsValue((G) o);
	}

	@Override
	public Iterator<G> iterator() {
		return solutions.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return solutions.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return solutions.values().toArray(ts);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return solutions.values().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends G> c) {
		return solutions.values().addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return solutions.values().retainAll(c);
	}

	@Override
	public void clear() {
		solutions.clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof ParetoFront)) {
			return false;
		} else {
			ParetoFront<N, E, G> pf = (ParetoFront<N, E, G>) o;
			boolean isEqual = true;
			isEqual &= this.solutions.equals(pf.solutions);
			isEqual &= this.costCalculator.equals(pf.costCalculator);
			return isEqual;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.solutions);
		hash = 13 * hash + Objects.hashCode(this.costCalculator);
		return hash;
	}

	/* Some map wrappers */
	public boolean containsKey(Object o) {
		return solutions.containsKey((Weight) o);
	}

	public boolean containsValue(Object o) {
		return solutions.containsValue((Weight) o);
	}

	public G get(Object o) {
		return solutions.get((Weight) o);
	}

	public Set<Weight> keySet() {
		return solutions.keySet();
	}

	public Collection<G> values() {
		return solutions.values();
	}

	public Set<Entry<Weight, G>> entrySet() {
		return solutions.entrySet();
	}

	/* Some NavigableMap wrappers */
	public Entry<Weight, G> lowerEntry(Weight k) {
		return solutions.lowerEntry(k);
	}

	public Weight lowerKey(Weight k) {
		return solutions.lowerKey(k);
	}

	public Entry<Weight, G> floorEntry(Weight k) {
		return solutions.floorEntry(k);
	}

	public Weight floorKey(Weight k) {
		return solutions.floorKey(k);
	}

	public Entry<Weight, G> ceilingEntry(Weight k) {
		return solutions.ceilingEntry(k);
	}

	public Weight ceilingKey(Weight k) {
		return solutions.ceilingKey(k);
	}

	public Entry<Weight, G> higherEntry(Weight k) {
		return solutions.higherEntry(k);
	}

	public Weight higherKey(Weight k) {
		return solutions.higherKey(k);
	}

	public Entry<Weight, G> firstEntry() {
		return solutions.firstEntry();
	}

	public Entry<Weight, G> lastEntry() {
		return solutions.lastEntry();
	}

	public Entry<Weight, G> pollFirstEntry() {
		return solutions.pollFirstEntry();
	}

	public Entry<Weight, G> pollLastEntry() {
		return solutions.pollLastEntry();
	}

	public NavigableMap<Weight, G> descendingMap() {
		return solutions.descendingMap();
	}

	public NavigableSet<Weight> navigableKeySet() {
		return solutions.navigableKeySet();
	}

	public NavigableSet<Weight> descendingKeySet() {
		return solutions.descendingKeySet();
	}

	public NavigableMap<Weight, G> subMap(Weight k, boolean bln, Weight k1,
			boolean bln1) {
		return solutions.subMap(k, bln, k1, bln1);
	}

	public NavigableMap<Weight, G> headMap(Weight k, boolean bln) {
		return solutions.headMap(k, bln);
	}

	public NavigableMap<Weight, G> tailMap(Weight k, boolean bln) {
		return solutions.tailMap(k, bln);
	}

	public SortedMap<Weight, G> subMap(Weight k, Weight k1) {
		return solutions.subMap(k, k1);
	}

	public SortedMap<Weight, G> headMap(Weight k) {
		return solutions.headMap(k);
	}

	public SortedMap<Weight, G> tailMap(Weight k) {
		return solutions.tailMap(k);
	}

	public Comparator<? super Weight> comparator() {
		return solutions.comparator();
	}

	public Weight firstKey() {
		return solutions.firstKey();
	}

	public Weight lastKey() {
		return solutions.lastKey();
	}

	/**
	 * @return the dominanceManagement
	 */
	public boolean isDominanceManagement() {
		return dominanceManagement;
	}
}
