import java.awt.*;

public class RibbonUnit {
	// public variables
	private Dot top, right, bottom, left;
	private boolean leftToRight;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public Dot getTop() {
		return top;
	}
	
	public Dot getRight() {
		return right;
	}
	
	public Dot getBottom() {
		return bottom;
	}
	
	public Dot getLeft() {
		return left;
	}
	
	public boolean isLeftToRight() {
		return leftToRight;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public RibbonUnit(Dot top, boolean leftToRight) {
		this(top, top.getSE(), top.getSE().getSW(), top.getSW(), leftToRight);
	}
	
	public RibbonUnit(Dot top, Dot right, Dot bottom, Dot left, boolean leftToRight) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		this.leftToRight = leftToRight;
	}
}