// Author:  Max McCord
// Created: 04/03/2014

import java.awt.*;
import java.util.*;

public class Game {
	// enum for representing move directions
	public enum MoveDir { RIGHT, LEFT, UP, DOWN }
	
	// constants
	public static final int BOARD_DIM = 4;  // the board will hold BOARD_DIM x BOARD_DIM tiles
	public static final int BOARD_OFFY = 120;
	public static final int BOARD_PAD = 20;
	public static final int BORDER_THICK = 5;
	
	public static final int QUEUE_SIZE = 4;
	
	// private members
	Tile tiles[][];
	Queue<MoveDir> commands;
	
	boolean isAnimating;
	boolean needNewTile;
	boolean gameIsOver;
	
	int score;
	
	/////////////////
	// CONSTRUCTOR //
	
	public Game() {
		tiles = new Tile[BOARD_DIM][BOARD_DIM];
		commands = new LinkedList<MoveDir>();
		
		isAnimating = false;
		needNewTile = false;
		gameIsOver = false;
	}
	
	///////////////////////////
	// PUBLIC STATIC METHODS //
	
	public static int getWidth() {
		return BOARD_DIM * Tile.TILE_SIZE + BORDER_THICK * (BOARD_DIM + 1) + 2 * BOARD_PAD;
	}
	
	public static int getHeight() {
		return BOARD_DIM * Tile.TILE_SIZE + BORDER_THICK * (BOARD_DIM + 1) + 2 * BOARD_PAD + BOARD_OFFY;
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void update() {
		if (!isAnimating) {
			// add a new tile if needed
			if (needNewTile) {
				addTile();
				needNewTile = false;
				
				// check for game over
				if (isGameOver()) {
					gameOver();
					return;
				}
			}
			
			processInput();
		}
		
		// update each tile, see if we're still animating
		isAnimating = false;
		
		for (int r = 0; r < BOARD_DIM; r++) {
			for (int c = 0; c < BOARD_DIM; c++) {
				if (tiles[r][c] != null) {
					tiles[r][c].update();
					if (tiles[r][c].isAnimating())
						isAnimating = true;
				}
			}
		}
	}
	
	public void draw(Graphics g) {
		// background
		g.setColor(new Color(255, 250, 248));
		g.fillRect(0, 0, Game.getWidth(), Game.getHeight());
		
		// score
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString(String.format("Score: %d", score), 20, 40);
		
		// board
		int boardSize = Tile.TILE_SIZE * BOARD_DIM + BORDER_THICK * (BOARD_DIM + 1);
		g.setColor(new Color(30, 30, 30));
		g.fillRect(BOARD_PAD, BOARD_OFFY + BOARD_PAD, boardSize, boardSize);
		
		g.setColor(new Color(192, 192, 192));
		for (int i = 0; i < BOARD_DIM; i++) {
			for (int j = 0; j < BOARD_DIM; j++) {
				Point p = getTilePoint(i, j);
				g.fillRect(p.x, p.y, Tile.TILE_SIZE, Tile.TILE_SIZE);
			}
		}
		
		// draw each tile
		for (int r = 0; r < BOARD_DIM; r++)
			for (int c = 0; c < BOARD_DIM; c++)
				if (tiles[r][c] != null)
					tiles[r][c].draw(g);
		
		// draw Game Over graphics
		if (gameIsOver) {
			// make board transparent
			g.setColor(new Color(255, 255, 255, 128));
			g.fillRect(BOARD_PAD, BOARD_OFFY + BOARD_PAD, boardSize, boardSize);
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("GAME OVER", BOARD_PAD + boardSize/2 - 145, BOARD_OFFY + BOARD_PAD + boardSize/2 - 20);
		}
	}
	
	public void newGame() {
		// clear the board and add two random tiles
		for (int r = 0; r < BOARD_DIM; r++)
			for (int c = 0; c < BOARD_DIM; c++)
				tiles[r][c] = null;
		
		addTile();
		addTile();
		
		score = 0;
		gameIsOver = false;
	}
	
	public void gameOver() {
		gameIsOver = true;
	}
	
	public void giveCommand(MoveDir dir) {
		if (commands.size() < QUEUE_SIZE)
			commands.offer(dir);
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private void processInput() {
		// processes the next movement in the queue, if there is one
		MoveDir dir = commands.poll();
		
		if (dir != null) {
			boolean changed = false;
			
			changed = push(dir);
			
			if (changed) {
				// move the tiles to their new locations
				for (int r = 0; r < BOARD_DIM; r++) {
					for (int c = 0; c < BOARD_DIM; c++) {
						if (tiles[r][c] != null) {
							Point p = getTilePoint(r, c);
							tiles[r][c].moveToX(p.x);
							tiles[r][c].moveToY(p.y);
						}
					}
				}
				
				// mark that we need to add a new tile
				needNewTile = true;
			}
		}
	}
	
	// Push the tiles in the supplied direction and return whether or
	// not the board actually changed. Rotates the board around so
	// that only pushLeft() is needed. Not efficient, but for a 4x4
	// board, you're not looping that many times. 
	private boolean push(MoveDir dir) {
		boolean changed = false;
		
		switch (dir) {
			case LEFT:
				changed = pushLeft();
				break;
			case RIGHT:
				rotateBoard();
				rotateBoard();
				changed = pushLeft();
				rotateBoard();
				rotateBoard();
				break;
			case UP:
				rotateBoard();
				changed = pushLeft();
				rotateBoard();
				rotateBoard();
				rotateBoard();
				
				break;
			case DOWN:
				rotateBoard();
				rotateBoard();
				rotateBoard();
				changed = pushLeft();
				rotateBoard();
				
				break;
		}
		
		return changed;
	}
	
	// rotates the entire board to the left
	private void rotateBoard() {
		Tile ret[][] = new Tile[BOARD_DIM][BOARD_DIM];
		
		for (int r = 0; r < BOARD_DIM; r++)
			for (int c = 0; c < BOARD_DIM; c++)
				ret[r][c] = tiles[c][BOARD_DIM - r - 1];
		
		tiles = ret;
	}
	
	// Performs the 2048 sliding algorithm. Slides tiles and performs combinations.
	private boolean pushLeft() {
		boolean changed = false;
		
		for (int r = 0; r < BOARD_DIM; r++) {
			boolean locked[] = new boolean[BOARD_DIM];
			
			for (int c = 1; c < BOARD_DIM; c++) {
				// we don't move empty tiles
				if (tiles[r][c] == null)
					continue;
				
				// look to the left until we find a number or wall
				int pos = c-1;
				while (pos > 0 && tiles[r][pos] == null) pos--;
				
				if (tiles[r][pos] == null) {
					// we hit the wall - move to empty space
					tiles[r][pos] = tiles[r][c];
					tiles[r][c] = null;
					changed = true;
				} else if (tiles[r][pos].getValue() == tiles[r][c].getValue()) {
					// found a matching number - combine if not locked
					if (!locked[pos]) {
						tiles[r][pos] = tiles[r][c];
						tiles[r][c] = null;
						tiles[r][pos].grow();
						score += tiles[r][pos].getValue();
						changed = true;
						
						// prevent multiple combinations by locking this space
						locked[pos] = true;
					}
				} else if (pos + 1 != c) {
					// number does not match, but there is empty space to the left
					tiles[r][pos + 1] = tiles[r][c];
					tiles[r][c] = null;
					changed = true;
				}
			}
		}
		
		return changed;
	}
	
	// adds a 2 or a 4 tile to a random location. returns false if operation failed.
	private boolean addTile() {
		// is the board full?
		boolean boardFull = true;
		for (int r = 0; r < BOARD_DIM && boardFull; r++)
			for (int c = 0; c < BOARD_DIM && boardFull; c++)
				if (tiles[r][c] == null)
					boardFull = false;
		
		// can't add a tile if the board is full!
		if (boardFull)
			return false;
		
		// find a random empty spot
		int r, c;
		do {
			r = randInt(0, BOARD_DIM);
			c = randInt(0, BOARD_DIM);
		} while (tiles[r][c] != null);
		
		int value = 2 * randInt(1, 3);
		Point p = getTilePoint(r, c);
		tiles[r][c] = new Tile(p.x, p.y, value);
		tiles[r][c].enter(); // perform entering animation
		
		return true;
	}
	
	// returns true if there are no more possible moves
	private boolean isGameOver() {
		// see if the board is full - return false if an empty space is found
		for (int r = 0; r < BOARD_DIM; r++)
			for (int c = 0; c < BOARD_DIM; c++)
				if (tiles[r][c] == null)
					return false;
		
		// if we get here, the board is full. look for neighboring similar cells
		for (int r = 0; r < BOARD_DIM; r++) {
			for (int c = 0; c < BOARD_DIM; c++) {
				// look left
				if (c != 0 && tiles[r][c-1].getValue() == tiles[r][c].getValue())
					return false;
				
				// look right
				if (c != BOARD_DIM-1 && tiles[r][c+1].getValue() == tiles[r][c].getValue())
					return false;
				
				// look up
				if (r != 0 && tiles[r-1][c].getValue() == tiles[r][c].getValue())
					return false;
				
				// look down
				if (r != BOARD_DIM-1 && tiles[r+1][c].getValue() == tiles[r][c].getValue())
					return false;
			}
		}
		
		// if we got here, the game is over!
		return true;
	}
	
	// returns the (x, y) coord for the Tile at row r and column c
	private Point getTilePoint(int r, int c) {
		int tileX = BOARD_PAD + BORDER_THICK + c * (Tile.TILE_SIZE + BORDER_THICK);
		int tileY = BOARD_PAD + BOARD_OFFY + BORDER_THICK + r * (Tile.TILE_SIZE + BORDER_THICK);
		return new Point(tileX, tileY);
	}
	
	// returns an integer in the rang [low, high)
	private static int randInt(int low, int high) {
		return (int)(Math.random() * (high - low)) + low;
	}
}
