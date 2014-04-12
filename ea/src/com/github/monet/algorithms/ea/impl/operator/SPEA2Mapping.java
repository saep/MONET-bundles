package com.github.monet.algorithms.ea.impl.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.impl.individual.STSPGenotype;
import com.github.monet.algorithms.ea.impl.individual.STSPPhenotype;
import com.github.monet.algorithms.ea.individual.Genotype;
import com.github.monet.algorithms.ea.individual.Phenotype;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.util.Functions;

import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.Graph;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.Weight;


/**
 * Converts a SPEA2Genotype into a PhenotypeSSSP.
 *
 * @author Andreas Pauly, Sven Selmke
 *
 * @param <GE> Annotated Graph Element
 * @param <GA> Graph Element Annotation
 * @param <N> Graph-Nodes
 * @param <E> Graph-Edges
 */
public class SPEA2Mapping<N extends Node, E extends Edge, G extends Graph<N, E, G>> extends PhenotypeMapping {

	private AnnotatedGraph<N, E, G> problemAnnotatedGraph;
    private Graph<N, E, G> problemGraph;
    private GraphElementAnnotator<E, Weight> weightAnnotator;
    private int numObjectives;

	/**
	 * Set parameters for the algorithm.
	 *
	 * @param parameters
	 *            parameters used to configure the mapping
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Map<String,Object> parameters) {
		this.problemAnnotatedGraph = Functions.getParam(parameters, "problemGraph", AnnotatedGraph.class, null);
		this.problemGraph          = problemAnnotatedGraph.getGraph();
		this.weightAnnotator       = this.problemAnnotatedGraph.getAnnotator(Functions.PARAM_EDGEANNOTATOR, GraphElementAnnotator.class);
		this.numObjectives         = Functions.getParam(parameters, "numObjectives", Integer.class, 0);
		return true;
	}

	@Override
	public String getName() {
		return "SPEA2-GPM";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Phenotype createPhenotype(Genotype g) {

		if (g instanceof STSPGenotype) {
			List<Edge> phenotypeEdges = new ArrayList<Edge>();
			STSPGenotype genotype    = (STSPGenotype)g;
			List<Node> nodes          = genotype.getNodes();
			Weight totalWeight        = new Weight(new double[numObjectives]);

			// iterate over nodes and get edges
			for (int i = 0; i < nodes.size()-1; i++) {
				Node n1 = nodes.get(i);
				Node n2 = nodes.get(i+1);
				Edge e  = problemGraph.getEdge((N)n1, (N)n2);
				phenotypeEdges.add(e);
				try { totalWeight.add( ((Weight)this.weightAnnotator.getAnnotation((E)e)) ); } catch(Exception error) {}
			}

			// Create and return phenotype
			STSPPhenotype result = new STSPPhenotype();
			result.setValid(true);
			result.setEdges(phenotypeEdges);
			result.setObjectiveValues( Functions.weightToDoubleArray(totalWeight) );
			return result;
		}

		Functions.log("SPEA2-Mapping: Given Genotype not supported!", Functions.LOG_ERROR);
		return null;
	}

}
