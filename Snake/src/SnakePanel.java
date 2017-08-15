import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

// board is 78x49

public class SnakePanel extends JPanel implements KeyListener {
	// variables
		Thread t;           // the updating thread
		Thread paintThread; // thread that repaints (avoids overpainting)
		
		Point target;                    // randomized target to be collected
		boolean targetIsReverse = false; // whether or not the target is yellow (reverses direction)
		int hitCount = 0;                // to make sure the player doesn't live off of bonus
		
		boolean isBonus = false;               // whether or not the bonus is available
		Point bonusTarget = new Point(-1, -1); // the position of the bonus
		Thread bonusThread;                    // the thread that controls bonus appearances
		
		ArrayList<Point> snake = new ArrayList<Point>(); // all points on the snake
		int length = 1; // length of the snake
		int xv = 0;  // x-velocity of the snake
		int yv = -1; // y-velocity of the snake
	
		ArrayDeque<Integer> commands = new ArrayDeque<Integer>(); // command deque
		
		boolean running = false; // whether or not the game is running
		boolean gameOver = true; // whether or not the game is over
		int score = 0;           // the player's score so far
		
		boolean compPlaying = false;
		ComputerSnake comp;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	int speed = 50;
	public SnakePanel() {
		// add the key listener
			setFocusable(true);
			addKeyListener(this);
			
		// set up the computer a.i.
			comp = new ComputerSnake(snake);
		
		// set up the snake and target
			target = randomPoint();
			snake.add(new Point(39, 24));
		
		// activate the threads
			t = new Thread(new Runnable() {
				public void run() {
					while (true) {
						update();
						try { Thread.sleep(speed); }
						catch (InterruptedException e) { }
					}
				}
			});
			t.start();
			
			paintThread = new Thread(new Runnable() {
				public void run() {
					while(true) {
						repaint();
						try { Thread.sleep((speed == 1) ? 1000 : 20); }
						catch (InterruptedException e) { }
					}
				}
			});
			paintThread.start();
		
		// activate the bonus thread
			bonusThread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (!isBonus) {
							try {
								int waitTime = 1000 * ((int)Math.floor(Math.random() * 10) + 20);
								Thread.sleep(waitTime);	
							} catch (InterruptedException e) { }
							
							if (running && hitCount >= 5) {
								do {
									bonusTarget = randomPoint();
								} while (bonusTarget.equals(target));
								
								isBonus = true;
								hitCount = 0;
							}
						} else {
							try { Thread.sleep(5000); }
							catch (InterruptedException e) { }
							
							bonusTarget = new Point(-1, -1);
							isBonus = false;
						}
					}
				}
			});
			bonusThread.start();
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private void resetGame() {
		snake.clear();
		snake.add(new Point(39, 24));
		commands.clear();
		hitCount = 0;
		length = 1;
		xv = 0;
		yv = -1;
		score = 0;
		//repaint();
	}
	
	private void endGame() {
		running = false;
		gameOver = true;
		//repaint();
	}
	
	private void update() {
		if (running) {
			// let the computer decide
				if (compPlaying)
					commands.offerLast(comp.calc(xv, yv));
			
			// process any key commands
				if (commands.size() > 0)
					processCommand(commands.pollFirst());
				
			// copy the snake
				ArrayList<Point> copy = new ArrayList<Point>(snake);
			
			// clear snake and reposition the head
				snake.clear();
				Point newHead = new Point(copy.get(0).x + xv, copy.get(0).y + yv);
				snake.add(newHead);
			
			// add the rest of the body minus one (unless the snake is getting longer)
				int factor = (copy.size() == length) ? 1 : 0;
				for (int i = 0; i < copy.size()-factor; i++)
					snake.add(copy.get(i));
			
			// if the head is now on the target, add to the length
				if (newHead.equals(target)) {
					if (targetIsReverse) {
						comp.reverse();
						
						ArrayList<Point> reverse = new ArrayList<Point>();
						for (int i = snake.size()-1; i >= 0; i--)
							reverse.add(snake.get(i));
						snake = new ArrayList<Point>(reverse);
						
						if (compPlaying)
							comp.remind(snake);
							
						newHead = snake.get(0);
						
						if (snake.size() > 1) {
							Point neck = snake.get(1);
							if (neck.x < newHead.x) {
								xv = 1;
								yv = 0;
							} else if (neck.x > newHead.x) {
								xv = -1;
								yv = 0;
							} else if (neck.y < newHead.y) {
								xv = 0;
								yv = 1;
							} else if (neck.y > newHead.y) {
								xv = 0;
								yv = -1;
							}
						} else {
							xv *= -1;
							yv *= -1;
						}
					} else {
						length += 5;
					}
					
					score += 10;
					
					hitCount++;
					
					targetIsReverse = (Math.random() < 0.1) ? true : false;
					target = randomPoint();
				}
				
				if (newHead.equals(bonusTarget)) {
					score += 50;
					
					bonusTarget = new Point(-1, -1);
					isBonus = false;
				}
			
			// if the head is colliding with the walls or with the body of the snake, end the game
				ArrayList<Point> headlessSnake = new ArrayList<Point>(snake);
				headlessSnake.remove(0);
				if (isOnSnake(headlessSnake, newHead)) {
					snake = new ArrayList<Point>(copy);
					if (compPlaying)
						comp.remind(snake);
					endGame();
					return;
				}
				
				if (newHead.x < 0 || newHead.x >= 78) {
					snake = new ArrayList<Point>(copy);
					if (compPlaying)
						comp.remind(snake);
					endGame();
					return;
				}
				
				if (newHead.y < 0 || newHead.y >= 49) {
					snake = new ArrayList<Point>(copy);
					if (compPlaying)
						comp.remind(snake);
					endGame();
					return;
				}
		}
		
		// update the screen
			//repaint();
	}
	
	private Point randomPoint() {
		Point p = new Point();
		
		do {
			int x = (int)Math.floor(Math.random() * 78);
			int y = (int)Math.floor(Math.random() * 49);
			p = new Point(x, y);
		} while (isOnSnake(snake, p));
		
		return p;
	}
	
	private boolean isOnSnake(ArrayList<Point> snake, Point p) {
		for (Point sp : snake)
			if (sp.equals(p))
				return true;
		
		return false;
	}
	
	private void processCommand(Integer e) {
		switch (e) {
			case KeyEvent.VK_RIGHT:
				if (xv == 0) {
					xv = 1;
					yv = 0;
				}
				
				break;
			case KeyEvent.VK_LEFT:
				if (xv == 0) {
					xv = -1;
					yv = 0;
				}
				
				break;
			case KeyEvent.VK_UP:
				if (yv == 0) {
					xv = 0;
					yv = -1;
				}
				
				break;
			case KeyEvent.VK_DOWN:
				if (yv == 0) {
					xv = 0;
					yv = 1;
				}
				
				break;
		}
	}
	
	////////////////////
	// JPANEL METHODS //
	////////////////////
	
	/*Color bg = new Color(255, 160, 160); // 230, 230, 230
	Color border = new Color(168, 0, 0); // black
	Color s = Color.black; // 140, 140, 180
	Color tNor = Color.white; // red
	Color tRev = Color.black; // 255, 255, 110
	Color tBon = new Color(0, 255, 128); // 0, 230, 0
	Color text = Color.black; // black */
	
	Color bg = new Color(230, 230, 230);
	Color border = Color.black;
	Color s = new Color(140, 140, 180);
	Color tNor = Color.red;                // normal color
	//Color tRev = new Color(255, 255, 110); // reverse color
	Color tRev = new Color(0, 0, 0); // reverse color
	Color tBon = new Color(0, 230, 0);     // bonus color
	Color text = Color.black;
	
	public void paintComponent(Graphics g) {
		// background and outlines
			g.setColor(bg);
			g.fillRect(0, 0, 624, 418);
			
			g.setColor(border);
			g.drawRect(0, 0, 624, 393);
			g.drawRect(0, 393, 624, 25);
		
		// stats
			g.setColor(text);
			g.drawString("Score: " + score, 5, 410);
			
			if (gameOver)
				g.drawString("GAME OVER - Press <Space> to start a new game.", 185, 410);
			else if (!running)
				g.drawString("PAUSED - Press <Space> to resume.", 210, 410);
			else
				g.drawString("Press <Space> to pause.", 230, 410);
			
		// target
			int targetX = 1 + target.x * 8;
			int targetY = 1 + target.y * 8;
			g.setColor((targetIsReverse) ? tRev : tNor);
			g.fillRect(targetX, targetY, 7, 7);
			
		// bonus target
			if (isBonus) {
				int bonusTargetX = 1 + bonusTarget.x * 8;
				int bonusTargetY = 1 + bonusTarget.y * 8;
				g.setColor(tBon);
				g.fillRect(bonusTargetX, bonusTargetY, 7, 7);
			}
		
		// snake
			for (Point p : snake) {
				int snakeX = 1 + p.x * 8;
				int snakeY = 1 + p.y * 8;
				g.setColor(s);
				g.fillRect(snakeX, snakeY, 7, 7);
			}
	}
	
	//////////////////////////
	// KEY LISTENER METHODS //
	//////////////////////////
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (running) {
				running = false;
			} else if (gameOver) {
				resetGame();
				running = true;
				gameOver = false;
			} else {
				running = true;
				gameOver = false;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_E) {
			compPlaying = !compPlaying;
			if (compPlaying)
				comp.remind(snake);
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			speed = (speed == 1) ? 100 :
				(speed == 100) ? 50 : 1;
		} else if (running && (commands.size() == 0 || commands.peekLast() != e.getKeyCode())) {
			if (e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_LEFT)
			commands.offerLast(e.getKeyCode());
		}
	}
	
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
}