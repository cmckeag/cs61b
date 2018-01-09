/**
 * Created by christophermckeag on 4/14/16.
 */
import org.xml.sax.Attributes;
import java.util.HashSet;

public class MPoint implements Comparable<MPoint> {
    private double lon;
    private double lat;
    private long iD;
    private HashSet<MPoint> adj;

    public MPoint() {
        lat = 0;
        lon = 0;
        iD = 0;
        adj = new HashSet<MPoint>();
    }

    public double lon() {
        return lon;
    }

    public double lat() {
        return lat;
    }

    public MPoint(Attributes attributes) {
        lon = Double.parseDouble(attributes.getValue("lon"));
        lat = Double.parseDouble(attributes.getValue("lat"));
        iD = Long.parseLong(attributes.getValue("id"));
        adj = new HashSet<MPoint>();
    }

    public int compareTo(MPoint other) {
        return (int) (this.iD - other.iD);
    }

    public void addNeighbor(MPoint add) {
        adj.add(add);
    }

    public HashSet<MPoint> getNeighbors() {
        return adj;
    }

    public long getID() {
        return iD;
    }

    @Override
    public boolean equals(Object o) {
        if (!this.getClass().equals(o.getClass())) {
            return false;
        }
        return (this.iD == ((MPoint) o).iD);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(iD);
    }

}
