package mycontroller;

import java.util.*;

import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{
	private HashMap<Coordinate,AugmentedMapTile> updatedMap; //most up to date view of the entire map
	private LinkedList<Coordinate> currentPath; 

	private final double EPS = 1e-7;

	public MyAIController(Car car) {
		super(car);
		initMap();
	}

	@Override
	public void update() {
		updateMap();
		Coordinate destination = getCoordinate(updatedMap);
		//try to get a path
		currentPath = pathFinder(destination);
		while (currentPath.size() == 0) {
			//couldn't find path, destination is bad
			updatedMap.get(destination).setBlackListed(true);
			//get new destination, find new route
			destination = getCoordinate(updatedMap);
			currentPath = pathFinder(destination);
		}
		moveTowards(destination);
	}


	private Coordinate getCoordinate(HashMap<Coordinate, AugmentedMapTile> map) {
		LinkedList<Coordinate> tiles = new LinkedList<Coordinate>();

		//insert coordinates
		tiles.addAll(map.keySet());

		//prioritise unvisited tiles first, then use distance
		tiles.sort(new Comparator<Coordinate>() {
			//sort such the closest unvisited tiles are at the front of the list
			@Override
			public int compare(Coordinate point1, Coordinate point2) {

				double distanceToPoint1 = distanceFrom(point1);
				double distanceToPoint2 = distanceFrom(point2);

				boolean visitedPoint1 = map.get(point1).getVisited();
				boolean visitedPoint2 = map.get(point2).getVisited();

				//first case is either both visited/unvisited so sort by distance
				if (!(visitedPoint1 || visitedPoint2) || (visitedPoint1 && visitedPoint2)) {
					if (Math.abs(distanceToPoint1 - distanceToPoint2) < EPS) {
						return 0;
					} else {
						return distanceToPoint1 < distanceToPoint2?-1:1; //better to go to tile 1 first
					}
				} else {
					return visitedPoint2?-1:1; //put tile1 infront
				}

			}

		});

		//use strategy pattern and concrete factory to select our tile
		ITileSelector tileSelector = TileSelectorFactory.getInstance().getTileSelector(this);
		Coordinate bestCoord = tileSelector.selectTile(tiles, this);

		//failed to select the most desirable tile, just go to default behaviour
		if (bestCoord == null) {
			tileSelector = TileSelectorFactory.getInstance().getDefaultTileSelector();
			bestCoord = tileSelector.selectTile(tiles, this);
		}

		return bestCoord;
	}

	/** helper function to calculate simple Euclidean distance between coordinates */
	private double distance(Coordinate point1, Coordinate point2) {
		return Math.sqrt(Math.pow(point1.x-point2.x, 2) + Math.pow(point1.y-point2.y, 2));
	}

	/** helper function to calculate distance from car to a point */
	public double distanceFrom(Coordinate point) {
		Coordinate currentCoord = new Coordinate(getPosition());
		return distance(currentCoord, point);
	}

	/** helper function to check whether we are on the same spot as a coordinate */
	public boolean isLocatedAt(Coordinate coord) {
		Coordinate ourCoord = new Coordinate(getPosition());
		return ourCoord.equals(coord);
	}

	public HashMap<Coordinate, AugmentedMapTile> getUpdatedMap() {
		return updatedMap;
	}

	
	/** Tries to find a path to a destination 
	 */
	private LinkedList<Coordinate> pathFinder(Coordinate destination) {
		AStar pathFinding= new AStar(this, destination);
		pathFinding.start();
		return pathFinding.listOfPathTiles;
	}
	
	/**
	 * Need the current Position and destination
	 * The path is legal for car to move along. Only need to move to next node in the path in this method.
	 */
	private void moveTowards(Coordinate destination){
		Coordinate currentPos = new Coordinate(getPosition());
		WorldSpatial.Direction orientation = getOrientation();
		float speed = getSpeed();
		Coordinate nextPos;

		//our last in the list is our own position
		currentPath.removeLast();
		
		if (currentPath.size() == 0) {
			//path doesn't want us to go anywhere
			applyBrake();
			return;
		}
		// move along the path
		
		nextPos = currentPath.getLast(); //path starts at destination, finishes at next coordinate
		System.out.println("next move: "+nextPos.x+","+nextPos.y);
		currentPath.removeLast();

		
		//first deal with stationary case
		if (Math.abs(speed) < EPS) {
			switch (orientation) {
			case NORTH:
				if (currentPos.y < nextPos.y) {
					applyForwardAcceleration();
				} else if (currentPos.y > nextPos.y) {
					applyReverseAcceleration();
				} else {
					tryToMove(); //need to turn but need some speed first
				}
			case SOUTH:
				if (currentPos.y < nextPos.y) {
					applyReverseAcceleration();
				} else if (currentPos.y > nextPos.y) {
					applyForwardAcceleration();
				} else {
					tryToMove(); //need to turn but need some speed first
				}
			case EAST:
				if (currentPos.x < nextPos.x) {
					applyForwardAcceleration();
				} else if (currentPos.x > nextPos.x) {
					applyReverseAcceleration();
				} else {
					tryToMove(); //need to turn but need some speed first
				}
			case WEST:
				if (currentPos.x < nextPos.x) {
					applyReverseAcceleration();
				} else if (currentPos.x > nextPos.x) {
					applyForwardAcceleration();
				} else {
					tryToMove(); //need to turn but need some speed first
				}
			}
			if (safeToMoveForward()) {
				applyForwardAcceleration();
			} else {
				applyReverseAcceleration(); // assume it's safe to move back then otherwise we can't win
			}
		}
			
		//deal with moving case
		if (currentPos.y < nextPos.y) {
			switch (orientation) {
			case NORTH:
				applyForwardAcceleration();
				break;
			case SOUTH:
				applyReverseAcceleration();
				break;
			case WEST:
				turnRight();
				break;
			case EAST:
				turnLeft();
				break;
			}
		} else if (currentPos.y > nextPos.y) {
			switch (orientation) {
			case NORTH:
				applyReverseAcceleration();
				break;
			case SOUTH:
				applyForwardAcceleration();
				break;
			case WEST:
				turnLeft();
				break;
			case EAST:
				turnRight();
				break;
			}
			
		} else if (currentPos.x < nextPos.x) {
			switch (orientation) {
			case WEST:
				applyReverseAcceleration();
				break;
			case EAST:
				applyForwardAcceleration();
				break;
			case NORTH:
				turnRight();
				break;
			case SOUTH:
				turnLeft();
				break;
			}
			
		} else if (currentPos.x > nextPos.x) {
			switch (orientation) {
			case WEST:
				applyForwardAcceleration();
				break;
			case EAST:
				applyReverseAcceleration();
				break;
			case NORTH:
				turnLeft();
				break;
			case SOUTH:
				turnRight();
				break;
			}
			
		}
	}

	//initialises the map by putting all of the tiles into AugmentedMapTiles which
	//* just have a boolean value attached to each tile to make path finding easier */
	private void initMap() {
		HashMap<Coordinate, MapTile> tempMap = getMap();
		updatedMap = new HashMap<Coordinate, AugmentedMapTile>();
		MapTile tempMapTile;
		for (Coordinate coord: tempMap.keySet()) {
			tempMapTile = tempMap.get(coord);
			AugmentedMapTile tempAugmentedMapTile = new AugmentedMapTile(tempMapTile);
			updatedMap.put(coord, tempAugmentedMapTile);
		}
	
	}
			
	/** Updates the map with the current view from the car using getView() */
	private void updateMap() {
		HashMap<Coordinate, MapTile> view = getView();
		for (Coordinate coord: view.keySet()) {
			if (updatedMap.containsKey(coord)) {
				updatedMap.get(coord).setTile(view.get(coord));
				updatedMap.get(coord).setVisited(true);
			}

		}
	}
		
	/** helper function to establish whether it's safe to accelerate forward */
	private boolean safeToMoveForward() {
		Coordinate currentPos = new Coordinate(getPosition());
		Coordinate nextPos = null;
		WorldSpatial.Direction orientation = getOrientation();
		switch (orientation) {
			case NORTH:
				nextPos = new Coordinate(currentPos.x, currentPos.y+1);
				break;
			case SOUTH:
				nextPos = new Coordinate(currentPos.x, currentPos.y-1);
				break;
			case EAST:
				nextPos = new Coordinate(currentPos.x+1, currentPos.y);
				break;
			case WEST:
				nextPos = new Coordinate(currentPos.x-1, currentPos.y);
		}
		
		MapTile nextTile = updatedMap.get(nextPos).getTile();
		if (nextTile.isType(Type.WALL) || 
				(nextTile.isType(Type.TRAP) && ((TrapTile)nextTile).getTrap() == "mud")) {
			return false;
		} else {
			return true;
		}
				
	}
	
	/** tries to get the car moving from stationary point */
	private void tryToMove() {
		if (safeToMoveForward()) {
			applyForwardAcceleration();
		} else {
			applyReverseAcceleration(); // assume it's okay to reverse
		}
	}
	
	/** helper function to get current tile we're on */
	public MapTile getMapTile() {
		return updatedMap.get(new Coordinate(getPosition())).getTile();
	}
	
	/** helper function to get current coordinate
	 */
	
	public Coordinate getCoordinate() {
		return new Coordinate(getPosition());
	}

}
