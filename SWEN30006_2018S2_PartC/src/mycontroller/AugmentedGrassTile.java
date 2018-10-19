package mycontroller;

import tiles.MapTile;
import world.WorldSpatial.Direction;

public class AugmentedGrassTile extends AugmentedTrapTile {

	public AugmentedGrassTile(MapTile tile) {
		super(tile);
	}
	
	public Direction[] movableDirections(Direction currentOrientation) {
		Direction[] directions = new Direction[2];
		//can only move in a line
		switch (currentOrientation) {
		case EAST:
		case WEST:
			directions[0] = Direction.EAST;
			directions[1] = Direction.WEST;
			break;
		case SOUTH:
		case NORTH:
			directions[0] = Direction.SOUTH;
			directions[1] = Direction.NORTH;
			break;
		}
		return directions;
		
	}

}
