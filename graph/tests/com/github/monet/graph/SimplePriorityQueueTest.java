package com.github.monet.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.graph.SimplePriorityQueue;

public class SimplePriorityQueueTest {

	@Test
	public void testQueue() {
		SimplePriorityQueue<Object> queue = new SimplePriorityQueue<>(3);

		Object n1 = new Object();
		Object n2 = new Object();
		Object n3 = new Object();
		Object n4 = new Object();
		Object n5 = new Object();

		// Test state
		assertTrue(queue.poll() == null);

		// Modify priority queue
		assertTrue(queue.add(n1, 2.0));
		assertFalse(queue.add(n1, 2.0));
		assertTrue(queue.add(n2, 2.0));
		assertTrue(queue.add(n3, 4.0));

		// Test state
		Object polled1 = queue.poll();
		assertTrue(polled1.equals(n1) || polled1.equals(n2));
		assertFalse(queue.update(polled1, 0.0));

		// Modify priority queue
		assertTrue(queue.update(n3, 1.0));
		assertTrue(queue.add(n4, 10.0));
		assertTrue(queue.add(n5, 3.0));
		assertTrue(queue.update(n4, -1.0));
		assertTrue(queue.update(n4, 10.0));

		// Test state
		assertEquals(queue.poll(), n3);
		Object polled2 = queue.poll();
		assertTrue((polled2.equals(n1) || polled2.equals(n2)) && !polled2.
				equals(polled1));

		// Modify priority queue
		assertTrue(queue.remove(n5));

		// Test state
		assertFalse(queue.remove(n5));
		assertEquals(queue.poll(), n4);
		assertTrue(queue.poll() == null);
	}
}
