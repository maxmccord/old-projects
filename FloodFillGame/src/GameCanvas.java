import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class GameCanvas extends DoubleBufferedCanvas {
	public static final int BOARD_SIZE = 14;
	public static final int ICON_SIZE = 30;
	
	private int[][] blocks;
	private BufferedImage[] icons;
	private boolean[][] checked;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public GameCanvas(BufferedImage[] icons) {
		setPreferredSize(new Dimension(ICON_SIZE*BOARD_SIZE, ICON_SIZE*BOARD_SIZE));
		
		this.icons = icons;
		blocks = new int[BOARD_SIZE][BOARD_SIZE];
		checked = new boolean[BOARD_SIZE][BOARD_SIZE];
	}
	
	////////////////////
	// PUBLIC METHODS //
	////////////////////
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int xOffset = getWidth()/2 - (BOARD_SIZE*ICON_SIZE)/2;
		int yOffset = getHeight()/2 - (BOARD_SIZE*ICON_SIZE)/2;
		
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				g.drawImage(icons[blocks[i][j]], xOffset+j*ICON_SIZE, yOffset+i*ICON_SIZE, ICON_SIZE, ICON_SIZE, null);
	}
	
	public void randomize() {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				blocks[i][j] = (int)(Math.floor(Math.random() * 6));
		
		repaint();
	}
	
	public void fill(int icon) {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				checked[i][j] = false;
		
		floodFill(0, 0, blocks[0][0], icon);
		repaint();
	}
	
	public boolean checkWin() {
		int icon = blocks[0][0];
		
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (blocks[i][j] != icon)
					return false;
		
		return true;
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private void floodFill(int r, int c, int target, int replace) {
		if (r < 0 || BOARD_SIZE <= r)
			return;
		if (c < 0 || BOARD_SIZE <= c)
			return;
		if (checked[r][c])
			return;
		
		checked[r][c] = true;
		
		if (blocks[r][c] != target)
			return;
		
		blocks[r][c] = replace;
		floodFill(r-1, c, target, replace);
		floodFill(r+1, c, target, replace);
		floodFill(r, c-1, target, replace);
		floodFill(r, c+1, target, replace);
	}
}