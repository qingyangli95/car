package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial.Direction;

import java.util.*;

/**
 * ClassName: AStar
 *
 * @author kesar
 * @Description: AStar Path Finding
 */
public class AStar implements IPathFinder {
    Queue<Node> openList;
    List<Node> closeList;
    LinkedList<Coordinate> listOfPathTiles;
    Node startPos;
    Node destination;
    Direction startOrientation;
    private HashMap<Coordinate, AugmentedMapTile> updatedMap;
    MyAIController controller;

    public AStar(MyAIController controller) {
    	this.controller = controller;
    }
    
    @Override
	public LinkedList<Coordinate> getPath(Coordinate destination) {
    	if (destination == null) {
    		//empty path
    		return new LinkedList<Coordinate>();
    	}
    	//initialise useful values
    	this.destination = new Node(destination);
    	startOrientation = controller.getOrientation();
    	updatedMap = controller.getUpdatedMap();
    	startPos = new Node(controller.getCoordinate());
    	listOfPathTiles = new LinkedList<Coordinate>();
    	openList = new PriorityQueue<Node>();
    	closeList = new ArrayList<Node>();
    	
    	//create best path
    	start();
    	
    	//considered danger of tiles but still path may not be safe enough
    	if (!safePath()) {
    		listOfPathTiles.clear();
    	}
    	//done
		return listOfPathTiles;
	}

    /**
     * Start the algorithm
     */
    public void start() {
        if (updatedMap == null) return;
        // clean
        openList.clear();
        closeList.clear();
        //add our spot to open list to begin
        openList.add(startPos);
        moveNodes();
    }

    /**
     * Move the current node, from open to close
     */
    private void moveNodes() {
        while (!openList.isEmpty()) {
            if (isCoordInClose(destination)) {
                drawPath(destination);
                break;
            }

            Node current = openList.poll();
            closeList.add(current);
            //add all nodes that we can go to from current
            addNeighborNodeInOpen(current);
        }
    }

    /**
     * Store the path in listOfPathTiles
     */
    public void drawPath(Node end) {
        if (end == null || updatedMap == null) return;
        // store the path in the listOfPathTiles
        while (end != null) {
            Coordinate c = end.coord;
            listOfPathTiles.add(c);
            end = end.parent;
        }
        return;
    }

    /**
     * Add all the neighbors to OpenList
     */
    private void addNeighborNodeInOpen(Node current) {
    	AugmentedMapTile currentTile = updatedMap.get(current.coord);
    	//can't add if not a path component
    	if (!(currentTile instanceof PathComponent)) {
    		return;
    	}
    	int x = current.coord.x;
        int y = current.coord.y;
        Direction currentOrientation;
        if (current.parent == null) { //this is the first point
        	currentOrientation = startOrientation;
        } else {
        	//assumption: car moving forward or backwards doesn't affect the directions
        	//we can go to
        	Coordinate parentCoord = current.parent.coord;
        	if (Math.abs(parentCoord.y - y) > 0) { //we moved vertically
        		currentOrientation = Direction.SOUTH; //or NORTH, but due to our assumption..
        	} else {
        		currentOrientation = Direction.EAST;
        	}
        }
        
        
        Direction[] directions = ((PathComponent)currentTile).movableDirections(currentOrientation);
        int gValue = ((PathComponent)currentTile).getGScore(controller);
        
        //add in nodes from directions we can move to
        for (Direction direction : directions) {
        	switch (direction) {
        	case NORTH:
        		addNeighborNodeInOpen(current, x, y+1, gValue);
        		break;
        	case SOUTH:
        		addNeighborNodeInOpen(current, x, y-1, gValue);
        		break;
        	case EAST:
        		addNeighborNodeInOpen(current, x+1, y, gValue);
        		break;
        	case WEST: 
        		addNeighborNodeInOpen(current, x-1, y, gValue);
        		break;
        	}
        }

    }

    /**
     * Add one neighbor to OpenList, this handles scoring and legality of adding certain neighbours*/
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
        Coordinate point = new Coordinate(x, y);
        if (x < 0 || y < 0 || !updatedMap.containsKey(point)) return false;
        //must be a path component
        AugmentedMapTile mapTile = updatedMap.get(point);
        if (!(mapTile instanceof PathComponent)) return false;
        //check whether we can add
        if (!((PathComponent)mapTile).isLegal()) return false;
        // whether the point is in closeList
        return !isCoordInClose(x, y);
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
    
    /** if a path causes us to lose more than our half max health, we reject it, 
     * we need to be able to return safely too */
    private boolean safePath() {
    	float healthLoss = 0;
    	for (Coordinate coord: listOfPathTiles) {
    		AugmentedMapTile tile = updatedMap.get(coord);
    		//accumulate health loss from path
    		if (tile instanceof PathComponent) {
    			healthLoss += ((PathComponent)tile).getHealthScore();
    		}
    		if (Math.abs(healthLoss) >= controller.MAX_HEALTH/2) {
    			return false;
    		}
    	}
    	return true;
    }

}

