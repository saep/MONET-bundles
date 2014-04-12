package com.github.monet.algorithms.sssp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.GraphElementHashAnnotator;
import com.github.monet.graph.GraphElementReverseHashAnnotator;
import com.github.monet.graph.ParetoFront;
import com.github.monet.graph.interfaces.DirectedEdge;
import com.github.monet.graph.interfaces.DirectedGraph;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.GraphElementWeightAnnotator;
import com.github.monet.graph.weighted.LabelSet;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.graph.weighted.WeightedEdgesCalculator;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.JobState.CustomState;
import com.github.monet.worker.ServiceDirectory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LabelCorrecting<G extends DirectedGraph<N, E, G>, N extends Node, E extends DirectedEdge>
		implements com.github.monet.interfaces.Algorithm {

	private G graph;
	private GraphElementWeightAnnotator<E> weights;
	private GraphElementHashAnnotator<N, LabelSet> labels;
	private N source, destination;
	private Meter meter;
	// private ServiceDirectory serviceDir;
	private boolean intMerge;
	private int dimension;
	private ArrayList<LinkedList<E>> solutions;
	private Job job;
	private int pathsExtended;
	private Logger logger;

	@SuppressWarnings("unchecked")
	public void execute(Object input, Map<String, Object> parameters,
			Meter meter, ServiceDirectory serviceDir, Job job){
		this.graph = (G) ((AnnotatedGraph<N, E, G>) input).getGraph();
		this.weights = new GraphElementWeightAnnotator<E>(
				((AnnotatedGraph<N, E, G>) input).getAnnotator("edges",
						GraphElementHashAnnotator.class));
		GraphElementReverseHashAnnotator<N, String> sdAnnotator = ((AnnotatedGraph<N, E, G>) input)
				.getAnnotator("sdAnnotator",
						GraphElementReverseHashAnnotator.class);
		this.source = sdAnnotator.getElements("startNode").iterator().next();
		this.destination = sdAnnotator.getElements("endNode").iterator().next();
		labels = new GraphElementHashAnnotator<N, LabelSet>();
		this.meter = meter;
		// this.serviceDir = serviceDir;
		this.job = job;
		if (job != null)
			this.logger = job.getLogger();
		this.solutions = new ArrayList<LinkedList<E>>();
		this.pathsExtended = 0;
		if (parameters.get("MERGE_MODE").equals(false))
			intMerge = false;
		else if (parameters.get("MERGE_MODE").equals(true))
			intMerge = true;
		dimension = weights.getAnnotation(
				graph.getOutgoingEdges(source).iterator().next())
				.getDimension();
		for (N n : graph.getAllNodes()) {
			labels.setAnnotation(n, new LabelSet());
		}
		run();
	}

	/**
	 * The main algorithm. Parameters have been stored in global variables by
	 * execute
	 */
	void run() {
		meter.startExperiment();
		if (logger != null)
			logger.log(Level.DEBUG, "Algorithm started");
		meter.startTimer("ALGORITHM");
		LinkedList<N> labeled = new LinkedList<N>();
		HashSet<N> isLabeled = new HashSet<N>();
		double[] nullVector = new double[dimension];
		for (int i = 0; i < nullVector.length; i++)
			nullVector[i] = 0.0;
		Weight nullWeight = new Weight(nullVector);
		labels.getAnnotation(source).insertLabel(nullWeight);
		labeled.add(source);
		isLabeled.add(source);

		while (!labeled.isEmpty()) {
			// While there are nodes to be processed
			// get next node to be processed (FIFO)
			N act = labeled.removeFirst();
			if (!act.equals(destination)) {
				isLabeled.remove(act);
				// get neighborhood of the processed node
				Collection<E> actOut = graph.getOutgoingEdges(act);
				// System.out.println(act);
				for (E e : actOut) {
					// merge labelsets
					pathsExtended++;
					boolean changed;
					changed = merge(act, e);
					// if merge changed the labelset and the node is not queued,
					// requeue the node
					if (changed && !isLabeled.contains(graph.getTarget(e))) {
						isLabeled.add(graph.getTarget(e));
						labeled.add(graph.getTarget(e));
					}
				}
			}
		}
		meter.stopTimer("ALGORITHM");
		if (logger != null) {
			logger.log(Level.DEBUG, "Algorithm finished. "
					+ labels.getAnnotation(destination).getLabels().size()
					+ " solutions found.");
			logger.log(Level.DEBUG, "Backtracking started");
		}
		// Result stored in labels.getAnnotation(destination);
		meter.startTimer("BACKTRACKING");
		meter.measureInt("PARETO_SIZE", labels.getAnnotation(destination)
				.getLabels().size());
		// backtrack each label of the source node separately
		for (Weight w : labels.getAnnotation(destination).getLabels()) {
			ArrayList<LinkedList<E>> tempsol = recBacktrackLabel(destination,
					w, new LinkedList<E>());
			solutions.addAll(tempsol);
			for (LinkedList<E> sol : tempsol) {
				ArrayList<String> edgestring = new ArrayList<String>();
				for (E e : sol) {
					edgestring.add(e.toString());
				}
				meter.measurePareto(w.getWeights(), edgestring);
			}

		}
		meter.stopTimer("BACKTRACKING");

		WeightedEdgesCalculator<N, E, G> calc = new WeightedEdgesCalculator<N, E, G>(
				weights);
		ParetoFront<N, E, G> front = new ParetoFront<N, E, G>(calc);
		for (LinkedList<E> solution : solutions) {
			front.add(graph.getSubgraphWithImpliedNodes(solution));
			if (logger != null)
				logger.log(Level.DEBUG, solution.toString());
		}

		meter.measureInt("PATHS_EXTENDED", pathsExtended);

		// meter.endExperiment();
	}

	/**
	 * Merges two labelsets by comparing all labels in the sets
	 *
	 * @param u
	 *            Node whose labelset, extended by the weight of e, is to be
	 *            merged.
	 * @param e
	 *            Edge whose weight is added to the labels of the labelset of u.
	 *            The labelset of its direction is the other labelset to be
	 *            merged.
	 * @return A boolean that indicates if the labelset of the direction of e
	 *         has been changed.
	 */
	boolean simpleMerge(N u, E e) {
		List<Weight> x = LabelSet.add(labels.getAnnotation(u),
				weights.getAnnotation(e)).getLabels();
		List<Weight> y = labels.getAnnotation(graph.getTarget(e)).getLabels();
		HashSet<Weight> isDominated = new HashSet<Weight>();
		for (Weight w : x) {
			for (Weight v : y) {
				switch (w.dominates(v)) {
				case PARETO_SMALLER:
					isDominated.add(v);
					break;
				case PARETO_GREATER:
					isDominated.add(w);
					break;
				case EQUAL:
					isDominated.add(w);
					break;
				case UNCOMPARABLE:
					break;
				}
			}
		}

		LabelSet merged = new LabelSet();
		boolean changed = false;
		for (Weight w : x) {
			if (!isDominated.contains(w)) {
				merged.insertLabel(w);
				changed = true;
			}
		}

		for (Weight w : y) {
			if (!isDominated.contains(w)) {
				merged.insertLabel(w);
			}
		}

		if (changed) {
			labels.setAnnotation(graph.getTarget(e), merged);
		}

		return changed;
	}

	/**
	 * Merges two labelsets, using properties of the labels present. Only usable
	 * for labelsets whose labels have exactly two dimensions.
	 *
	 * @param n
	 *            Node whose labelset, extended by the weight of e, is to be
	 *            merged.
	 * @param e
	 *            Edge whose weight is added to the labels of the labelset of u.
	 *            The labelset of its direction is the other labelset to be
	 *            merged.
	 * @return A boolean that indicates if the labelset of the direction of e
	 *         has been changed.
	 */
	boolean intelligentMerge2d(N n, E e) {
		List<Weight> x = LabelSet.add(labels.getAnnotation(n),
				weights.getAnnotation(e)).getLabels();
		List<Weight> y = labels.getAnnotation(graph.getTarget(e)).clone()
				.getLabels();
		List<Weight> z = new ArrayList<Weight>();
		boolean changed = false;
		int xRemCount = x.size();

		if (y.isEmpty()) {
			z = x;
			changed = true;
		} else if ((x.get(0).getWeight(0) >= y.get(y.size() - 1).getWeight(0) && x
				.get(x.size() - 1).getWeight(1) >= y.get(y.size() - 1)
				.getWeight(1))
				|| (x.get(x.size() - 1).getWeight(1) >= y.get(0).getWeight(1) && x
						.get(0).getWeight(0) >= y.get(0).getWeight(0))) {
			z = y;
		} else {
			while (!x.isEmpty() || !y.isEmpty()) {
				if (x.isEmpty())
					z.add(y.remove(0));
				else if (y.isEmpty())
					z.add(x.remove(0));
				else {
					if (x.get(0).getFirstWeight() < y.get(0).getFirstWeight())
						z.add(x.remove(0));
					else
						z.add(y.remove(0));
				}
			}
			for (int i = 0; i < z.size() - 1; i++) {
				if (z.get(i).getWeight(1) <= z.get(i + 1).getWeight(1)) {
					if (y.contains(z.get(i + 1)))
						changed = true;
					else
						xRemCount--;
					z.remove(z.get(i + 1));
					i--;
				}
			}
			if (xRemCount != 0)
				changed = true;
		}
		labels.setAnnotation(graph.getTarget(e), new LabelSet(z));

		return changed;
	}

	/**
	 * Divide and conquer algorithm for merging two labelsets with more than 2
	 * dimensions
	 *
	 * @param n
	 *            Node whose labelset, extended by the weight of e, is to be
	 *            merged.
	 * @param e
	 *            Edge whose weight is added to the labels of the labelset of u.
	 *            The labelset of its direction is the other labelset to be
	 *            merged.
	 * @return A boolean that indicates if the labelset of the direction of e
	 *         has been changed.
	 */
	boolean intelligentMerge(N n, E e) {
		List<Weight> x = LabelSet.add(labels.getAnnotation(n),
				weights.getAnnotation(e)).getLabels();
		List<Weight> y = labels.getAnnotation(graph.getTarget(e)).clone()
				.getLabels();
		boolean changed = false;
		int dimension = x.get(0).getDimension() - 1;
		List<Weight> z = new ArrayList<Weight>(x);
		if (!y.isEmpty()) {
			z.addAll(y);
			z = recFindMinima(this.sortByDimension(z, dimension));
			// z = recFindMinima(mergeSort(x, y));
		} else {
			z = x;
		}
		if (!z.equals(y)) {
			changed = true;
			labels.setAnnotation(graph.getTarget(e), new LabelSet(z));
		}
		return changed;
	}

	/**
	 * Divide and conquer algorithm for finding the pareto minima of a list of
	 * weights
	 *
	 * @param labels
	 *            the list of weights whose minima are to be found
	 * @return the minima of labels
	 */
	List<Weight> recFindMinima(List<Weight> labels) {
		// End of recursion
		if (labels.size() == 2) {
			List<Weight> retval = new ArrayList<Weight>();
			switch (labels.get(0).dominates(labels.get(1))) {
			case PARETO_SMALLER:
				// labels.remove(1);
				retval = new ArrayList<Weight>(labels.subList(0, 1));
				break;
			case PARETO_GREATER:
				// labels.remove(0);
				retval = new ArrayList<Weight>(labels.subList(1, 2));
				break;
			case EQUAL:
				// labels.remove(1);
				retval = new ArrayList<Weight>(labels.subList(0, 1));
				break;
			case UNCOMPARABLE:
				retval = labels;
				break;
			}
			return retval;
		} else if (labels.size() <= 1) {
			return labels;
		} else {
			// Divide the LabelSet and make a recursive call for each part
			int halve = labels.size() / 2;
			// labels = this.sortByDimension(labels,
			// labels.get(0).getDimension() - 1);
			List<Weight> r = labels.subList(0, halve + 1);
			List<Weight> s = labels.subList(halve + 1, labels.size());

			List<Weight> r_min = recFindMinima(r);
			List<Weight> s_min = recFindMinima(s);

			// Remove labels from s_min, that are dominated by any label in
			// r_min and return all labels in r_min and the remaining ones from
			// s_min
			List<Weight> retval = new ArrayList<Weight>(r_min);
			retval.addAll(recRemoveDominated(r_min, s_min));
			return retval;
		}
	}

	/**
	 * Divide and conquer algorithm for removing the elements in a list of
	 * weights, that are dominated by any weight in a second list of weights
	 *
	 * @param r
	 *            the list of weights, that are potentially dominating a weight
	 *            in s
	 * @param s
	 *            the list of weights whose elements are to be checked for
	 *            domination
	 * @return a list of all weights from s which are not dominated by any
	 *         weight of r
	 */
	List<Weight> recRemoveDominated(List<Weight> r, List<Weight> s) {
		List<Weight> retval = null;
		if (!r.isEmpty()) {
			Weight weight = r.get(0);
			retval = this.recRemoveDominated(r, s, weight.getDimension() - 1);
		} else {
			retval = s;
		}
		return retval;
	}

	/**
	 * Divide and conquer algorithm for removing the elements in a list of
	 * weights, that are dominated by any weight in a second list of weights.
	 *
	 * @param r
	 *            the list of weights that are potentially dominating a weight
	 *            in s
	 * @param s
	 *            the list of weights whose elements are to be checked for
	 *            domination by an element from r
	 * @param dimension
	 *            the dimension on which the algorithm is operating, which
	 *            correlates to the recursion depth
	 * @return a list of weights containing all elements from s which are not
	 *         dominated by any elements of r
	 */
	List<Weight> recRemoveDominated(List<Weight> r, List<Weight> s,
			int dimension) {
		ArrayList<Weight> retval = new ArrayList<Weight>();
		if (s.size() == 0) {
			// Do nothing
		} else if (r.size() == 0) {
			retval.addAll(s);
		} else if (s.size() == 1) {
			/*
			 * If there is only one element in s, compare it with all elements
			 * in r trivially.
			 */
			boolean isDominated = false;
			for (Weight w : r) {
				if (this.compareProjections(w, s.get(0), dimension) == 1) {
					isDominated = true;
				}
			}
			if (isDominated == false) {
				retval.addAll(s);
			}
		} else if (r.size() == 1) {
			/*
			 * If there is only one element in r, compare it with all element in
			 * s trivially.
			 */
			for (Weight w : s) {
				if (this.compareProjections(r.get(0), w, dimension) != 1) {
					retval.add(w);
				}
			}
		} else if (dimension == 2) {
			/*
			 * This is the recursion end described in kung[75] algorithm 5.1
			 * currently this is dead code, because i search for bugs in the
			 * rest of the code. Usually this gets called with dimension <= 2
			 */
			AVLTree t = new AVLTree();
			int indexR = 0;
			int indexS = 0;
			Weight weightR;
			Weight weightS;
			while (indexR < r.size() && indexS < s.size()) {
				weightR = r.get(indexR);
				weightS = s.get(indexS);
				if (weightR.getWeight(2) <= weightS.getWeight(2)) {
					/*
					 * if weightR's competitive dimension is smaller then the
					 * one from weightS, weightR could dominate weightS in the
					 * first 3 components. So it has to be inserted to t before
					 * weightS is considered.
					 */
					t.insert(weightR);
					indexR++;
				} else {
					if (!t.containsDominator(weightS)) {
						retval.add(weightS);
					}
					indexS++;
				}
			}
			while (indexS < s.size()) {
				weightS = s.get(indexS);
				if (!t.containsDominator(weightS)) {
					retval.add(weightS);
				}
				indexS++;
			}
		} else if (dimension <= 1) {
			/*
			 * This is a simple recursion end. Dead code in theory, but you
			 * never now.
			 */
			for (Weight i : s) {
				boolean isDominated = false;
				for (Weight j : r) {
					if (this.compareProjections(j, i, dimension) == 1) {
						isDominated = true;
					}
				}
				if (!isDominated) {
					retval.add(i);
				}
			}
		} else {
			/*
			 * This is the division of the recursion described in kung[75] At
			 * this point, each r and s contain at least 2 elements and have to
			 * be compared in more than three dimensions.
			 */
			List<Weight> sortedR = r; // this.sortByDimension(r, dimension);
			List<Weight> sortedS = s; // this.sortByDimension(s, dimension);
			List<Weight> r1;
			List<Weight> r2;
			List<Weight> s1 = sortedS.subList(0, sortedS.size() / 2);
			List<Weight> s2 = sortedS.subList(sortedS.size() / 2,
					sortedS.size());
			double threshold = s2.get(0).getWeight(dimension);
			int index = this.findWeightInList(sortedR, threshold, dimension);
			r1 = sortedR.subList(0, index);
			r2 = sortedR.subList(index, sortedR.size());

			for (Weight w : recRemoveDominated(r1, s1, dimension)) {
			//	if (!retval.contains(w)) {
			//		retval.add(w);
			//	}
				this.insertIntoList(retval, w, dimension);
			}
			List<Weight> temp = recRemoveDominated(r2, s2, dimension);
			for (Weight w : recRemoveDominated(
					this.sortByDimension(r1, dimension - 1),
					this.sortByDimension(s2, dimension - 1), dimension - 1)) {
				//if (!retval.contains(w) && temp.contains(w)) {
				//	retval.add(w);
				//}
				if(temp.contains(w)) {
					retval.add(w);
				}
			}
		}
		return retval;
	}

	/**
	 * returns a new List of vectors, which consists of the elements of the list
	 * from the parameters, but is sorted ascending by the component given by
	 * the parameter dimension
	 *
	 * @param list
	 *            a list of elements to be sorted
	 * @param dimension
	 *            the component by which the list is to be sorted
	 * @return sorted list
	 */
	List<Weight> sortByDimension(List<Weight> list, int dimension) {
		if (list.size() <= 1)
			return list;
		boolean sideToggle = false;
		boolean isSorted = true;
		List<Weight> retval = new ArrayList<Weight>();
		List<Weight> left = new ArrayList<Weight>();
		List<Weight> right = new ArrayList<Weight>();
		double lastWeight = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getWeight(dimension) < lastWeight) {
				sideToggle = !sideToggle;
				isSorted = false;
			}
			if (sideToggle) {
				left.add(list.get(i));
			} else {
				right.add(list.get(i));
			}
			lastWeight = list.get(i).getWeight(dimension);
		}
		if (isSorted)
			return list;
		left = this.sortByDimension(left, dimension);
		right = this.sortByDimension(right, dimension);
		int l = 0, r = 0;
		Weight lw = null, rw = null;
		while (l < left.size() && r < right.size()) {
			lw = left.get(l);
			rw = right.get(r);
			if (lw.getWeight(dimension) <= rw.getWeight(dimension)) {
				retval.add(lw);
				l++;
			} else {
				retval.add(rw);
				r++;
			}
		}
		while (l < left.size()) {
			retval.add(left.get(l));
			l++;
		}
		while (r < right.size()) {
			retval.add(right.get(r));
			r++;
		}
		return retval;
	}

	/**
	 * inserts an element into a list, containing ascending order, or does
	 * nothing, if the weight is already in the list.
	 *
	 * @param list
	 *            the list in which the weight is to be inserted to
	 * @param w
	 *            the weight to be inserted
	 * @param dimension
	 *            the dimension for which the list is sorted
	 */
	void insertIntoList(List<Weight> list, Weight w, int dimension) {
		int index = 0;
		if (list.size() > 1) {
			index = this.findWeightInList(list, w.getWeight(dimension),
					dimension);
		} else if (list.isEmpty()) {
			index = 0;
		} else {
			// list contains exactly one element
			if (list.get(0).getWeight(dimension) > w.getWeight(dimension)) {
				index = 0;
			} else {
				index = 1;
			}
		}
		boolean isContained = false;
		int i = 0;
		while (index+i < list.size() && list.get(index + i).getWeight(dimension) == w
				.getWeight(dimension)) {
			if (list.get(index + i).getWeights().equals(w.getWeights())) {
				isContained = true;
			}
			i++;
		}
		if (!isContained) {
			list.add(index, w);
		}
	}

	/**
	 * for given list sorted ascending, returns the index of the first weight
	 * for which the weight is not less than the threshold value.
	 *
	 * @param list
	 *            the list to be scanned
	 * @param threshold
	 *            the threshold for which to be scanned
	 * @param dimension
	 *            the dimension for which the list is sorted
	 * @return index of the first element for which the weight is not less than
	 *         the threshold value.
	 */
	int findWeightInList(List<Weight> list, double threshold, int dimension) {
		int b = 0;
		int left = 0;
		int right = list.size();
		while (right - left > 1) {
			b = (left + right) / 2;
			if (list.get(b).getWeight(dimension) < threshold) {
				left = b;
			} else {
				right = b;
			}
		}
		if (list.get(left).getWeight(dimension) < threshold) {
			return right;
		} else {
			return left;
		}
	}

	/**
	 * compares two vectors in their first d components
	 *
	 * @param x
	 *            first vector
	 * @param y
	 *            second vector
	 * @param d
	 *            amount of dimensions to be projected off
	 * @return 0 if none of the vector dominated, 1 if x is smaller than y, -1
	 *         if y is smaller than x
	 */
	int compareProjections(Weight x, Weight y, int d) {
		boolean xIsGreater = true;
		boolean xIsSmaller = true;
		assert (x.getDimension() == y.getDimension());
		assert (d < x.getDimension());
		for (int i = 0; i <= d; i++) {
			if (x.getWeight(i) < y.getWeight(i)) {
				xIsGreater = false;
			} else if (x.getWeight(i) > y.getWeight(i)) {
				xIsSmaller = false;
			}
		}
		if (xIsGreater == xIsSmaller) {
			/*
			 * This also handles the case of two equal vectors, where both
			 * values would be true
			 */
			return 0;
		} else if (xIsSmaller == true && xIsGreater == false) {
			return 1;
		} else {
			/*
			 * The only case left is y being dominated by x, or xIsSmaller ==
			 * false and xIsGreater == true
			 */
			return -1;
		}
	}

	/**
	 * given two lists of weights that are sorted ascending in the first
	 * dimension computes a sorted list containing the elements of both lists
	 *
	 * @param x
	 *            the first list of weights
	 * @param y
	 *            the second list of weights
	 * @return a list containing all the elements from x and y, sorted ascending
	 *         by the first argument of each weight
	 */
	List<Weight> mergeSort(List<Weight> x, List<Weight> y) {
		List<Weight> z = new ArrayList<Weight>();
		int dimension = x.get(0).getDimension() - 1;
		while (!x.isEmpty() || !y.isEmpty()) {
			if (x.isEmpty())
				z.add(y.remove(0));
			else if (y.isEmpty())
				z.add(x.remove(0));
			else {
				if (x.get(0).getFirstWeight() < y.get(0).getFirstWeight())
					z.add(x.remove(0));
				else
					z.add(y.remove(0));
			}
		}
		return z;
	}

	boolean merge(N n, E e) {
		if (!intMerge) // intelligentMerge toggled off
			return simpleMerge(n, e);
		else {
			// intelligentMerge toggled on
			if (dimension == 2)
				return intelligentMerge2d(n, e);
			else {
				return intelligentMerge(n, e);
			}
		}
	}

	/**
	 * Recursive procedure to find the path that corresponds to a given label.
	 *
	 * @param n
	 *            The node at which the path will end
	 * @param w
	 *            The label whose corresponding path is to be found.
	 * @param subPath
	 *            A path from the destination node to node n
	 * @return
	 */
	ArrayList<LinkedList<E>> recBacktrackLabel(N n, Weight w,
			LinkedList<E> subPath) {
		ArrayList<LinkedList<E>> retval = new ArrayList<LinkedList<E>>();

		if (n.equals(source))
			retval.add(subPath);
		else {
			for (E e : this.graph.getIncomingEdges(n)) {
				Weight tempWeight = Weight
						.add(w,
								Weight.scalarProduct(-1,
										this.weights.getAnnotation(e)));
				if (labels.getAnnotation(this.graph.getSource(e)).getLabels()
						.contains(tempWeight)) {

					@SuppressWarnings("unchecked")
					LinkedList<E> extSubPath = (LinkedList<E>) subPath.clone();
					extSubPath.addFirst(e);
					retval.addAll(recBacktrackLabel(this.graph.getSource(e),
							tempWeight, extSubPath));
				}
			}
		}
		return retval;
	}

	public GraphElementHashAnnotator<N, LabelSet> getLabels() {
		return labels;
	}

	public ArrayList<LinkedList<E>> getSolutions() {
		return solutions;
	}

	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir) throws Exception{
		assert (job != null);
		// Get AnnotatedGraph from Job
		Object inputGraph = job.getInputGraph();
		assert (inputGraph != null);
		// Get Parameters
		Map<String, Object> params = job.getParameters();
		assert (params != null);
		// Start execution
		this.execute(inputGraph, params, meter, serviceDir, job);
	}
}
