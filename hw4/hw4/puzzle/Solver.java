package hw4.puzzle;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.LinkedList;

public class Solver {
    private MinPQ<Node> qqPQ;
    private Node end;
    private LinkedList<Board> history;
    private int moves;

    public Solver(Board initial) {
        // Instantiate the initial node, insert it to the minPQ
        Node start = new Node(initial, 0, null);
        qqPQ = new MinPQ<Node>();
        qqPQ.insert(start);
        // Solve the puzzle
        end = solve(qqPQ);
        // Create a list of boards
        history = new LinkedList<Board>();
        while (end != null) {
            history.addFirst(end.current());
            end = end.previous();
        }
        // Find the # of moves
        moves = history.size() - 1;

    }

    private Node solve(MinPQ<Node> a) {
        Node latest = a.delMin();
        while (!latest.current().isGoal()) {
            for (Board b: BoardUtils.neighbors(latest.current())) {
                if (latest.previous() == null) {
                    a.insert(new Node(b, latest.movesx() + 1, latest));
                } else if (!latest.previous().current().equals(b)) {
                    a.insert(new Node(b, latest.movesx() + 1, latest));
                }
            }
            latest = a.delMin();
        }
        return latest;
    }

    private Board duplicate(Board b) {
        int q = b.size();
        int[][] tiles = new int[q][q];
        int iIndex = 0;
        int jIndex = 0;
        while (iIndex < q) {
            while (jIndex < q) {
                tiles[iIndex][jIndex] = b.tileAt(iIndex, jIndex);
                jIndex += 1;
            }
            iIndex += 1;
        }
        Board dupe = new Board(tiles);
        return dupe;
    }

    public int moves() {
        return moves;
    }

    public Iterable<Board> solution() {
        return history;
    }

    private class Node implements Comparable<Node> {
        private Board current;
        private int moves;
        private Node previous;
        private int priority;

        public Node(Board initial, int moves, Node previous) {
            this.current = initial;
            this.moves = moves;
            this.previous = previous;
            priority = initial.manhattan() + moves;
        }

        public Board current() {
            return this.current;
        }

        public int movesx() {
            return this.moves;
        }

        public Node previous() {
            return this.previous;
        }

        public int compareTo(Node other) {
            return Integer.compare(this.priority, other.priority);
        }
    }



    // DO NOT MODIFY MAIN METHOD
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);
        StdOut.println("Minimum number of moves = " + solver.moves());
        for (Board board : solver.solution()) {
            StdOut.println(board);
        }
    }

}
