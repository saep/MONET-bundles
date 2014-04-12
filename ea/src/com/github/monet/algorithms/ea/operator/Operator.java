package com.github.monet.algorithms.ea.operator;

import java.util.Map;

import com.github.monet.algorithms.ea.util.Configuration;
import com.github.monet.algorithms.ea.util.Nameable;


/**
 * abstract class for any kind of operator Operators can be Creation (0-ary),
 * Mutation (1-ary), Recombination (x-ary), Selection and Fitness evaluation.
 *
 * @author Sven Selmke
 *
 */
public abstract class Operator implements Nameable {

	/**
	 * Configuration object so we don't have to copy everything that is passed
	 * to the configure method.
	 */
	private Configuration opConfig;

	/**
	 * Constructor
	 */
	public Operator() {

	}

	@Override
	public String toString() {
		return "Operator " + this.getName();
	}

	@Override
	public boolean configure(Map<String,Object> parameters) {
		return true;
	}

	public Configuration getOpConfig() {
		return opConfig;
	}
	public void setOpConfig(Configuration opConfig) {
		this.opConfig = opConfig;
	}

}
