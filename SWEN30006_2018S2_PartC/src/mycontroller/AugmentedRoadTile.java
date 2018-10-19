package mycontroller;

import mycontroller.MyAIController.State;
import tiles.MapTile;
import world.WorldSpatial.Direction;

public class AugmentedRoadTile extends AugmentedMapTile implements Selectable, PathComponent {
	public AugmentedRoadTile(MapTile tile) {
		super(tile);
	}

	@Override
	public boolean isLegal() {
		return true;
	}

	@Override
	public int getGScore(MyAIController controller) {
		return MOVE_SCORE;
	}

	@Override
	public Direction[] movableDirections(Direction currentOrientation) {
		Direction[] directions = {Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST};
		return directions;
	}

	@Override
	public boolean goodToSelect(MyAIController controller) {
		//check if blacklisted
		if (getBlackListed()) {
			return false;
		}
		if (controller.getCurrentState() == State.DEFAULT && !getVisited()) {
			return true;
		} else {
			return false;
		}
	}

}
