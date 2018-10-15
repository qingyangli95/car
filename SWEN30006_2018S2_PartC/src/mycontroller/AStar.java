package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

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
    private HashMap<Coordinate, MapTile> updatedMap;

    public AStar(Coordinate startPos, HashMap updatedMap, Coordinate destination) {
        // pass in the current position as starrPos, pass in the current updatedMap
        this.startPos.coord = startPos;
        this.destination.coord = destination;
        this.updatedMap = updatedMap;
    }

    /**
     * 开始算法
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
     * 移动当前结点
     */
    private void moveNodes() {
        while (!openList.isEmpty()) {
            if (isCoordInClose(destination)) {
                drawPath(updatedMap, destination);
                break;
            }
            Node current = openList.poll();
            closeList.add(current);
            addNeighborNodeInOpen(current);
        }
    }

    /**
     * 在二维数组中绘制路径
     */
    private void drawPath(HashMap maps, Node end) {
        if (end == null || maps == null) return;
        // store the path in the listOfPathTiles
        LinkedList<Coordinate> listOfPathTiles = new LinkedList<>();
        System.out.println("Total Cost：" + end.G);
        while (end != null) {
            Coordinate c = end.coord;
            listOfPathTiles.add(c);
            end = end.parent;
        }
    }

    /**
     * Add all the neighbors to OpenList
     */
    private void addNeighborNodeInOpen(Node current) {
        int x = current.coord.x;
        int y = current.coord.y;
        // 左
        addNeighborNodeInOpen(current, x - 1, y, DIRECT_VALUE);
        // 上
        addNeighborNodeInOpen(current, x, y - 1, DIRECT_VALUE);
        // 右
        addNeighborNodeInOpen(current, x + 1, y, DIRECT_VALUE);
        // 下
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
     * 判断结点能否放入Open列表
     */
    private boolean canAddNodeToOpen(int x, int y) {
        // Whether the point is in map
        Coordinate point = null;
        point.x = x;
        point.y = y;
        if (x < 0 || y < 0 || !updatedMap.containsKey(point)) return false;
        // Need to add the type mud
        if (updatedMap.get(point).isType(MapTile.Type.WALL)) return false;
        // whether the point is in closeList
        return !isCoordInClose(x, y);
    }

    /**
     * 判断坐标是否在close表中
     */
    private boolean isCoordInClose(Node coord) {
        return coord != null && isCoordInClose(coord.coord.x, coord.coord.y);
    }

    /**
     * 判断坐标是否在close表中
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

