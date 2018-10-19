package mycontroller;

import tiles.MapTile;

public class AugmentedMapTile {
	/*This class stores a MapTile and a boolean holding whether its type is known to us. 
	 * It is essentially a data structure. Used as a superclass for more important indirect classes */
	public final int MOVE_SCORE = 1; //default cost to move kept in superclass
	public final int HEALTH_IMPACT = 0;//default cost to health for moving to a tile
	public final double DELTA_SCALE = 0.25; //scales health impact
	
	private MapTile tile;
	private boolean visited;
	private boolean blackListed;
	
	/** Constructor */
	public AugmentedMapTile(MapTile tile) {
		setTile(tile);
		visited = false;
		blackListed = false;
	}
	
	/** Getters and setters */
	public MapTile getTile() {
		return this.tile;
	}
	public void setTile(MapTile tile) {
		this.tile = tile;
	}
	public boolean getVisited() {
		return this.visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public boolean getBlackListed() {
		return this.blackListed;
	}
	public void setBlackListed(boolean blackList) {
		blackListed = blackList;
	}
	
}
