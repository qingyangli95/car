package mycontroller;

import mycontroller.MyAIController.State;
import tiles.MapTile;

public class AugmentedHealthTile extends AugmentedTrapTile {

	public AugmentedHealthTile(MapTile tile) {
		super(tile);
	}

	@Override
	public boolean goodToSelect(MyAIController controller) {
		//check if blacklisted
		if (getBlackListed()) {
			return false;
		}
		State condition = controller.getCurrentState();
		if (condition == State.HEALING) {
			return true;
		} else {
			return false;
		}
	}

}
