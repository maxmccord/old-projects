// Author:   Max McCord
// Created:  03/31/2014

import java.awt.*;
import javax.swing.*;

public class Program {
	// program entry point
	public static void main(String[] args) {
		JFrame frame = new JFrame("2048");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new BorderLayout());
		MainCanvas game = new MainCanvas();
		panel.add(game, BorderLayout.CENTER);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.requestFocusInWindow();
	}
}

