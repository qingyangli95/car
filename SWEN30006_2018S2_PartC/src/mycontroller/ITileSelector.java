package mycontroller;

import java.util.List;

import utilities.Coordinate;

//strategy pattern
public interface ITileSelector {
	//selects tile from an ordered list of coordinates
	public Coordinate selectTile(List<Coordinate> tiles, MyAIController mycontroller);
}
