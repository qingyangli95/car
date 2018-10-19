package mycontroller;

import utilities.Coordinate;

public class Node implements Comparable<Node>
{

    public Coordinate coord; 
    public Node parent; //where the node is connected from
    public int G;  //cost to move from start to here
    public int H;  // cost to move from here to destination

    public Node(int x, int y)
    {
        this.coord = new Coordinate(x, y);
    }
    
    public Node(Coordinate coord) {
    	this.coord = coord;
    }
    
    public Node(Coordinate coord, Node parent, int g, int h)
    {
        this.coord = coord;
        this.parent = parent;
        G = g;
        H = h;
    }

    @Override
    /** for use in priority queue */
    public int compareTo(Node o)
    {
        if (o == null) return -1;
        if (G + H > o.G + o.H)
            return 1;
        else if (G + H < o.G + o.H) return -1;
        return 0;
    }
}