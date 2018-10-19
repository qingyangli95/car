package mycontroller;

import java.util.LinkedList;

import utilities.Coordinate;

public interface IPathFinder {
	//find a path. First coordinate is the destination, last coordinate is our current position
	public LinkedList<Coordinate> getPath(Coordinate destination);
}
