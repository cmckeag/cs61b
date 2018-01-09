import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.HashSet;

/* Maven is used to pull in these dependencies. */
import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * This MapServer class is the entry point for running the JavaSpark web server for the BearMaps
 * application project, receiving API calls, handling the API call processing, and generating
 * requested images and routes.
 * @author Alan Yao
 */
public class MapServer {
    /**
     * The root upper left/lower right longitudes and latitudes represent the bounding box of
     * the root tile, as the images in the img/ folder are scraped.
     * Longitude == x-axis; latitude == y-axis.
     */
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    /** Each tile is 256x256 pixels. */
    public static final int TILE_SIZE = 256;
    /** HTTP failed response. */
    private static final int HALT_RESPONSE = 403;
    /** Route stroke information: typically roads are not more than 5px wide. */
    public static final float ROUTE_STROKE_WIDTH_PX = 5.0f;
    /** Route stroke information: Cyan with half transparency. */
    public static final Color ROUTE_STROKE_COLOR = new Color(108, 181, 230, 200);
    /** The tile images are in the IMG_ROOT folder. */
    private static final String IMG_ROOT = "img/";
    /**
     * The OSM XML file path. Downloaded from <a href="http://download.bbbike.org/osm/">here</a>
     * using custom region selection.
     **/
    private static final String OSM_DB_PATH = "berkeley.osm";
    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside getMapRaster(). <br>
     * ullat -> upper left corner latitude,<br> ullon -> upper left corner longitude, <br>
     * lrlat -> lower right corner latitude,<br> lrlon -> lower right corner longitude <br>
     * w -> user viewport window width in pixels,<br> h -> user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
        "lrlon", "w", "h"};
    /**
     * Each route request to the server will have the following parameters
     * as keys in the params map.<br>
     * start_lat -> start point latitude,<br> start_lon -> start point longitude,<br>
     * end_lat -> end point latitude, <br>end_lon -> end point longitude.
     **/
    private static final String[] REQUIRED_ROUTE_REQUEST_PARAMS = {"start_lat", "start_lon",
        "end_lat", "end_lon"};
    /* Define any static variables here. Do not define any instance variables of MapServer. */
    private static GraphDB g;
    private static QuadTree quadTree;
    private static LinkedList<Long> route;
    private static LinkedList<Long> otherRoute = new LinkedList<>();


    /**
     * Place any initialization statements that will be run before the server main loop here.
     * Do not place it in the main function. Do not place initialization code anywhere else.
     * This is for testing purposes, and you may fail tests otherwise.
     **/
    public static void initialize() {
        quadTree = new QuadTree();
        g = new GraphDB(OSM_DB_PATH);
    }

    public static void main(String[] args) {
        initialize();
        staticFileLocation("/page");
        /* Allow for all origin requests (since this is not an authenticated server, we do not
         * care about CSRF).  */
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        /* Define the raster endpoint for HTTP GET requests. I use anonymous functions to define
         * the request handlers. */
        get("/raster", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_RASTER_REQUEST_PARAMS);
            /* The png image is written to the ByteArrayOutputStream */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            /* getMapRaster() does almost all the work for this API call */
            Map<String, Object> rasteredImgParams = getMapRaster(params, os);
            /* On an image query success, add the image data to the response */
            if (rasteredImgParams.containsKey("query_success")
                    && (Boolean) rasteredImgParams.get("query_success")) {
                String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
                rasteredImgParams.put("b64_encoded_image_data", encodedImage);
            }
            /* Encode response to Json */
            Gson gson = new Gson();
            return gson.toJson(rasteredImgParams);
        });

        /* Define the routing endpoint for HTTP GET requests. */
        get("/route", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_ROUTE_REQUEST_PARAMS);
            route = findAndSetRoute(params);
            return !route.isEmpty();
        });

        /* Define the API endpoint for clearing the current route. */
        get("/clear_route", (req, res) -> {
            clearRoute();
            return true;
        });

        /* Define the API endpoint for search */
        get("/search", (req, res) -> {
            Set<String> reqParams = req.queryParams();
            String term = req.queryParams("term");
            Gson gson = new Gson();
            /* Search for actual location data. */
            if (reqParams.contains("full")) {
                List<Map<String, Object>> data = getLocations(term);
                return gson.toJson(data);
            } else {
                /* Search for prefix matching strings. */
                List<String> matches = getLocationsByPrefix(term);
                return gson.toJson(matches);
            }
        });

        /* Define map application redirect */
        get("/", (request, response) -> {
            response.redirect("/map.html", 301);
            return true;
        });
    }

    /**
     * Validate & return a parameter map of the required request parameters.
     * Requires that all input parameters are doubles.
     * @param req HTTP Request
     * @param requiredParams TestParams to validate
     * @return A populated map of input parameter to it's numerical value.
     */
    private static HashMap<String, Double> getRequestParams(
            spark.Request req, String[] requiredParams) {
        Set<String> reqParams = req.queryParams();
        HashMap<String, Double> params = new HashMap<>();
        for (String param : requiredParams) {
            if (!reqParams.contains(param)) {
                halt(HALT_RESPONSE, "Request failed - parameters missing.");
            } else {
                try {
                    params.put(param, Double.parseDouble(req.queryParams(param)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    halt(HALT_RESPONSE, "Incorrect parameters - provide numbers.");
                }
            }
        }
        return params;
    }

    /**
     * Handles raster API calls, queries for tiles and rasters the full image. <br>
     * <p>
     *     The rastered photo must have the following properties:
     *     <ul>
     *         <li>Has dimensions of at least w by h, where w and h are the user viewport width
     *         and height.</li>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *         <li>If a current route exists, lines of width ROUTE_STROKE_WIDTH_PX and of color
     *         ROUTE_STROKE_COLOR are drawn between all nodes on the route in the rastered photo.
     *         </li>
     *     </ul>
     *     Additional image about the raster is returned and is to be included in the Json response.
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query bounding box and
     *               the user viewport width and height.
     * @param os     An OutputStream that the resulting png image should be written to.
     * @return A map of parameters for the Json response as specified:
     * "raster_ul_lon" -> Double, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Double, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Double, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Double, the bounding lower right latitude of the rastered image <br>
     * "raster_width"  -> Double, the width of the rastered image <br>
     * "raster_height" -> Double, the height of the rastered image <br>
     * "depth"         -> Double, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image string. <br>
     * "query_success" -> Boolean, whether an image was successfully rastered. <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public static Map<String, Object> getMapRaster(Map<String, Double> params, OutputStream os) {
        HashMap<String, Object> rasteredImageParams = new HashMap<String, Object>();
        Coordinates xNW = new Coordinates(params.get("ullon"), params.get("ullat"));
        Coordinates xSE = new Coordinates(params.get("lrlon"), params.get("lrlat"));
        double width = params.get("w");
        double reqDPP = (params.get("lrlon") - params.get("ullon")) / width;
        double currentDPP = (ROOT_LRLON - ROOT_ULLON) / 256.0;
        double heightDPP = (ROOT_ULLAT - ROOT_LRLAT) / 256.0;
        int depth = 0;
        while (currentDPP > reqDPP && depth < 7) {
            currentDPP = currentDPP / 2;
            heightDPP = heightDPP / 2;
            depth += 1;
        }
        LinkedList<Tile> rasterTiles = quadTree.getInQueryWindow(xNW, xSE, depth);
        boolean querySuccess = !rasterTiles.isEmpty();
        double rasterULLON = rasterTiles.peekFirst().getCorners().peekFirst().getX();
        double rasterULLAT = rasterTiles.peekFirst().getCorners().peekFirst().getY();
        double rasterLRLON = rasterTiles.peekLast().getCorners().peekLast().getX();
        double rasterLRLAT = rasterTiles.peekLast().getCorners().peekLast().getY();
        int tilesWidth = 0;
        ListIterator<Tile> iterator = rasterTiles.listIterator();
        double currentheight = rasterTiles.peekFirst().getCorners().peekFirst().getY();
        while (iterator.hasNext() && iterator.next().getCorners().peekFirst().getY()
                == currentheight) {
            tilesWidth += 1;
        }
        int tilesHeight = 0;
        if (tilesWidth > 0) {
            tilesHeight = rasterTiles.size() / tilesWidth;
        }
        int rasterWidth = tilesWidth * 256;
        int rasterHeight = tilesHeight * 256;
        rasteredImageParams.put("raster_ul_lon", rasterULLON);
        rasteredImageParams.put("raster_ul_lat", rasterULLAT);
        rasteredImageParams.put("raster_lr_lon", rasterLRLON);
        rasteredImageParams.put("raster_lr_lat", rasterLRLAT);
        rasteredImageParams.put("raster_width", rasterWidth);
        rasteredImageParams.put("raster_height", rasterHeight);
        rasteredImageParams.put("depth", depth);
        rasteredImageParams.put("query_success", querySuccess);
        BufferedImage im = new BufferedImage(rasterWidth, rasterHeight, BufferedImage.TYPE_INT_RGB);
        drawAll(rasterTiles, im, os, rasterWidth, new Coordinates(rasterULLON, rasterULLAT),
                new Coordinates(rasterLRLON, rasterLRLAT), currentDPP, heightDPP);
        return rasteredImageParams;
    }

    private static void drawAll(LinkedList<Tile> tilesInput, BufferedImage im,
                                OutputStream os, int rasterWidth, Coordinates rasterUL,
                                Coordinates rasterLR, double currentDPP, double heightDPP) {
        double rasterULLON = rasterUL.getX();
        double rasterULLAT = rasterUL.getY();
        double rasterLRLON = rasterLR.getX();
        double rasterLRLAT = rasterLR.getY();
        ListIterator<Tile> images = tilesInput.listIterator(0);
        Graphics gr = im.getGraphics();
        int x = 0;
        int y = 0;
        try {
            while (images.hasNext()) {
                Tile grabb = images.next();
                File tileImage = new File(IMG_ROOT + grabb.getName() + ".png");
                BufferedImage img = ImageIO.read(tileImage);
                gr.drawImage(img, x, y, null);
                x += 256;
                if (x == rasterWidth) {
                    x = 0;
                    y += 256;
                }
            }
            if (!otherRoute.isEmpty()) {
                ((Graphics2D) gr).setStroke(new BasicStroke(MapServer.ROUTE_STROKE_WIDTH_PX,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                gr.setColor(ROUTE_STROKE_COLOR);
                ListIterator<Long> routetravel = otherRoute.listIterator();
                while (routetravel.hasNext()) {
                    Long inspectID = routetravel.next();
                    MPoint startPoint = g.getNodes().get(inspectID);
                    Coordinates startPixel = new Coordinates((startPoint.lon() - rasterULLON)
                            / currentDPP, (rasterULLAT - startPoint.lat()) / heightDPP);
                    if (routetravel.hasNext()) {
                        Long nextID = routetravel.next();
                        MPoint endPoint = g.getNodes().get(nextID);
                        Coordinates endPixel = new Coordinates((endPoint.lon() - rasterULLON)
                                / currentDPP, (rasterULLAT - endPoint.lat()) / heightDPP);
                        gr.drawLine((int) startPixel.getX(), (int) startPixel.getY(),
                                (int) endPixel.getX(), (int) endPixel.getY());
                        routetravel.previous();
                    }
                }
            }
            ImageIO.write(im, "png", os);
        } catch (IOException e) {
            System.out.println("There was an error drawing the image");
        }
    }

    /**
     * Searches for the shortest route satisfying the input request parameters, sets it to be the
     * current route, and returns a <code>LinkedList</code> of the route's node ids for testing
     * purposes. <br>
     * The route should start from the closest node to the start point and end at the closest node
     * to the endpoint. Distance is defined as the euclidean between two points (lon1, lat1) and
     * (lon2, lat2).
     * @param params from the API call described in REQUIRED_ROUTE_REQUEST_PARAMS
     * @return A LinkedList of node ids from the start of the route to the end.
     */
    public static LinkedList<Long> findAndSetRoute(Map<String, Double> params) {
        clearRoute();
        HashMap<Long, MPoint> allNodes = g.getNodes();
        MPoint[] startAndEndPoints = findStartAndEndNodes(params, allNodes);
        MPoint start = startAndEndPoints[0];
        MPoint destination = startAndEndPoints[1];
        SearchNode base = new SearchNode(start, null, destination);
        PriorityQueue<SearchNode> q = new PriorityQueue<>();
        HashSet<Long> visited = new HashSet<>();
        visited.add(base.current().getID());
        q.add(base);
        SearchNode result = solve(q, destination, visited);
        LinkedList<Long> path = new LinkedList<>();
        while (result.previous() != null) {
            path.addFirst(result.current().getID());
            result = result.previous();
        }
        path.addFirst(result.current().getID());
        otherRoute = path;
        return path;
    }

    private static SearchNode solve(PriorityQueue<SearchNode> q,
                                    MPoint target, HashSet<Long> visited) {
        SearchNode latest = q.poll();
        while (!latest.isGoal()) {
            for (MPoint m : latest.current().getNeighbors()) {
                if (latest.previous() == null) {
                    q.add(new SearchNode(m, latest, target));
                } else if (!visited.contains(m.getID())) {
                    q.add(new SearchNode(m, latest, target));
                }
            }
            latest = q.poll();
            visited.add(latest.current().getID());
        }
        return latest;
    }

    private static MPoint[] findStartAndEndNodes(Map<String, Double> params,
                                                 HashMap<Long, MPoint> allNodes) {
        double startx = params.get("start_lon");
        double starty = params.get("start_lat");
        double endx = params.get("end_lon");
        double endy = params.get("end_lat");
        double distanceFromStart = -1;
        double distanceFromEnd = -1;
        MPoint[] startAndEndPoints = new MPoint[2];
        Iterator<MPoint> nodeIterator = allNodes.values().iterator();
        startAndEndPoints[0] = new MPoint();
        startAndEndPoints[1] = new MPoint();
        while (nodeIterator.hasNext()) {
            MPoint inspect = nodeIterator.next();
            if (distanceFromStart < 0 || distanceFromEnd < 0) {
                distanceFromStart = Math.sqrt(Math.pow(startx - inspect.lon(), 2)
                        + Math.pow(starty - inspect.lat(), 2));
                distanceFromEnd = Math.sqrt(Math.pow(endx - inspect.lon(), 2)
                        + Math.pow(endy - inspect.lat(), 2));
            } else {
                if (Math.sqrt(Math.pow(startx - inspect.lon(), 2)
                        + Math.pow(starty - inspect.lat(), 2)) < distanceFromStart) {
                    distanceFromStart = Math.sqrt(Math.pow(startx - inspect.lon(), 2)
                            + Math.pow(starty - inspect.lat(), 2));
                    startAndEndPoints[0] = inspect;
                }
                if (Math.sqrt(Math.pow(endx - inspect.lon(), 2)
                        + Math.pow(endy - inspect.lat(), 2)) < distanceFromEnd) {
                    distanceFromEnd = Math.sqrt(Math.pow(endx - inspect.lon(), 2)
                            + Math.pow(endy - inspect.lat(), 2));
                    startAndEndPoints[1] = inspect;
                }
            }
        }
        return startAndEndPoints;
    }

    /**
     * Clear the current found route, if it exists.
     */
    public static void clearRoute() {
        otherRoute.clear();
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public static List<String> getLocationsByPrefix(String prefix) {
        return new LinkedList<>();
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public static List<Map<String, Object>> getLocations(String locationName) {
        return new LinkedList<>();
    }
}
