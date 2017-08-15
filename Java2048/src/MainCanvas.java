// Author:   Max McCord
// Created:  03/31/2014

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MainCanvas extends DoubleBufferedCanvas implements KeyListener, MouseListener, MouseMotionListener {
	// constants
	public static final String IMAGE_DIR = "res\\images\\";
	public static final int MS_PER_TICK = 10;
	
	// private member variables
	private Game game;
	
	private Point clickLocation;
	private boolean dragProcessed;
	
	/////////////////
	// CONSTRUCTOR //
	
	public MainCanvas() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setPreferredSize(new Dimension(Game.getWidth(), Game.getHeight()));
		
		ImageUtility.loadImages(IMAGE_DIR);
		
		game = new Game();
		game.newGame();
		
		dragProcessed = false;
		
		// create and activate the timer
		javax.swing.Timer t = new javax.swing.Timer(MS_PER_TICK, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gameLoop();
				}
			});
		t.start();
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void paint(Graphics g) {
		game.draw(g);
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private void gameLoop() {
		game.update();
		repaint();
	}
	
	//////////////////
	// KEY LISTENER //
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_L:
				game.giveCommand(Game.MoveDir.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_J:
				game.giveCommand(Game.MoveDir.LEFT);
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_I:
				game.giveCommand(Game.MoveDir.UP);
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_K:
				game.giveCommand(Game.MoveDir.DOWN);
				break;
			case KeyEvent.VK_SPACE:
				game.newGame();
				break;
			case KeyEvent.VK_Q:
				game.gameOver();
				break;
		}
	}
	
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	
	////////////////////
	// MOUSE LISTENER //
	
	public void mousePressed(MouseEvent e) {
		clickLocation = new Point(e.getX(), e.getY());
		dragProcessed = false;
	}
	
	public void mouseReleased(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	
	public void mouseDragged(MouseEvent e) {
		if (!dragProcessed) {
			Point end = new Point(e.getX(), e.getY());
			
			// determine if the user has dragged a certain distance
			double dx = end.x - clickLocation.x;
			double dy = end.y - clickLocation.y;
			double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			if (dist > 30) {
				// determine the angle the user dragged at
				double angle = Math.atan(-dy/dx) * 180.0 / Math.PI;
				
				boolean horz = (-30 <= angle && angle <= 30);
				boolean vert = (60 <= angle && angle <= 90 || -90 <= angle && angle <= -60);
				
				if (horz && dx >= 0)
					game.giveCommand(Game.MoveDir.RIGHT);
				else if (horz && dx <= 0)
					game.giveCommand(Game.MoveDir.LEFT);
				else if (vert && dy <= 0)
					game.giveCommand(Game.MoveDir.UP);
				else if (vert && dy >= 0)
					game.giveCommand(Game.MoveDir.DOWN);
				
				dragProcessed = true;
			}
		}
	}
	
	public void mouseMoved(MouseEvent e) { }
}
