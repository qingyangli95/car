package mycontroller;

import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

//look for health traps!
public class LowHealthTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		HashMap<Coordinate, MapTile> updatedMap = mycontroller.getUpdatedMap();
		
		for (Coordinate coord: tiles) {
			MapTile currentTile = updatedMap.get(coord);
			Type currentType = currentTile.getType();
			//go to health trap if we can find it
			if (currentType.equals(Type.TRAP) && ((TrapTile)currentTile).getTrap().equals("health")) {
				return coord;
			}
		}

		return null;
	}

}
