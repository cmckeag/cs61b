/**
 * Created by christophermckeag on 4/17/16.
 */
public class SearchNode implements Comparable<SearchNode> {
    private MPoint current;
    private SearchNode previous;
    private double priority;
    private double distanceTraveled;

    public SearchNode(MPoint initial, SearchNode previous, MPoint target) {
        current = initial;
        if (previous == null) {
            distanceTraveled = 0;
        } else {
            distanceTraveled = previous.distanceTraveled + Math.sqrt(Math.pow(initial.lon()
                    - previous.current.lon(), 2)
                    + Math.pow(initial.lat() - previous.current.lat(), 2));
        }
        this.previous = previous;
        priority = Math.sqrt(Math.pow(initial.lon() - target.lon(), 2)
                + Math.pow(initial.lat() - target.lat(), 2)) + distanceTraveled;
    }

    public boolean isGoal() {
        return ((priority - distanceTraveled) == 0);
    }

    public MPoint current() {
        return this.current;
    }

    public SearchNode previous() {
        return this.previous;
    }

    public double distanceTraveled() {
        return distanceTraveled;
    }

    public int compareTo(SearchNode other) {
        if (this.priority > other.priority) {
            return 1;
        } else if (this.priority < other.priority) {
            return -1;
        } else {
            return 0;
        }
    }
}
