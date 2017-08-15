// Author:   Max McCord
// Created:  03/31/2014

import java.awt.*;

public class Tile {
	// constants
	public static final int TILE_SIZE       = 140; // the size of a tile, in pixels
	public static final int TILE_GROW_SIZE  = 180; // maximum size of a tile while growing
	public static final int TILE_MOVE_SPEED = 40;  // speed of tile movement, in pixels/tick
	public static final int TILE_GROW_SPEED = 20;  // speed of tile growth, in pixels/tick
	
	// private member variables
	private int x, y;
	private int targetX, targetY;
	private int value;
	
	private boolean isMoving;
	private boolean isGrowing;
	private int growDirection;
	private int size;
	
	/////////////////
	// CONSTRUCTOR //
	
	public Tile(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.targetX = x;
		this.targetY = y;
		
		this.value = value;
		
		this.isMoving = false;
		this.isGrowing = false;
		this.growDirection = 0;
		this.size = TILE_SIZE;
	}
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	
	public int getValue() {
		return value;
	}
	
	public boolean isAnimating() {
		return (isMoving || isGrowing);
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	// updates the target and begins movement animation
	public void moveToX(int x) {
		if (this.x != x) {
			targetX = x;
			isMoving = true;
		}
	}
	
	// updates the target and begins movement animation
	public void moveToY(int y) {
		if (this.y != y) {
			targetY = y;
			isMoving = true;
		}
	}
	
	// updates the value of the tile and begins the grow animation
	public void grow() {
		value *= 2;
		isGrowing = true;
		growDirection = 1;
	}
	
	// reuses the growing animation, with a lower starting size
	public void enter() {
		isGrowing = true;
		growDirection = 1;
		size = TILE_SIZE / 10;
	}
	
	// draws the tile based on current information
	public void draw(Graphics g) {
		int drawX = x - (size - TILE_SIZE) / 2;
		int drawY = y - (size - TILE_SIZE) / 2;
		
		g.drawImage(ImageUtility.getImageForValue(value), drawX, drawY, size, size, null);
	}
	
	// updates values needed for animation
	public void update() {
		// perform movement animation
		if (isMoving) {
			int dirX = (int)Math.signum(targetX - x);
			int dirY = (int)Math.signum(targetY - y);
			
			if (dirX != 0) {
				x += dirX * TILE_MOVE_SPEED;
				
				// did we go past? aka did direction switch?
				if (dirX != Math.signum(targetX - x))
					x = targetX;
			}
			
			if (dirY != 0) {
				y += dirY * TILE_MOVE_SPEED;
				
				// did we go past? aka did direction switch?
				if (dirY != Math.signum(targetY - y))
					y = targetY;
			}
			
			if (x == targetX && y == targetY)
				isMoving = false;
		}
		
		// perform growing animation
		if (isGrowing) {
			if (growDirection == 1) {
				size += TILE_GROW_SPEED;
				
				// have we reached the maximum size?
				if (size >= TILE_GROW_SIZE) {
					size = TILE_GROW_SIZE;
					
					// switch to shrinking
					growDirection = -1;
				}
			} else if (growDirection == -1) {
				size -= TILE_GROW_SPEED;
				
				// have we gotten back to normal size?
				if (size <= TILE_SIZE) {
					size = TILE_SIZE;
					
					// stop performing growth animation
					growDirection = 0;
					isGrowing = false;
				}
			}
		}
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	
}
