package mycontroller;

import world.WorldSpatial;
import world.WorldSpatial.Direction;

/* information required for path finding to work */
public interface PathComponent {
	public boolean isLegal(); //whether we can move to that spot
	public int getGScore(MyAIController controller); //cost to move 
	 //directions we can move in from a spot
	public WorldSpatial.Direction[] movableDirections(Direction currentOrientation);
}
