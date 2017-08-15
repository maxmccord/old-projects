// Author:   Max McCord
// Created:  04/02/2014
//
// Project:  2048 Game
// Desc:     Simple test to make sure the ImageUtility works.

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ImageTest extends Canvas {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		ImageTest test = new ImageTest();
		panel.add(test, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.CENTER);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		test.requestFocusInWindow();
	}
	
	public ImageTest() {
		setPreferredSize(new Dimension(800, 200));
		
		ImageUtility.loadImages();
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (int i = 0; i < 12; i++) {
			Image im = ImageUtility.getImageForValue((int)Math.pow(2, i));
			g.drawImage(im, 20 + 60*i, 20, 50, 50, null);
		}
	}
}