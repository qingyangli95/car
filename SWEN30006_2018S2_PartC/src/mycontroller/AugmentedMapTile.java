package mycontroller;

import tiles.MapTile;

public class AugmentedMapTile {
	/*This class stores a MapTile and a boolean holding whether its type is known to us. It is essentially a data
	 structure. */
	public final int MOVE_SCORE = 1; //default cost to move kept in superclass
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
