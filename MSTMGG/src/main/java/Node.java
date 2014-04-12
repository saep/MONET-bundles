public class Node {
	int id;
	public Node(int id) {
		this.id = id;
	}
	public int getId() {
		return this.id;
	}
	public boolean equals(Node v) {
		return this.id == v.getId(); 
	}
};
