package mycontroller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;

public class MyAIController extends CarController{
	private HashMap<Coordinate, MapTile> visitedTiles; //tiles that have come into our view i.e updated
	private HashMap<Coordinate, MapTile> updatedMap; //most up to date view of the entire map
	
	private double EPS = 1e-7;
	
	private enum State {FINDING_KEYS, FOUND_KEYS};
	private State currentState;
	
	public MyAIController(Car car) {
		super(car);
		updatedMap = new HashMap<Coordinate, MapTile>();
		updatedMap.putAll(getMap()); //copy in map
		visitedTiles = new HashMap<Coordinate, MapTile>();
		visitedTiles.putAll(getView()); //copy current starting view
		currentState = State.FINDING_KEYS;
	}

	@Override
	public void update() {
		//update our state first
		if (getKeys().size() == numKeys()) {
			currentState = State.FOUND_KEYS;
		} else {
			currentState = State.FINDING_KEYS;
		}
		Coordinate destination = getCoordinate(visitedTiles, updatedMap);
		
	}

	
	private Coordinate getCoordinate(HashMap<Coordinate, MapTile> visitedTiles,
			HashMap<Coordinate, MapTile> updatedMap) {
		LinkedList<Coordinate> listOfTiles = new LinkedList<Coordinate>();
		
		//insert coordinates
		listOfTiles.addAll(updatedMap.keySet());
		
		//prioritise unvisited tiles first, then use distance
		listOfTiles.sort(new Comparator<Coordinate>() {

			@Override
			public int compare(Coordinate point1, Coordinate point2) {
				Coordinate currentCoord = new Coordinate(getPosition());
				
				double distanceToPoint1 = distance(currentCoord, point1);
				double distanceToPoint2 = distance(currentCoord, point2);
				
				boolean visitedPoint1 = visitedTiles.containsKey(point1);
				boolean visitedPoint2 = visitedTiles.containsKey(point1);
				
				//first case is either both visited/unvisited so sort by distance
				if (!(visitedPoint1 || visitedPoint2) || (visitedPoint1 && visitedPoint2)) {
					if (Math.abs(distanceToPoint1 - distanceToPoint2) < EPS) {
						return 0;
					} else {
						return distanceToPoint1 < distanceToPoint2? 1:-1; //better to go to tile 1
					}
				} else {
					return visitedPoint2?1:-1; //better to go to tile1 is already visited tile2
				}
				
			}
			
		});
		
		//select tile coordinate based on our state
		if (currentState.equals(State.FINDING_KEYS)) {
			for (Coordinate coord: listOfTiles) {
				MapTile currentTile = updatedMap.get(coord);
				Type currentType = currentTile.getType();
				//go to lava if it contains a key we need
				if (currentType.equals(Type.TRAP) && ((TrapTile)currentTile).getTrap().equals("lava")) {
					int lavaTileKey = ((LavaTrap) updatedMap.get(coord)).getKey();
					if (lavaTileKey!=0 && !(getKeys().contains(lavaTileKey))) {
						return coord;
					}
				}
			}
			//no lava to our knowledge.. just pick the closest road that isn't the same spot as
			//the car.
			for (Coordinate coord: listOfTiles) {
				Type currentType = updatedMap.get(coord).getType();
				Coordinate currentCoord = new Coordinate(getPosition());
				if (currentType.equals(Type.ROAD) && (distance(currentCoord, coord)>EPS)) {
					return coord;
				}
			}
			//there should always be a road to go to
		
		} else if (currentState.equals(State.FOUND_KEYS)) {
			//head to nearest exit
			for (Coordinate coord: listOfTiles) {
				if (updatedMap.get(coord).isType(Type.FINISH)) {
					return coord;
				}
			}
		}
		
		return null;
	}
	
	/** helper function to calculate simple Euclidean distance between coordinates */
	private double distance(Coordinate point1, Coordinate point2) {
		return Math.sqrt(Math.pow(point1.x-point2.x, 2) + Math.pow(point1.y-point2.y, 2));
	}

}
