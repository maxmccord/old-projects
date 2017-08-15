import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

public class DotCanvas extends DoubleBufferedCanvas implements MouseMotionListener {
	// private member variables
	DotSpace space;
	
	/////////////////
	// CONSTRUCTOR //
	
	public DotCanvas(String imagePath) {
		setPreferredSize(new Dimension(Constants.CANVAS_SIZE, Constants.CANVAS_SIZE));
		addMouseMotionListener(this);
		
		// load the image to be passed into the DotSpace
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException ex) {
			// couldn't read image - just exit the program
			System.out.println("Couldn't read image from file.");
			System.exit(1);
		}
		
		// create the dot space with the loaded image
		space = new DotSpace(image);
		
		// create the timer used to animate the dots
		javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() {
				public void actionPerformed(ActionEvent e) { update(); }
			});
		timer.start();
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void update() {
		space.update();
		
		repaint();
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		space.paint(g);
	}
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	
	public void mouseMoved(MouseEvent e) {
		// send mouse location to DotSpace
		space.activate(e.getX(), e.getY());
	}
	
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
}