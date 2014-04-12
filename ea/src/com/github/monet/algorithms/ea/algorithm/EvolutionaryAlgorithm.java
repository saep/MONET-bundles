package com.github.monet.algorithms.ea.algorithm;

import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.operator.Operator;
import com.github.monet.algorithms.ea.util.Functions;
import com.github.monet.interfaces.Meter;

/**
 * Super class for evolutionary algorithms. Every algorithm can be configured
 * and executed. During the execution, a meter object can be used in order to do
 * measurements.
 *
 * @author Sven Selmke
 *
 */
public abstract class EvolutionaryAlgorithm extends Operator {

	/**
	 * Meter object given by the monet framework.
	 * Used to "measure" measurements and results
	 */
	protected Meter meter;


	@Override
	public boolean configure(Map<String,Object> parameters) {
		return true;
	}


	@Override
	public String toString() {
		return "Algorithm " + this.getName();
	}


	/**
	 * Start the execution of the algorithm.
	 *
	 * @return List of "good" individuals found by the algorithm.
	 */
	public abstract List<Individual> execute();


	/**
	 * Log a given population using the meter
	 */
	public void measureObjectiveValues(List<Individual> population) {
		if (this.meter != null) {
			for (Individual ind : population) {
				//this.meter.measureDouble("result/#" + i, ind.getObjectiveValues());
				this.meter.measurePareto(ind.getObjectiveValues(), null);
			}
		} else {
			Functions.log("Cannot measure results. Meter is null in EvolutionaryAlgorithm!");
		}
	}


	public Meter getMeter() {
		return this.meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

}
