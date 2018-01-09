/**
 * Created by christophermckeag on 4/12/16.
 */
import java.util.TreeSet;
import java.util.ListIterator;
import java.util.LinkedList;

public class QuadTree {
    private Node root;

    private class Node {
        private Tile item;
        private Node xNW;
        private Node xNE;
        private Node xSW;
        private Node xSE;
        private int depth;

        Node(Tile item, Node xNW, Node xNE, Node xSW, Node xSE, int depth) {
            this.item = item;
            this.xNW = xNW;
            this.xNE = xNE;
            this.xSW = xSW;
            this.xSE = xSE;
            this.depth = depth;
        }
    }

    public QuadTree() {
        double aULx = -122.2998046875;
        double aULy = 37.892195547244356;
        double aLRx = -122.2119140625;
        double aLRy = 37.82280243352756;
        Coordinates xNW = new Coordinates(aULx, aULy);
        Coordinates xNE = new Coordinates(aLRx, aULy);
        Coordinates xSW = new Coordinates(aULx, aLRy);
        Coordinates xSE = new Coordinates(aLRx, aLRy);

        root = new Node(new Tile("root", xNW, xNE, xSW, xSE), null, null, null, null, 0);
        populate(root);
    }

    private void populate(Node r) {
        Tile current = r.item;
        if (r.depth == 7) {
            return;
        }
        ListIterator<Coordinates> iterator = current.getCorners().listIterator();
        Coordinates northwest = iterator.next();
        Coordinates northeast = iterator.next();
        Coordinates southwest = iterator.next();
        Coordinates southeast = iterator.next();
        Coordinates northcenter = new Coordinates((northwest.getX()
                + northeast.getX()) / 2, northwest.getY());
        Coordinates westcenter = new Coordinates(northwest.getX(),
                (northwest.getY() + southwest.getY()) / 2);
        Coordinates southcenter = new Coordinates((southwest.getX() + southeast.getX()) / 2,
                southwest.getY());
        Coordinates eastcenter = new Coordinates(northeast.getX(),
                (northeast.getY() + southeast.getY()) / 2);
        Coordinates center = new Coordinates((northwest.getX() + northeast.getX()) / 2,
                (northwest.getY() + southwest.getY()) / 2);

        String nwName = "";
        String neName = "";
        String swName = "";
        String seName = "";
        if (current.getName().equals("root")) {
            nwName = "1";
            neName = "2";
            swName = "3";
            seName = "4";
        } else {
            nwName = current.getName().concat("1");
            neName = current.getName().concat("2");
            swName = current.getName().concat("3");
            seName = current.getName().concat("4");
        }
        Tile nwChild = new Tile(nwName, northwest, northcenter, westcenter, center);
        Tile neChild = new Tile(neName, northcenter, northeast, center, eastcenter);
        Tile swChild = new Tile(swName, westcenter, center, southwest, southcenter);
        Tile seChild = new Tile(seName, center, eastcenter, southcenter, southeast);
        r.xNW = new Node(nwChild, null, null, null, null, r.depth + 1);
        r.xNE = new Node(neChild, null, null, null, null, r.depth + 1);
        r.xSW = new Node(swChild, null, null, null, null, r.depth + 1);
        r.xSE = new Node(seChild, null, null, null, null, r.depth + 1);

        populate(r.xNW);
        populate(r.xNE);
        populate(r.xSW);
        populate(r.xSE);
    }

    public LinkedList<Tile> getInQueryWindow(Coordinates xNW, Coordinates xSE, int depth) {
        if (depth > 7) {
            depth = 7;
        }
        TreeSet<Tile> list = new TreeSet<Tile>();
        findInQueryWindow(xNW, xSE, depth, list, root);
        LinkedList<Tile> returnList = new LinkedList<Tile>();
        for (Tile q : list) {
            returnList.addLast(q);
        }
        return returnList;
    }

    private void findInQueryWindow(Coordinates xNW, Coordinates xSE,
                                   int depth, TreeSet<Tile> list, Node inspect) {
        if (depth > 0) {
            if (inspect.item.intersects(xNW, xSE)) {
                findInQueryWindow(xNW, xSE, depth - 1, list, inspect.xNW);
                findInQueryWindow(xNW, xSE, depth - 1, list, inspect.xNE);
                findInQueryWindow(xNW, xSE, depth - 1, list, inspect.xSW);
                findInQueryWindow(xNW, xSE, depth - 1, list, inspect.xSE);
            }
        } else {
            if (inspect.item.intersects(xNW, xSE)) {
                list.add(inspect.item);
            }
        }
    }
}
