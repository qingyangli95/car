package mycontroller;

import java.util.List;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class FindKeysTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		
		for (Coordinate coord: tiles) {
			//check if blacklisted
			AugmentedMapTile tileInQuestion = mycontroller.getUpdatedMap().get(coord);
			if (tileInQuestion.getBlackListed()) {
				continue;
			} 
			
			MapTile currentMapTile = tileInQuestion.getTile();
			Type currentType = currentMapTile.getType();
			//go to lava if it contains a key we need
			if (currentType.equals(Type.TRAP) && ((TrapTile)currentMapTile).getTrap().equals("lava")) {
				int lavaTileKey = ((LavaTrap) currentMapTile).getKey();
				if (lavaTileKey!=0 && !(mycontroller.getKeys().contains(lavaTileKey))) {
					return coord;
				}
			}
		}
		
		//no lava with a key we want
		return null;
	}

}
