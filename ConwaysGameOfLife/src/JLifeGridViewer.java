import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JLifeGridViewer extends JComponent implements MouseListener, MouseMotionListener {
	// constants for tools
		public static final String PENCIL = "Pencil";
		public static final String ERASER = "Eraser";
	
	// declare variables
		public int gridWidth, gridHeight;    // the size of the grid (in cells)
		public int cellSize;                 // the size (px) of each cell
		public boolean playing;              // whether or not the viewer is playing
		
		private Color cellColor;             // the color of a live cell
		private LifeGrid grid;               // the LifeGrid object being viewed
		private Timer updateTimer;           // timer for updating the grid
		
		private String currentTool = PENCIL; // stores the selected tool
	
	public JLifeGridViewer(LifeGrid g, int cs, int delay) {
		// set properties
			grid = g;
			gridWidth = grid.getWidth();
			gridHeight = grid.getHeight();
			cellSize = cs;
			playing = false;
		
		// determine size
			setSize(new Dimension((gridWidth * cellSize) + 1, (gridHeight * cellSize) + 1));
		
		// set up the timer
			updateTimer = new Timer(delay, new ActionListener() {
					public void actionPerformed(ActionEvent e) { grid.step(); repaint(); }
				});
		
		// add the mouse listeners
			addMouseListener(this);
			addMouseMotionListener(this);
	}
    
    //////////////////////
    // PLAYBACK METHODS //
    //////////////////////
    
    public void play() {
    	playing = true;
    	updateTimer.start();
    }
    
    public void pause() {
    	playing = false;
    	updateTimer.stop();
    }
    
    public void step() {
    	if (!playing) {
	    	grid.step();
	    	repaint();
    	}
    }
    
    public void setSpeed(int delay) {
    	updateTimer.setDelay(delay);
    }
    
    //////////////////////
    // LIFEGRID METHODS //
    //////////////////////
    
    public void create(int x, int y) {
    	if (!playing) {
	    	grid.create(x, y);
	    	repaint();
    	}
    }
    
    public void destroy(int x, int y) {
    	if (!playing) {
	    	grid.destroy(x, y);
	    	repaint();
    	}
    }
    
    public void clear() {
    	if (!playing) {
    		grid.clear();
    		repaint();
    	}
    }
    
    private void changeAtMouse(int x, int y) {
    	if (!playing) {
    		try {
		    	x = (int)Math.floor((double)x / cellSize);
		    	y = (int)Math.floor((double)y / cellSize);
		    	
		    	if (currentTool.equals(PENCIL))
		    		create(x, y);
		    	else if (currentTool.equals(ERASER))
		    		destroy(x, y);
    		} catch (ArrayIndexOutOfBoundsException ex) { }
    	}
    }
    
    /////////////////////
    // TOOL MANAGEMENT //
    /////////////////////
    
    public void setCurrentTool(String tool) {
    	if (tool.equals(PENCIL) || tool.equals(ERASER)) {
    		currentTool = tool;
    	}
    }
    
    public String getCurrentTool() {
    	return currentTool;
    }
    
    //////////////////////
    // COLOR MANAGEMENT //
    //////////////////////
    
    public void setColorScheme(Color back, Color fore, Color cell) {
    	setBackground(back);
    	setForeground(fore);
    	setCellColor(cell);
    }
    
    public void setCellColor(Color cell) {
    	cellColor = cell;
    }
    
    public Color getCellColor() {
    	return cellColor;
    }
    
    /////////////////////
    // SIZE MANAGEMENT //
    /////////////////////
   	
    public Dimension getPreferredSize() {
        return new Dimension((gridWidth * cellSize), (gridHeight * cellSize));
    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    public Dimension getMaximumSize() {
    	return getPreferredSize();
    }
    
    public void zoom(int factor) {
    	cellSize += factor;
    	
    	if (cellSize < 1)
    		cellSize -= factor;
    }
    
    public void zoomIn() {
    	zoom(10);
    }
    
    public void zoomOut() {
    	zoom(-10);
    }
    
    //////////////////////
    // PAINTING METHODS //
    //////////////////////
    
    protected void paintComponent(Graphics g) {
    	// store width and height
    		int w = (int)getPreferredSize().getWidth();
    		int h = (int)getPreferredSize().getHeight();
    	
    	// draw the background
    		g.setColor(getBackground());
    		g.fillRect(0, 0, w, h);
    	
    	// draw live cells
    		g.setColor(getCellColor());
    		for (int y = 0; y < gridHeight; y++)
    			for (int x = 0; x < gridWidth; x++)
    				if (grid.checkAlive(x, y))
    					g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    	
    	// draw grid lines
    		g.setColor(getForeground());
    		for (int x = 0; x <= w; x += cellSize)
    			g.drawLine(x, 0, x, h);
    		for (int y = 0; y <= h; y += cellSize)
    			g.drawLine(0, y, w, y);
    }
	
	////////////////////////////////////
	// OVERRIDE MOUSELISTENER METHODS //
	////////////////////////////////////
	
    public void mouseExited(MouseEvent e) {  }
    
	public void mouseReleased(MouseEvent e) {  }
	
	public void mousePressed(MouseEvent e) {
		changeAtMouse(e.getX(), e.getY());
	}
	
	//////////////////////////////////////////
	// OVERRIDE MOUSEMOTIONLISTENER METHODS //
	//////////////////////////////////////////
	
	public void mouseDragged(MouseEvent e) {
		changeAtMouse(e.getX(), e.getY());
	}
    
    //////////////////////////////
    // UNUSED OVERIDDEN METHODS //
    //////////////////////////////
    
    public void mouseEntered(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
    
}