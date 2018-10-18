package mycontroller;

import java.lang.reflect.Array;
import java.util.*;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{
	private HashMap<Coordinate,AugmentedMapTile> updatedMap; //most up to date view of the entire map

	private double EPS = 1e-7;

	public MyAIController(Car car) {
		super(car);
		initMap();
		updateMap();
	}

	@Override
	public void update() {
		updateMap();
		Coordinate destination = getCoordinate(updatedMap);
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

	/**
	 * Need the current Position and destination
	 * The path is legal for car to move along. Only need to move to next node in the path in this method.
	 */
	private void moveTowards(Coordinate destination){
		Coordinate currentPos = new Coordinate(getPosition());
		WorldSpatial.Direction orientation = getOrientation();
		int velocity = getVelocity();
		Coordinate nextPos;
		AStar pathFinding= new AStar(this, destination);
		pathFinding.start();
		LinkedList<Coordinate> path = new LinkedList<>();
		if(pathFinding.listOfPathTiles!= null) {
			path.addAll(pathFinding.listOfPathTiles);
		}
		// move along the path

		nextPos= path.getLast(); //path starts at destination, finishes at next coordinate
		path.removeLast();
		if (currentPos.equals(nextPos)) {
			applyBrake();
			return;
		}
		
		//first deal with stationary case
		if (speed == 0)
		//first work out directional change
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
					if (speed)
			}
		} else if (currentPos.y > nextPos.y) {
			
		} else if (currentPos.x < nextPos.x) {
			
		} else if (currentPos.x < nextPos.x) {
			
		}
	}

	//initialises the map by putting all of the tiles into AugmentedMapTiles which
	//* just have a boolean value attached to each tile to make path finding easier */
	private void initMap() {
		HashMap<Coordinate, MapTile> tempMap = getMap();
		updatedMap = new HashMap<Coordinate, AugmentedMapTile>();
		MapTile tempMapTile;
		for (Coordinate coord: tempMap.keySet())
		{
			tempMapTile = tempMap.get(coord);
			AugmentedMapTile tempAugmentedMapTile = new AugmentedMapTile(tempMapTile);
			updatedMap.put(coord, tempAugmentedMapTile);
		}
	
	}
			
	/** Updates the map with the current view from the car using getView() */
	private void updateMap() {
		HashMap<Coordinate, MapTile> view = getView();
		for (Coordinate coord: view.keySet()) {
			updatedMap.get(coord).setVisited(true);
			if (updatedMap.containsKey(coord))
			{
				updatedMap.get(coord).setTile(view.get(coord));
				updatedMap.get(coord).setVisited(true);
			}

	}



}



}
