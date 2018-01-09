package hw2;                       

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.ArrayList;

public class Percolation {
    private ArrayList<Node> nodes;
    private WeightedQuickUnionUF connectivity;
    private WeightedQuickUnionUF pc;
    protected int gridSize;
    protected int openSites;
    private Node top;
    private Node bottom;
    private int topNode;
    private int bottomNode;

    public Percolation(int N) {
        connectivity = new WeightedQuickUnionUF((N * N) + 1);
        pc = new WeightedQuickUnionUF((N * N) + 2);
        nodes = new ArrayList<Node>((N * N) + 2);
        top = new Node(-1, -1);
        bottom = new Node(N + 1, N + 1);
        openSites = 0;
        gridSize = N;
        int i = 0;
        int j = 0;
        while (i < N) {
            j = 0;
            while (j < N) {
                nodes.add(new Node(i, j));
                j += 1;
            }
            i += 1;
        }
        nodes.add(top);
        nodes.add(bottom);
        topNode = N * N;
        bottomNode = topNode + 1;
    }

    public class Node {
        protected boolean state;
        // true = open
        // false = blocked
        private int row;
        private int column;

        public Node(int rowNumber, int columnNumber) {
            row = rowNumber;
            column = columnNumber;
            state = false;
            // blocked by default
        }
    }

    private int linearize(int row, int column) {
        int value = (row * gridSize) + column;
        return value;
    }

    public void open(int row, int col) {
        int value = linearize(row, col);
        if (value >= (gridSize * gridSize)) {
            throw new IndexOutOfBoundsException("Attempted to open a site that is not in the grid");
        }
        Node checking = nodes.get(value);
        if (checking.state) {
            return;
        }
        checking.state = true;
        // Now we must connect this one to any open adjacent nodes.
        if (row == 0) {
            // Connect this node to the top node
            connectivity.union(value, topNode);
            pc.union(value, topNode);
        } else if (nodes.get(linearize(row - 1, col)).state) {
            connectivity.union(value, linearize(row - 1, col));
            pc.union(value, linearize(row - 1, col));
        }
        // Connect to an open node to the left
        if (col != 0 && nodes.get(linearize(row, col - 1)).state) {
            connectivity.union(value, linearize(row, col - 1));
            pc.union(value, linearize(row, col - 1));
        }
        // Connect to an open node to the right
        if (col < (gridSize - 1) && nodes.get(linearize(row, col + 1)).state) {
            connectivity.union(value, linearize(row, col + 1));
            pc.union(value, linearize(row, col + 1));
        }
        // Connect to an open node below
        if (row == gridSize - 1) {
            pc.union(value, bottomNode);
        } else if (nodes.get(linearize(row + 1, col)).state) {
            connectivity.union(value, linearize(row + 1, col));
            pc.union(value, linearize(row + 1, col));
        }
        openSites += 1;
    }

    public boolean isOpen(int row, int col) {
        int value = linearize(row, col);
        if (value >= (gridSize * gridSize)) {
            throw new IndexOutOfBoundsException("Attempted to check an invalid site");
        }
        Node checking = nodes.get(value);
        return checking.state;
    }

    public boolean isFull(int row, int col) {
        int value = linearize(row, col);
        if (value >= (gridSize * gridSize)) {
            throw new IndexOutOfBoundsException("Attempted to check an invalid site");
        }
        return connectivity.connected(value, topNode);
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    public boolean percolates() {
        return pc.connected(topNode, bottomNode);
    }

}                       
