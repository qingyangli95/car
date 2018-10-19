package mycontroller;

import tiles.MapTile;
import world.WorldSpatial.Direction;

//defines some default behaviour
public abstract class AugmentedTrapTile extends AugmentedMapTile implements Selectable, PathComponent {
	public AugmentedTrapTile(MapTile tile) {
		super(tile);
	}

	public boolean isLegal() {
		return true;
	}
	
	public int getGScore(MyAIController controller) {
		return MOVE_SCORE;
	}
	
	public Direction[] movableDirections(Direction currentOrientation) {
		Direction[] directions = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
		return directions;
	}
	
	public float getHealthScore() {
		return HEALTH_IMPACT;
	}

}
