package com.github.monet.algorithms.ea.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Global random functions available for all EA classes. This class contains a
 * Random object which should be used throughout the whole program (in order to
 * be able to reproduce results).
 *
 * @author Sven Selmke
 *
 */
public abstract class EaRandom {
	/*
	 * Random object used throughout the whole program.
	 * This way a global seed can be used in order to reproduce results.
	 */
	private static long seed = (new Random()).nextLong();
	private static Random rand = new Random(seed); // long seed

	/**
	 * Returns a random element of the given list.
	 */
	public static <T> T getRandomElement(List<T> list) {
		if (list.size() == 0) {
			return null;
		} else {
			int i = EaRandom.getRandomNumber(0, list.size());
			return list.get(i);
		}
	}

	/**
	 * Returns a random element of the given array.
	 */
	public static <T> T getRandomElement(T[] arr) {
		if (arr.length == 0) {
			return null;
		} else {
			int i = EaRandom.getRandomNumber(0, arr.length);
			return arr[i];
		}
	}

	/**
	 * Returns a random element of the given Collection (slow!).
	 */
	public static <T> T getRandomElement(Collection<T> col) {
		if (col.size() == 0) {
			return null;
		} else {
			Iterator<T> iter = col.iterator();
			for (int i = EaRandom.getRandomNumber(0, col.size()); i>0; i--)
				iter.next();
			return iter.next();
		}
	}

	/**
	 * Returns a random integer (including intervalBegin, excluding intervalEnd)
	 */
	public static int getRandomNumber(int intervalBegin, int intervalEnd) {
		if (intervalBegin > intervalEnd)
			return 0;
		if (intervalBegin == intervalEnd)
			return intervalEnd;
		return (EaRandom.rand.nextInt(intervalEnd - intervalBegin)) + intervalBegin;
	}

	/**
	 * Returns a random double (including intervalBegin, excluding intervalEnd)
	 */
	public static double getRandomDouble(double intervalBegin, double intervalEnd) {
		if (intervalBegin > intervalEnd)
			return 0;
		if (intervalBegin == intervalEnd)
			return intervalEnd;
		return intervalBegin + (intervalEnd - intervalBegin) * EaRandom.rand.nextDouble();
	}

	/**
	 * Returns a random number [0.0, 1.0). Shorthand for EaRandom.rand.random()
	 */
	public static double random() {
		return EaRandom.rand.nextDouble();
	}

	/**
	 * Returns true with given probability
	 */
	public static boolean nextBoolean(double probability) {
		return (EaRandom.random() < probability);
	}

	/**
	 * Returns next long
	 */
	public static long nextLong() {
		return EaRandom.rand.nextLong();
	}

	/**
	 * Sets a new random seed
	 */
	public static void setNewSeed(long seed) {
		EaRandom.seed = seed;
		EaRandom.rand.setSeed(seed);
		Functions.log("Using Random-Seed: " + EaRandom.getRandomSeed() + ".");
	}

	/**
	 * Sets a new random seed and returns it.
	 */
	public static long getNewRandomSeed() {
		long seed = (new Random()).nextLong();
		EaRandom.seed = seed;
		EaRandom.rand.setSeed(seed);
		Functions.log("Generating new Random-Seed: " + EaRandom.getRandomSeed() + ".");
		return seed;
	}

	/**
	 * Get current seed
	 */
	public static long getRandomSeed() {
		return EaRandom.seed;
	}

	/**
	 * Get the random generator
	 */
	public static Random getRand() {
		return rand;
	}

}
