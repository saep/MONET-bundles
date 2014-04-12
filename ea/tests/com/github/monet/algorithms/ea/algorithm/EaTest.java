package com.github.monet.algorithms.ea.algorithm;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.github.monet.common.ParetoPoint;
import com.github.monet.generator.MonetGraphExporter;
import com.github.monet.generator.MonetGraphGenerator;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;
import com.github.monet.graph.SimpleUndirectedGraph;
import com.github.monet.graph.interfaces.DirectedEdge;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.GraphParser;
import com.github.monet.parser.MonetParser;

import org.junit.Ignore;
import org.junit.Test;

import com.github.monet.algorithms.ea.impl.individual.MSTPhenotype;
import com.github.monet.algorithms.ea.impl.individual.PrueferEncoding;
import com.github.monet.algorithms.ea.impl.individual.PrueferGenotype;
import com.github.monet.algorithms.ea.impl.operator.PrueferCreator;
import com.github.monet.algorithms.ea.impl.operator.PrueferMapping;
import com.github.monet.algorithms.ea.impl.operator.PrueferMutator;
import com.github.monet.algorithms.ea.impl.operator.PrueferRecombinator;
import com.github.monet.algorithms.ea.impl.operator.SelectorRouletteWheel;
import com.github.monet.algorithms.ea.impl.operator.TerminatorSimple;
import com.github.monet.algorithms.ea.individual.Individual;
import com.github.monet.algorithms.ea.individual.Phenotype;
import com.github.monet.algorithms.ea.main.EaConfigurator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.util.EaRandom;
import com.github.monet.algorithms.ea.util.Functions;

public class EaTest {
	private static boolean testOutput = true;

	// INDIVIDUAL TESTS
	// ########################################################################

	/**
	 * Test: Individual.dominates Individual.calculateRanks
	 * Individual.getNondominatedSolutions
	 */
	@Test
	public void testDomination() {
		Individual.minimizationOfObjectives = true;

		// Create Individuals
		List<Individual> individuals = new ArrayList<Individual>();

		Individual ind1 = new Individual();
		ind1.setPhenotype(new MSTPhenotype());
		ind1.setObjectiveValues(new double[] { 1, 2, 3 });
		individuals.add(ind1);

		Individual ind2 = new Individual();
		ind2.setPhenotype(new MSTPhenotype());
		ind2.setObjectiveValues(new double[] { 1, 2, 4 });
		individuals.add(ind2);

		Individual ind3 = new Individual();
		ind3.setPhenotype(new MSTPhenotype());
		ind3.setObjectiveValues(new double[] { 1, 1, 4 });
		individuals.add(ind3);

		Individual ind4 = new Individual();
		ind4.setPhenotype(new MSTPhenotype());
		ind4.setObjectiveValues(new double[] { 1, 0, 5 });
		individuals.add(ind4);

		Individual ind5 = new Individual();
		ind5.setPhenotype(new MSTPhenotype());
		ind5.setObjectiveValues(new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE });
		individuals.add(ind5);

		// Check domination for arrays
		boolean dominates;
		dominates = Individual.dominates(new double[] { 0, 0, 6 }, new double[] { 1, 0, 5 });
		assertTrue(!dominates);
		dominates = Individual.dominates(new double[] { 0, 0, 6 }, new double[] { 1, 0, 5 }, 2);
		assertTrue(dominates);

		// Check strength and ranks
		Individual.calculateStrength(individuals);
		assertTrue(individuals.get(0).getStrength() == 2);
		assertTrue(individuals.get(1).getStrength() == 1);
		assertTrue(individuals.get(2).getStrength() == 2);
		assertTrue(individuals.get(3).getStrength() == 1);
		assertTrue(individuals.get(4).getStrength() == 0);
		Individual.calculateRank(individuals);
		assertTrue(individuals.get(0).getRank() == 0);
		assertTrue(individuals.get(1).getRank() == 2);
		assertTrue(individuals.get(2).getRank() == 0);
		assertTrue(individuals.get(3).getRank() == 0);
		assertTrue(individuals.get(4).getRank() == 4);

		// Get dominated individuals
		List<Individual> nonDominated = Individual.getNondominatedSolutions(individuals);
		assertTrue(nonDominated != null);
		assertTrue(nonDominated.size() == 3);
		assertTrue(nonDominated.contains(ind1) && nonDominated.contains(ind3) && nonDominated.contains(ind4));

		// Create new individuals and update set
		Individual ind6 = new Individual();
		ind6.setPhenotype(new MSTPhenotype());
		ind6.setObjectiveValues(new double[] { 1, 0, 5 }); // Same point as already in. This should be added to nonDominated
		individuals.add(ind6);
		Individual.updateNondominatedSolutions(nonDominated, individuals);
		assertTrue(nonDominated.size() == 4);

		Individual ind7 = new Individual();
		ind7.setPhenotype(new MSTPhenotype());
		ind7.setObjectiveValues(new double[] { 1, 0, 6 }); // This point is dominated and should not be added
		individuals.add(ind7);
		Individual.updateNondominatedSolutions(nonDominated, individuals);
		assertTrue(nonDominated.size() == 4);

		Individual ind8 = new Individual();
		ind8.setPhenotype(new MSTPhenotype());
		ind8.setObjectiveValues(new double[] { 1, 0, 3 }); // this point dominates all other points
		individuals.add(ind8);
		Individual.updateNondominatedSolutions(nonDominated, individuals);
		assertTrue(nonDominated.size() == 1);

		// Test dominated
		List<Individual> individuals2 = new ArrayList<Individual>();
		individuals2.add(ind1);
		assertFalse(ParetoPoint.containsDominatedSolution(individuals2, 3, true));
		individuals2.add(ind2);
		assertTrue(ParetoPoint.containsDominatedSolution(individuals2, 3, true));


		// Check contributions
		List<Individual> individuals3 = new ArrayList<Individual>();

		Individual ind101 = new Individual();
		ind101.setPhenotype(new MSTPhenotype());
		ind101.setObjectiveValues(new double[] { 2, 4 });
		individuals3.add(ind101);

		Individual ind102 = new Individual();
		ind102.setPhenotype(new MSTPhenotype());
		ind102.setObjectiveValues(new double[] { 3, 1 });
		individuals3.add(ind102);

		Individual max = ParetoPoint.calcSMetricContrib(individuals3, null, 2, true); // get max contribution (considering maximization)
		assertTrue(max == ind101);
	}

	/**
	 * Test Individual sorting
	 */
	@Test
	public void testIndividualSorting() {
		List<Individual> individuals = new ArrayList<Individual>();
		Individual ind1 = new Individual();
		ind1.setFitness(10);
		individuals.add(ind1);
		Individual ind2 = new Individual();
		ind2.setFitness(100);
		individuals.add(ind2);

		Individual.minimizationOfFitness = false;
		Individual.IndividualBestFitnessComparator comp = new Individual.IndividualBestFitnessComparator();
		Collections.sort(individuals, comp);

		if (testOutput) {
			Functions.log("Fitness: " + individuals.get(0).getFitness(), Functions.LOG_TEST);
		}
		assertTrue(true);
	}

	/**
	 * Test: Individual.getSMetricByHSO
	 */
	@Test
	public void testSMetric() {
		// Create Individuals (values from Hypervolume-Paper Example Fig.5)
		List<Individual> individuals = new ArrayList<Individual>();

		Individual ind1 = new Individual();
		ind1.setPhenotype(new MSTPhenotype());
		ind1.setObjectiveValues(new double[] { 4, 4, 11 });
		individuals.add(ind1);

		Individual ind2 = new Individual();
		ind2.setPhenotype(new MSTPhenotype());
		ind2.setObjectiveValues(new double[] { 5, 2, 9 });
		individuals.add(ind2);

		Individual ind3 = new Individual();
		ind3.setPhenotype(new MSTPhenotype());
		ind3.setObjectiveValues(new double[] { 7, 6, 5 });
		individuals.add(ind3);

		Individual ind4 = new Individual();
		ind4.setPhenotype(new MSTPhenotype());
		ind4.setObjectiveValues(new double[] { 10, 3, 3 });
		individuals.add(ind4);

		// Test Min/Max methods
		assertTrue(Arrays.equals(new double[] { 10, 6, 11 }, ParetoPoint.getMax(individuals)));
		assertTrue(Arrays.equals(new double[] { 4, 2, 3 }, ParetoPoint.getMin(individuals)));
		assertTrue(ParetoPoint.getMinValue(individuals, 0) == 4);
		assertTrue(ParetoPoint.getMaxValue(individuals, 0) == 10);

		// Test normalization
		ParetoPoint.setNormalizedAndNormalizedInvertedValues(individuals);
		assertTrue(Arrays.equals(new double[] { 0, 0.5, 1 }, ind1.getPhenotype().getNormalizedValues()));
		assertTrue(Arrays.equals(new double[] { 1, 0.5, 0 }, ind1.getPhenotype().getInvertedValues()));

		// Transform objectives for maximization
		// Note: Given values of example already made for maximization
		// for (Individual ind : individuals) {
		// ind.setObjectiveValues(ind.getPhenotype().getInvertedValues());
		// }

		// Calculate Metric (341)
		Individual.minimizationOfObjectives = false;
		double[] ref = {0,0,0};
		double result = ParetoPoint.getMaximizationSMetricByHSO(individuals, ref, 3);
		Individual.minimizationOfObjectives = true;
		assertTrue(result == 341);
	}
	@Test
	public void testSTSPSmetric(){
		/*double resExact = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/Repetition 1 of 4h-Experiment-STSP-Namoa-20-2-1_front.txt",
				true);
		double resSPEA = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/Repetition 2 of 4h-Experiment-STSP-SPEA2-20-2-1_front.txt",
				true);
					System.out.println("Exakte Front: " + resExact + "\n EA-Front: " + resSPEA);

		double resK = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/4h-Experiment-MST-KBest-4-2-1_front.txt",
				true);
		double resB = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/4h-Experiment-MST-BranchBound-4-2-1_front.txt",
				true);
		double resS = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/4h-Experiment-MST-SMSEMOA-4-2-1_front.txt",
				true);
		double resP = ParetoPoint.calcSMetricfromCSV("/home/andreasdesktop/Arbeitsfläche/Plots/txt/4h-Experiment-MST-PrueferEA-4-2-1_front.txt",
				true);
		System.out.println("KBest: " + resK + "\nBranchBound: " + resB + "\nSMS-EMOA: " + resS + "\nPrueferEA: " + resP);
		*/
		double val42BB = 0;
		double val42KB = 0;
		double val42PE = 0;
		double val42SM = 0;

		double val52BB = 0;
//		double val52KB = 0;
		double val52PE = 0;
		double val52SM = 0;

		double val202LC  = 0;
		double val202LCM = 0;
		double val202NA = 0;
		double val202SP = 0;

		double val402LC = 0;
		double val402LCM = 0;
		double val402NA = 0;
		double val402SP = 0;

		double val103LC = 0;
		double val103LCM = 0;
		double val103NA = 0;
		double val103SP = 0;

		double val203LC = 0;
		double val203LCM = 0;
		double val203NA = 0;
		double val203SP = 0;

		File folder = new File("/home/andreasdesktop/Arbeitsfläche/Plots/txt/");
		File [] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			double tmp = ParetoPoint.calcSMetricfromCSV(file.getAbsolutePath(),
					false);
		   /* if (file.isFile()) {
		        System.out.println(file.getName());
		    }*/
			//MST 4-2
			if(file.getName().contains("BranchBound-4-2")){
				val42BB += tmp;
			}
			if(file.getName().contains("KBest-4-2")){
				val42KB += tmp;
			}
			if(file.getName().contains("PrueferEA-4-2")){
				val42PE += tmp;
			}
			if(file.getName().contains("SMSEMOA-4-2")){
				val42SM += tmp;
			}

			//MST 5-2
			if(file.getName().contains("BranchBound-5-2")){
				val52BB += tmp;
			}
			if(file.getName().contains("PrueferEA-5-2")){
				val52PE += tmp;
			}
			if(file.getName().contains("SMSEMOA-5-2")){
				val52SM += tmp;
			}

			//STSP 20-2
			if(file.getName().contains("LabelCorrecting-20-2")){
				val202LC += tmp;
			}
			if(file.getName().contains("LabelCorrectingMerge-20-2")){
				val202LCM += tmp;
			}
			if(file.getName().contains("Namoa-20-2")){
				val202NA += tmp;
			}
			if(file.getName().contains("STSP-SPEA2-20-2")){
				val202SP += tmp;
			}

			//STSP 40-2
			if(file.getName().contains("LabelCorrecting-40-2")){
				val402LC += tmp;
			}
			if(file.getName().contains("LabelCorrectingMerge-40-2")){
				val402LCM += tmp;
			}
			if(file.getName().contains("Namoa-40-2")){
				val402NA += tmp;
			}
			if(file.getName().contains("STSP-SPEA2-40-2")){
				val402SP += tmp;
			}

			//STSP 10-3
			if(file.getName().contains("LabelCorrecting-10-3")){
				val103LC += tmp;
			}
			if(file.getName().contains("LabelCorrectingMerge-10-3")){
				val103LCM += tmp;
			}
			if(file.getName().contains("Namoa-10-3")){
				val103NA += tmp;
			}
			if(file.getName().contains("STSP-SPEA2-10-3")){
				val103SP += tmp;
			}

			//STSP 20-3
			if(file.getName().contains("LabelCorrecting-20-3")){
				val203LC += tmp;
			}
			if(file.getName().contains("LabelCorrectingMerge-20-3")){
				val203LCM += tmp;
			}
			if(file.getName().contains("Namoa-20-3")){
				val203NA += tmp;
			}
			if(file.getName().contains("STSP-SPEA2-20-3")){
				val203SP += tmp;
			}
		}

		val42BB /= listOfFiles.length;
		val42KB /= listOfFiles.length;
		val42PE /= listOfFiles.length;
		val42SM /= listOfFiles.length;

		val52BB /= listOfFiles.length;
		val52PE /= listOfFiles.length;
		val52SM /= listOfFiles.length;

		val202LC /= listOfFiles.length;
		val202LCM /= listOfFiles.length;
		val202NA /= listOfFiles.length;
		val202SP /= listOfFiles.length;

		val402LC /= listOfFiles.length;
		val402LCM /= listOfFiles.length;
		val402NA /= listOfFiles.length;
		val402SP /= listOfFiles.length;

		val103LC /= listOfFiles.length;
		val103LCM /= listOfFiles.length;
		val103NA /= listOfFiles.length;
		val103SP /= listOfFiles.length;

		val203LC /= listOfFiles.length;
		val203LCM /= listOfFiles.length;
		val203NA /= listOfFiles.length;
		val203SP /= listOfFiles.length;


		System.out.println("val42BBHy <- " + val42BB);
		System.out.println("val42KBHy <- " + val42KB);
		System.out.println("val42PEHy <- " + val42PE);
		System.out.println("val42SMHy <- " + val42SM);

		System.out.println("val52BBHy <- " + val52BB);
		System.out.println("val52PEHy <- " + val52PE);
		System.out.println("val52SMHy <- " + val52SM);

		System.out.println("val202LCHy <- " + val202LC);
		System.out.println("val202LCMHy <- " + val202LCM);
		System.out.println("val202NAHy <- " + val202NA);
		System.out.println("val202SPHy <- " + val202SP);

		System.out.println("val402LCHy <- " + val402LC);
		System.out.println("val402LCMHy <- " + val402LCM);
		System.out.println("val402NAHy <- " + val402NA);
		System.out.println("val402SPHy <- " + val402SP);

		System.out.println("val103LCHy <- " + val103LC);
		System.out.println("val103LCMHy <- " + val103LCM);
		System.out.println("val103NAHy <- " + val103NA);
		System.out.println("val103SPHy <- " + val103SP);

		System.out.println("val203LCHy <- " + val203LC);
		System.out.println("val203LCMHy <- " + val203LCM);
		System.out.println("val203NAHy <- " + val203NA);
		System.out.println("val203SPHy <- " + val203SP);

	}

	/**
	 * Test S-Metric for Minimization
	 */
	@Test
	public void testSMetricMin() {
		double[] ref    = { 1, 1 };
		double[] point1 = {  5.0, 2.0 };
		double[] point1duplcicate = {  5.0, 2.0 };
		double[] point2 = {  2.0, 6.0 };
		double[] point3 = {  3.0, 3.0 };
		List<double[]> front = new ArrayList<double[]>();
		front.add(point1);
		front.add(point1duplcicate);
		front.add(point2);
		front.add(point3);
		double s = ParetoPoint.calculateSMetric(front, ref, true);
		assertTrue (s == 6);
	}

	// OPERATOR TESTS
	// ########################################################################

	/**
	 * Test SimpleTerminator
	 */
	@Test
	public void testSimpleTerminator() {
		TerminatorSimple term = new TerminatorSimple();

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("maxGenerations", 50);
		params.put("maxTime", 60000l);
		params.put("fitnessThreshold", 0);
		params.put("fitnessMaximization", false);
		params.put("constantFitnessGenLimit", 2);
		term.configure(params);

		List<Individual> individuals = new ArrayList<Individual>();
		Individual ind1 = new Individual();
		ind1.setFitness(10);
		individuals.add(ind1);
		Individual ind2 = new Individual();
		ind2.setFitness(200);
		individuals.add(ind2);

		assertFalse(term.terminate(1, 0, null, individuals));
		ind2.setFitness(300);
		assertFalse(term.terminate(2, 0, null, individuals));
		ind2.setFitness(300.01);
		assertFalse(term.terminate(3, 0, null, individuals));
		assertTrue(term.terminate(4, 0, null, individuals));
	}

	/**
	 * Test: PrueferCreator PrueferMutator PrueferRecombinator
	 * RouletteWheelSelector PrueferEncoding
	 */
	@Test
	public void testPrueferOperators() {
		// Create some operators
		@SuppressWarnings("rawtypes")
		PrueferCreator creator = new PrueferCreator();
		PrueferMutator mutator = new PrueferMutator();
		PrueferRecombinator recombinator = new PrueferRecombinator();
		Selector selector = new SelectorRouletteWheel();

		// Create Pruefer-Encoding
		PrueferEncoding enc = new PrueferEncoding();
		enc.setLength(8);
		List<Integer> validSymbols = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			validSymbols.add(i);
		}
		enc.setValidSymbols(validSymbols);

		// Test creation, mutation and recombination
		creator.setCreateFromSpanningTree(false);
		creator.setEncoding(enc);
		Individual ind1 = creator.createIndividual();
		Individual ind2 = creator.createIndividual();
		Individual ind3 = mutator.mutateIndividual(ind1);
		Individual ind4 = recombinator.recombinateIndividuals(ind1, ind2, null);

		// Test selection
		List<Individual> individuals = new ArrayList<Individual>();
		ind1.setFitness(10);
		individuals.add(ind1);
		ind2.setFitness(20);
		individuals.add(ind2);
		ind3.setFitness(300);
		individuals.add(ind3);
		ind4.setFitness(1000);
		individuals.add(ind4);
		List<Individual> selected = selector.select(individuals, null, 2);

		// Print results
		if (testOutput) {
			Functions.log("Operator Test:", Functions.LOG_TEST);
			Functions.log("Genotypes of created Individuals:", Functions.LOG_TEST);
			Functions.log("  " + ind1.getGenotype(), Functions.LOG_TEST);
			Functions.log("  " + ind2.getGenotype(), Functions.LOG_TEST);
			Functions.log("  " + ind3.getGenotype(), Functions.LOG_TEST);
			Functions.log("  " + ind4.getGenotype(), Functions.LOG_TEST);
			Functions.log("Selected Individuals:", Functions.LOG_TEST);
			Functions.log(Individual.toString(selected), Functions.LOG_TEST);
			Functions.log("\n\n", Functions.LOG_TEST);
		}
		assertTrue(true);
	}

	/**
	 * Test: PrueferMapping
	 */
	@Test
	public void testPrueferMapping() {
		// Create problem graph
		GraphParser gp = new MonetParser();
		@SuppressWarnings("unchecked")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> annotatedGraph = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(
				Functions.TEST_GRAPHDIR + "graph_ea_test1.txt", null);
		Graph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = annotatedGraph.getGraph();

		@SuppressWarnings("unchecked")
		GraphElementAnnotator<SimpleEdge, Weight> annotator = annotatedGraph.getAnnotator("edges", GraphElementAnnotator.class); //
		int numObjectives = ((Weight) annotator.getAnnotation(annotator.getAnnotatedElements().iterator().next())).getDimension(); //

		// Create a map for Node IDs -> Node objects
		HashMap<Integer, SimpleNode> idNodeMap = new HashMap<Integer, SimpleNode>();
		int i = 0;
		for (SimpleNode node : g.getAllNodes()) {
			idNodeMap.put(i, node);
			i++;
		}

		// Create mapping
		PhenotypeMapping mapping = new PrueferMapping<SimpleNode, SimpleEdge, SimpleUndirectedGraph>();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("problemGraph", annotatedGraph);
		params.put("idNodeMap", idNodeMap);
		params.put("numObjectives", numObjectives); //
		mapping.configure(params);

		// Create a genotype to map
		PrueferEncoding pe = new PrueferEncoding();
		pe.setLength(2);
		List<Integer> validSymbols = new ArrayList<Integer>();
		validSymbols.add(0);
		validSymbols.add(1);
		validSymbols.add(2);
		validSymbols.add(3);
		pe.setValidSymbols(validSymbols);
		PrueferGenotype pg = new PrueferGenotype();
		pg.setEncoding(pe);
		List<Integer> value = new ArrayList<Integer>();
		value.add(1);
		value.add(3);
		pg.setValue(value);

		// Execute mapping
		Phenotype p = mapping.createPhenotype(pg);

		// Check if created phenotype equals expected phenotype
		MSTPhenotype expected = new MSTPhenotype();
		List<Edge> edges = new ArrayList<Edge>();
		edges.add(g.getEdge(idNodeMap.get(0), idNodeMap.get(1)));
		edges.add(g.getEdge(idNodeMap.get(1), idNodeMap.get(3)));
		edges.add(g.getEdge(idNodeMap.get(2), idNodeMap.get(3)));
		expected.setEdges(edges);
		assertTrue(expected.equals(p));

		if (EaTest.testOutput) {
			Functions.log("Pruefer mapping test result: ", Functions.LOG_TEST);
			Functions.log("  " + p.toString(), Functions.LOG_TEST);
			Functions.log("Pruefer mapping expected result: ", Functions.LOG_TEST);
			Functions.log("  " + expected.toString(), Functions.LOG_TEST);
		}
		assertTrue(true);
	}

	/**
	 * Test: PhenotypeMST Individual.getUniquePhenotypeIndividuals
	 */
	@Test
	public void testPhenotypeMst() {
		GraphParser gp = new MonetParser();
		@SuppressWarnings("unchecked")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(Functions.TEST_GRAPHDIR
				+ "graph_ea_test1.txt", null);
		List<Edge> edges = new ArrayList<Edge>(g.getGraph().getAllEdges());

		// Phenotype 1
		List<Edge> edges1 = new ArrayList<Edge>(edges);
		MSTPhenotype p1 = new MSTPhenotype();
		p1.setEdges(edges1);
		// Phenotype 2
		List<Edge> edges2 = new ArrayList<Edge>(edges);
		MSTPhenotype p2 = new MSTPhenotype();
		p2.setEdges(edges2);

		// Individuals
		Individual ind1 = new Individual();
		ind1.setPhenotype(p1);
		Individual ind2 = new Individual();
		ind2.setPhenotype(p2);

		// Check Equality
		List<Individual> inds = new ArrayList<Individual>();
		inds.add(ind1);
		inds.add(ind2);
		List<Individual> result = Individual.getUniquePhenotypeIndividuals(inds);
		assertTrue(p1.equals(p2));
		assertTrue(result.size() == 1);
	}

	// EA TESTS
	// ########################################################################

	@Test
	public void testPrueferEA() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eaName", "Pruefer-EA");
		params.put("creatorName", "Pruefer-Creator");
		params.put("mutatorName", "Uniform-Mutation");
		params.put("recombinatorName", "Pruefer-Recombinator");
		params.put("selectorName", "Roulette-Wheel-Selection");
		params.put("evaluatorName", "Pruefer-Evaluator-2");
		params.put("terminatorName", "Simple-Terminator");
		params.put("mappingName", "Pruefer-GPM");
		params.put("popSize", "500");
		params.put("offspringSize", "500");
		params.put("maxGenerations", "10");
		params.put("maxTime", "10000l");
		params.put("fitnessThreshold", "1d");
		params.put("minimization", "true");
		EaTestProblem problem = new EaTestProblem("testPrueferEA", "graph_ea_test3.txt", false, 3l, null, params, true);
		problem.startTest();
	}

	@Test
	public void testSMSEMOA() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eaName", "SMS-EMOA");
		params.put("creatorName", "Pruefer-Creator");
		params.put("mutatorName", "Uniform-Mutation");
		params.put("recombinatorName", "Pruefer-Recombinator");
		params.put("terminatorName", "Simple-Terminator");
		params.put("mappingName", "Pruefer-GPM");
		params.put("popSize", "500");
		params.put("maxGenerations", "10");
		params.put("maxTime", "10000l");
		params.put("fitnessThreshold", "1d");
		params.put("minimization", "true");
		EaTestProblem problem = new EaTestProblem("testSMSEMOA", "graph_ea_test2.txt", false, 3l, null, params, true);
		problem.startTest();
	}

	@Test
	public void testSPEA2() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eaName", "SPEA-2");
		params.put("creatorName", "SPEA2-Creator");
		params.put("mutatorName", "SPEA2-Mutator");
		params.put("recombinatorName", "SPEA2-Recombinator");
		params.put("terminatorName", "Simple-Terminator");
		params.put("selectorName", "Tournament-Selection");
		params.put("evaluatorName", "SPEA2-Evaluator");
		params.put("mappingName", "SPEA2-GPM");
		params.put("popSize", "100");
		params.put("archiveSize", "100");
		params.put("maxGenerations", "20");
		params.put("maxTime", "10000l");
		params.put("fitnessThreshold", "1d");
		params.put("minimization", "true");
		EaTestProblem problem = new EaTestProblem("testSPEA2", "graph_ea_test2.txt", false, 1l, null, params, true);
		problem.startTest();
	}

	@Test
	public void testSPEA2directed() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eaName", "SPEA-2");
		params.put("creatorName", "SPEA2-Creator");
		params.put("mutatorName", "SPEA2-Mutator");
		params.put("recombinatorName", "SPEA2-Recombinator");
		params.put("terminatorName", "Simple-Terminator");
		params.put("selectorName", "Tournament-Selection");
		params.put("evaluatorName", "SPEA2-Evaluator");
		params.put("mappingName", "SPEA2-GPM");
		params.put("popSize", "100");
		params.put("archiveSize", "100");
		params.put("maxGenerations", "20");
		params.put("maxTime", "10000l");
		params.put("fitnessThreshold", "1d");
		params.put("minimization", "true");
		EaTestProblem problem = new EaTestProblem("testSPEA2directed", "graph_ea_test2.txt", true, 1l, null, params, true);
		problem.startTest();
	}

	// FUNCTION TESTS
	// ########################################################################

	/**
	 * Test: Functions
	 */
	@Test
	public void testFunctions() {
		EaConfigurator.initizalize();
		List<String> availableOps = Functions.getOperatorNames();
		if (EaTest.testOutput) {
			Functions.log("Available Operators: " + availableOps + ".", Functions.LOG_TEST);
		}
		assertTrue(true);
	}

	/**
	 * Test: Make graph complete
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMakeGraphComplete() {
		// Get a graph
		GraphParser gp = new MonetParser();
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(Functions.TEST_GRAPHDIR
				+ "graph_ea_test2.txt", null);
		GraphElementAnnotator<Edge, Weight> weightAnnotator = g.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class);
		int numObjs = ((Weight)weightAnnotator.getAnnotation(weightAnnotator.getAnnotatedElements().iterator().next())).getDimension();

		// Make complete
		assertTrue(!Functions.isGraphComplete(g.getGraph()));
		Functions.makeGraphComplete(g.getGraph(), g.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class), numObjs);
		assertTrue(Functions.isGraphComplete(g.getGraph()));
	}

	/**
	 * Test: Creation of a random ST (Using random walk)
	 */
	@Test
	public void testRandomSpanningTree() {
		EaRandom.setNewSeed(37128743l);

		// Get Graph
		GraphParser gp = new MonetParser();
		@SuppressWarnings("unchecked")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(Functions.TEST_GRAPHDIR
				+ "graph_ea_test2.txt", null);

		// Create Spanning Tree
		List<Edge> st = Functions.createRandomMST(g.getGraph());
		if (EaTest.testOutput) {
			Functions.log("ST Edges:", Functions.LOG_TEST);
			for (Edge e : st) {
				Functions.log(e.toString(), Functions.LOG_TEST);
				// g.getGraph().getIncidentNodes((SimpleEdge)e);
			}
		}

		// Create Pruefer-Encoding
		HashMap<Integer, Node> idNodeMap = new HashMap<Integer, Node>();
		HashMap<Node, Integer> nodeIdMap = new HashMap<Node, Integer>();
		int n = 0;
		for (SimpleNode node : g.getGraph().getAllNodes()) {
			idNodeMap.put(n, node);
			nodeIdMap.put(node, n);
			n++;
		}
		List<Integer> validSymbols = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			validSymbols.add(i);
		}
		PrueferEncoding enc = new PrueferEncoding();
		enc.setLength(8);
		enc.setValidSymbols(validSymbols);
		enc.setNodeIdMap(nodeIdMap);
		enc.setIdNodeMap(idNodeMap);

		// Create Prüfer-Number (Korrekt: 01241259, selber nachgerechnet)
		List<Integer> number = enc.tree2Pruefer(g.getGraph(), st);
		assertTrue(number.get(0) == 0 && number.get(1) == 1 && number.get(2) == 2 && number.get(3) == 4 && number.get(4) == 1 && number.get(5) == 2 && number.get(6) == 5
				&& number.get(7) == 9);
	}

	/**
	 * Test: Creation of a random ST (Using random walk)
	 */
	@Test
	public void testRandomSpanningTreeKruskal() {
		EaRandom.setNewSeed(37178743l);

		// Get Graph
		GraphParser gp = new MonetParser();
		@SuppressWarnings("unchecked")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(Functions.TEST_GRAPHDIR
				+ "graph_ea_test2.txt", null);

		// nodeIdMap
		HashMap<Node, Integer> nodeIdMap = new HashMap<Node, Integer>();
		int n = 0;
		for (SimpleNode node : g.getGraph().getAllNodes()) {
			nodeIdMap.put(node, n);
			n++;
		}

		// Create Spanning Tree
		List<Edge> st = Functions.createRandomMST_Kruskal(g.getGraph(), nodeIdMap, null, 0);
		if (EaTest.testOutput) {
			Functions.log("ST Edges:", Functions.LOG_TEST);
			for (Edge e : st) {
				Functions.log(e.toString(), Functions.LOG_TEST);
				// g.getGraph().getIncidentNodes((SimpleEdge)e);
			}
		}
		assertTrue(true);
	}

	/**
	 * Test: Create Random Path (undirected)
	 */
	@Test
	public void testRandomPath() {
		EaRandom.setNewSeed(1); // 1 for an interesting solution

		// Get Graph
		GraphParser gp = new MonetParser();
		@SuppressWarnings("unchecked")
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> g = (AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph>) gp.parse(Functions.TEST_GRAPHDIR
				+ "graph_ea_test2.txt", null);

		// Select start and end
		SimpleNode startNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		SimpleNode endNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		while (startNode == endNode) {
			endNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		}
		if (EaTest.testOutput) {
			Functions.log("Start: " + startNode + ", End: " + endNode, Functions.LOG_TEST);
		}

		// Create Path
		List<Node> path = Functions.createRandomPath(g.getGraph(), startNode, endNode);

		// Show Path
		if (EaTest.testOutput) {
			for (Node n : path) {
				Functions.log(n.toString(), Functions.LOG_TEST);
			}
		}

		assertTrue(true);
	}

	/**
	 * Test: Create Random Path (directed)
	 */
	@Test
	public void testRandomPathDirected() {
		EaRandom.setNewSeed(156423l);

		// Get Graph
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> g = this.createDirectedGraph();

		// Create Path
		SimpleNode startNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		SimpleNode endNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		while (startNode == endNode) {
			endNode = EaRandom.getRandomElement(g.getGraph().getAllNodes());
		}
		if (EaTest.testOutput) {
			Functions.log("Start: " + startNode + ", End: " + endNode, Functions.LOG_TEST);
		}

		// Select start and end
		List<Node> path = Functions.createRandomPath(g.getGraph(), startNode, endNode);

		// Show Path
		if (EaTest.testOutput) {
			if (path != null) {
				for (Node n : path) {
					Functions.log("Node " + n.toString(), Functions.LOG_TEST);
				}
			} else {
				Functions.log("No path found.", Functions.LOG_TEST);
			}
		}

		assertTrue(true);
	}

	// MISC TESTS
	// ########################################################################

	/**
	 * Test: Misc Tests
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMisc() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Integer> testparam = new HashMap<String, Integer>();
		testparam.put("N1", 3);
		params.put("testparam", testparam);

		HashMap<String, Integer> nodeIdMap = Functions.getParam(params, "testparam", HashMap.class, null);
		if (EaTest.testOutput) {
			Functions.log(nodeIdMap.get("N1").toString(), Functions.LOG_TEST);
		}
		assertTrue(true);
	}

	@Test
	public void testTrivial() {
		assertTrue(Boolean.parseBoolean("true"));
	}

	// CUSTOM GRAPH CREATION
	// ########################################################################

	/**
	 * Create a directed graph
	 *
	 * @return
	 */
	private AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> createDirectedGraph() {
		// Create directed graph (taken from LabelCorrectingTest)
		SimpleDirectedGraph graph = new SimpleDirectedGraph();
		SimpleNode a = graph.addNode();
		SimpleNode b = graph.addNode();
		SimpleNode c = graph.addNode();
		SimpleNode d = graph.addNode();
		SimpleNode e = graph.addNode();
		SimpleNode f = graph.addNode();
		SimpleNode g = graph.addNode();
		SimpleNode h = graph.addNode();
		SimpleEdge ab = graph.addEdge(a, b);
		SimpleEdge ac = graph.addEdge(a, c);
		SimpleEdge bd = graph.addEdge(b, d);
		SimpleEdge be = graph.addEdge(b, e);
		SimpleEdge cb = graph.addEdge(c, b);
		SimpleEdge ce = graph.addEdge(c, e);
		SimpleEdge de = graph.addEdge(d, e);
		SimpleEdge df = graph.addEdge(d, f);
		SimpleEdge eb = graph.addEdge(e, b);
		SimpleEdge ef = graph.addEdge(e, f);
		SimpleEdge dg = graph.addEdge(d, g);
		SimpleEdge gh = graph.addEdge(g, h);
		SimpleEdge fh = graph.addEdge(f, h);

		GraphElementHashAnnotator<DirectedEdge, Weight> adapted = new GraphElementHashAnnotator<DirectedEdge, Weight>();
		GraphElementWeightAnnotator<DirectedEdge> weights = new GraphElementWeightAnnotator<DirectedEdge>(adapted);

		double[] abWeightD = { 3, 12 };
		Weight abWeight = new Weight(abWeightD);
		weights.setAnnotation(ab, abWeight);
		double[] acWeightD = { 4, 6 };
		Weight acWeight = new Weight(acWeightD);
		weights.setAnnotation(ac, acWeight);
		double[] bdWeightD = { 8, 1 };
		Weight bdWeight = new Weight(bdWeightD);
		weights.setAnnotation(bd, bdWeight);
		double[] beWeightD = { 1, 5 };
		Weight beWeight = new Weight(beWeightD);
		weights.setAnnotation(be, beWeight);
		double[] cbWeightD = { 5, 6 };
		Weight cbWeight = new Weight(cbWeightD);
		weights.setAnnotation(cb, cbWeight);
		double[] ceWeightD = { 6, 4 };
		Weight ceWeight = new Weight(ceWeightD);
		weights.setAnnotation(ce, ceWeight);
		double[] deWeightD = { 7, 5 };
		Weight deWeight = new Weight(deWeightD);
		weights.setAnnotation(de, deWeight);
		double[] dfWeightD = { 9, 2 };
		Weight dfWeight = new Weight(dfWeightD);
		weights.setAnnotation(df, dfWeight);
		double[] ebWeightD = { 1, 1 };
		Weight ebWeight = new Weight(ebWeightD);
		weights.setAnnotation(eb, ebWeight);
		double[] efWeightD = { 9, 11 };
		Weight efWeight = new Weight(efWeightD);
		weights.setAnnotation(ef, efWeight);
		double[] dgWeightD = { 9, 11 };
		Weight dgWeight = new Weight(dgWeightD);
		weights.setAnnotation(dg, dgWeight);
		double[] ghWeightD = { 9, 11 };
		Weight ghWeight = new Weight(ghWeightD);
		weights.setAnnotation(gh, ghWeight);
		double[] fhWeightD = { 9, 11 };
		Weight fhWeight = new Weight(fhWeightD);
		weights.setAnnotation(fh, fhWeight);

		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> aGraph = new AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph>(graph);
		aGraph.addAnnotator("edges", weights);

		return aGraph;
	}

	/**
	 * Use Graph-Generator
	 */
	public AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> createGraphDirected() {
		// type, #nodes, #objectives,density
		MonetGraphGenerator<SimpleDirectedGraph> gen = new MonetGraphGenerator<SimpleDirectedGraph>("directed", 100, 4, 1.2);
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gen.getGraph();
		return graph;
	}

	/**
	 * Use Graph-Generator AnnotatedGraph<SimpleNode, SimpleEdge,
	 * SimpleUndirectedGraph> g = this.createGraphUndirected();
	 */
	public AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> createGraphUndirected() {
		// type, #nodes, #objectives,density
		MonetGraphGenerator<SimpleUndirectedGraph> gen = new MonetGraphGenerator<SimpleUndirectedGraph>("undirected", 100, 4, 1.2);
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleUndirectedGraph> graph = gen.getGraph();
		return graph;
	}

	/**
	 * Graph Exporter
	 */
	public void exportNewGraph() {
		MonetGraphGenerator<SimpleDirectedGraph> gn = new MonetGraphGenerator<>("undirected", 100, 4, 10);
		AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gn.getGraph();
		MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph> exporter = new MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph>();
		exporter.export("../graph_instances/", "monet.txt", graph);
	}

	// EA PRESET TESTS
	// These tests might take some while so we don't execute them at each test
	// ########################################################################

	@Ignore @Test
	public void tSPEA2Preset() {
		// AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> g =
		// this.createDirectedGraph();
		EaTestProblem problem = new EaTestProblem("testSPEA2Preset", "directed_integer_grid_graphs/grid_dir_15_5.txt", true, 1l, "DirectSSSP-SPEA2", null, true);
		problem.startTest();
	}

	@Ignore @Test
	public void tSPEA2Preset_2() {
		EaTestProblem problem = new EaTestProblem("testSPEA2Preset_2", "randomGraph_200_3_100.txt", false, 3l, "DirectSSSP-SPEA2", null, true);
		problem.startTest();
	}

	@Ignore @Test
	public void tDirectMST() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Functions.setNewParam(params, "maxDegree", 2);
		EaTestProblem problem = new EaTestProblem("testDirectMST", "graph_ea_test3.txt", false, 3l, "DirectMST-SMSEMOA", params, true);
		problem.startTest();
	}

	@Ignore @Test
	public void tDegreeMST() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Functions.setNewParam(params, "maxDegree", 2);
		EaTestProblem problem = new EaTestProblem("testDegreeMST", "graph_ea_test4.txt", false, 4l, "DirectMST-SMSEMOA", params, true);
		problem.startTest();
	}

	/**
	 * Check multiple graphs and log time
	 */
	@SuppressWarnings("unused")
	@Ignore @Test
	public void testEa() {
		// Create problems manually
		// Presets: DirectSSSP-SPEA2, DirectSSSP-SMSEMOA, DirectSSSP-PrueferEA,
		// Parser parameters: "startNodeid", "endNodeid"
		// Example: problems.add( new EaTestProblem("directed_integer_grid_graphs/grid_dir_4_2.txt", false, 3l, "PrueferMST-PrueferEA", null, true) );
		// STSP Files: "grid_dir_10_2.txt", "grid_dir_10_3.txt", "grid_dir_10_5.txt", "grid_dir_10_7.txt", "grid_dir_15_2.txt", "grid_dir_15_3.txt", "grid_dir_15_5.txt", "grid_dir_15_7.txt", "grid_dir_25_2.txt", "grid_dir_25_3.txt", "grid_dir_25_5.txt", "grid_dir_25_7.txt", "grid_dir_40_2.txt", "grid_dir_40_3.txt", "grid_dir_40_5.txt", "grid_dir_40_7.txt", "grid_dir_4_2.txt", "grid_dir_60_2.txt"
		// EaTest Constructor: String expName, String graphFileName, boolean directed, Long seed, String preset, HashMap<String, Object> params, boolean setRandomStartEndNodes

		Functions.LOGGENERATIONS = 1;
		Functions.PRINTLOGFILE = true;
		Functions.clearLogFile();

		int times = 3;

		String[] filesStsp = new String[]{"st_sp_grid_dir_10_3_1.txt", "st_sp_grid_dir_10_3_2.txt", "st_sp_grid_dir_10_3_3.txt", "st_sp_grid_dir_20_2_1.txt", "st_sp_grid_dir_20_2_2.txt", "st_sp_grid_dir_20_2_3.txt", "st_sp_grid_dir_20_3_1.txt", "st_sp_grid_dir_20_3_2.txt", "st_sp_grid_dir_20_3_3.txt", "st_sp_grid_dir_40_2_1.txt", "st_sp_grid_dir_40_2_2.txt", "st_sp_grid_dir_40_2_3.txt"};
		String[] filesMst  = new String[]{"grid_undir_4_2_1.txt", "grid_undir_4_2_2.txt", "grid_undir_4_2_3.txt", "grid_undir_5_2_1.txt", "grid_undir_5_2_2.txt", "grid_undir_5_2_3.txt"};

		List<EaTestProblem> problems = new ArrayList<EaTestProblem>();
		HashMap<String, Object> params = new HashMap<String, Object>();

		HashMap<String, Object> paramsRandomSearchStsp = new HashMap<String, Object>();
		paramsRandomSearchStsp.put("eaName", "RandomSearch");
		paramsRandomSearchStsp.put("creatorName", "SPEA2-Creator");
		paramsRandomSearchStsp.put("mappingName", "SPEA2-GPM");
		paramsRandomSearchStsp.put("popSize", "50000");
		paramsRandomSearchStsp.put("minimization", "true");

		HashMap<String, Object> paramsRandomSearchMst = new HashMap<String, Object>();
		paramsRandomSearchMst.put("eaName", "RandomSearch");
		paramsRandomSearchMst.put("creatorName", "MST-Creator");
		paramsRandomSearchMst.put("mappingName", "MST-GPM");
		paramsRandomSearchMst.put("popSize", "50000");
		paramsRandomSearchMst.put("minimization", "true");

		HashMap<String, Object> paramsRandomWalk = new HashMap<String, Object>();
		paramsRandomWalk.put("eaName", "RandomWalk");
		paramsRandomWalk.put("creatorName", "SPEA2-Creator");
		paramsRandomWalk.put("mappingName", "SPEA2-GPM");
		paramsRandomWalk.put("mutatorName", "SPEA2-Mutator");
		paramsRandomWalk.put("popSize", "50000");
		paramsRandomWalk.put("minimization", "true");

		HashMap<String, Object> paramsSpea2 = new HashMap<String, Object>();
		//paramsSpea2.put("maxGenerations", "500");
		//paramsSpea2.put("popSize", "300");
		//paramsSpea2.put("archiveSize", "300");
		//paramsSpea2.put("constantFitnessGenLimit", 2);

		HashMap<String, Object> paramsSmsemoa = new HashMap<String, Object>();

		HashMap<String, Object> paramsPruefer = new HashMap<String, Object>();

		// Single Tests
		//problems.add( new EaTestProblem("experiments/stsp/st_sp_grid_dir_40_2_1.txt", true, 3l, "DirectSSSP-SPEA2", paramsSpea2, false) );
		//problems.add( new EaTestProblem("experiments/stsp/st_sp_grid_dir_40_2_1.txt", true, 3l, null, paramsRandomSearch, false) );
		//problems.add( new EaTestProblem("experiments/stsp/st_sp_grid_dir_40_2_1.txt", true, 3l, null, paramsRandomWalk, false) );

		// All STSP-SMSEMOA Tests
//		paramsSpea2.put("autoGenerateSeed", true);
//		problems.add( new EaTestProblem("st_sp_grid_dir_40_2_2.txt" , "experiments/experiments_4h/stsp/"+"st_sp_grid_dir_40_2_2.txt", true, 3l,  "DirectSSSP-SPEA2", paramsSpea2, false) );
		/*
		for (String graphFile : filesStsp) {
			String graphName = graphFile.substring(0, graphFile.indexOf('.'));
			for (int i = 1; i <= times; i++) {
				paramsSmsemoa.put("autoGenerateSeed", true);
				problems.add( new EaTestProblem(graphName+"_run_stsp_smsemoa"+i, "experiments/experiments_4h/stsp/"+graphFile, true, 3l, "DirectSSSP-SMSEMOA", paramsSmsemoa, false) );
				//problems.add( new EaTestProblem(graphName+"_rand", "experiments/experiments_4h/mst/"+graphFile, false, 3l, null, paramsRandomSearch, false) );
			}
		}
		//*/

		// All STSP-SPEA2 Tests
		/*
		for (String graphFile : filesStsp) {
			String graphName = graphFile.substring(0, graphFile.indexOf('.'));
			for (int i = 1; i <= times; i++) {
				paramsSpea2.put("autoGenerateSeed", true);
				problems.add( new EaTestProblem(graphName+"_run_stsp_spea2"+i, "experiments/experiments_4h/stsp/"+graphFile, true, 3l, "DirectSSSP-SPEA2", paramsSpea2, false) );
				//problems.add( new EaTestProblem(graphName+"_rand", "experiments/experiments_4h/stsp/"+graphFile, true, 3l, null, paramsRandomSearch, false) );
			}
		}
		//*/

		// All MST-SMSEMOA Tests
		/*
		for (String graphFile : filesMst) {
			String graphName = graphFile.substring(0, graphFile.indexOf('.'));
			for (int i = 1; i <= times; i++) {
				paramsSmsemoa.put("autoGenerateSeed", true);
				problems.add( new EaTestProblem(graphName+"_run_mst_smsemoa"+i, "experiments/experiments_4h/mst/"+graphFile, false, 3l, "DirectMST-SMSEMOA", paramsSmsemoa, false) );
				//problems.add( new EaTestProblem(graphName+"_rand", "experiments/experiments_4h/mst/"+graphFile, false, 3l, null, paramsRandomSearch, false) );
			}
		}
		//*/

		// All MST-Pruefer Tests
		/*
		for (String graphFile : filesMst) {
			String graphName = graphFile.substring(0, graphFile.indexOf('.'));
			for (int i = 1; i <= times; i++) {
				paramsPruefer.put("autoGenerateSeed", true);
				problems.add( new EaTestProblem(graphName+"_run_mst_pruefer"+i, "experiments/experiments_4h/mst/"+graphFile, false, 3l, "DirectMST-PrueferEA", paramsPruefer, false) );
				//problems.add( new EaTestProblem(graphName+"_rand", "experiments/experiments_4h/mst/"+graphFile, false, 3l, null, paramsRandomSearchMst, false) );
			}
		}
		//*/

		// Test problems
		EaTestProblem.testProblems(problems);
		for(String filepath: Individual.writtenFiles){
			System.out.println("SMetric - filepath: " + filepath + " - value: " + ParetoPoint.calcSMetricfromCSV(filepath, true));
		}

	}

}


