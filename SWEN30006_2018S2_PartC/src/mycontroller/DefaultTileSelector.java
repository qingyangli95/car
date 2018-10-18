package mycontroller;

import java.util.List;

import tiles.MapTile.Type;
import utilities.Coordinate;

//when we can't find/see the tiles we want 
public class DefaultTileSelector implements ITileSelector {

	@Override
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller) {
		//Default to picking the closest unvisited road that isn't the same spot as
		//the car.
		for (Coordinate coord: tiles) {
			//check if blacklisted
			AugmentedMapTile tileInQuestion = mycontroller.getUpdatedMap().get(coord);
			if (tileInQuestion.getBlackListed()) {
				continue;
			} 
			
			Type currentType = tileInQuestion.getTile().getType();
			if (currentType.equals(Type.ROAD) && !mycontroller.isLocatedAt(coord)) {
				return coord;
			}
		} 
		//should always be a road to go otherwise map is unwinnable...just stay on the same spot
		return mycontroller.getCoordinate();
	}

}
