import java.awt.*;

public class Player {
	// player information
	private String name;
	private Color color;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
	}
}