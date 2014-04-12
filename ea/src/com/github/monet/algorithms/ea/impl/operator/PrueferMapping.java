package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.github.monet.algorithms.ea.impl.individual.MSTPhenotype;
import com.github.monet.algorithms.ea.impl.individual.PrueferEncoding;
import com.github.monet.algorithms.ea.impl.individual.PrueferGenotype;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.individual.Phenotype;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.interfaces.UndirectedGraph;
import com.github.monet.graph.weighted.Weight;

/**
 * Converts a PrueferGenotype into a PhenotypeMST.
 *
 * GraphElementAnnotator -> <Graphelement,GraphAnnotation>
 * Generic: PrueferMapping<GE extends GraphElement, GA extends GraphElementAnnotation, N extends Node, E extends UndirectedEdge>
 * Specific: PrueferMapping<GE extends UndirectedEdge, GA extends Weight, N extends Node, E extends UndirectedEdge>
 *
 * @author Sven Selmke
 *
 * @param <GE> Annotated Graph Element
 * @param <GA> Graph Element Annotation
 * @param <N> Graph-Nodes
 * @param <E> Graph-Edges
 */
public class PrueferMapping<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends PhenotypeMapping {

	// The GPM needs information about the "problem"-graph, so we store it here
	//private AnnotatedGraph<GE, GA, N, E> problemAnnotatedGraph; // private UndirectedGraph<?,?> problemGraph;
	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
	// Weight Annotator and Graph of the problem Graph for easy access
	private Graph<N, E, G> problemGraph;
	private GraphElementAnnotator<E, Weight> weightAnnotator;
	private int numObjectives;
	// Mapping node IDs to Node objects of the problem Graph
	private HashMap<Integer, Node> idNodeMap;


	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> params) {

		// Get Parameters
		this.problemAnnotatedGraph = Functions.getParam(params, "problemGraph", AnnotatedGraph.class, null);
		this.problemGraph          = problemAnnotatedGraph.getGraph();
		this.weightAnnotator       = this.problemAnnotatedGraph.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class);
		this.idNodeMap             = Functions.getParam(params, "idNodeMap", HashMap.class, null);
		this.numObjectives         = Functions.getParam(params, "numObjectives", Integer.class, 0);

		if (this.numObjectives == 0 || !(this.problemAnnotatedGraph.getGraph() instanceof UndirectedGraph) || this.weightAnnotator == null) {
			return false;
		}
		return true;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Phenotype createPhenotype(Genotype g) {

		// Prufer-Number to Graph
		//  1. Prufer-Number P, Set P' of unused Node IDs. j = Smallest Element in P', k = first Element in P.
		//  2. Create Edge {j,k} and remove j and k from their respective sets
		//  3. k not in P => add k to P'.
		//  4. Repeat until P empty. Create Edge for last two elements in P'
		if (g instanceof PrueferGenotype) {
			boolean phenotypeIsValid  = true;
			int nonExistingEdgeCount  = 0;
			List<Edge> phenotypeEdges = new ArrayList<Edge>();
			Weight totalWeight        = new Weight(new double[numObjectives]);

			// Get Pruefer-Number from genotype
			PrueferGenotype genotype = (PrueferGenotype)g;
			List<Integer> prueferNumber = genotype.getValue();

			// Get unused numbers
			List<Integer> unused = new ArrayList<Integer>(((PrueferEncoding)genotype.getEncoding()).getValidSymbols());
			unused.removeAll(prueferNumber);
			PriorityQueue<Integer> unusedQueue = new PriorityQueue<Integer>(unused);

			// Iterate over Pruefer-Number
			for (int i = 0; i < prueferNumber.size(); i++) {
				Integer k = prueferNumber.get(i);
				Integer j = unusedQueue.poll();
				Node nodeK = this.idNodeMap.get(k);
				Node nodeJ = this.idNodeMap.get(j);

				// Create edge j-k
				Edge e = this.problemGraph.getEdge((N)nodeK, (N)nodeJ);
				if (e != null) {
					phenotypeEdges.add(e);
					try { totalWeight.add( ((Weight)this.weightAnnotator.getAnnotation((E)e)) ); } catch(Exception error) {}
				} else {
					nonExistingEdgeCount++;
					phenotypeIsValid = false;
					Weight infty = new Weight(Functions.createDoubleArray(numObjectives,Double.MAX_VALUE));
					try { totalWeight.add(infty); } catch(Exception error) {}
				}

				// If k is not in P anymore, add it to P' (Step 3)
				boolean found = false;
				for (int x = i+1; x < prueferNumber.size(); x++) {
					if (prueferNumber.get(x).equals(k)) {
						found = true;
						break;
					}
				}
				if (!found) {
					unusedQueue.add(k);
				}
			}

			// Create edge for last two elements in P'
			assert(unusedQueue.size() == 2);
			Integer last1 = unusedQueue.poll();
			Integer last2 = unusedQueue.poll();
			Node nodeLast1 = this.idNodeMap.get(last1);
			Node nodeLast2 = this.idNodeMap.get(last2);
			// Create edge last1-last2
			Edge e = this.problemGraph.getEdge((N)nodeLast1, (N)nodeLast2);
			if (e != null) {
				phenotypeEdges.add(e);
				try { totalWeight.add( ((Weight)this.weightAnnotator.getAnnotation((E)e)) ); } catch(Exception error) {}
			} else {
				nonExistingEdgeCount++;
				phenotypeIsValid = false;
				Weight infty = new Weight(Functions.createDoubleArray(numObjectives,Double.MAX_VALUE));
				try { totalWeight.add(infty); } catch(Exception error) {}
			}

			// Create and return phenotype
			MSTPhenotype result = new MSTPhenotype();
			result.setValid(phenotypeIsValid);
			result.setEdges(phenotypeEdges);
			result.setObjectiveValues( Functions.weightToDoubleArray(totalWeight) );
			if (nonExistingEdgeCount > 0) {
				//Functions.log("Pruefer-Mapping: " + nonExistingEdgeCount + " edges do not exist.", Functions.LOG_PRINT);
				result.setMissingEdges(nonExistingEdgeCount);
			} else {
				assert(phenotypeEdges.size() == this.problemGraph.getNumNodes()-1);
			}
			return result;
		}

		Functions.log("Pruefer-Mapping: Given Genotype not supported!", Functions.LOG_ERROR);
		return null;
	}


	@Override
	public String getName() {
		return "Pruefer-GPM";
	}

}
