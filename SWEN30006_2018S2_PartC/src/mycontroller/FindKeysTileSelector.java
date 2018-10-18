package mycontroller;

import java.util.HashMap;
import java.util.List;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class FindKeysTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		HashMap<Coordinate, AugmentedMapTile> updatedMap = mycontroller.getUpdatedMap();
		
		for (Coordinate coord: tiles) {
			MapTile currentTile = updatedMap.get(coord).getTile();
			Type currentType = currentTile.getType();
			//go to lava if it contains a key we need
			if (currentType.equals(Type.TRAP) && ((TrapTile)currentTile).getTrap().equals("lava")) {
				int lavaTileKey = ((LavaTrap) updatedMap.get(coord)).getKey();
				if (lavaTileKey!=0 && !(mycontroller.getKeys().contains(lavaTileKey))) {
					return coord;
				}
			}
		}
		
		//no lava with a key we want
		return null;
	}

}
