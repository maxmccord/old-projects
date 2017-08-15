import java.awt.*;

public class Dot {
	// static dot types
	public static final int NULL = 0;
	public static final int BLANK = 1;
	public static final int CORNER = 2;
	public static final int EDGE = 3;
	public static final int INNER_CORNER = 4;
	public static final int INNER_EDGE = 5;
	public static final int INSIDE = 6;
	
	// a static Dot instance which represents the default dot, which has
	// no useful location, type, or neighbor information
	public static final Dot DEFAULT_DOT;
	
	static {
		DEFAULT_DOT = new DefaultDot(-1, -1);
	}
	
	private static class DefaultDot extends Dot {
		public DefaultDot(int x, int y) {
			super(x, y);
		}
		
		public int getType() {
			return NULL;
		}
	}
	
	// variables
	private Dot ne, nw, sw, se;
	private int x, y;
	private int type;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	// dot type
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	// neighbor information
	public int getNeighborCount() {
		int count = 0;
		if (!ne.isNull()) count++;
		if (!nw.isNull()) count++;
		if (!sw.isNull()) count++;
		if (!se.isNull()) count++;
		return count;
	}
	
	public int getNeighborCount(int type) {
		int count = 0;
		if (ne.getType() == type) count++;
		if (nw.getType() == type) count++;
		if (sw.getType() == type) count++;
		if (se.getType() == type) count++;
		return count;
	}
	
	public Dot getNE() {
		return ne;
	}
	
	public Dot getNW() {
		return nw;
	}
	
	public Dot getSW() {
		return sw;
	}
	
	public Dot getSE() {
		return se;
	}
	
	public void setNE(Dot ne) {
		this.ne = ne;
	}
	
	public void setNW(Dot nw) {
		this.nw = nw;
	}
	
	public void setSW(Dot sw) {
		this.sw = sw;
	}
	
	public void setSE(Dot se) {
		this.se = se;
	}
	
	public void setNeighbors(Dot ne, Dot nw, Dot sw, Dot se) {
		this.ne = ne;
		this.nw = nw;
		this.sw = sw;
		this.se = se;
	}
	
	// location info
	public Point getLocation() {
		return new Point(x, y);
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Dot(int x, int y) {
		this.x = x;
		this.y = y;
		this.type = BLANK;
		ne = nw = se = sw = DEFAULT_DOT;
	}
	
	////////////////////
	// PUBLIC METHODS //
	////////////////////
	
	public boolean isNull() {
		return (getType() == NULL);
	}
}