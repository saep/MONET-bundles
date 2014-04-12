package com.github.monet.algorithms.ea.individual;

import java.util.Map;

import com.github.monet.algorithms.ea.util.Nameable;

/**
 * Super class for all encodings. Each genotype has a certain encoding. The
 * encoding contains information such as length, valid symbols, etc., which are
 * shared by all genotypes and thus shouldn't be saved in each genotype
 * individually. Note: the Encoding class does NOT represent genotypes! See
 * {@link Genotype}
 *
 * @author Sven Selmke
 *
 * @see Genotype
 *
 */
public abstract class Encoding implements Nameable {

	@Override
	public boolean configure(Map<String,Object> parameters) {
		return true;
	}

}
