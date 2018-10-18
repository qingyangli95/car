package mycontroller;

import java.util.List;

import tiles.MapTile.Type;
import utilities.Coordinate;

public class FoundKeysTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		//head to nearest exit
		for (Coordinate coord: tiles) {
			//check if blacklisted
			AugmentedMapTile tileInQuestion = mycontroller.getUpdatedMap().get(coord);
			if (tileInQuestion.getBlackListed()) {
				continue;
			} 
			if (tileInQuestion.getTile().isType(Type.FINISH)) {
				return coord;
			}
		}
		
		return null;
	}

}
