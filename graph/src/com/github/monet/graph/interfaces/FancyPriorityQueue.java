package com.github.monet.graph.interfaces;

/**
 * Models a priority queue.
 *
 * @author Christopher Morris
 *
 * @param <T>
 *            type of stored objects
 */
public interface FancyPriorityQueue<T> {

	/**
	 * Add new element to the priority queue with priority p.
	 *
	 * @param e new element
	 * @param p priority of newly added element
	 * @return true, if element e was not already in priority queue, otherwise false
	 */
	public boolean add(T e, double p);

	/**
	 * Updates the priority of the element e to priority p.
	 *
	 * @param e
	 *            element, which is to be updated
	 * @param p
	 *            new priority
	 * @return return true, if element e does exist in graph, otherwise false
	 */
	public boolean update(T e, double p);

	/**
	 * Returns the element with minimal priority, but does not remove it.
	 *
	 * @return the element with minimal priority, if exists, otherwise
	 *         null
	 */
	public T poll();

	/**
	 * Removes graph element e from the priority queue.
	 *
	 * @param e
	 *            element, which is to be deleted
	 * @return true, if element e exists in priority queue, otherwise false
	 */
	public boolean remove(T e);

	/**
	 * Returns number of elements in the priority queue.
	 *
	 * @return number of element in the priority queue
	 */
	public int getSize();
}
