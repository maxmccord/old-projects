import static java.lang.System.*;
import java.awt.*;

public class AnimatedPoint {
	// private member variables
	private double x, y;
	private double endX, endY;
	private int color;
	private double vx, vy;
	private boolean done;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	
	public int getX() {
		return (int)x;
	}
	
	public int getY() {
		return (int)y;
	}
	
	public int getEndX() {
		return (int)endX;
	}
	
	public int getEndY() {
		return (int)endY;
	}
	
	public int getColor() {
		return color;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public AnimatedPoint(int startX, int startY, int endX, int endY, int color) {
		this.x = startX;
		this.y = startY;
		this.endX = endX;
		this.endY = endY;
		this.color = color;
		
		// calculate motion vector based on supplied speed
		double theta = Math.atan((double)(endY-startY) / (endX-startX));
		this.vx = Math.abs(Math.cos(theta)*30);
		this.vy = Math.abs(Math.sin(theta)*30);
		if (endX < startX)
			vx *= -1;
		if (endY < startY)
			vy *= -1;
			
		done = false;
	}
	
	public void move() {
		if (!done) {
			double xo = x;
			double yo = y;
			x += vx;
			y += vy;
			
			if (Math.abs(xo-x) > Math.abs(xo-endX) || Math.abs(yo-y) > Math.abs(yo-endY)) {
				x = endX;
				y = endY;
				done = true;
			}
		}
	}
}