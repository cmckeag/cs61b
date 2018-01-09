import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ListIterator;
import java.util.Iterator;

/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *  See OSM documentation on
 *  <a href="http://wiki.openstreetmap.org/wiki/Key:highway">the highway tag</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Way">the way XML element</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Node">the node XML element</a>,
 *  and the java
 *  <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *  @author Alan Yao
 */
public class MapDBHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible. Note that in Berkeley, many of the campus roads are tagged as motor vehicle
     * roads, but in practice we walk all over them with such impunity that we forget cars can
     * actually drive on them.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));
    private String activeState = "";
    private boolean validHighway;
    private final GraphDB g;
    private MPoint currentNode;
    private HashMap<Long, MPoint> points = new HashMap<Long, MPoint>();
    private LinkedList<MPoint> wayQ = new LinkedList<MPoint>();

    public MapDBHandler(GraphDB g) {
        this.g = g;
    }

    /**
     * Called at the beginning of an element. Typically, you will want to handle each element in
     * here, and you may want to track the parent element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        /* Some example code on how you might begin to parse XML files. */
        if (qName.equals("node")) {
            activeState = "node";
            handleNode(attributes);
        } else if (qName.equals("way")) {
            activeState = "way";
            validHighway = false;
            wayQ.clear();
        } else if (activeState.equals("way") && qName.equals("tag")
                && attributes.getValue("k").equals("highway")
                && ALLOWED_HIGHWAY_TYPES.contains(attributes.getValue("v"))) {
            validHighway = true;
        } else if (activeState.equals("way") && qName.equals("nd")) {
            wayQ.addLast(points.get(Long.parseLong(attributes.getValue("ref"))));

        }
    }

    private void handleNode(Attributes attributes) {
        long id = Long.parseLong(attributes.getValue("id"));
        //System.out.println("Found a node with ID " + ID);
        MPoint current = new MPoint(attributes);
        points.put(id, current);
        currentNode = current;
    }

    /**
     * Receive notification of the end of an element. You may want to take specific terminating
     * actions here, like finalizing vertices or edges found.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way") && validHighway) {
            ListIterator<MPoint> listit = wayQ.listIterator();
            while (listit.hasNext()) {
                MPoint working = listit.next();
                if (listit.hasNext()) {
                    working.addNeighbor(listit.next());
                    listit.previous();
                }
            }
            while (listit.hasPrevious()) {
                MPoint working = listit.previous();
                if (listit.hasPrevious()) {
                    working.addNeighbor(listit.previous());
                    listit.next();
                }
            }
        } else if (qName.equals("way") && !validHighway) {
            wayQ.clear();
        }
    }

    public void clean() {
        Iterator<MPoint> iterator = points.values().iterator();
        while (iterator.hasNext()) {
            MPoint inspect = iterator.next();
            if (inspect.getNeighbors().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public HashMap<Long, MPoint> nodes() {
        return this.points;
    }

}
