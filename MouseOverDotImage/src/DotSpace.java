import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class DotSpace {
	// private member variables
	int numImages;
	private BufferedImage  [] images;
	private ArrayList<Dot>    dots;
	
	/////////////////
	// CONSTRUCTOR //
	
	public DotSpace(BufferedImage masterImage) {
		// determine the number of images to be created by doing log_2(width) + 1
		numImages = (int)( Math.log(masterImage.getWidth()) / Math.log(2) ) + 1;
		
		// process the supplied image to create the different levels
		// lower index = bigger image
		images = new BufferedImage[numImages];
		images[0] = masterImage;
		for (int i = 1; i < numImages; i++)
			images[i] = ImageUtility.simplifyImage(images[i - 1]);
		
		dots = new ArrayList<Dot>();
		
		// the starting color will be color of the only pixel in the smallest image
		Color startColor = new Color(images[numImages - 1].getRGB(0, 0));
		
		// the first dot will be at the highest level (i.e. the smallest image)
		// the level is used to index the images array depending on how many times the dot
		// has been split into pieces
		dots.add(new Dot(
			Constants.CANVAS_SIZE,     // int diameter
			startColor,                // Color color
			Constants.CANVAS_SIZE / 2, // int x
			Constants.CANVAS_SIZE / 2, // int y
			Constants.CANVAS_SIZE / 2, // int targetX
			Constants.CANVAS_SIZE / 2, // int targetY
			0,                         // int imageX
			0,                         // int imageY
			numImages - 1              // int level
			));
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void update() {
		for (Dot d : dots)
			d.update();
	}
	
	public void paint(Graphics g) {
		for (Dot d : dots)
			d.paint(g);
	}
	
	// determines if there is a circle at the given point, and explodes it if necessary
	public void activate(int x, int y) {
		int numOfDots = dots.size();
		for (int i = 0; i < numOfDots; i++) {
			Dot d = dots.get(i);
			if (!d.isMoving() && d.getLevel() > 0 && d.getDiameter() > Constants.DOT_MIN_SIZE && d.intersects(x, y)) {
				// we're exploding a dot - replace it with new dots
				dots.remove(i);
				
				// these coordinates are used for the target locations of the four new dots
				int x1 = d.getX() - d.getDiameter() / 4;
				int x2 = d.getX() + d.getDiameter() / 4;
				int y1 = d.getY() - d.getDiameter() / 4;
				int y2 = d.getY() + d.getDiameter() / 4;
				
				// the next four dots will be one level deeper
				int newLevel = d.getLevel() - 1;
				
				// these are the coordinates of the pixels in the next image that these dots will represent
				int imageX1 = d.getImageX() * 2;
				int imageX2 = imageX1 + 1;
				int imageY1 = d.getImageY() * 2;
				int imageY2 = imageY1 + 1;
				
				// grab the colors of the four new pixels to display
				Color[] colors = new Color[4];
				colors[0] = new Color(images[newLevel].getRGB(imageX1, imageY1));
				colors[1] = new Color(images[newLevel].getRGB(imageX2, imageY1));
				colors[2] = new Color(images[newLevel].getRGB(imageX1, imageY2));
				colors[3] = new Color(images[newLevel].getRGB(imageX2, imageY2));
				
				// Ddd four new dots - they will start at the exploded dot's location, and
				// move to all four corners of the original dot.
				dots.add(new Dot(
					d.getDiameter() / 2, // int diameter
					colors[0],           // Color color
					d.getX(),            // int x
					d.getY(),            // int y
					x1,                  // int targetX
					y1,                  // int targetY
					imageX1,             // int imageX
					imageY1,             // int imageY
					newLevel             // int level
					));
					
				dots.add(new Dot(
					d.getDiameter() / 2, // int diameter
					colors[1],           // Color color
					d.getX(),            // int x
					d.getY(),            // int y
					x2,                  // int targetX
					y1,                  // int targetY
					imageX2,             // int imageX
					imageY1,             // int imageY
					newLevel             // int level
					));
					
				dots.add(new Dot(
					d.getDiameter() / 2, // int diameter
					colors[2],           // Color color
					d.getX(),            // int x
					d.getY(),            // int y
					x1,                  // int targetX
					y2,                  // int targetY
					imageX1,             // int imageX
					imageY2,             // int imageY
					newLevel             // int level
					));
					
				dots.add(new Dot(
					d.getDiameter() / 2, // int diameter
					colors[3],           // Color color
					d.getX(),            // int x
					d.getY(),            // int y
					x2,                  // int targetX
					y2,                  // int targetY
					imageX2,             // int imageX
					imageY2,             // int imageY
					newLevel             // int level
					));
				
				break;
			}
		}
	}
}










