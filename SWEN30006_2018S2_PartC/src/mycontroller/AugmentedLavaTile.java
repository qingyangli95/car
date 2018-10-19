package mycontroller;

import mycontroller.MyAIController.State;
import tiles.LavaTrap;
import tiles.MapTile;

public class AugmentedLavaTile extends AugmentedTrapTile {
	public final int SCORE_SCALE = 400;
	
	public AugmentedLavaTile(MapTile tile) {
		super(tile);
	}

	@Override
	public boolean goodToSelect(MyAIController controller) {
		//check if blacklisted
		if (getBlackListed()) {
			return false;
		}
		//check condition of controller first
		State condition = controller.getCurrentState();
		int key = ((LavaTrap)getTile()).getKey();
		if (condition == State.FINDING_KEYS && key!=0 && !controller.getKeys().contains(key)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getGScore(MyAIController controller) {
		//inversely proportional to the amount of health we have
		return (int)(SCORE_SCALE/controller.getHealth());
	}


}
