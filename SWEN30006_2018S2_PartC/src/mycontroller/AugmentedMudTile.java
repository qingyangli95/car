package mycontroller;

import tiles.MapTile;

public class AugmentedMudTile extends AugmentedTrapTile {

	public AugmentedMudTile(MapTile tile) {
		super(tile);
	}

	@Override
	public boolean goodToSelect(MyAIController controller) {
		// never good
		return false;
	}
	
	public boolean isLegal() {
		return false; //will die
	}
	

}
