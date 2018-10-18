package mycontroller;

import tiles.MapTile;

public class AugmentedMapTile {
	/*This class stores a MapTile and a boolean holding whether its type is known to us. It is essentially a data
	 structure. */
	
	private MapTile tile;
	private boolean visited;
	
	/** Constructor */
	public AugmentedMapTile(MapTile tile) {
		setTile(tile);
		visited = false;
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
	
}
