package mycontroller;

import java.lang.reflect.Constructor;

import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;

public class AugmentedTileFactory {
	private static AugmentedTileFactory instance;
	

	public static AugmentedTileFactory getInstance() {
		if (instance == null) {
			instance = new AugmentedTileFactory();
		}
		return instance;
	}
	
	public AugmentedMapTile getAugmentedMapTile(MapTile mapTile) {
		//initialise to null for now
		Class<?> mapTileClass = null;
		Constructor<?> mapTileConstructor = null;
		AugmentedMapTile augMapTile = null;
		
		//Split into the different maptile types
		try {
			String centralName = null;
			if (mapTile.isType(Type.TRAP)) {
				String trap = ((TrapTile)mapTile).getTrap();
				//capitalise first letter
				centralName = trap.substring(0, 1).toUpperCase()+trap.substring(1);
			} else if (mapTile.isType(Type.ROAD) || 
					mapTile.isType(Type.START)) { //no difference for algorithms
				centralName = "Road";	
			} else if (mapTile.isType(Type.FINISH)) {
				centralName = "Finish";
			} else {
				//just use default
				centralName = "Map";
			}
			mapTileClass = Class.forName("mycontroller.Augmented"+centralName+"Tile");
			mapTileConstructor = mapTileClass.getConstructor(MapTile.class);
			augMapTile = (AugmentedMapTile) mapTileConstructor.newInstance(mapTile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return augMapTile;
		
	}
	
}
