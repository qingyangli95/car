package mycontroller;

import java.util.*;

import tiles.MapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{
	private HashMap<Coordinate,AugmentedMapTile> updatedMap; //most up to date view of the entire map
	private LinkedList<Coordinate> currentPath; 
	private IPathFinder pathFinder;

	private final double EPS = 1e-7; //for floating point comparison
	private final int LOW_HEALTH = 50; //arbitrary low point
	public final int MAX_HEALTH = 100;
	private float lastHealth; //to check whether we're healing from some source
	
	public enum State {DEFAULT, FINDING_KEYS, FOUND_KEYS, HEALING};
	private State currentState;
	
	public MyAIController(Car car) {
		super(car);
		initMap();
		setCurrentState(State.FINDING_KEYS);
		pathFinder = new AStar(this);
		lastHealth = getHealth();
	}

	@Override
	public void update() {
		updateMap();
		updateState();
		Coordinate destination = getDestCoordinate(updatedMap);
		//try to get a path
		currentPath = pathFinder.getPath(destination);
		while (currentPath.size() == 0) {
			//couldn't find path, destination is bad
			updatedMap.get(destination).setBlackListed(true);
			//get new destination, find new route
			destination = getDestCoordinate(updatedMap);
			currentPath = pathFinder.getPath(destination);
		}
		moveTowards(destination);
	}

	/** Uses an up to date map of the world to select the best coordinate to head towards */
	private Coordinate getDestCoordinate(HashMap<Coordinate, AugmentedMapTile> updatedMap) {
		LinkedList<Coordinate> tiles = new LinkedList<Coordinate>();

		//insert coordinates
		tiles.addAll(updatedMap.keySet());

		//prioritise unvisited tiles first, then use distance
		tiles.sort(new Comparator<Coordinate>() {
			//sort such the closest unvisited tiles are at the front of the list
			@Override
			public int compare(Coordinate point1, Coordinate point2) {

				double distanceToPoint1 = distanceFrom(point1);
				double distanceToPoint2 = distanceFrom(point2);

				boolean visitedPoint1 = updatedMap.get(point1).getVisited();
				boolean visitedPoint2 = updatedMap.get(point2).getVisited();

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

		AugmentedMapTile tileInQuestion;
		Coordinate bestCoord = null; 
		//let Augmented tiles use their own strategy to work out whether we should take it
		for (Coordinate coord : tiles) {
			tileInQuestion = updatedMap.get(coord);
			if (tileInQuestion instanceof Selectable && 
					((Selectable)tileInQuestion).goodToSelect(this)) {
				bestCoord = coord;
				break;
			}
		}

		//failed to select the most desirable tile, just go to default behaviour
		if (bestCoord == null) {
			currentState = State.DEFAULT;
		}
		
		//Refind appropriate tile, should always be something we can default to
		for (Coordinate coord : tiles) {
			tileInQuestion = updatedMap.get(coord);
			if (tileInQuestion instanceof Selectable && 
					((Selectable)tileInQuestion).goodToSelect(this)) {
				bestCoord = coord;
				break;
			}
		}
		
		return bestCoord;
	}

	/** helper function to calculate Manhattan distance between coordinates */
	private double distance(Coordinate point1, Coordinate point2) {
		return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
	}

	/** helper function to calculate distance from car to a point */
	public double distanceFrom(Coordinate point) {
		Coordinate currentCoord = new Coordinate(getPosition());
		return distance(currentCoord, point);
	}

	public HashMap<Coordinate, AugmentedMapTile> getUpdatedMap() {
		return updatedMap;
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

	//initialises the map by putting all of the tiles into AugmentedMapTiles (indirect object) */
	private void initMap() {
		HashMap<Coordinate, MapTile> tempMap = getMap();
		updatedMap = new HashMap<Coordinate, AugmentedMapTile>();
		MapTile tempMapTile;
		//singleton factory pattern
		AugmentedTileFactory tileCreator = AugmentedTileFactory.getInstance();
		for (Coordinate coord: tempMap.keySet()) {
			tempMapTile = tempMap.get(coord);
			//convert to indirect object for us to use
			AugmentedMapTile tempAugmentedMapTile = tileCreator.getAugmentedMapTile(tempMapTile);
			updatedMap.put(coord, tempAugmentedMapTile);
		}
	
	}
			
	/** Updates the map with the current view from the car using getView() */
	private void updateMap() {
		HashMap<Coordinate, MapTile> view = getView();
		for (Coordinate coord: view.keySet()) {
			//factory pattern
			AugmentedTileFactory tileCreator = AugmentedTileFactory.getInstance();
			if (updatedMap.containsKey(coord)) {
				//add in correct type of augmented map tile
				updatedMap.remove(coord);
				updatedMap.put(coord, tileCreator.getAugmentedMapTile(view.get(coord)));
				updatedMap.get(coord).setVisited(true);
			}

		}
	}
	
	/** updates the state of our controller */
	private void updateState() {
		//prioritise staying healthy! if we just got healed by something, stay there, probably good
		if (getHealth() <= LOW_HEALTH || lastHealth < getHealth()) {
			setCurrentState(State.HEALING);		
		} else if (getKeys().size() == numKeys()) {
			//done with finding keys
			setCurrentState(State.FOUND_KEYS);
		} else { 		
			//still need keys
			setCurrentState(State.FINDING_KEYS);
		}
		lastHealth = getHealth(); //update health condition
		return;
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
		
		if (updatedMap.get(nextPos) instanceof PathComponent &&
				((PathComponent)updatedMap.get(nextPos)).isLegal()) {
			return true;
		} else {
			return false;
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
	public AugmentedMapTile getAugmentedMapTile() {
		return updatedMap.get(getCoordinate());
	}
	
	/** helper function to get current coordinate
	 */
	
	public Coordinate getCoordinate() {
		return new Coordinate(getPosition());
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	
}
