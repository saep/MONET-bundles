package com.github.monet.algorithms.ea.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.monet.algorithms.ea.algorithm.EvolutionaryAlgorithm;
import com.github.monet.algorithms.ea.operator.Creator;
import com.github.monet.algorithms.ea.operator.Evaluator;
import com.github.monet.algorithms.ea.operator.Mutator;
import com.github.monet.algorithms.ea.operator.PhenotypeMapping;
import com.github.monet.algorithms.ea.operator.Recombinator;
import com.github.monet.algorithms.ea.operator.Selector;
import com.github.monet.algorithms.ea.operator.Terminator;
import com.github.monet.graph.interfaces.Edge;
import com.github.monet.graph.interfaces.GraphElementAnnotator;
import com.github.monet.graph.interfaces.Node;
import com.github.monet.graph.weighted.Weight;
import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;


/**
 * Class for storing all information about the given problem that are needed by
 * different algorithms and operators. Storing all the information in a single
 * object eases the configuration of operators.
 *
 * @author Sven Selmke
 *
 */
public class Configuration {

	// General information
	private String expName;
	private Map<String,Object> params;
	private Job job;
	private Meter meter;
	private ServiceDirectory serviceDir;

	// Information about selected operators
	private List<Nameable> operatorList = new ArrayList<Nameable>();
	private EvolutionaryAlgorithm algorithm;
	private Creator creator;
	private Mutator mutator;
	private Recombinator recombinator;
	private PhenotypeMapping mapping;
	private Evaluator evaluator;
	private Selector selector;
	private Terminator terminator;

	// Information about the graph
	private Object annotatedGraph;
	private GraphElementAnnotator<Edge, Weight> weightAnnotator;
	private boolean isGraphComplete;
	private int numNodes;
	private int numEdges;
	private int dim;
	private HashMap<Integer, Node> idNodeMap;
	private HashMap<Node, Integer> nodeIdMap;

	// Problem specific information
	private Node startNode;
	private Node endNode;

	// get a String representation
	public String toString() {
		String result = "";
		result  = "Algorithm Configuration:\n";
		result += "  - Algorithm: " + this.algorithm + "\n";
		result += "  - Problem: " + this.numNodes + " Nodes, " + this.numEdges + " Edges, " + this.dim + " Dimensions" + "\n";
		if (this.startNode != null || this.endNode != null)
			result += "  - Start-Node: " + this.startNode + ", Endnode " + this.endNode + "\n";
		if (this.expName != null)
			result += "  - Name of the Configuration: " + this.expName + "\n";
		return result;
	}

	// Getters and Setters for all attributes
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public Meter getMeter() {
		return meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}
	public int getNumNodes() {
		return numNodes;
	}
	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}
	public int getNumEdges() {
		return numEdges;
	}
	public void setNumEdges(int numEdges) {
		this.numEdges = numEdges;
	}
	public int getDim() {
		return dim;
	}
	public void setDim(int dim) {
		this.dim = dim;
	}
	public HashMap<Integer, Node> getIdNodeMap() {
		return idNodeMap;
	}
	public void setIdNodeMap(HashMap<Integer, Node> idNodeMap) {
		this.idNodeMap = idNodeMap;
	}
	public HashMap<Node, Integer> getNodeIdMap() {
		return nodeIdMap;
	}
	public void setNodeIdMap(HashMap<Node, Integer> nodeIdMap) {
		this.nodeIdMap = nodeIdMap;
	}
	public ServiceDirectory getServiceDir() {
		return serviceDir;
	}
	public void setServiceDir(ServiceDirectory serviceDir) {
		this.serviceDir = serviceDir;
	}
	public List<Nameable> getOperatorList() {
		return operatorList;
	}
	public void setOperatorList(List<Nameable> operatorList) {
		this.operatorList = operatorList;
	}
	public Creator getCreator() {
		return creator;
	}
	public void setCreator(Creator creator) {
		this.creator = creator;
	}
	public Mutator getMutator() {
		return mutator;
	}
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}
	public Recombinator getRecombinator() {
		return recombinator;
	}
	public void setRecombinator(Recombinator recombinator) {
		this.recombinator = recombinator;
	}
	public PhenotypeMapping getMapping() {
		return mapping;
	}
	public void setMapping(PhenotypeMapping mapping) {
		this.mapping = mapping;
	}
	public Evaluator getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	public Selector getSelector() {
		return selector;
	}
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	public Terminator getTerminator() {
		return terminator;
	}
	public void setTerminator(Terminator terminator) {
		this.terminator = terminator;
	}
	public Object getAnnotatedGraph() {
		return annotatedGraph;
	}
	public void setAnnotatedGraph(Object annotatedGraph) {
		this.annotatedGraph = annotatedGraph;
	}
	public EvolutionaryAlgorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(EvolutionaryAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	public GraphElementAnnotator<Edge, Weight> getWeightAnnotator() {
		return weightAnnotator;
	}
	public void setWeightAnnotator(GraphElementAnnotator<Edge, Weight> weightAnnotator) {
		this.weightAnnotator = weightAnnotator;
	}
	public boolean isGraphComplete() {
		return isGraphComplete;
	}
	public void setGraphComplete(boolean isGraphComplete) {
		this.isGraphComplete = isGraphComplete;
	}
	public Node getStartNode() {
		return startNode;
	}
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	public Node getEndNode() {
		return endNode;
	}
	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
	public String getExpName() {
		return expName;
	}
	public void setExpName(String expName) {
		this.expName = expName;
	}

}
