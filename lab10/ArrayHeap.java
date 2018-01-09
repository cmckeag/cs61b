import java.util.ArrayList;
import java.util.ListIterator;

/**
 * A Generic heap class. Unlike Java's priority queue, this heap doesn't just
 * store Comparable objects. Instead, it can store any type of object
 * (represented by type T), along with a priority value.
 */
public class ArrayHeap<T> {
	private ArrayList<Node> contents = new ArrayList<Node>();

	/**
	 * Inserts an item with the given priority value. This is enqueue, or offer.
	 */
	public void insert(T item, double priority) {
		Node lmao = new Node(item, priority);
		if (contents.size() == 0) {
			contents.add(null);
			contents.add(lmao);
			return;
		}
		contents.add(lmao);
		bubbleUp(contents.size() - 1);
	}

	/**
	 * Returns the Node with the smallest priority value, but does not remove it
	 * from the heap.
	 */
	public Node peek() {
		// We want the smallest item, which will always be the 1 item
		return contents.get(1);
	}

	/**
	 * Returns the Node with the smallest priority value, and removes it from
	 * the heap. This is dequeue, or poll.
	 */
	public Node removeMin() {
		if (contents.size() == 0) {
			return null;
		}
		if (contents.size() == 1) {
			return null;
		}
		if (contents.size() == 2) {
			return contents.remove(1);
		}
		// Swap the top and bottom items
		swap(1, contents.size() - 1);
		// Remove the new bottom item (old top item)
		Node removed = contents.remove(contents.size() - 1);
		bubbleDown(1);
		return removed;
	}

	/**
	 * Change the node in this heap with the given item to have the given
	 * priority. For this method only, you can assume the heap will not have two
	 * nodes with the same item. Check for item equality with .equals(), not ==
	 */
	public void changePriority(T item, double priority) {
		ListIterator<Node> iterator = contents.listIterator(1);
		while (iterator.hasNext()) {
			Node inspect = iterator.next();
			T inspectItem = inspect.item();
			double inspectPriority = inspect.priority();
			if (item.equals(inspectItem)) {
				iterator.set(new Node(item, priority));
				if (priority > inspectPriority) {
					bubbleDown(iterator.previousIndex());
				} else if (priority < inspectPriority) {
					bubbleUp(iterator.previousIndex());
				}
				return;
			}
		}
		return;
	}

	/**
	 * Prints out the heap sideways.
	 */
	@Override
	public String toString() {
		return toStringHelper(1, "");
	}

	/* Recursive helper method for toString. */
	private String toStringHelper(int index, String soFar) {
		if (getNode(index) == null) {
			return "";
		} else {
			String toReturn = "";
			int rightChild = getRightOf(index);
			toReturn += toStringHelper(rightChild, "        " + soFar);
			if (getNode(rightChild) != null) {
				toReturn += soFar + "    /";
			}
			toReturn += "\n" + soFar + getNode(index) + "\n";
			int leftChild = getLeftOf(index);
			if (getNode(leftChild) != null) {
				toReturn += soFar + "    \\";
			}
			toReturn += toStringHelper(leftChild, "        " + soFar);
			return toReturn;
		}
	}

	private Node getNode(int index) {
		if (index >= contents.size()) {
			return null;
		} else {
			return contents.get(index);
		}
	}

	private void setNode(int index, Node n) {
		// In the case that the ArrayList is not big enough
		// add null elements until it is the right size
		while (index + 1 >= contents.size()) {
			contents.add(null);
		}
		contents.set(index, n);
	}

	/**
	 * Swap the nodes at the two indices.
	 */
	private void swap(int index1, int index2) {
		Node node1 = getNode(index1);
		Node node2 = getNode(index2);
		this.contents.set(index1, node2);
		this.contents.set(index2, node1);
	}

	/**
	 * Returns the index of the node to the left of the node at i.
	 */
	private int getLeftOf(int i) {
		return 2 * i;
	}

	/**
	 * Returns the index of the node to the right of the node at i.
	 */
	private int getRightOf(int i) {
		return (2 * i) + 1;
	}

	/**
	 * Returns the index of the node that is the parent of the node at i.
	 */
	private int getParentOf(int i) {
		return i / 2;
	}

	/**
	 * Adds the given node as a left child of the node at the given index.
	 */
	private void setLeft(int index, Node n) {
		setNode(2 * index, n);
	}

	/**
	 * Adds the given node as the right child of the node at the given index.
	 */
	private void setRight(int index, Node n) {
		setNode((2 * index) + 1, n);
	}

	/**
	 * Bubbles up the node currently at the given index.
	 */
	private void bubbleUp(int index) {
		if (index == 1) {
			return;
		}
		Node active = contents.get(index);
		Node parent = contents.get(index / 2);
		if (active.priority() < parent.priority()) {
			swap(index, index / 2);
			bubbleUp(index / 2);
			return;
		} else {
			return;
		}
	}

	/**
	 * Bubbles down the node currently at the given index.
	 */
	private void bubbleDown(int index) {
		Node active = contents.get(index);
		
		if (getNode(getLeftOf(index)) == null && getNode(getRightOf(index)) == null) {
			return;
		}
		if (getNode(getLeftOf(index)) == null) {
			Node child = getNode(getRightOf(index));
			if (child.priority() < active.priority()) {
				swap(index, getRightOf(index));
				bubbleDown(getRightOf(index));
				return;
			} else {
				return;
			}
		}
		if (getNode(getRightOf(index)) == null) {
			Node child = getNode(getLeftOf(index));
			if (child.priority() < active.priority()) {
				swap(index, getLeftOf(index));
				bubbleDown(getLeftOf(index));
				return;
			} else {
				return;
			}
		}
		int smaller = min(getRightOf(index), getLeftOf(index));
		swap(index, smaller);
		bubbleDown(smaller);
		return;
	}

	/**
	 * Returns the index of the node with smaller priority. Precondition: Not
	 * both of the nodes are null.
	 */
	private int min(int index1, int index2) {
		Node node1 = getNode(index1);
		Node node2 = getNode(index2);
		if (node1 == null) {
			return index2;
		} else if (node2 == null) {
			return index1;
		} else if (node1.myPriority < node2.myPriority) {
			return index1;
		} else {
			return index2;
		}
	}

	public class Node {
		private T myItem;
		private double myPriority;

		private Node(T item, double priority) {
			myItem = item;
			myPriority = priority;
		}

		public T item() {
			return myItem;
		}

		public double priority() {
			return myPriority;
		}

		@Override
		public String toString() {
			return item().toString() + ", " + priority();
		}
	}

	public static void main(String[] args) {
		ArrayHeap<String> heap = new ArrayHeap<String>();
		heap.insert("c", 3);
		heap.insert("i", 9);
		heap.insert("g", 7);
		heap.insert("d", 4);
		heap.insert("a", 1);
		heap.insert("h", 8);
		heap.insert("e", 5);
		heap.insert("b", 2);
		heap.insert("c", 3);
		heap.insert("d", 4);
		System.out.println(heap);
	}

}
