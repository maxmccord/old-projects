// Quick dev test to test out the simplifyImage(...) function.
// Press up and down on the arrow keys to view varying levels of simplification.

import static java.lang.System.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

public class ImageUtilityTest extends Canvas implements KeyListener {
	// program entry point
	public static void main(String[] args) {
		JFrame frame = new JFrame("ImageUtility Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		ImageUtilityTest test = new ImageUtilityTest();
		panel.add(test, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.CENTER);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		test.requestFocusInWindow();
	}

	// public static constants
	public static int MAX_SIZE = 512;

	// private members
	private BufferedImage    originalImage;
	private BufferedImage [] images;

	private int numImages     = 0;
	private int selectedImage = 0;

	/////////////////
	// CONSTRUCTOR //

	public ImageUtilityTest() {
		setPreferredSize(new Dimension(MAX_SIZE, MAX_SIZE));
		addKeyListener(this);

		// read the original image from file
		try {
			originalImage = ImageIO.read(new File("mario.png"));
		} catch (IOException ex) {
			out.println("Image could not be read.");
			System.exit(1);
		}

		// determine the number of images to be created by doing log_2(width) + 1
		numImages = (int)( Math.log(originalImage.getWidth()) / Math.log(2) ) + 1;

		// process the original image to create the images array
		images = new BufferedImage[numImages];
		images[0] = originalImage;

		for (int i = 1; i < numImages; i++)
			images[i] = ImageUtility.simplifyImage(images[i - 1]);
	}

	////////////////////
	// PUBLIC METHODS //

	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawImage(images[selectedImage], 0, 0, getWidth(), getHeight(), null);
	}

	//////////////////
	// KEY LISTENER //

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				selectedImage++;
				break;
			case KeyEvent.VK_DOWN:
				selectedImage--;
				break;
		}

		// make sure the index isn't invalid
		if (selectedImage >= numImages)
			selectedImage = 0;
		if (selectedImage < 0)
			selectedImage = numImages - 1;

		repaint();
	}

	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
}