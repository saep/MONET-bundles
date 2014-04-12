import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Main {
	
	public static void main(String args[]) {
		generateGraph(50, 8, 20, 5, 25, 3, "./graphExample.txt");
	}
	
	/**
	 * Used to create a simple Graph, which at first creates a spanning tree on
	 * the given number of nodes and then adds the edges of (non-complete) matchings
	 * @param nodes 
	 * 			the number of nodes to be contained
	 * @param matchings
	 * 			the number of matchings inserted to the graph 
	 * @param matchingSize
	 * 			the number of edges of each matching inserted to the graph
	 * @param pathName
	 * 			the pathname of the graph data which is to be created
	 * @param minWeight
	 * 			the minimum weight occuring as a weight of the created graph
	 * @param maxWeight
	 * 			the maximum weight occuring as a weight of the created graph
	 * @param parameters
	 * 			the number of weights each edge holds
	 */
	public static void generateGraph(int nodes, int matchings, int matchingSize, int minWeight, int maxWeight, int parameters, String pathName) {
		ArrayList<Node> nodeSet = new ArrayList<Node>();
		ArrayList<Edge> edgeSet = new ArrayList<Edge>();

		ArrayList<Node> nodeSet1 = new ArrayList<Node>();
		ArrayList<Node> nodeSet2 = new ArrayList<Node>();
		
		for(int i=1; i<=nodes; i++) {
			Node u = new Node(i);
			nodeSet.add(u);
			if(i == 1)
				nodeSet1.add(u);
			else
				nodeSet2.add(u);
		}
		
		/*
		 * create a random spanning tree on the nodes.
		 * This is realized by putting all but one node
		 * into the second list, and only that one node
		 * into the first list. Then, until the second list
		 * is empty, connect a random node from the first
		 * list with a random node from the second list,
		 * which then will be removed from the second list
		 * and added to the first list. This way, the nodes
		 * in the first list always are connected via MST,
		 * therefore as soon as the second list is empty,
		 * the edgeSet represents a coherent graph 
		 */
		while(!nodeSet2.isEmpty()) {
			Node u = nodeSet1.get((int)(Math.random() * (nodeSet1.size()-1)));
			Node v = nodeSet2.get((int)(Math.random() * (nodeSet2.size()-1)));
			
			nodeSet2.remove(v);
			nodeSet1.add(v);

			if(!u.equals(v)) {
				edgeSet.add(new Edge(u, v));
			}
		}
		
		/*
		 * now create a number of matchings containing <matchingSize>
		 * edges. this way i expect to create a graph containing many
		 * different spanning trees.
		 */
		for(int createdMatchings = 0; createdMatchings < matchings; createdMatchings++) {
			nodeSet1.removeAll(nodeSet1);
			nodeSet2.addAll(nodeSet);
			for(int addedEdges = 0; addedEdges < matchingSize; addedEdges++) {
				Node u = nodeSet2.get((int)(Math.random() * (nodeSet2.size()-1)));
				nodeSet2.remove(u);
				nodeSet1.add(u);
				
				Node v = nodeSet2.get((int)(Math.random() * (nodeSet2.size()-1)));
				nodeSet2.remove(v);
				nodeSet1.add(v);
				
				if(!u.equals(v)) {
					edgeSet.add(new Edge(u, v));
				}
			}
		}
		
		/*
		 * write the graph described by edgeSet into a file
		 */
		FileOutputStream stream = null;
		PrintWriter out = null;
		File newFile = null;
		try {
			newFile = new File(pathName);
			stream = new FileOutputStream(newFile);
			out = new PrintWriter(stream, false);
			String eol = System.getProperty("line.separator");
			out.write("" + nodes + eol);
			out.write("" + edgeSet.size() + eol);
			out.write("" + parameters + eol);
			String nextLine = "";
			for(Edge e: edgeSet) {
				nextLine = "" + e.getSource() + " " + e.getSink() + "";
				for(int p = 0; p < parameters; p++) {
					nextLine += " " + getRandomIntegerWeight(minWeight, maxWeight);
				}
				out.write(nextLine + eol);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Creation of Graph failed");
		} finally {
			if(out != null)
				out.close();
		}
	}
	
	private static int getRandomIntegerWeight(int minWeight, int maxWeight) {
		return minWeight + (int)(Math.random() * (maxWeight - minWeight));
	}
}
