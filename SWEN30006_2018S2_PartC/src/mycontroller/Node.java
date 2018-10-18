package mycontroller;

import utilities.Coordinate;

public class Node implements Comparable<Node>
{

    public Coordinate coord; // å��æ ‡
    public Node parent; // çˆ¶ç»“ç‚¹
    public int G; // Gï¼šæ˜¯ä¸ªå‡†ç¡®çš„å€¼ï¼Œæ˜¯èµ·ç‚¹åˆ°å½“å‰�ç»“ç‚¹çš„ä»£ä»·
    public int H; // Hï¼šæ˜¯ä¸ªä¼°å€¼ï¼Œå½“å‰�ç»“ç‚¹åˆ°ç›®çš„ç»“ç‚¹çš„ä¼°è®¡ä»£ä»·

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
    public int compareTo(Node o)
    {
        if (o == null) return -1;
        if (G + H > o.G + o.H)
            return 1;
        else if (G + H < o.G + o.H) return -1;
        return 0;
    }
}