import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class DoubleBufferedCanvas extends Canvas {
	Image offScreenImage;
	Dimension offScreenSize;
	Graphics offScreenGraphics;
		
	public void update(Graphics g) {
		// d is the size of the canvas
		Dimension d=size();
		
		// if image is null or isn't the size of the canvas, create a new image to draw onto
		if ((offScreenImage == null) || (d.width != offScreenSize.width) || (d.height != offScreenSize.height)) {
		    offScreenImage = createImage(d.width, d.height);
		    offScreenSize = d;
		    offScreenGraphics = offScreenImage.getGraphics();
		}
		
		// fill the background the canvas' background
		offScreenGraphics.setColor(getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		
		// paint onto the image normally
		paint(offScreenGraphics);
		
		// paint the image to the screen
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	public void paint(Graphics g) {
		paint((Graphics2D)g);
	}
	
	public void paint(Graphics2D g) {
		
	}
	
	public static void show(DoubleBufferedCanvas c, int w, int h) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(w+8, h+34);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(c, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}