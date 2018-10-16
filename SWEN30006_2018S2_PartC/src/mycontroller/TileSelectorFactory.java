package mycontroller;

// Singleton factory pattern
public class TileSelectorFactory {
	private static TileSelectorFactory instance;
	private final int LOW_HEALTH = 40;
	
	public static TileSelectorFactory getInstance() {
		if (instance == null) {
			instance = new TileSelectorFactory();
		}
		return instance;
	}
	
	public ITileSelector getTileSelector(MyAIController mycontroller) {
		//select correct tile selector based on current state of controller
		//prioritise staying healthy!
		if (mycontroller.getHealth() <= LOW_HEALTH) {
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
