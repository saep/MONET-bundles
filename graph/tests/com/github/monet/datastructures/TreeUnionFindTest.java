package com.github.monet.datastructures;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.monet.datastructures.TreeUnionFind;

public class TreeUnionFindTest {

	@Test
	public void testChrisExample() {
		TreeUnionFind<Integer> test = new TreeUnionFind<>();

		test.add(4);
		test.union(4, 4);
		test.makeRepresentative(4);
	}

	@Test
	public void testUnionFind() {
		TreeUnionFind<Object> tuf = new TreeUnionFind<>();
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();

		tuf.add(o1);
		tuf.add(o2);
		tuf.add(o3);
		tuf.add(o4);
		assertEquals(o1, tuf.find(o1));
		assertEquals(o2, tuf.find(o2));
		assertEquals(o3, tuf.find(o3));
		assertEquals(o4, tuf.find(o4));

		tuf.union(o1, o2);
		assertEquals(tuf.find(o1), tuf.find(o2));
		assertEquals(o2, tuf.find(o2));
		assertEquals(o3, tuf.find(o3));
		assertEquals(o4, tuf.find(o4));

		tuf.makeRepresentative(o3);
		assertEquals(tuf.find(o1), tuf.find(o2));
		assertEquals(o2, tuf.find(o2));
		assertEquals(o3, tuf.find(o3));
		assertEquals(o4, tuf.find(o4));

		tuf.union(o2, o3);
		tuf.union(o3, o4);
		assertEquals(tuf.find(o1), tuf.find(o2));
		assertEquals(tuf.find(o1), tuf.find(o3));
		assertEquals(tuf.find(o1), tuf.find(o4));

		tuf.makeRepresentative(o3);
		assertEquals(tuf.find(o1), o3);
		assertEquals(tuf.find(o2), o3);
		assertEquals(tuf.find(o3), o3);
		assertEquals(tuf.find(o4), o3);

		tuf.makeRepresentative(o1);
		assertEquals(tuf.find(o1), o1);
		assertEquals(tuf.find(o2), o1);
		assertEquals(tuf.find(o3), o1);
		assertEquals(tuf.find(o4), o1);
	}

	@Test
	public void testEquals() {
		TreeUnionFind<Object> tuf1 = new TreeUnionFind<>();
		TreeUnionFind<Object> tuf2 = new TreeUnionFind<>();
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
		assertEquals(tuf1, tuf2);

		tuf1.add(o1);
		tuf1.add(o2);
		tuf1.add(o3);
		tuf1.add(o4);
		tuf1.union(o1, o2);
		tuf1.union(o1, o3);
		tuf1.makeRepresentative(o3);
		assertNotEquals(tuf1, tuf2);

		tuf2.add(o1);
		tuf2.add(o2);
		tuf2.add(o3);
		tuf2.add(o4);
		assertNotEquals(tuf1, tuf2);
		tuf2.union(o3, o2);
		tuf2.union(o2, o1);
		tuf2.makeRepresentative(o1);
		assertEquals(tuf1, tuf2);

		tuf2.makeRepresentative(o2);
		assertEquals(tuf1, tuf2);

		tuf2.union(o2, o4);
		assertNotEquals(tuf1, tuf2);

		tuf1.union(o3, o4);
		assertEquals(tuf1, tuf2);
	}
}
