package mycontroller;

import java.util.List;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class FoundKeysTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		//head to nearest exit
		for (Coordinate coord: tiles) {
			MapTile currentTile = mycontroller.getUpdatedMap().get(coord).getTile();
			if (currentTile.isType(Type.FINISH)) {
				return coord;
			}
		}
		
		return null;
	}

}
