package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import controller.CarController;
import world.Car;

public class MyAIController extends CarController{
	private HashMap<Coordinate, MapTile> visitedTiles; //tiles that have come into our view i.e updated
	private HashMap<Coordinate, MapTile> updatedMap; //most up to date view of the entire map
	
	public MyAIController(Car car) {
		super(car);
		updatedMap = new HashMap<Coordinate, MapTile>();
		updatedMap.putAll(getMap()); //copy in map
		visitedTiles = new HashMap<Coordinate, MapTile>();
		visitedTiles.putAll(getView()); //copy current starting view
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
