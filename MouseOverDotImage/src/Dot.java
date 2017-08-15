import java.awt.*;

public class Dot {
	// private member variables
	private double x;
	private double y;
	private Color color;
	private int diameter;
	
	private int imageX;
	private int imageY;
	private int level;
	
	private int targetX;
	private int targetY;
	private double speed;
	private boolean isMoving;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	
	public int getDiameter() {
		return diameter;
	}
	
	public int getX() {
		return (int)x;
	}
	
	public int getY() {
		return (int)y;
	}
	
	public int getImageX() {
		return imageX;
	}
	
	public int getImageY() {
		return imageY;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	/////////////////
	// CONSTRUCTOR //
	
	public Dot(int diameter, Color color, int x, int y, int targetX, int targetY, int imageX, int imageY, int level) {
		this.diameter = diameter;
		this.color = color;
		this.x = x;
		this.y = y;
		this.targetX = targetX;
		this.targetY = targetY;
		
		this.imageX = imageX;
		this.imageY = imageY;
		this.level = level;
		
		speed = distance(x, y, targetX, targetY) / Constants.DOT_MOVE_TIME_TICKS;
		isMoving = true;
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void update() {
		if (isMoving) {
			if (Math.abs(targetX - x) < speed)
				x = targetX;
			else if (x < targetX)
				x += speed;
			else if (x > targetX)
				x -= speed;
				
			if (Math.abs(targetY - y) < speed)
				y = targetY;
			else if (y < targetY)
				y += speed;
			else if (y > targetY)
				y -= speed;
			
			if (x == targetX && y == targetY)
				isMoving = false;
		}
	}
	
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillOval((int)x - diameter / 2, (int)y  - diameter / 2, diameter, diameter);
		/*
		// create Graphics2D and save original paint
		Graphics2D g = (Graphics2D)gg;
		Paint p = g.getPaint();
		
		Color darker      = darkerColor(color);
		Color transDarker = new Color(
			darker.getRed(),
			darker.getBlue(),
			darker.getGreen(),
			0
			);
		
		float[] fractions = { 0.0f , 1.0f   };
		Color[] colors    = { color, darker };
		RadialGradientPaint paint = new RadialGradientPaint(x + diameter / 5, y - diameter / 5, diameter / 2, fractions, colors);
		g.setPaint(paint);
		
		g.fillOval(x - diameter / 2, y  - diameter / 2, diameter, diameter);
		
		// restore original paint
		g.setPaint(p);
		*/
	}
	
	// returns true if the specified point lies within the circle
	public boolean intersects(int px, int py) {
		return ( distance(x, y, px, py) < (diameter / 2) );
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private double distance(double x1, double y1, double x2, double y2) {
		return (int)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	private Color darkerColor(Color color) {
		final int delta = 20;
		
		return new Color(
			Math.max(0, color.getRed() - delta),
			Math.max(0, color.getGreen() - delta),
			Math.max(0, color.getBlue() - delta)
			);
	}
}





