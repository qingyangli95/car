package mycontroller;

import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;

// Singleton factory pattern
public class TileSelectorFactory {
	private static TileSelectorFactory instance;
	private final int LOW_HEALTH = 40;
	private final int FULL_HEALTH = 100;
	private final double EPS = 1e-7; //for floating point comparison
	
	public static TileSelectorFactory getInstance() {
		if (instance == null) {
			instance = new TileSelectorFactory();
		}
		return instance;
	}
	
	public ITileSelector getTileSelector(MyAIController mycontroller) {
		//select correct tile selector based on current state of controller
		MapTile currentTile = mycontroller.getMapTile();
		//prioritise staying healthy! go to health tile if low or if still healing up
		if (mycontroller.getHealth() <= LOW_HEALTH || (currentTile.isType(Type.TRAP) && 
				((TrapTile)currentTile).getTrap()=="health") && 
				Math.abs(mycontroller.getHealth()-FULL_HEALTH) > EPS) {
			return new LowHealthTileSelector();
			
		} else if (mycontroller.getKeys().size() != mycontroller.numKeys()) { 
			//still need keys
			return new FindKeysTileSelector();
		} else if (mycontroller.getKeys().size() == mycontroller.numKeys()) {
			//done with finding keys
			return new FoundKeysTileSelector();
		} else {
			return null;  //shouldn't be here
		}
	}
	
	/**
	 * Used when we are unable to find the tile we want
	 * @return A default tile selector
	 */
	public ITileSelector getDefaultTileSelector() {
		return new DefaultTileSelector();
	}
}
