package com.github.monet.algorithms.sssp;

import com.github.monet.graph.weighted.Weight;

/**
 * Important note: This AVL-Tree only works on the first two dimensions of the
 * weights put in. Also checks if all weights put in are of same dimension
 *
 * @author Davdav
 *
 */
public class AVLTree {
	private AVLTree left, right;
	private Node root;

	public AVLTree(Weight w) {
		this.root = new Node(w);
		this.left = null;
		this.right = null;
	}

	public AVLTree() {
		this.root = new Node(null);
		this.left = null;
		this.right = null;
	}

	public boolean isBalanced() {
		if (left == null && right == null) {
			return true;
		} else if (this.left == null) {
			return this.right.height() <= 1;
		} else if (this.right == null) {
			return this.left.height() <= 1;
		} else {
			return Math.abs(left.height() - right.height()) <= 1;
		}
	}

	public boolean recIsBalanced() {
		if (left == null && right == null) {
			return true;
		} else if (this.left == null) {
			return this.right.recIsBalanced();
		} else if (this.right == null) {
			return this.left.recIsBalanced();
		} else {
			return (this.isBalanced() && this.right.recIsBalanced() && this.left
					.recIsBalanced());
		}
	}

	public int balance() {
		if (left == null && right == null) {
			return 0;
		} else if (this.left == null) {
			return this.right.height();
		} else if (this.right == null) {
			return this.left.height() * (-1);
		} else {
			return this.right.height() - this.left.height();
		}
	}

	public void insert(Weight w) {
		if (this.isEmpty()) {
			this.setRoot(new Node(w));
		} else if (this.root.belongsLeft(w)) {
			assert (w.getDimension() == this.root.getContent().getDimension());
			if (this.left == null) {
				this.left = new AVLTree(w);
			} else {
				this.left.insert(w);
				if (!this.isBalanced()) {
					if (this.left.balance() > 0) {
						this.left.rotateLeft();
					}
					this.rotateRight();
				}
			}
		} else if (this.root.belongsRight(w)) {
			assert (w.getDimension() == this.root.getContent().getDimension());
			if (this.right == null) {
				this.right = new AVLTree(w);
			} else {
				this.right.insert(w);
				if (!this.isBalanced()) {
					if (this.right.balance() < 0) {
						this.right.rotateRight();
					}
					this.rotateLeft();
				}
			}
		}
	}

	/**
	 * removes a Node from the tree which contains a weight which equals w.
	 *
	 * @param w
	 *            the weight to be removed from the tree.
	 */
	public void remove(Weight w) {
		if (!this.isEmpty()) {
			assert (w.getDimension() == this.root.getContent().getDimension());
			if (this.root.contentEquals(w)) {
				/*
				 * Get maximum of left subtree, set it as new root, if left tree
				 * is empty, get minimum of right tree.
				 */
				Weight newRootWeight = null;
				if (left != null) {
					newRootWeight = left.getMaximum();
					left.remove(newRootWeight);
					if (left.isEmpty()) {
						left = null;
					}
				} else if (right != null) {
					newRootWeight = right.getMaximum();
					right.remove(newRootWeight);
					if (right.isEmpty()) {
						right = null;
					}
				}
				this.root.setContent(newRootWeight);
			} else if (this.root.belongsLeft(w)) {
				if (left != null) {
					left.remove(w);
				}
			} else if (this.root.belongsRight(w)) {
				if (right != null) {
					right.remove(w);
				}
			}
			// TODO: rebalance, maybe. This method is not called at any time in
			// this algorithm.
		}
	}

	/**
	 * gets the Weight-object which is concidered a maximum concerning the
	 * second dimension
	 *
	 * @return the weight-object which is concidered a maximum concerning the
	 *         second dimension
	 */
	public Weight getMaximum() {
		if (right != null) {
			return this.right.getMaximum();
		} else if (left != null) {
			Weight l = left.getMaximum();
			if (l.getWeight(1) == this.root.getWeight(1)) {
				return l;
			} else {
				return this.root.getContent();
			}
		} else {
			return this.root.getContent();
		}
	}

	/**
	 * gets the Weight-object which is concidered a minimum concerning the
	 * second-last dimension
	 *
	 * @return the weight-object which is concidered a minimum concerning the
	 *         second-last dimension
	 */
	public Weight getMinimum() {
		if (left != null) {
			return this.left.getMinimum();
		} else {
			return this.root.getContent();
		}
	}

	/**
	 * indicates wether the Tree contains a Weight dominating comp in its last
	 * two components or not.
	 *
	 * @param comp
	 *            the vector which is checked for dominance
	 * @return true if at least one weight in the avl-tree is really smaller
	 *         than comp in its last two components.
	 */
	public boolean containsDominator(Weight comp) {
		boolean retval = false;
		if (this.isEmpty())
			return false;
		assert (this.root.getContent().getDimension() == comp.getDimension());
		/*
		 * Always check the root
		 */
		if (this.root.dominates(comp)) {
			retval = true;
		}
		/*
		 * Always check in left subtree
		 */
		if (!retval && left != null) {
			if (left.containsDominator(comp)) {
				retval = true;
			}
		}
		/*
		 * Only check in right subtree if comp belongs right to the root. If
		 * comp belongs left to the root, the right subtree only contains
		 * weights which are greater then comp in its significant component.
		 */
		if (!retval && this.root.belongsRight(comp)) {
			if (right != null) {
				if (right.containsDominator(comp)) {
					retval = true;
				}
			}
		}
		return retval;
	}

	public int height() {
		if (left == null && right == null) {
			return 1;
		} else if (left == null) {
			return right.height() + 1;
		} else if (right == null) {
			return left.height() + 1;
		} else {
			return Math.max(left.height(), right.height()) + 1;
		}
	}

	public Node getRoot() {
		return this.root;
	}

	public void rotateLeft() {
		if (this.right != null) {
			AVLTree tempLeft = new AVLTree(this.root.getContent());
			tempLeft.setLeft(this.left);
			tempLeft.setRight(this.right.getLeft());
			this.setRoot(this.right.getRoot().getContent());
			this.setLeft(tempLeft);
			this.setRight(this.right.getRight());
		}
	}

	public void rotateRight() {
		if (this.left != null) {
			AVLTree tempRight = new AVLTree(this.root.getContent());
			tempRight.setRight(this.right);
			tempRight.setLeft(this.left.getRight());
			this.setRoot(this.left.getRoot().getContent());
			this.setRight(tempRight);
			this.setLeft(this.left.getLeft());
		}
	}

	public AVLTree getLeft() {
		return this.left;
	}

	public void setLeft(AVLTree t) {
		this.left = t;
	}

	public AVLTree getRight() {
		return this.right;
	}

	public void setRight(AVLTree t) {
		this.right = t;
	}

	public void setRoot(Node r) {
		this.root = r;
	}

	public void setRoot(Weight w) {
		assert (w.getDimension() == this.root.getContent().getDimension());
		this.root.setContent(w);
	}

	public boolean isEmpty() {
		return this.root.getContent() == null;
	}

	private class Node {
		private Weight content;

		public Node(Weight content) {
			this.content = content;
		}

		public boolean dominates(Weight comp) {
			double t1 = this.content.getWeight(1);
			double t0 = this.content.getWeight(0);
			double c1 = comp.getWeight(1);
			double c0 = comp.getWeight(0);
			if ((t1 < c1 && t0 <= c0) || (t1 <= c1 && t0 < c0)) {
				return true;
			} else {
				return false;
			}
		}

		public Weight getContent() {
			return this.content;
		}

		public void setContent(Weight w) {
			assert (this.content.getDimension() == w.getDimension());
			this.content = w;
		}

		public double getWeight(int dimension) {
			return this.content.getWeight(dimension);
		}

		/**
		 * determines wether the vector comp has to be sorted right to the node
		 * on which this method is called.
		 *
		 * @param comp
		 *            the weight which currently is to be inserted to the tree
		 * @return true if comp belongs right to the actual node.
		 */
		public boolean belongsRight(Weight comp) {
			assert (comp.getDimension() == this.content.getDimension());
			return (this.content.getWeight(1) < comp.getWeight(1));
		}

		/**
		 * determines wether the vector comp has to be sorted left to the node
		 * on which this method is called.
		 *
		 * @param comp
		 *            the weight which currently is to be inserted to the tree
		 * @return true if comp belongs left to the actual node.
		 */
		public boolean belongsLeft(Weight comp) {
			assert (comp.getDimension() == this.content.getDimension());
			return (this.content.getWeight(1) >= comp.getWeight(1));
		}

		/**
		 * determines wether given weight's first two components equal the first
		 * two components of the current weight.
		 *
		 * @param comp
		 *            weight to be compared with
		 * @return true if and only if both weights are equal in their first two
		 *         components.
		 */
		public boolean contentEquals(Weight comp) {
			assert (comp.getDimension() == this.content.getDimension());
			return this.content.getWeight(1) == comp.getWeight(1)
					&& this.content.getWeight(0) == comp.getWeight(0);
		}
	}
}
