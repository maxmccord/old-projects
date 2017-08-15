

public class Block {
	// constants
	static final int TYPE_REFLECT = 0;
	static final int TYPE_NONREFLECT = 1;
	static final int TYPE_GLASS = 2;
	
	// private members
	private int type;
	private boolean fixed;
	
	///////////////
	// ACCESSORS //
	
	public int getType() {
		return type;
	}
	
	public boolean isFixed() {
		return fixed;
	}
	
	////////////////
	// CONSTRUCTOR //
	
	public Block(int type) {
		this.type = type;
		fixed = false;
	}
	
	//////////////////////
	// PUBLIC FUNCTIONS //
	
	public void toggleFixed() {
		fixed = !fixed;
	}
}