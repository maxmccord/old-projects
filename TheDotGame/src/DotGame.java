import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class DotGame extends DoubleBufferedCanvas implements MouseListener, MouseMotionListener {
	// size constants
	private static final int UISIZE = 400;
	private static final int PAD = 20;
	private static int BOARD_SIZE;
	private static int CELL_SIZE;
	
	// game information
	private boolean active = false;  // if the game has been started
	private int size;                 // width and height of the game board
	private boolean[][] lines;        // stores which lines are drawn in
	private int[][] boxes;            // stores the captures
	
	// player information
	private int turn = 1;          // whose turn it is
	private Player user, opponent; // the players in the game
	private int userScore = 0;     // user's score
	private int opponentScore = 0; // opponent's score
	
	// graphics information
	private String message = "Host or Join a game.";
	private BufferedImage ghostImage;
	private int hoverR = -1; // the row of the line being hovered over
	private int hoverC = -1; // the col of the line being hovered over
	
	// network information
	private LineListener listener;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public void setMessage(String message) {
		this.message = new String(message);
		repaint();
	}
	
	public void setUser(Player user) {
		this.user = new Player(user.getName(), user.getColor());
		repaint();
	}
	
	public void setOpponent(Player opponent) {
		this.opponent = new Player(opponent.getName(), opponent.getColor());
		repaint();
	}
	
	public int getUserScore() {
		return userScore;
	}
	
	public int getOpponentScore() {
		return opponentScore;
	}
	
	public void setActive(boolean active) {
		if (!active) {
			// if making unactive, paint to the ghost image first
			Graphics g = ghostImage.getGraphics();
			paint(g);
			g.setColor(new Color(255, 255, 255, 128)); // transparent white
			g.fillRect(0, 0, UISIZE, UISIZE);
		}
		
		this.active = active;
		repaint();
	}
	
	public boolean isActive() {
		return active;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public DotGame(LineListener listener, Player user) {
		// assign variables
		this.listener = listener;
		this.user = user;
		setPreferredSize(new Dimension(UISIZE, UISIZE));
		
		// create empty ghost image for when game is inactive
		ghostImage = new BufferedImage(UISIZE, UISIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ghostImage.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, UISIZE, UISIZE);
		
		// add listeners
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	////////////////////
	// PUBLIC METHODS //
	////////////////////
	
	public void drawLine(int row, int col) {
		if (!lines[row][col]) {
			// fill the line
			lines[row][col] = true;
			
			// if the user added this line, the listener must send this
			// information to the opponent
			if (turn == 1)
				listener.lineAdded(row, col);
			
			int scoreBefore = userScore+opponentScore;
			
			// update appropriate boxes
			if (row%2 == 0) { // horizontal line
				updateBox(row/2-1, col);
				updateBox(row/2, col);
			} else { // vertical line
				updateBox(row/2, col-1);
				updateBox(row/2, col);
			}
			
			// check for end of game
			if (userScore+opponentScore == size*size) {
				setActive(false);
				String winner = (userScore > opponentScore) ? user.getName() : opponent.getName();
				setMessage("Game Over - " + winner + " wins!");
			} else {
				// trade turns if no boxes were made
				if (scoreBefore == (userScore+opponentScore))
					turn = (turn == 1) ? 2 : 1;
			}
			
			// repaint the display
			repaint();
		}
	}
	
	// begins a new game
	public void startGame(int turn, int size) {
		this.size = size;
		this.turn = turn;
		
		BOARD_SIZE = (UISIZE-PAD*2)/size*size;
		CELL_SIZE = BOARD_SIZE/size;
		
		// init lines array
		lines = new boolean[size*2+1][size+1];
		for (int r = 0; r < lines.length; r++)
			for (int c = 0; c < lines[0].length; c++)
				lines[r][c] = false;
		
		// init boxes array
		boxes = new int[size][size];
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				boxes[r][c] = 0;
		
		userScore = 0;
		opponentScore = 0;
		
		active = true;
		repaint();
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private void updateBox(int row, int col) {
		// exit if invalid box
		if (row < 0 || row >= size || col < 0 || col >= size)
			return;
		
		// check if this box is surrounded
		boolean box = (lines[row*2][col] // top
			&& lines[row*2+1][col] // left
			&& lines[row*2+1][col+1] // right
			&& lines[row*2+2][col]); // bottom
		
		
		if (box) {
			// give score to correct player
			if (turn == 1) userScore++;
			else opponentScore++;
			
			// fill in the box
			boxes[row][col] = turn;
		}
	}
	
	////////////////////////////
	// DOUBLE BUFFERED CANVAS //
	////////////////////////////
	
	public void paint(Graphics g) {
		if (active) {
			// background
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, UISIZE, UISIZE);
			
			// draw filled boxes
			for (int r = 0; r < size; r++) {
				for (int c = 0; c < size; c++) {
					if (boxes[r][c] == 0)
						continue;
					else if (boxes[r][c] == 1)
						g.setColor(user.getColor());
					else if (boxes[r][c] == 2)
						g.setColor(opponent.getColor());
					
					int x = c*CELL_SIZE+PAD;
					int y = r*CELL_SIZE+PAD;
					g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
				}
			}
			
			// draw dots
			g.setColor(Color.BLACK);
			for (int r = 0; r < size+1; r++) {
				for (int c = 0; c < size+1; c++) {
					int x = c*CELL_SIZE+PAD;
					int y = r*CELL_SIZE+PAD;
					g.fillOval(x-4, y-4, 8, 8);
				}
			}
			
			// draw filled lines
			for (int r = 0; r < lines.length; r++)
				for (int c = 0; c < lines[0].length; c++)
					if (lines[r][c])
						paintLine(g, r, c);
			
			// draw the hover line if need be
			if (hoverR != -1) {
				g.setColor(Color.RED);
				paintLine(g, hoverR, hoverC);
			}
		} else {
			// draw ghost image
			g.drawImage(ghostImage, 0, 0, UISIZE, UISIZE, null);
			/*g.setColor(Color.WHITE);
			g.fillRect(0, 0, UISIZE, UISIZE);*/
			
			// obtain font metrics for positioning
			g.setFont(new Font("Arial", Font.PLAIN, 18));
			FontMetrics metrics = g.getFontMetrics();
			int width = metrics.stringWidth(message);
			int height = metrics.getHeight();
			int pad = 20;
			int border = 2;
			
			// draw message background
			g.setColor(Color.BLACK);
			g.fillRect((UISIZE-width-pad)/2, (UISIZE-height-pad)/2, width+pad, height+pad);
			g.setColor(new Color(112, 154, 209)); // bluish
			g.fillRect((UISIZE-width-pad)/2+border, (UISIZE-height-pad)/2+border, width+pad-border*2, height+pad-border*2);
			
			// draw the message
			g.setColor(Color.BLACK);
			int stringX = (UISIZE-width)/2;
			int stringY = (UISIZE-height)/2+metrics.getAscent();
			g.drawString(message, stringX, stringY);
		}
	}
	
	private void paintLine(Graphics g, int r, int c) {
		int x1, x2, y1, y2;
		
		if (r % 2 == 0) {
			// calc horizontal line coords
			x1 = c*CELL_SIZE+PAD+10;
			x2 = (c+1)*CELL_SIZE+PAD-10;
			y1 = y2 = r/2*CELL_SIZE+PAD;
		} else {
			// calc vertical line coords
			x1 = x2 = c*CELL_SIZE+PAD;
			y1 = r/2*CELL_SIZE+PAD+10;
			y2 = (r/2+1)*CELL_SIZE+PAD-10;
		}
		
		// draw the line
		g.drawLine(x1, y1, x2, y2);
	}
	
	////////////////////
	// MOUSE LISTENER //
	////////////////////
	
	public void mouseReleased(MouseEvent e) {
		if (active) {
			// add line on click if it is the user's turn and
			// the mouse is currently hovering over an empty space
			if (turn == 1 && hoverR != -1) {
				drawLine(hoverR, hoverC);
				hoverR = -1;
				hoverC = -1;
			}
		}
	}
	
	public void mouseExited(MouseEvent e) {
		if (active) {
			hoverR = -1;
			hoverC = -1;
		}
	}
	
	public void mousePressed(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	///////////////////////////
	
	public void mouseMoved(MouseEvent e) {
		if (active) {
			if (turn == 1) {
				double motionRadius = CELL_SIZE*Math.sqrt(6.0)/8.0;
				
				// check horizontal lines
				for (int r = 0; r < size*2+1; r += 2) {
					for (int c = 0; c < size; c++) {
						if (!lines[r][c]) {
							double x = PAD + c*CELL_SIZE + CELL_SIZE/2.0;
							double y = r/2*CELL_SIZE+PAD;
							double dist = Math.sqrt(Math.pow(x-e.getX(), 2) + Math.pow(y-e.getY(), 2));
							if (dist < motionRadius) {
								hoverR = r;
								hoverC = c;
								repaint();
								return;
							}
						}
					}
				}
				
				// check vertical lines
				for (int r = 1; r < size*2+1; r += 2) {
					for (int c = 0; c < size+1; c++) {
						if (!lines[r][c]) {
							double x = c*CELL_SIZE+PAD;
							double y = PAD + (r/2)*CELL_SIZE + CELL_SIZE/2.0;
							double dist = Math.sqrt(Math.pow(x-e.getX(), 2) + Math.pow(y-e.getY(), 2));
							if (dist < motionRadius) {
								hoverR = r;
								hoverC = c;
								repaint();
								return;
							}
						}
					}
				}
				
				// if the program reaches this point, the mouse is near no lines
				if (hoverR != -1) {
					hoverR = -1;
					hoverC = -1;
					repaint();
				}
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) { mouseMoved(e); }
}