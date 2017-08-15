import java.awt.*;

public class WedgeUnit {
	// private variables
	Point center;
	int angle;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public Point getCenter() {
		return center;
	}
	
	public int getStartAngle() {
		return angle;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public WedgeUnit(Dot centerDot, Dot controlDot1, Dot controlDot2) {
		center = centerDot.getLocation();
		Point control1 = controlDot1.getLocation();
		Point control2 = controlDot2.getLocation();
		
		boolean horizontal = (control1.y == control2.y);
		
		if (horizontal)
			if (control1.y < center.y)
				angle = 45;
			else
				angle = 225;
		else
			if (control1.x < center.x)
				angle = 135;
			else
				angle = 315;
	}
	
	public boolean equals(Object o) {
		return (o instanceof WedgeUnit) ? equals((WedgeUnit)o) : false;
	}
	
	public boolean equals(WedgeUnit w) {
		return (getStartAngle() == w.getStartAngle() && getCenter().equals(w.getCenter()));
	}
}