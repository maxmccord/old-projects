public class Point {
	public int x;
	public int y;
	
	public Point() {
		this.x = 0;
		this.y = 0;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Point p) {
		if (x == p.x)
			if (y == p.y)
				return true;
				
		return false;
	}
}