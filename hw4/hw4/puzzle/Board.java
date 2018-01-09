package hw4.puzzle;

public class Board {
    private final int[][] board;
    private final int N;
    private int hamming;
    private int manhattan;

    public Board(int[][] tiles) {
        N = tiles[0].length;
        board = new int[N][N];
        int iIndex = 0;
        while (iIndex < N) {
            int jIndex = 0;
            while (jIndex < N) {
                board[iIndex][jIndex] = tiles[iIndex][jIndex];
                jIndex += 1;
            }
            iIndex += 1;
        }
        hamming = hammingx();
        manhattan = manhattanx();
    }

    public int tileAt(int i, int j) {
        if (i < 0 || j < 0 || i >= N || j >= N) {
            throw new IndexOutOfBoundsException("Coordinates must be between 0 and " + (N - 1));
        }
        return board[i][j];
    }

    public int size() {
        return N;
    }

    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object y) {
        if (!y.getClass().equals(this.getClass())) {
            return false;
        }
        if (y == null) {
            return false;
        }
        int iIndex = 0;
        if (((Board) y).size() != N) {
            return false;
        }
        while (iIndex < N) {
            int jIndex = 0;
            while (jIndex < N) {
                if (this.tileAt(iIndex, jIndex) != ((Board) y).tileAt(iIndex, jIndex)) {
                    return false;
                }
                jIndex += 1;
            }
            iIndex += 1;
        }
        return true;
    }

    public boolean isGoal() {
        int iIndex = 0;
        int counter = 1;
        while (iIndex < N) {
            int jIndex = 0;
            while (jIndex < N) {
                int x = board[iIndex][jIndex];
                if (x != counter) {
                    return false;
                }
                jIndex += 1;
                counter = (counter + 1) % (N * N);
            }
            iIndex += 1;
        }
        return true;
    }

    private int hammingx() {
        int iIndex = 0;
        int counter = 1;
        int anotherCounter = 0;
        while (iIndex < N) {
            int jIndex = 0;
            while (jIndex < N) {
                int x = board[iIndex][jIndex];
                if (x != counter) {
                    anotherCounter += 1;
                }
                jIndex += 1;
                counter = (counter + 1) % (N * N);
            }
            iIndex += 1;
        }
        return anotherCounter;
    }

    private int manhattanx() {
        int iIndex = 0;
        int actualCounter = 0;
        while (iIndex < N) {
            int jIndex = 0;
            while (jIndex < N) {
                int found = board[iIndex][jIndex];
                int expectedi = -1;
                int expectedj = -1;
                if (found == 0) {
                    expectedi = N - 1;
                    expectedj = N - 1;
                } else {
                    expectedi = (found - 1) / N;
                    expectedj = (found - 1) % N;
                }
                int idifference = Math.abs(iIndex - expectedi);
                int jdifference = Math.abs(jIndex - expectedj);
                actualCounter += (idifference + jdifference);
                jIndex += 1;
            }
            iIndex += 1;
        }
        return actualCounter;
    }

    public int hamming() {
        return this.hamming;
    }

    public int manhattan() {
        return this.manhattan;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        int Q = size();
        s.append(Q + "\n");
        for (int i = 0; i < Q; i++) {
            for (int j = 0; j < Q; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
