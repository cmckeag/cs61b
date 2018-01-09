import java.util.LinkedList;

public class Tile implements Comparable<Tile> {
    private String fileName;
    private int zoomLevel;
    private LinkedList<Coordinates> corners;

    public Tile(String fileName, Coordinates nw, Coordinates ne, Coordinates sw, Coordinates se) {
        this.fileName = fileName;
        corners = new LinkedList<Coordinates>();
        corners.addLast(nw);
        corners.addLast(ne);
        corners.addLast(sw);
        corners.addLast(se);
        zoomLevel = fileName.length();
        if (fileName.equals("root")) {
            zoomLevel = 0;
        }
    }

    public String getName() {
        return fileName;
    }

    public int getZoom() {
        return zoomLevel;
    }

    public LinkedList<Coordinates> getCorners() {
        return corners;
    }

    public boolean intersects(Coordinates xNW, Coordinates xSE) {
        if (corners.peekFirst().getX() > xSE.getX()) {
            return false;
        }
        if (corners.peekFirst().getY() < xSE.getY()) {
            return false;
        }
        if (corners.peekLast().getX() < xNW.getX()) {
            return false;
        }
        if (corners.peekLast().getY() > xNW.getY()) {
            return false;
        }
        return true;
    }

    public int compareTo(Tile other) {
        if (other.corners.peekFirst().getY() > this.corners.peekFirst().getY()) {
            // If the other tile is higher up, then this tile is "larger"
            return 1;
        } else if (other.corners.peekFirst().getY() < this.corners.peekFirst().getY()) {
            // If the other tile is lower down, then the other tile is "larger"
            return -1;
        } else {
            // If the tiles are on the same level, we compare them by their X values
            if (other.corners.peekFirst().getX() > this.corners.peekFirst().getX()) {
                return -1;
            } else if (other.corners.peekFirst().getX() < this.corners.peekFirst().getX()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
