package mycontroller;


import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

/**
 * ClassName: AStar
 *
 * @author kesar
 * @Description: AStar Path Finding
 */
public class AStar {
    public final static int DIRECT_VALUE = 10; // Cost For action
    Queue<Node> openList = new PriorityQueue<>(); // 优先队列(升序)
    List<Node> closeList = new ArrayList<>();
    Node startPos;
    Node destination;
    WorldSpatial.Direction orientation;
    private HashMap<Coordinate, MapTile> updatedMap;
    private float speed;
    public AStar(Coordinate startPos, HashMap updatedMap, Coordinate destination, WorldSpatial.Direction orientation, float speed) {
        // pass in the current position as starrPos, pass in the current updatedMap
        this.startPos.coord = startPos;
        this.destination.coord = destination;
        this.updatedMap = updatedMap;
        this.orientation = orientation;
        this.speed = speed;
    }

    /**
     * Start the algorithm
     */
    public void start() {
        if (updatedMap == null) return;
        // clean
        openList.clear();
        closeList.clear();
        // 开始搜索
        openList.add(startPos);
        moveNodes();
    }

    /**
     * Move the current node, from open to close
     */
    private void moveNodes() {
        while (!openList.isEmpty()) {
            if (isCoordInClose(destination)) {
                drawPath(updatedMap, destination.coord);
                break;
            }
            Node current = openList.poll();
            closeList.add(current);
            addNeighborNodeInOpen(current);
        }
    }

    /**
     * Store the path in listOfPathTiles
     */
    public LinkedList drawPath(HashMap maps, Coordinate destination) {
        Node end = new Node(destination.x, destination.y);
        if (end == null || maps == null) return null;
        // store the path in the listOfPathTiles
        LinkedList<Coordinate> listOfPathTiles = new LinkedList<>();
        System.out.println("Total Cost：" + end.G);
        while (end != null) {
            Coordinate c = end.coord;
            listOfPathTiles.add(c);
            end = end.parent;
        }
        return listOfPathTiles;
    }

    /**
     * Add all the neighbors to OpenList
     */
    private void addNeighborNodeInOpen(Node current) {
        int x = current.coord.x;
        int y = current.coord.y;
        if(speed<=0){
            switch (orientation){
                case EAST:
                case WEST:
                    addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
                    addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
                case NORTH:
                case SOUTH:
                    addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
                    addNeighborNodeInOpen(current, x, y + 1, DIRECT_VALUE);
            }
        }

        else{
            switch (orientation){
                case EAST:
                    // north
                    addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
                    // east
                    addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
                    // south
                    addNeighborNodeInOpen(current, x, y + 1, DIRECT_VALUE);
                case SOUTH:
                    // east
                    addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
                    // south
                    addNeighborNodeInOpen(current, x, y + 1, DIRECT_VALUE);
                    // west
                    addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
                case NORTH:
                    // west
                    addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
                    // north
                    addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
                    // east
                    addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
                case WEST:
                    // west
                    addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
                    // north
                    addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
                    // south
                    addNeighborNodeInOpen(current, x, y + 1, DIRECT_VALUE);
            }
        }
        // west
        addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
        // north
        addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
        // east
        addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
        // south
        addNeighborNodeInOpen(current, x, y + 1, DIRECT_VALUE);
    }

    /**
     * Add one neighbor to OpenList
     */
    private void addNeighborNodeInOpen(Node current, int x, int y, int value) {
        if (canAddNodeToOpen(x, y)) {
            Coordinate coord = new Coordinate(x, y);
            int G = current.G + value; // Calculate the G value of neighbors
            Node child = findNodeInOpen(coord);
            if (child == null) {
                int H = calcH(destination.coord, coord); // Calculate the H value
                if (isEndNode(destination.coord, coord)) {
                    child = destination;
                    child.parent = current;
                    child.G = G;
                    child.H = H;
                } else {
                    child = new Node(coord, current, G, H);
                }
                openList.add(child);
            } else if (child.G > G) {
                child.G = G;
                child.parent = current;
                openList.add(child);
            }
        }
    }

    /**
     * Find Nodes in OpenList
     */
    private Node findNodeInOpen(Coordinate coord) {
        if (coord == null || openList.isEmpty()) return null;
        for (Node node : openList) {
            if (node.coord.equals(coord)) {
                return node;
            }
        }
        return null;
    }


    /**
     * Calculate H by Manhattan Distance
     */
    private int calcH(Coordinate end, Coordinate coord) {
        return Math.abs(end.x - coord.x)
                + Math.abs(end.y - coord.y);
    }

    /**
     * Judge whether current node is destination
     */
    private boolean isEndNode(Coordinate end, Coordinate coord) {
        return end.equals(coord);
    }

    /**
     * Whether the node can be added to OpenList
     */
    private boolean canAddNodeToOpen(int x, int y) {
        // Whether the point is in map
        Coordinate point = null;
        point.x = x;
        point.y = y;
        if (x < 0 || y < 0 || !updatedMap.containsKey(point)) return false;
        // Need to add the type mud
        if (!isLegal(point)) return false;
        // whether the point is in closeList
        return !isCoordInClose(x, y);
    }

    /**
     * Whether the coord is legal to visit
     *  Wall, Mud are illegal
     *  While speed = 0, left and right are illegal
     *  While speed >0 backward is illegal
     */
    private boolean isLegal(Coordinate coord){
        MapTile currentTile=updatedMap.get(coord);
        if(currentTile.isType(MapTile.Type.WALL)) return false;
        if((currentTile.isType(MapTile.Type.TRAP) && ((TrapTile)currentTile).getTrap().equals("mud"))) return false;
        return true;
        }


    /**
     * Whether the nodes are in close list
     */
    private boolean isCoordInClose(Node coord) {
        return coord != null && isCoordInClose(coord.coord.x, coord.coord.y);
    }

    /**
     * Whether the coordinates are in close list
     */
    private boolean isCoordInClose(int x, int y) {
        if (closeList.isEmpty()) return false;
        for (Node node : closeList) {
            if (node.coord.x == x && node.coord.y == y) {
                return true;
            }
        }
        return false;
    }
}

