import java.awt.*;

public class LaserNode {
	// constants
	public static final int DIR_NE = 0;
	public static final int DIR_NW = 1;
	public static final int DIR_SE = 2;
	public static final int DIR_SW = 3;
	
	// private members
	private Point loc;
	private int dir;
	
	///////////////
	// ACCESSORS //
	
	public Point getLoc() {
		return loc;
	}
	
	public int getDir() {
		return dir;
	}
	
	//////////////////
	// CONSTRUCTORS //
	
	public LaserNode(Point loc, int dir) {
		this.loc = new Point(loc.x, loc.y);
		this.dir = dir;
	}
	
	public LaserNode(int x, int y, int dir) {
		this(new Point(x, y), dir);
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void rotateDir() {
		switch (dir) {
			case DIR_NE: dir = DIR_SE; break;
			case DIR_SE: dir = DIR_SW; break;
			case DIR_SW: dir = DIR_NW; break;
			case DIR_NW: dir = DIR_NE; break;
		}
	}
	
	public int hashCode() {
		int x = (loc.x-130)/30;
		int y = (loc.y-40)/30;
		return (x*13*4 + y*4 + dir);
	}
	
	public boolean equals(Object o) {
		if (o instanceof LaserNode) {
			LaserNode node = (LaserNode)o;
			return ( hashCode() == node.hashCode() );
		} else
			return false;
	}
}