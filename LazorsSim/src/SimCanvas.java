import static java.lang.System.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

public class SimCanvas extends DoubleBufferedCanvas implements MouseListener, MouseMotionListener, KeyListener {
	// constants
	private final int SIZE = 60;
	private final int BOARDW = 6;
	private final int BOARDH = 6;
	
	// private members
	private Block[][] blocks;
	private boolean[][] enabled;
	private ArrayList<LaserNode> sources;
	private ArrayList<Point> targets;
	private boolean solved;
	
	private BufferedImage laserPathImg;
	
	private int mouseX, mouseY;
	private int heldBlock;
	private boolean holdingSource;
	private boolean holdingTarget;
	private Cell prevCell;
	
	/////////////////
	// CONSTRUCTOR //
	
	public SimCanvas() {
		int width = 170+BOARDW*SIZE;
		int height = Math.max(440, 80+BOARDH*SIZE);
		setPreferredSize(new Dimension(width, height));
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		// init grid
		blocks = new Block[BOARDH][BOARDW];
		enabled = new boolean[BOARDH][BOARDW];
		for (int r = 0; r < BOARDH; r++) {
			for (int c = 0; c < BOARDW; c++) {
				blocks[r][c] = null;
				enabled[r][c] = true;
			}
		}
		
		sources = new ArrayList<LaserNode>();
		targets = new ArrayList<Point>();
		solved = false;
		
		laserPathImg = new BufferedImage(BOARDW*60, BOARDH*60, BufferedImage.TYPE_INT_ARGB);
		updateLaserPath();
		
		mouseX = 0;
		mouseY = 0;
		heldBlock = -1;
		holdingSource = false;
		holdingTarget = false;
		prevCell = null;
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void paint(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		
		// background
		g.setColor(new Color(100, 100, 100));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setStroke(new BasicStroke(2f));
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth(), getHeight());
		
		// toolbox
		g.drawRect(0, 0, 90, getHeight());
		
		drawBlock(g, Block.TYPE_REFLECT, false, 20, 20, 50);
		drawBlock(g, Block.TYPE_NONREFLECT, false, 20, 90, 50);
		drawBlock(g, Block.TYPE_GLASS, false, 20, 160, 50);
		
		drawLaserSource(g, 20+25, 20+3*70+25);
		drawLaserTarget(g, 20+25, 20+4*70+25);
		
		g.setColor(Color.black);
		for (int r = 3; r < 6; r++)
			g.drawRect(20, 20+r*70, 50, 50);
		
		// draw enabled spaces
		g.setColor(new Color(192, 192, 192));
		for (int r = 0; r < BOARDH; r++)
			for (int c = 0; c < BOARDW; c++)
				if (enabled[r][c])
					g.fillRect(130+c*SIZE+5, 40+r*SIZE+5, SIZE-10, SIZE-10);
		
		// draw grid lines
		g.setStroke(new BasicStroke(1f));
		g.setColor(Color.black);
		for (int r = 0; r < BOARDH+1; r++)
			g.drawLine(130, 40+r*SIZE, 130+SIZE*BOARDW, 40+r*SIZE);
		for (int c = 0; c < BOARDW+1; c++)
			g.drawLine(130+c*SIZE, 40, 130+c*SIZE, 40+SIZE*BOARDH);
		
		// draw sources and targets
		for (LaserNode node : sources)
			drawLaserSource(g, node.getLoc().x, node.getLoc().y);
		for (Point p : targets)
			drawLaserTarget(g, p.x, p.y);
		
		// draw the laser path
		g.drawImage(laserPathImg, 130, 40, null);
			
		// draw all blocks
		for (int r = 0; r < BOARDH; r++)
			for (int c = 0; c < BOARDW; c++)
				if (blocks[r][c] != null)
					drawBlock(g, blocks[r][c].getType(), blocks[r][c].isFixed(), 130+c*SIZE, 40+r*SIZE, SIZE);
		
		// draw held item
		if (heldBlock != -1)
			drawBlock(g, heldBlock, false, mouseX - SIZE/2, mouseY - SIZE/2, 60);
		else if (holdingSource)
			drawLaserSource(g, mouseX, mouseY);
		else if (holdingTarget)
			drawLaserTarget(g, mouseX, mouseY);
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	public void drawBlock(Graphics2D g, int type, boolean fixed, int x, int y, int size) {
		// save previous paint and stroke
		Paint prevP = g.getPaint();
		Stroke prevS = g.getStroke();
		
		// select appropriate color
		Color color1 = Color.black;
		Color color2 = Color.black;
		switch (type) {
			case Block.TYPE_REFLECT:
				color1 = Color.white;
				color2 = new Color(200, 200, 200); // light gray
				break;
			case Block.TYPE_NONREFLECT:
				color1 = new Color(60, 60, 60); // dark gray
				color2 = new Color(30, 30, 30); // darker gray
				break;
			case Block.TYPE_GLASS:
				color1 = new Color(190, 190, 230, 200); // light blue
				color2 = new Color(150, 150, 230, 200); // darker blue
				break;
		}
		
		RoundRectangle2D blockShape = new RoundRectangle2D.Float(x, y, size, size, 35, 35);
		g.setPaint(new RadialGradientPaint(x+size/2, y+size/2, size/2, new float[] { 0.1f, 1 }, new Color[] { color1, color2 }));
		g.fill(blockShape);
		g.setStroke(new BasicStroke(2.0f));
		g.setColor(Color.black);
		g.draw(blockShape);
		
		if (fixed) {
			g.setColor(new Color(192, 192, 192));
			g.fillOval(x+7, y+7, 10, 10);
			g.fillOval(x+43, y+7, 10, 10);
			g.fillOval(x+7, y+43, 10, 10);
			g.fillOval(x+43, y+43, 10, 10);
		}
		
		// restore previous paint and brush
		g.setPaint(prevP);
		g.setStroke(prevS);
	}
	
	public void drawLaserSource(Graphics2D g, int x, int y) {
		g.setColor(Color.red);
		g.fillOval(x-8, y-8, 16, 16);
	}
	
	public void drawLaserTarget(Graphics2D g, int x, int y) {
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(2.0f));
		g.setColor(Color.black);
		g.drawOval(x-2, y-2, 4, 4);
		g.drawOval(x-6, y-6, 12, 12);
		g.drawOval(x-10, y-10, 20, 20);
		g.setStroke(s);
	}
	
	private void updateLaserPath() {
		// clear the image, set clip, and translate coords
		Graphics2D g = (Graphics2D)laserPathImg.getGraphics();
		g.setBackground(new Color(255, 255, 255, 0));
		g.clearRect(0, 0, BOARDW*60, BOARDH*60);
		g.setClip(0, 0, BOARDW*60, BOARDH*60);
		g.translate(-130, -40);
		
		solved = false;
		Queue<LaserNode> nodes = new LinkedList<LaserNode>();
		ArrayList<Integer> checked = new ArrayList<Integer>();
		ArrayList<Point> hitTargets = new ArrayList<Point>();
		
		// add all sources (unless the source is "squeezed")
		for (LaserNode node : sources) {
			boolean horizontal = ((node.getLoc().y-40) % 60) == 0;
			Cell one, two;
			if (horizontal) {
				one = cellFromNode(new LaserNode(node.getLoc(), LaserNode.DIR_NE));
				two = cellFromNode(new LaserNode(node.getLoc(), LaserNode.DIR_SE));
			} else {
				one = cellFromNode(new LaserNode(node.getLoc(), LaserNode.DIR_NE));
				two = cellFromNode(new LaserNode(node.getLoc(), LaserNode.DIR_NW));
			}
			
			Block b1 = null, b2 = null;
			if (rectContains(one.c, one.r, 0, 0, BOARDW-1, BOARDH-1) && rectContains(two.c, two.r, 0, 0, BOARDW-1, BOARDH-1)) {
				b1 = blocks[one.r][one.c];
				b2 = blocks[two.r][two.c];
			}
			
			if (b1 == null || b2 == null) {
				nodes.offer(node);
			} else {
				int type1 = b1.getType();
				int type2 = b2.getType();
				if (type1 != Block.TYPE_REFLECT && type1 != Block.TYPE_NONREFLECT || type2 != Block.TYPE_REFLECT && type2 != Block.TYPE_NONREFLECT)
					nodes.offer(node);
			}
		}
		
		// trace the path of the laser
		g.setStroke(new BasicStroke(2.5f));
		g.setColor(Color.red);
		while (!nodes.isEmpty()) {
			LaserNode node = nodes.poll();
			
			// skip this node if it is out of bounds or has already been checked
			if (!rectContains(node.getLoc().x, node.getLoc().y, 130, 40, BOARDW*60, BOARDH*60))
				continue;
			if (checked.contains(node.hashCode()))
				continue;
				
			// check to see if a target is being checked
			if (targets.contains(node.getLoc()) && !hitTargets.contains(node.getLoc()))
				hitTargets.add(node.getLoc());
			
			// react appropriately to the block being entered
			Cell cell = cellFromNode(node);
			
			Block block = null;
			if (rectContains(cell.c, cell.r, 0, 0, BOARDW-1, BOARDH-1)) // bit of a hack used here.
				block = blocks[cell.r][cell.c];
				
			int type = -1;
			
			if (block != null)
				type = blocks[cell.r][cell.c].getType();
			
			// if reflecting or glass, add reflected node
			if (type == Block.TYPE_REFLECT || type == Block.TYPE_GLASS) {
				boolean horizontal = ((node.getLoc().y-40) % 60) == 0;
				int dir = node.getDir();
				
				// reflect direction properly
				if (horizontal) {
					switch (dir) {
						case LaserNode.DIR_NE: dir = LaserNode.DIR_SE; break;
						case LaserNode.DIR_NW: dir = LaserNode.DIR_SW; break;
						case LaserNode.DIR_SE: dir = LaserNode.DIR_NE; break;
						case LaserNode.DIR_SW: dir = LaserNode.DIR_NW; break;
					}
				} else {
					switch (dir) {
						case LaserNode.DIR_NE: dir = LaserNode.DIR_NW; break;
						case LaserNode.DIR_NW: dir = LaserNode.DIR_NE; break;
						case LaserNode.DIR_SE: dir = LaserNode.DIR_SW; break;
						case LaserNode.DIR_SW: dir = LaserNode.DIR_SE; break;
					}
				}
				
				// add the next node and draw the path toward it
				Point next = new Point(node.getLoc().x, node.getLoc().y);
				next.x += (dir == LaserNode.DIR_NE || dir == LaserNode.DIR_SE) ? 30 : -30;
				next.y += (dir == LaserNode.DIR_SE || dir == LaserNode.DIR_SW) ? 30 : -30;
				nodes.offer(new LaserNode(next, dir));
			
				g.drawLine(node.getLoc().x, node.getLoc().y, next.x, next.y);
			}
			
			// if no block present or glass, add node in the same direction
			if (block == null || type == Block.TYPE_GLASS) {
				int dir = node.getDir();
				
				// add the next node and draw the path toward it
				Point next = new Point(node.getLoc().x, node.getLoc().y);
				next.x += (dir == LaserNode.DIR_NE || dir == LaserNode.DIR_SE) ? 30 : -30;
				next.y += (dir == LaserNode.DIR_SE || dir == LaserNode.DIR_SW) ? 30 : -30;
				nodes.offer(new LaserNode(next, dir));
			
				g.drawLine(node.getLoc().x, node.getLoc().y, next.x, next.y);
			}
			
			// note that this node has now been checked
			checked.add(node.hashCode());
		}
		
		// check to see if the puzzle is solved
		if (hitTargets.size() == targets.size())
			solved = true;
	}
	
	public boolean rectContains(int px, int py, int rx, int ry, int rw, int rh) {
		return (rx <= px && px <= rx+rw && ry <= py && py <= ry+rh);
	}
	
	public Cell getCellCoords(int x, int y) {
		if (rectContains(x, y, 130, 40, SIZE*BOARDW, SIZE*BOARDH))
			for (int r = 0; r < BOARDH; r++)
				for (int c = 0; c < BOARDW; c++)
					if (rectContains(x, y, 130+c*SIZE, 40+r*SIZE, SIZE, SIZE))
						return new Cell(r, c);
		
		return null;
	}
	
	public Cell cellFromNode(LaserNode node) {
		int x = node.getLoc().x;
		int y = node.getLoc().y;
		int dir = node.getDir();
		boolean horizontal = ((y-40) % 60) == 0;
		
		if (horizontal) {
			Cell c = new Cell((y-40)/60, (x-130-30)/60);
			if (dir == LaserNode.DIR_NE || dir == LaserNode.DIR_NW)
				c.r--;
			return c;
		} else {
			Cell c = new Cell((y-40-30)/60, (x-130)/60);
			if (dir == LaserNode.DIR_NW || dir == LaserNode.DIR_SW)
				c.c--;
			return c;
		}
	}
	
	// iterate all possible combinations of block positions to find solution
	private void solvePuzzle() {
		// count the number of enabled spaces and the number of moveable blocks
		int MAX = 0;
		int ITEMS = 0;
		ArrayList<Cell> availableCells = new ArrayList<Cell>();
		ArrayList<Integer> blockIds = new ArrayList<Integer>();
		
		for (int r = 0; r < BOARDH; r++) {
			for (int c = 0; c < BOARDW; c++) {
				if (enabled[r][c])
					if (blocks[r][c] == null || blocks[r][c] != null && !blocks[r][c].isFixed())
						availableCells.add(new Cell(r, c));
				if (blocks[r][c] != null && !blocks[r][c].isFixed())
					blockIds.add(blocks[r][c].getType());
			}
		}
		
		// avoid unnecessary solving
		MAX = availableCells.size();
		ITEMS = blockIds.size();
		if (ITEMS == 0)
			return;
		
		// digits array contains which positions will contain blocks 
		int[] digits = new int[ITEMS];
		for (int i = 0; i < ITEMS; i++)
			digits[i] = i+1;
			
		// sort the blockIds in lexographic order
		Collections.sort(blockIds);
		
		// iterate through every possible combination of occupied/unoccupied cells
		boolean done = false;
		while (!done) {
			// run through every permutation of blocks in the selected cells
			Integer[] blockIdsArray = blockIds.toArray(new Integer[ITEMS]);
			
			boolean permuting = true;
			while (permuting) {
				// clear the board
				for (int r = 0; r < BOARDH; r++)
					for (int c = 0; c < BOARDW; c++)
						if (blocks[r][c] != null && !blocks[r][c].isFixed())
							blocks[r][c] = null;
						
				// translate current combination/permutation to actual board positions
				for (int i = 0; i < ITEMS; i++) {
					Cell cell = availableCells.get(digits[i]-1);
					blocks[cell.r][cell.c] = new Block(blockIdsArray[i]);
				}
				
				updateLaserPath();
				if (solved) {
					permuting = false;
					done = true;
					continue;
				}
				
				// find the larget index k such that a[k] < a[k+1] - if no such index, last permutation
				int k = -1;
				for (int i = 0; i < ITEMS-1; i++)
					if (blockIdsArray[i] < blockIdsArray[i+1])
						k = i;
				
				if (k == -1) {
					permuting = false;
					continue;
				}
				
				// find largest index l such that a[k] < a[l]
				int l = -1;
				for (int i = k+1; i < ITEMS; i++)
					if (blockIdsArray[k] < blockIdsArray[i])
						l = i;
				
				// swap a[k] with a[l]
				int temp = blockIdsArray[k];
				blockIdsArray[k] = blockIdsArray[l];
				blockIdsArray[l] = temp;
				
				// reverse sequence from a[k+1] up to and including final element a[n]
				int a = k+1;
				int b = ITEMS-1;
				while (a < b) {
					temp = blockIdsArray[a];
					blockIdsArray[a] = blockIdsArray[b];
					blockIdsArray[b] = temp;
					a++;
					b--;
				}
			}
			
			if (done)
				continue;
			
			int index = ITEMS-1;
			boolean incrementing = true;
			while (incrementing) {
				digits[index]++;
				for (int i = index+1; i < ITEMS; i++)
					digits[i] = digits[i-1]+1;
				
				if (digits[index] > MAX-ITEMS+index+1)
					index--;
				else
					incrementing = false;
				
				if (index < 0) {
					incrementing = false;
					done = true;
				}
			}
		}
		
		repaint();
	}
	
	public void clearPuzzle() {
		sources.clear();
		targets.clear();
		for (int r = 0; r < BOARDH; r++) {
			for (int c = 0; c < BOARDW; c++) {
				enabled[r][c] = true;
				blocks[r][c] = null;
			}
		}
		
		updateLaserPath();
		repaint();
	}
	
	////////////////////
	// MOUSE LISTENER //
	
	public void mousePressed(MouseEvent e) {
		if (heldBlock != -1 || holdingSource || holdingTarget)
			return;
		
		int x = e.getX();
		int y = e.getY();
		
		// check to see if a block was grabbed from the toolbox
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (rectContains(x, y, 20, 20, 50, 50))
				heldBlock = Block.TYPE_REFLECT;
			else if (rectContains(x, y, 20, 90, 50, 50))
				heldBlock = Block.TYPE_NONREFLECT;
			else if (rectContains(x, y, 20, 160, 50, 50))
				heldBlock = Block.TYPE_GLASS;
			else if (rectContains(x, y, 20, 230, 50, 50))
				holdingSource = true;
			else if (rectContains(x, y, 20, 300, 50, 50))
				holdingTarget = true;
		}
		
		if (rectContains(x, y, 130-10, 40-10, BOARDW*60+20, BOARDH*60+20)) {
			// check to see if a laser source on the board was clicked
			LaserNode grabbedSource = null;
			for (LaserNode source : sources) {
				double dist = Math.sqrt(Math.pow(x-source.getLoc().x, 2) + Math.pow(y-source.getLoc().y, 2));
				if (dist <= 16.0) {
					grabbedSource = source;
					break;
				}
			}
			
			if (grabbedSource != null) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// left click will grab the source
					sources.remove(grabbedSource);
					holdingSource = true;
					updateLaserPath();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					// right click will rotate the direction of the source
					grabbedSource.rotateDir();
					updateLaserPath();
				}
				
				repaint();
				return;
			}
			
			// check to see if a laser target on the board was clicked
			if (e.getButton() == MouseEvent.BUTTON1) {
				Point grabbedTarget = null;
				for (Point target : targets) {
					double dist = Math.sqrt(Math.pow(x-target.x, 2) + Math.pow(y-target.y, 2));
					if (dist <= 20.0) {
						grabbedTarget = target;
						break;
					}
				}
				
				if (grabbedTarget != null) {
					targets.remove(grabbedTarget);
					holdingTarget = true;
					updateLaserPath();
					repaint();
					return;
				}
			}
		}
		
		// check to see if a block was clicked on the board
		Cell c = getCellCoords(x, y);
		if (c!= null) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (blocks[c.r][c.c] != null && !blocks[c.r][c.c].isFixed()) {
					heldBlock = blocks[c.r][c.c].getType();
					blocks[c.r][c.c] = null;
					prevCell = new Cell(c.r, c.c);
					updateLaserPath();
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				// right click will toggle "fixed" for a block, or toggle "enabled" for an empty spot
				if (blocks[c.r][c.c] != null)
					blocks[c.r][c.c].toggleFixed();
				else
					enabled[c.r][c.c] = !enabled[c.r][c.c];
			}
		}
		
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			// check to see if a block was placed in an empty space
			if (heldBlock != -1) {
				Cell c = getCellCoords(x, y);
				if (c != null) {
					if (blocks[c.r][c.c] == null && enabled[c.r][c.c]) {
						blocks[c.r][c.c] = new Block(heldBlock);
					} else {
						if (prevCell != null) {
							blocks[prevCell.r][prevCell.c] = new Block(heldBlock);
							prevCell = null;
						}
					}
				}
				
				heldBlock = -1;
				updateLaserPath();
			}
			
			// check to see if a laser source or target was placed appropriately
			if (holdingTarget || holdingSource) {
				if (rectContains(x, y, 130-10, 40-10, BOARDW*60+20, BOARDH*60+20)) {
					// check if in horizontal or vertical range
					Point p = null;
					
					if (Math.abs((y-40)%60 - 30) > 20) {
						// in horizontal range
						if (Math.abs((x-130-30)%60 - 30) > 20) {
							double roundX = Math.round((x-130-30)/60.0)*60 + 130 + 30;
							double roundY = Math.round((y-40)/60.0)*60 + 40;
							p = new Point((int)roundX, (int)roundY);
						}
					} else if (Math.abs((y-40-30)%60 - 30) > 20) {
						// in vertical range
						if (Math.abs((x-130)%60 - 30) > 20) {
							double roundX = Math.round((x-130)/60.0)*60 + 130;
							double roundY = Math.round((y-40-30)/60.0)*60 + 40 + 30;
							p = new Point((int)roundX, (int)roundY);
						}
					}
					
					// if nearby available spot was found, add and clear flags
					if (p != null) {
						boolean taken = false;
						for (LaserNode node : sources) {
							Point point = node.getLoc();
							if (point.equals(p)) {
								taken = true;
								break;
							}
						}
						
						taken = taken || targets.contains(p);
						
						if (!taken) {
							if (holdingSource)
								sources.add(new LaserNode(p, LaserNode.DIR_NE));
							else
								targets.add(p);
						}
					}
				}
				
				holdingTarget = holdingSource = false;
				updateLaserPath();
			}
		}
		
		repaint();
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
		repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
		repaint();
	}

	//////////////////
	// KEY LISTENER //
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_S:
				solvePuzzle();
				break;
			case KeyEvent.VK_C:
				clearPuzzle();
				break;
		}
	}
	
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }

}

