import java.util.*;
import java.awt.event.*;

public class ComputerSnake {
	private final int up = KeyEvent.VK_UP;
	private final int down = KeyEvent.VK_DOWN;
	private final int right = KeyEvent.VK_RIGHT;
	private final int left = KeyEvent.VK_LEFT;
	private final int nothing = -1;
	
	private ArrayList<Point> snake;
	private boolean isReverse = false;
	
	public ComputerSnake(ArrayList<Point> snake) {
		this.snake = snake;
	}
	
	public void remind(ArrayList<Point> snake) {
		this.snake = snake;
	}
	
	public int calc(int xv, int yv) {
		Point head = snake.get(0);
		int x = head.x;
		int y = head.y;
		int dir = calcDirection(xv, yv);
		
		if (!isReverse) {
			switch (dir) {
				case up:
					if (y == 0)
						return left;
					else if (y == 1 && x != 77)
						return right;
					else
						return up;
				case down:
					if (y == 48)
						return right;
					else
						return down;
				case right:
					if (y == 1)
						return down;
					else
						return up;
				case left:
					if (x == 0)
						return down;
					else
						return left;
			}
		} else {
			switch(dir) {
				case up:
					if (y == 0)
						return right;
					else if (y == 1 && x != 0)
						return left;
					else
						return up;
				case down:
					if (y == 48)
						return left;
					else
						return down;
				case right:
					if (x == 77)
						return down;
					else
						return right;
				case left:
					if (y == 1)
						return down;
					else
						return up;
			}
		}
		
		return nothing;
	}
	
	public void reverse() {
		isReverse = !isReverse;
	}
	
	private int calcDirection(int xv, int yv) {
		return (xv == 1) ? right :
			(xv == -1) ? left :
			(yv == 1) ? down :
			(yv == -1) ? up : nothing;
	}
}