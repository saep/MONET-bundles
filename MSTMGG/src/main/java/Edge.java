
	public class Edge {
		Node u, v;
		public Edge(Node u, Node v) {
			this.u = u;
			this.v = v;
		}
		public boolean isIncident(Node u) {
			return (this.u.equals(u) || this.v.equals(u));
		}
		public int getSource() {
			return u.getId();
		}
		public int getSink() {
			return v.getId();
		}
	};