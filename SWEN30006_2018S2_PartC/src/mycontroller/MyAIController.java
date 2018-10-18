package mycontroller;

import java.lang.reflect.Array;
import java.util.*;

import sun.plugin.dom.core.CoreConstants;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{
	private HashMap<Coordinate, MapTile> visitedTiles; //tiles that have come into our view i.e updated
	private HashMap<Coordinate, MapTile> updatedMap; //most up to date view of the entire map
	
	private double EPS = 1e-7;
	
	private float Max_Speed=1;
	private float Min_Speed=-1;
	
	public MyAIController(Car car) {
		super(car);
		updatedMap = new HashMap<Coordinate, MapTile>();
		updatedMap.putAll(getMap()); //copy in map
		visitedTiles = new HashMap<Coordinate, MapTile>();
		visitedTiles.putAll(getView()); //copy current starting view
	}

	@Override
	public void update() {
		Coordinate destination = getCoordinate(visitedTiles, updatedMap);
		
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

	public HashMap<Coordinate, MapTile> getUpdatedMap() {
		return updatedMap;
	}

    /**
     * Need the current Position and destination
	 * The path is legal for car to move along. Only need to move to next node in the path in this method.
     */
	private void moveTowards(Coordinate currentPos, Coordinate destination){
		WorldSpatial.Direction orientation= getOrientation();
		float speed= getSpeed();
		AStar pathFinding= new AStar(currentPos,updatedMap,destination,orientation,speed);
		pathFinding.start();
		LinkedList<Coordinate> path = new LinkedList<>();
		if(pathFinding.drawPath(updatedMap,destination) != null) {
			path.addAll(pathFinding.drawPath(updatedMap, destination));
		}
		// move along the path

		while (currentState==State.FINDING_KEYS) {
			Coordinate nextPos= path.getLast();
			path.removeLast();
			switch (orientation) {
				case WEST:
					if (currentPos.y < nextPos.y) turnLeft();
					else if(currentPos.y >nextPos.y) turnRight();
					else if (currentPos.x> nextPos.x && speed<Max_Speed) applyForwardAcceleration();
					else if (currentPos.x<nextPos.x && speed > Min_Speed) applyReverseAcceleration();
					if (currentPos.equals(nextPos)) break;
				case EAST:
					if (currentPos.y < nextPos.y) turnRight();
					else if(currentPos.y >nextPos.y) turnLeft();
					else if (currentPos.x> nextPos.x && speed > Min_Speed) applyReverseAcceleration();
					else if (currentPos.x<nextPos.x && speed< Max_Speed) applyForwardAcceleration();
					if (currentPos.equals(nextPos)) break;
				case NORTH:
					if (currentPos.y < nextPos.y && speed > Min_Speed) applyReverseAcceleration();
					else if(currentPos.y >nextPos.y && speed < Max_Speed) applyForwardAcceleration();
					else if (currentPos.x> nextPos.x) turnLeft();
					else if (currentPos.x<nextPos.x) turnRight();
					if (currentPos.equals(nextPos)) break;
				case SOUTH:
					if (currentPos.y < nextPos.y && speed < Max_Speed) applyForwardAcceleration();
					else if(currentPos.y >nextPos.y && speed > Min_Speed) applyReverseAcceleration();
					else if (currentPos.x> nextPos.x) turnRight();
					else if (currentPos.x<nextPos.x) turnLeft();
					if (currentPos.equals(nextPos)) break;
			}
		}
		applyBrake(); //brake when arrived destination
	}

	ialises the map by putting all of the tiles into AugmentedMapTiles which
	 * just have a boolean value attached to each tile to make path finding easier */
	private void initMap() {
		HashMap<Coordinate, MapTile> tempMap = getMap();
		map = new HashMap<Coordinate, AugmentedMapTile>();
		
		for (Coordinate coord: tempMap.keySet()) {
			MapTile = tempMap.get(coord);
			
			AugmentedMapTile tempMapTile = new AugmentedMapTile(MapTile, false);
			map.put(coord, AugmentedTile)
		}
		 updatedMap();
		
		/** Get map dimensions */
		int i, j;
		for (i = 0; tempMap.containsKey(new Coordinate(i,0)); i++);
		int mapWidth = i;
		int mapHeight = tempMap.size()/i;
		Coordinate n;
		
		/** copy contents of tempMap into map with the AugmentedMapTile */
		for (i = 0; i <= mapWidth; i++) {
			for (j = 0; j <= mapHeight; j++) {
				n = new Coordinate(i, j);
				map.put(n, new AugmentedMapTile(tempMap.get(n)));
				
			}
		}
	}
	/** Updates the map with the current view from the car using getView() */
	private void updateMap() {
		HashMap<Coordinate, MapTile> view = getView();
		//
		//
		//
		/** Get the cars position and view square */
		String[] posXY=getPosition().split(",");
		int posX = Integer.parseInt(posXY[0]);
		int posY = Integer.parseInt(posXY[1]);
		int viewSquare = getViewSquare();
       System.out.println("viewSquare:"+viewSquare);
		Coordinate n;	// Helper coordinate to simplify code and aid readability

       /**Update the current position is visited*/
       n=new Coordinate(posX, posY);
       if (map.containsKey(n)) {
           map.get(n).setVisited(true);}

		/** Update all tiles in map visible in view */
		int i, j;
		for (Coordinate coord: view.keySet()) {
			map.get(coord).setVisited(true);
		}
		for (i = posX - viewSquare; i <= posX + viewSquare; i ++) {
			for (j = posY - viewSquare; j <= posY + viewSquare; j++) {
				n = new Coordinate(i, j);
				if (map.containsKey(n)) {
					map.get(n).setTile(view.get(n));
					//System.out.println(n.toString() + map.get(n).getTile().getType().toString());
				}
			}
		}
	}
	

	
}



}
