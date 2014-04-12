package com.github.monet.algorithms.ea.individual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.common.ObjectivePoint;
import com.github.monet.common.ParetoPoint;


/**
 * Individual class for Evolutionary Algorithms. Each individual has a genotype
 * and a phenotype. In order to create a phenotype, a given PhenotypeMapping is
 * used. The amount of individuals created by the program is tracked by an
 * idCounter.
 *
 * @author Sven Selmke
 *
 */
public class Individual implements Comparable<Individual>, ObjectivePoint {

	/**
	 * Comparator for sorting according to minimization problem. Best
	 * individuals are stored at the beginning of a list when calling
	 * Collections.sort with this comparator.
	 */
	public static class IndividualBestFitnessComparator implements Comparator<Individual> {
		@Override
		public int compare(Individual i1, Individual i2) {
			return (Individual.minimizationOfFitness) ? i1.compareTo(i2) : -1 * i1.compareTo(i2);
		}
	}

	/**
	 * Consider minimization or maximization of OBJECTIVE VALUES?
	 * Changes behavior of methods such as domination
	 * Note: this value refers to the objective values (NOT fitness!)
	 * ALWAYS TRUE FOR MONET!
	 */
	public static boolean minimizationOfObjectives = true;

	/**
	 * Consider minimization or maximization of FITNESS?
	 * Set by Evaluator
	 */
	public static boolean minimizationOfFitness = true;

	/*
	 * Counter used to give a unique id to every individual created.
	 */
	private static int idCounter = 0;
	private int id;

	/*
	 * Standard variables for every individual
	 * An individual consists of a genotype and a phenotype.
	 * The fitness is based on the phenotype of the individual and in some cases on other individuals (e.g. sharing).
	 */
	private Genotype genotype;
	private Phenotype phenotype;
	private PhenotypeMapping mapping;
	private double fitness;
	private double sharing; // Sharing or Density value
	private int strength; // # of individuals this individual dominates
	private int rank; // # of individuals dominating this individual
	private double result; // temporary result of last executed action (e.g. hypervolume contribution)
	private TreeSet<IndividualDistTupel> sortedDistances; // sorted distances to other individuals (SPEA-2)

	/*
	 * Specifies the objective values returned by getObjectiveValues();
	 * Sometimes methods use the getObjectiveValues() method, but we want the
	 * method to work on other values instead!
	 */
	private ActiveObjectives activeObjectives;

	/**
	 * Constructor.
	 */
	public Individual() {
		// Set Counter
		this.id = Individual.idCounter;
		Individual.idCounter++;
		// Set standard variables
		this.genotype        = null;
		this.phenotype       = null;
		this.mapping         = null;
		this.fitness         = 0;
		this.sharing         = 0;
		// Objective values to use
		this.activeObjectives = ObjectivePoint.ActiveObjectives.STANDARD;
	}
	public Individual(Genotype genotype) {
		this();
		this.genotype = genotype;
	}
	public Individual(Genotype genotype, PhenotypeMapping mapping) {
		this();
		this.genotype = genotype;
		this.mapping  = mapping;
	}


	/**
	 * Return the given individual as String for debugging purpose
	 *
	 * @return String representation of the individual
	 */
	@Override
	public String toString() {
		String result = "Individual " + this.id + " (Fitness: " + this.fitness + ").\n";
		if (this.getGenotype() != null) {
			result += "  Genotype: " + this.getGenotype().toString() + "\n";
		} else {
			result += "  Genotype: NULL\n";
		}
		if (this.getObjectiveValues() != null) {
			result += "  Objective-Values: " + Arrays.toString(this.getObjectiveValues()) + "\n";
		} else {
			result += "  Objective-Values: NULL\n";
		}
		if (this.getPhenotype() != null) {
			result += "  Phenotype: " + this.getPhenotype().toString() + "\n";
		} else {
			result += "  Phenotype: NULL\n";
		}
		return result;
	}


	/**
	 * Prints a short version of the individual for debugging purpose
	 */
	public void printShort() {
		System.out.println("Individual " + this.id + " - " + Arrays.toString(this.getObjectiveValues()));
	}


	/**
	 * Compare this to another individual (considers FITNESS only!). This way
	 * lists can be sorted according to the fitness function using efficient
	 * Java sorting implementations. Note that this method is independent of
	 * whether the current problem is a minimization or maximization problem.
	 *
	 * @param o
	 *            Individual which will be compared to this.
	 *
	 * @return -1 if this individual has a lower fitness than the individual
	 *         given as a parameter. 0 if both individuals have equal fitness. 1
	 *         if the parameter individual has a lower fitness.
	 */
	@Override
	public int compareTo(Individual o) {
		if (this.getFitness() < o.getFitness())
			return -1;
		if (this.getFitness() > o.getFitness())
			return 1;
		return 0;
	}


	/**
	 *
	 */
	public boolean isBetterThan(Individual o) {
		if (Individual.minimizationOfFitness) {
			return this.getFitness() < o.getFitness();
		} else {
			return this.getFitness() > o.getFitness();
		}
	}


	/**
	 * Checks if this individual dominates the given individual (considers
	 * OBJECTIVE-VALUES!)
	 *
	 * @param competitor
	 *            Individual for which domination is checked.
	 *
	 * @return true if this individual dominates the competitor given as
	 *         parameter
	 */
	public boolean dominates(Individual competitor) {
		return Individual.dominates(this.getObjectiveValues(), competitor.getObjectiveValues());
	}


	/**
	 * Checks if this individual dominates the given individual (considers
	 * OBJECTIVE-VALUES!)
	 *
	 * @param competitor
	 *            Individual for which domination is checked.
	 *
	 * @param k
	 *            only the first k objectives will be considered (k >= num of
	 *            objectives)
	 *
	 * @return true if this individual dominates the competitor given as
	 *         parameter
	 */
	public boolean dominates(Individual competitor, int k) {
		return Individual.dominates(this.getObjectiveValues(), competitor.getObjectiveValues(), k);
	}


	/**
	 * Are the objective values of this individual to be minimized?
	 */
	public boolean isMinimization() {
		return Individual.minimizationOfObjectives;
	}



	// ########################################################################
	// GETTER AND SETTER METHODS
	// ########################################################################

	public void createDistanceSet(TreeSet<IndividualDistTupel> sortedDistance) {
		this.sortedDistances = new TreeSet<IndividualDistTupel>(sortedDistance);
	}
	public void setSortedDistance(TreeSet<IndividualDistTupel> sortedDistance) {
		this.sortedDistances = sortedDistance;
	}
	public TreeSet<IndividualDistTupel> getSortedDistance() {
		return this.sortedDistances;
	}

	public Phenotype getPhenotype() {
		if (this.phenotype == null && this.genotype != null && this.mapping != null)
			this.phenotype = this.mapping.createPhenotype(this.genotype);
		return phenotype;
	}
	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}

	@Override
	public double[] getObjectiveValues() {
		if (this.phenotype != null) {
			if (this.activeObjectives.equals(ObjectivePoint.ActiveObjectives.STANDARD))
				return this.phenotype.getObjectiveValues();
			else if (this.activeObjectives.equals(ObjectivePoint.ActiveObjectives.NORMALIZED))
				return this.phenotype.getNormalizedValues();
			else if (this.activeObjectives.equals(ObjectivePoint.ActiveObjectives.INVERTED))
				return this.phenotype.getInvertedValues();
			return null;
		} else {
			return null;
		}
	}
	@Override
	public void setObjectiveValues(double[] values) {
		if (this.phenotype != null) {
			this.phenotype.setObjectiveValues(values);
		}
	}

	@Override
	public double[] getNormalizedValues() {
		if (this.phenotype != null) {
			return this.phenotype.getNormalizedValues();
		} else {
			return null;
		}
	}
	@Override
	public void setNormalizedValues(double[] values) {
		if (this.phenotype != null) {
			this.phenotype.setNormalizedValues(values);
		}
	}

	@Override
	public double[] getInvertedValues() {
		if (this.phenotype != null) {
			return this.phenotype.getInvertedValues();
		} else {
			return null;
		}
	}
	@Override
	public void setInvertedValues(double[] values) {
		if (this.phenotype != null) {
			this.phenotype.setInvertedValues(values);
		}
	}

	@Override
	public ActiveObjectives getActiveObjectives() {
		return activeObjectives;
	}
	@Override
	public void setActiveObjectives(ActiveObjectives activeObjectives) {
		this.activeObjectives = activeObjectives;
	}

	@Override
	public double getResult() {
		return result;
	}
	@Override
	public void setResult(double result) {
		this.result = result;
	}

	public Genotype getGenotype() {
		return genotype;
	}
	public void setGenotype(Genotype genotype) {
		this.genotype = genotype;
	}

	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getSharing() {
		return sharing;
	}
	public void setSharing(double sharing) {
		this.sharing = sharing;
	}

	public int getId() {
		return id;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}



	// ########################################################################
	// STATIC METHODS
	// ########################################################################

	/**
	 * Calculate the strength of given individuals (sets their "strength" variable).
	 * S(i) := |#of individuals dominated by i|
	 * "Strength" (SPEA2), "Dominanzzahl" (SMSEMOA)
	 *
	 * @param population
	 */
	public static void calculateStrength(List<Individual> population) {
		for (Individual i1 : population) {
			int strength = 0;
			for (Individual i2 : population) {
				if (i1.dominates(i2)) {
					strength++;
				}
			}
			i1.setStrength(strength);
		}
	}


	/**
	 * Calculate the rank of given individuals (sets their "rank" variable).
	 * S(i) := |#of individuals that dominate i|
	 *
	 * @param population
	 */
	public static void calculateRank(List<Individual> population) {
		for (Individual i1 : population) {
			int rank = 0;
			for (Individual i2 : population) {
				if (i2.dominates(i1)) {
					rank++;
				}
			}
			i1.setRank(rank);
		}
	}


	/**
	 * Gets individual with maximum rank from given population (worst
	 * individual!). If multiple individuals share the same rank, the first to
	 * be found will be returned.
	 *
	 * @param population
	 *
	 * @return individual with maximum rank (worst individual!)
	 */
	public static Individual getMaxRankIndividual(List<Individual> population) {
		if (population.size() == 0) return null;

		Individual result = population.get(0);
		for (Individual ind : population) {
			if (ind.getRank() > result.getRank()) { // FIXED
				result = ind;
			}
		}
		return result;
	}


	/**
	 * Gets individual with maximum strength from given population (best
	 * individual!). If multiple individuals share the same rank, the first to
	 * be found will be returned.
	 *
	 * @param population
	 *
	 * @return individual with maximum rank (worst individual!)
	 */
	public static Individual getMaxStrenghIndividual(List<Individual> population) {
		if (population.size() == 0) return null;

		Individual result = population.get(0);
		for (Individual ind : population) {
			if (ind.getStrength() > result.getStrength()) {
				result = ind;
			}
		}
		return result;
	}


	/**
	 * Print list of given individuals.
	 *
	 * @param individuals
	 *            list of individuals to print
	 */
	public static void printIndividuals(List<Individual> individuals) {
		for (int i = 0; i < individuals.size(); i++)
			System.out.print( individuals.get(i).toString() );
	}


	/**
	 * Get a string describing the given individuals.
	 * @param individuals
	 * @return String describing the individuals
	 */
	public static String toString(List<Individual> individuals) {
		String result = "";
		for (int i = 0; i < individuals.size(); i++)
			result += individuals.get(i).toString();
		return result;
	}


	/**
	 * Get Individuals with unique phenotypes. If two individuals sharing the
	 * same phenotype are found, one of them is discarded (a new list is
	 * created; the parameter list is not modified!).
	 *
	 * @param inds
	 *            list of individuals which might contains individuals having
	 *            equal phenotypes (independent of the genotypes used in order
	 *            to create these phenotypes).
	 *
	 * @return a new list of individuals in which non of them shares an equal
	 *         phenotype.
	 */
	public static List<Individual> getUniquePhenotypeIndividuals(List<Individual> inds) {
		List<Individual> unique = new ArrayList<Individual>();
		for (Individual ind1 : inds) {
			// Check if individual with same phenotype is already in "unique"-list
			boolean found = false;
			for (Individual ind2 : unique) {
				if (ind1.getPhenotype().equals(ind2.getPhenotype())) {
					found = true;
					break;
				}
			}
			// Add new phenotype to unique-list if not found
			// NOTE: This is not the same as a check !unique.contains(ind1) because PHENOTYPES are checked (not individuals!)
			if (!found) {
				unique.add(ind1);
			}
		}
		return unique;
	}


	/**
	 * Get current id-counter (represents number of created individuals)
	 *
	 * @return id counter
	 */
	public static int getIdCounter() {
		return Individual.idCounter;
	}


	public static List<String> writtenFiles = new ArrayList<String>();
	/**
	 * Log the current generation into a csv file. The name of the file will be
	 * generated automatically.
	 *
	 * @param pop
	 * 		population to log
	 * @param expName
	 * 	   	name of the experiment, used to determine filename
	 * @param genName
	 * 		type of generation ("initial", "gen<number>", "result" or another custom String), used to determine filename
	 * @append append to file or create new one?
	 */
	public static <P extends ObjectivePoint> void logGeneration(List<P> pop, String expName, String genName, boolean append) {
		if (Functions.LOGGENERATIONS > 0) {
			if (Functions.LOGGENERATIONS < 2 && !genName.equals("result")) return;
			if (pop == null) return;
			if (expName == null) expName = "unnamedExp";
			String filePath = (genName != null) ? (Functions.LOGPATH + expName + "_" + genName + ".txt") : (Functions.LOGPATH + expName + ".txt");
			ParetoPoint.exportObjectiveValuesToCSV(pop, filePath, append);
			Individual.writtenFiles.add(filePath);
		}
	}
	public static <P extends ObjectivePoint> void logGeneration(P p, String expName, String genName, boolean append) {
		if (Functions.LOGGENERATIONS > 0) {
			if (Functions.LOGGENERATIONS < 2 && !genName.equals("result")) return;
			if (p == null) return;
			if (expName == null) expName = "unnamedExp";
			String filePath = (genName != null) ? (Functions.LOGPATH + expName + "_" + genName + ".txt") : (Functions.LOGPATH + expName + ".txt");
			ParetoPoint.exportObjectiveValuesToCSV(p, filePath, append);
			Individual.writtenFiles.add(filePath);
		}
	}



	// ########################################################################
	// CONVENIENCE FUNCTIONS FOR USING ParetoPoint
	// ########################################################################

	public static boolean dominates(double[] first, double[] second, int k) {
		return ParetoPoint.dominates(first, second, k, Individual.minimizationOfObjectives);
	}
	public static boolean dominates(double[] first, double[] second) {
		return (Individual.dominates(first, second, first.length));
	}


	public static <P extends ObjectivePoint> List<P> getNondominatedSolutions(List<P> population, int k) {
		return ParetoPoint.getNondominatedSolutions(null, population, k, Individual.minimizationOfObjectives);
	}
	public static <P extends ObjectivePoint> List<P> getNondominatedSolutions(List<P> population) {
		if (population.size() == 0) return new ArrayList<P>();
		int k = population.get(0).getObjectiveValues().length;
		return Individual.getNondominatedSolutions(population, k);
	}

	public static <P extends ObjectivePoint> List<P> updateNondominatedSolutions(List<P> nondominatedSolutions, List<P> newIndividuals) {
		int k = -1;
		if (nondominatedSolutions.size() > 0)
			k = nondominatedSolutions.get(0).getObjectiveValues().length;
		else if (newIndividuals.size() > 0)
			k = newIndividuals.get(0).getObjectiveValues().length;
		else
			return new ArrayList<P>();
		// MODIFIES nondominatedSolutions IF IT IS NOT NULL!
		return ParetoPoint.getNondominatedSolutions(nondominatedSolutions, newIndividuals, k, Individual.minimizationOfObjectives);
	}

	public static <P extends ObjectivePoint> List<P> getDominatedSolutions(List<P> population) {
		if (population.size() == 0) return new ArrayList<P>();
		int k = population.get(0).getObjectiveValues().length;
		return ParetoPoint.getDominatedSolutions(population, k, Individual.minimizationOfObjectives);
	}

	public static <P extends ObjectivePoint> P getWorst(List<P> inds, int k) {
		return ParetoPoint.getWorst(inds, k, Individual.minimizationOfObjectives);
	}

	public static <P extends ObjectivePoint> boolean containsDominatedSolution(List<P> population) {
		if (population.size() == 0) return false;
		int k = population.get(0).getObjectiveValues().length;
		return ParetoPoint.containsDominatedSolution(population, k, Individual.minimizationOfObjectives);
	}


}

