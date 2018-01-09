import java.util.Observable;
import java.util.TreeSet;
import java.util.LinkedList;
/**
 *  @author Josh Hug
 */

public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int start;
    private int target;
    private boolean targetFound = false;
    private Maze maze;
    private LinkedList<Integer> queue;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        start = maze.xyTo1D(sourceX, sourceY);
        target = maze.xyTo1D(targetX, targetY);
        distTo[start] = 0;
        edgeTo[start] = start;
        queue = new LinkedList<Integer>();
    }

    /** Conducts a breadth first search of the maze starting at vertex x. */
    private void bfs(int s) {
        if (s == target) {
            targetFound = true;
            return;
        }
        queue.add(s);
        outerloop:
        while (!targetFound) {
            int inspect = queue.removeFirst();
            if (!marked[inspect]) {
                marked[inspect] = true;
                for (int q : maze.adj(inspect)) {
                    if (!marked[q]) {
                        edgeTo[q] = inspect;
                        distTo[q] = distTo[inspect] + 1;
                        announce();
                        queue.add(q);
                        if (inspect == target) {
                            targetFound = true;
                            break outerloop;
                        }
                    }
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs(start);
    }
}

