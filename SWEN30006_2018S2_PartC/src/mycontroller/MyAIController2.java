package mycontroller;

import java.util.HashMap;
import controller.CarController;
import tiles.MapTile;
import world.Car;
import utilities.Coordinate;



public class MyAIController extends CarController{
	
	private HashMap<Coordinate, AugmentedMapTile> map;


	public MyAIController(Car car) {
		super(car);
		initMap();
	}

    @Override
	public void update()
    {
		updateMap();
    }

    /** Initialises the map by putting all of the tiles into AugmentedMapTiles which
	 * just have a boolean value attached to each tile to make path finding easier */
	private void initMap() {
		HashMap<Coordinate, MapTile> tempMap = getMap();
		map = new HashMap<Coordinate, AugmentedMapTile>();
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









