package mycontroller;

import java.util.List;

import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

//look for health traps!
public class LowHealthTileSelector implements ITileSelector {

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
			//go to health trap if we can find it
			if (currentType.equals(Type.TRAP) && ((TrapTile)currentMapTile).getTrap().equals("health")) {
				return coord;
			}
		}

		return null;
	}

}
