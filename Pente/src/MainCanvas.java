import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import static java.awt.Color.*;
import javax.swing.*;
import java.util.*;

public class MainCanvas extends Canvas implements MouseListener, MouseMotionListener {
	// program entry point
	public static void main(String[] args) {
		JFrame frame = new JFrame("Let's Play Pente!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainCanvas main = new MainCanvas();
		frame.add(main, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null); // center frame on screen
		frame.setVisible(true);
		main.init(); // game must initialize after frame is visible
	}
	
	// private member variables
	private int[][] grid;
	private int hoverX = -1;
	private int hoverY = -1;
	private Color p1Color = RED;
	private Color p2Color = BLUE;
	private int p1Caps;
	private int p2Caps;
	private int turn;
	private int turnCount;
	
	ArrayList<AnimatedPoint> newAnimationQueue;
	ArrayList<AnimatedPoint> animations;
	boolean animating;
	
	//////////////////
	// CONSTRUCTORS //
	
	public MainCanvas() {
		setPreferredSize(new Dimension(550, 600));
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void init() {
		// allow for double buffering
		if (getBufferStrategy() == null)
			createBufferStrategy(3);
		
		// grab focus
		requestFocus();
		
		// init variables
		grid = new int[19][19];
		newAnimationQueue = new ArrayList<AnimatedPoint>();
		animations = new ArrayList<AnimatedPoint>();
		animating = false;
		
		// set up the game
		resetGame();
		
		// start the game loop
		new Thread(new Runnable() { public void run() { gameLoop(); } }).start();
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private void gameLoop() {
		tick();
		render();
		try { Thread.sleep(17); }
		catch (Exception e) { }
		gameLoop();
	}
	
	private void resetGame() {
		// empty grid
		for (int r = 0; r < 19; r++)
			for (int c = 0; c < 19; c++)
				grid[r][c] = 0;
				
		// zero captures
		p1Caps = p2Caps = 0;
		
		turnCount = 0;
		turn = (int)Math.floor(Math.random()*2) + 1;
		grid[9][9] = turn;
		switchTurns();
	}
	
	///////////
	// TICKS //
	
	private void tick() {
		// if there are new animations in the queue, add them
		while (newAnimationQueue.size() > 0) {
			AnimatedPoint p = newAnimationQueue.get(0);
			animations.add(new AnimatedPoint(p.getX(), p.getY(), p.getEndX(), p.getEndY(), p.getColor()));
			newAnimationQueue.remove(0);
		}
		
		if (animating) {
			// move every animations
			boolean done = false;
			for (AnimatedPoint a : animations) {
				a.move();
				done = done || a.isDone();
			}
			
			// if all animations are finished, stop animation
			if (done)
				animating = false;
		}
	}
	
	///////////////
	// RENDERING //
	
	private void render() {
		// obtain graphics
		BufferStrategy bs = getBufferStrategy();
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		
		// draw background
		g.setColor(WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// draw the board
		g.setColor(BLACK);
		Stroke s = g.getStroke();
		
		// basic lines
		g.setStroke(new BasicStroke(1.5f));
		for (int i = 0; i < 19; i++)
			g.drawLine(23, 23+i*28, 527, 23+i*28);
		for (int i = 0; i < 19; i++)
			g.drawLine(23+i*28, 23, 23+i*28, 527);
		
		// thicker lines
		g.setStroke(new BasicStroke(4.5f));
		g.drawRect(23, 23, 504, 504);
		g.drawLine(23, 275, 527, 275);
		g.drawLine(275, 23, 275, 527);
		
		// board/stats divider
		g.setStroke(new BasicStroke(3f));
		g.drawLine(0, 550, getWidth(), 550);
		
		// draw stats
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString("Turn:", 10, 582);
		g.drawString(String.format("%03d", turnCount), 60, 582);
		g.drawString("Captures:", 145, 582);
		
		renderStone(g, 110, 575, turn);
		
		int caps = (turn == 1) ? p1Caps : p2Caps;
		for (int i = 0; i < caps; i++) {
			renderStone(g, 240+i*57, 575, (turn == 1) ? 2 : 1);
			renderStone(g, 265+i*57, 575, (turn == 1) ? 2 : 1);
		}
		
		// draw hover circle
		if (hoverX > 0) {
			g.setStroke(new BasicStroke(3f));
			g.setColor(BLACK);
			g.drawOval(hoverX-8, hoverY-8, 16, 16);
		}
		
		// draw placed stones
		for (int i = 0; i < 19; i++)
			for (int j = 0; j < 19; j++)
				if (grid[i][j] > 0)
					renderStone(g, 23+j*28, 23+i*28, grid[i][j]);
		
		// draw animated stones
		for (AnimatedPoint a : animations)
			renderStone(g, a.getX(), a.getY(), a.getColor());
		
		// release resources and display
		g.dispose();
		bs.show();
	}
	
	private void renderStone(Graphics2D g, int x, int y, int color) {
		Stroke s = g.getStroke();
		Color c = g.getColor();
		g.setStroke(new BasicStroke(3f));
		g.setColor((color == 1) ? p1Color : p2Color);
		g.fillOval(x-10, y-10, 20, 20);
		g.setColor(BLACK);
		g.drawOval(x-10, y-10, 20, 20);
		g.setColor(c);
		g.setStroke(s);
	}
	
	////////////////////
	// HELPER METHODS //
	
	private double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	private void switchTurns() {
		if (turn == 1)
			turn = 2;
		else
			turn = 1;
		turnCount++;
	}
	
	// returns true if the recently placed piece made 5 in a row
	private boolean checkForWin(int r, int c) {
		// check horizontal
		int checkR = r;
		int checkC = c;
		while(getValue(checkR, checkC-1) == turn) checkC--;
		int count = checkC;
		while (getValue(checkR, checkC+1) == turn) checkC++;
		count = checkC-count+1;
		
		if (count >= 5)
			return true;
		
		// check vertical
		checkR = r; checkC = c;
		while (getValue(checkR-1, checkC) == turn) checkR--;
		count = checkR;
		while (getValue(checkR+1, checkC) == turn) checkR++;
		count = checkR-count+1;
		
		if (count >= 5)
			return true;
		
		// check forward diag
		checkR = r; checkC = c;
		while (getValue(checkR-1, checkC-1) == turn) {
			checkR--;
			checkC--;
		}
		count = checkC;
		while (getValue(checkR+1, checkC+1) == turn) {
			checkR++;
			checkC++;
		}
		count = checkC-count+1;
		
		if (count >= 5)
			return true;
		
		// check backward diag
		checkR = r; checkC = c;
		while (getValue(checkR-1, checkC+1) == turn) {
			checkR--;
			checkC++;
		}
		count = checkR;
		while (getValue(checkR+1, checkC-1) == turn) {
			checkR++;
			checkC--;
		}
		count = checkR-count+1;
		
		if (count >= 5)
			return true;
		
		// 5 in a row was not found - no win!
		return false;
	}
	
	// returns true if there was a capture and removes the pieces
	private boolean checkForCapture(int r, int c) {
		boolean[] caps = new boolean[8];
		for (int i = 0; i < 8; i++)
			caps[i] = false;
		int friend = turn;
		int enemy = (turn == 1) ? 2 : 1;
		
		// check all 8 possible directions
		if (getValue(r-3, c-3) == friend && getValue(r-2, c-2) == enemy && getValue(r-1, c-1) == enemy) {
			caps[0] = true;
			grid[r-2][c-2] = 0;
			grid[r-1][c-1] = 0;
		}
		
		if (getValue(r-3, c) == friend && getValue(r-2, c) == enemy && getValue(r-1, c) == enemy) {
			caps[1] = true;
			grid[r-2][c] = 0;
			grid[r-1][c] = 0;
		}
		
		if (getValue(r-3, c+3) == friend && getValue(r-2, c+2) == enemy && getValue(r-1, c+1) == enemy) {
			caps[2] = true;
			grid[r-2][c+2] = 0;
			grid[r-1][c+1] = 0;
		}
		
		if (getValue(r, c+3) == friend && getValue(r, c+2) == enemy && getValue(r, c+1) == enemy) {
			caps[3] = true;
			grid[r][c+2] = 0;
			grid[r][c+1] = 0;
		}
		
		if (getValue(r+3, c+3) == friend && getValue(r+2, c+2) == enemy && getValue(r+1, c+1) == enemy) {
			caps[4] = true;
			grid[r+2][c+2] = 0;
			grid[r+1][c+1] = 0;
		}
		
		if (getValue(r+3, c) == friend && getValue(r+2, c) == enemy && getValue(r+1, c) == enemy) {
			caps[5] = true;
			grid[r+2][c] = 0;
			grid[r+1][c] = 0;
		}
		
		if (getValue(r+3, c-3) == friend && getValue(r+2, c-2) == enemy && getValue(r+1, c-1) == enemy) {
			caps[6] = true;
			grid[r+2][c-2] = 0;
			grid[r+1][c-1] = 0;
		}
		
		if (getValue(r, c-3) == friend && getValue(r, c-2) == enemy && getValue(r, c-1) == enemy) {
			caps[7] = true;
			grid[r][c-2] = 0;
			grid[r][c-1] = 0;
		}
		
		return (caps[0] || caps[1] || caps[2] || caps[3] || caps[4] || caps[5] || caps[6] || caps[7]);
	}
	
	private int getValue(int r, int c) {
		if (0 <= r && r < 19 && 0 <= c && c < 19)
			return grid[r][c];
		else
			return -1;
	}
	
	////////////////////
	// MOUSE LISTENER //
	
	public void mouseReleased(MouseEvent e) {
		if (animating)
			return;
			
		if (hoverX > 0) {
			// valid intersection is selected - place stone there
			int r = (hoverY-23)/28;
			int c = (hoverX-23)/28;
			if (grid[r][c] == 0) {
				// start animation
				newAnimationQueue.add(new AnimatedPoint(110, 575, hoverX, hoverY, turn));
				animating = true;
				// wait here until finished animating
				while (animating);
				animations.clear();
				
				grid[r][c] = turn;
				
				if (checkForCapture(r, c))
					if (turn == 1)
						p1Caps++;
					else
						p2Caps++;
				
				if (checkForWin(r, c))
					resetGame();
				else
					switchTurns();
			}
		}
	}
	
	public void mousePressed(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	
	public void mouseMoved(MouseEvent e) {
		// mouse will be in between 4 intersections - find upper left one
		int upperX = 23+((e.getX()-23)/28)*28;
		int upperY = 23+((e.getY()-23)/28)*28;
		
		// distance of 8 is close enough to hover the intersection
		if (distance(e.getX(), e.getY(), upperX, upperY) <= 8) {
			hoverX = upperX;
			hoverY = upperY;
		} else if (distance(e.getX(), e.getY(), upperX+28, upperY) <= 8) {
			hoverX = upperX+28;
			hoverY = upperY;
		} else if (distance(e.getX(), e.getY(), upperX+28, upperY+28) <= 8) {
			hoverX = upperX+28;
			hoverY = upperY+28;
		} else if (distance(e.getX(), e.getY(), upperX, upperY+28) <= 8) {
			hoverX = upperX;
			hoverY = upperY+28;
		} else {
			hoverX = -1;
			hoverY = -1;
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		// dragging is the same as moving
		mouseMoved(e);
	}
}