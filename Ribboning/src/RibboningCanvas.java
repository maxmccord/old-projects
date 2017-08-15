import static java.lang.System.*;
import static java.awt.Color.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

public class RibboningCanvas extends JComponent implements MouseListener, MouseMotionListener {
	// static tool variables
	public static final int TOOL_DOT = 0;
	public static final int TOOL_BOX = 1;
	
	// ribboning variables
	private int spacing = 32;
	private int size = 8;
	private DotCollection dots = new DotCollection(spacing);
	private Set<RibbonUnit> ribbonUnits = new HashSet<RibbonUnit>();
	private Set<WedgeUnit> wedgeUnits = new HashSet<WedgeUnit>();
	
	// view variables
	private int transX = 0;
	private int transY = 0;
	private int initX = 0;
	private int initY = 0;
	private int initTransX = 0;
	private int initTransY = 0;
	private boolean editMode = true;
	
	// editing variables
	private int tool = TOOL_DOT;
	private Point hoverDot = null;
	private Rectangle selectBox = new Rectangle(-1, -1, 0, 0);
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public Dot[] getDots() {
		return dots.getDots();
	}
	
	public void setSpacing(int spacing) {
		this.spacing = spacing;
		dots.setSpacing(spacing);
		computeComponents();
		repaint();
	}
	
	public int getSpacing() {
		return spacing;
	}
	
	public void setRibbonSize(int size) {
		this.size = size;
		repaint();
	}
	
	public int getRibbonSize() {
		return size;
	}
	
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		repaint();
	}
	
	public boolean isInEditMode() {
		return editMode;
	}
	
	public int getTool() {
		return tool;
	}
	
	public void setTool(int tool) {
		this.tool = tool;
		repaint();
	}
	
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public RibboningCanvas() {
		// register listeners
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void paintComponent(Graphics g) {
		paint((Graphics2D)g);
	}
	
	public void paint(Graphics2D g) {
		// fill background
		g.setColor(white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (editMode) {
			// draw faded dots
			g.setColor(new Color(200, 200, 200)); // light gray
			int shiftX = transX % spacing;
			int shiftY = transY % spacing;
			
			for (int y = shiftY; y <= getHeight(); y += spacing)
				for (int x = shiftX; x <= getWidth(); x += spacing)
					g.fillOval(x-2, y-2, 4, 4);
			
			for (int y = shiftY-spacing/2; y <= getHeight(); y += spacing)
				for (int x = shiftX-spacing/2; x <= getWidth(); x += spacing)
					g.fillOval(x-2, y-2, 4, 4);
			
			// for the remaining paint operations, translate the
			// graphics to match with the drag position
			g.translate(transX, transY);
			
			// draw current tool
			if (tool == TOOL_DOT) {
				if (hoverDot != null) {
					// draw a dot at the dot nearest the mouse pointer
					g.setColor(new Color(160, 190, 225)); // light blue
					g.fillOval(hoverDot.x-5, hoverDot.y-5, 10, 10);
					g.setColor(new Color(122, 138, 153)); // dark blue
					g.drawOval(hoverDot.x-5, hoverDot.y-5, 9, 9);
				}
			} else if (tool == TOOL_BOX) {
				// correct negative dimensions
				Rectangle box = (Rectangle)selectBox.clone();
				
				if (box.width < 0) {
					box.x += box.width;
					box.width *= -1;
				}
				
				if (box.height < 0) {
					box.y += box.height;
					box.height *= -1;
				}
				
				// draw the altered box
				g.setColor(new Color(160, 190, 225, 128)); // transparentlight blue
				g.fill(box);
				g.setColor(new Color(122, 138, 153)); // dark blue
				g.draw(box);
			}
			
			// draw dots
			g.setColor(black);
			for (Dot d : dots.getDots()) {
				Point point = d.getLocation();
				g.fillOval(point.x-3, point.y-3, 6, 6);
			}
		} else {
			// translate graphics to match with drag position
			g.translate(transX, transY);
			
			g.setColor(black);
			// draw all ribbon units
			for (RibbonUnit r : ribbonUnits) {
				Point top = r.getTop().getLocation();
				Point right = r.getRight().getLocation();
				Point left = r.getLeft().getLocation();
				Point bottom = r.getBottom().getLocation();
				
				// left to right points
				Point one   = new Point(top.x-size/2,           top.y+size/2          );
				Point two   = new Point(top.x+spacing/2-size/2, top.y+spacing/2+size/2);
				Point three = new Point(top.x-spacing/2+size/2, top.y+spacing/2-size/2);
				Point four  = new Point(top.x+size/2,           top.y+spacing-size/2  );
				
				// right to left points
				Point five  = new Point(top.x+size/2,           top.y+size/2          );
				Point six   = new Point(top.x-spacing/2+size/2, top.y+spacing/2+size/2);
				Point seven = new Point(top.x+spacing/2-size/2, top.y+spacing/2-size/2);
				Point eight = new Point(top.x-size/2,           top.y+spacing-size/2  );
				
				// inside points
				Point nine   = new Point(top.x,                top.y+size        );
				Point ten    = new Point(top.x-spacing/2+size, top.y+spacing/2   );
				Point eleven = new Point(top.x+spacing/2-size, top.y+spacing/2   );
				Point twelve = new Point(top.x,                top.y+spacing-size);
				
				// draw the ribbon intersection, based on the direction
				if (r.isLeftToRight()) {
					g.draw(new Line2D.Double(one, two));
					g.draw(new Line2D.Double(three, four));
					
					g.draw(new Line2D.Double(five, nine));
					g.draw(new Line2D.Double(seven, eleven));
					
					g.draw(new Line2D.Double(ten, six));
					g.draw(new Line2D.Double(twelve, eight));
				} else {
					g.draw(new Line2D.Double(five, six));
					g.draw(new Line2D.Double(seven, eight));
					
					g.draw(new Line2D.Double(one, nine));
					g.draw(new Line2D.Double(three, ten));
					
					g.draw(new Line2D.Double(eleven, two));
					g.draw(new Line2D.Double(twelve, four));
				}
			}
			
			// draw wedge units
			for (WedgeUnit w : wedgeUnits) {
				double d = size/2;
				int r1 = (int)Math.sqrt(Math.pow(d, 2) + Math.pow(d, 2));
				d = spacing/2 - size/2;
				int r2 = (int)Math.sqrt(Math.pow(d, 2) + Math.pow(d, 2));
				
				g.drawArc(w.getCenter().x-r1, w.getCenter().y-r1, r1*2, r1*2, w.getStartAngle(), 90);
				g.drawArc(w.getCenter().x-r2, w.getCenter().y-r2, r2*2, r2*2, w.getStartAngle(), 90);
			}
			
		}
		
		// return to original translation
		g.translate(-transX, -transY);
	}
	
	public void add(int x, int y) {
		activateDot(x, y);
	}
	
	public void clear() {
		dots.clear();
	}
	
	private void alertListeners() {
		for (ChangeListener l : listeners)
			l.stateChanged(new ChangeEvent(this));
	}
	
	// computes the ribbons and wedges required to draw the pattern
	private void computeComponents() {
		if (dots.getDots().length > 0) {
			// clear units
			ribbonUnits.clear();
			wedgeUnits.clear();
			
			// calculate the farthest left x coordinate
			int leftSide = dots.getDots()[0].getLocation().x;
			for (Dot d : dots.getDots())
				leftSide = Math.min(leftSide, d.getLocation().x);
			
			// cycle through dots and calculate ribbon and wedge units
			for (Dot d : dots.getDots()) {
				// if possible, use the dot as the top of a ribbon unit
				if (d.getType() != Dot.NULL)
					if (d.getSE().getType() != Dot.NULL)
						if (d.getSW().getType() != Dot.NULL)
							if (d.getSW().getSE().getType() != Dot.NULL)
								ribbonUnits.add(new RibbonUnit(d, ((d.getLocation().x - leftSide) % spacing == 0)));
								
				// if possible, use the dot as the center for (a) wedge unit(s)
				if (d.getType() == Dot.INSIDE && d.getNeighborCount(Dot.INSIDE) != 4) {
					// gather the types of all neighbors
					int ne = d.getNE().getType();
					int nw = d.getNW().getType();
					int sw = d.getSW().getType();
					int se = d.getSE().getType();
					
					// if two adjacent neighbors are not of type INSIDE, use them as
					// controls for a wedge unit with the current dot as the center
					if (ne != Dot.INSIDE && nw != Dot.INSIDE && d.getNE().getNW().isNull())
						wedgeUnits.add(new WedgeUnit(d, d.getNE(), d.getNW()));
					if (nw != Dot.INSIDE && sw != Dot.INSIDE && d.getNW().getSW().isNull())
						wedgeUnits.add(new WedgeUnit(d, d.getNW(), d.getSW()));
					if (sw != Dot.INSIDE && se != Dot.INSIDE && d.getSW().getSE().isNull())
						wedgeUnits.add(new WedgeUnit(d, d.getSW(), d.getSE()));
					if (se != Dot.INSIDE && ne != Dot.INSIDE && d.getSE().getNE().isNull())
						wedgeUnits.add(new WedgeUnit(d, d.getSE(), d.getNE()));
				}
			}
		}
	}
	
	// checks to see if the specified dot can be used as the top of a ribbon unit
	private boolean isRibbonUnit(Dot top) {
		if (top.getType() != Dot.NULL) {
			Dot right = top.getSE();
			Dot left = top.getSW();
			if (right.getType() != Dot.NULL) {
				if (left.getType() != Dot.NULL) {
					if (left.getSE().getType() != Dot.NULL)
						return true;
				}
			}
		}
		
		return false;
	}
	
	// switches the state of the given dot
	private void activateDot(int x, int y) {
		if (!dots.add(x, y))
			dots.remove(x, y);
		computeComponents();
		alertListeners();
	}
	
	// states whether or not the given horizontal dot axis is a shifted one
	private boolean isShiftedAxis(int y) {
		return (y % spacing != 0);
	}
	
	////////////////////
	// MOUSE LISTENER //
	////////////////////
	
	public void mousePressed(MouseEvent e) {
		initX = e.getX();
		initY = e.getY();
		initTransX = transX;
		initTransY = transY;
		
		if (tool == TOOL_BOX) {
			selectBox.setLocation(initX, initY);
			selectBox.setSize(0, 0);
		}
		
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		if (editMode) {
			// if not releasing from dragging, add the dot
			if (!isShiftClick(e)) {
				if (tool == TOOL_DOT && hoverDot != null) {
					activateDot(hoverDot.x, hoverDot.y);
				} else if (tool == TOOL_BOX) {
					// adjust the selection area if its either or both dimensions are negative
					Rectangle box = (Rectangle)selectBox.clone();
				
					if (box.width < 0) {
						box.x += box.width;
						box.width *= -1;
					}
					
					if (box.height < 0) {
						box.y += box.height;
						box.height *= -1;
					}
					
					for (int i = box.y; i <= (box.y+box.height); i++) {
						if (i % (spacing/2) == 0) {
							for (int j = box.x; j <= (box.x+box.width); j++) {
								if (isShiftedAxis(i)) {
									if ((j-spacing/2) % spacing == 0)
										activateDot(j, i);
								} else {
									if (j % spacing == 0)
										activateDot(j, i);
								}
							}
						}
					}
					
					// reset the selection box
					selectBox.setLocation(-1, -1);
					selectBox.setSize(0, 0);
				}
			}
		}
		
		repaint();
	}
	
	public void mouseExited(MouseEvent e) {
		hoverDot = null;
	}
	
	public void mouseEntered(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	
	///////////////////////////
	// MOUSE MOTION LISTENER //
	///////////////////////////
	
	public void mouseMoved(MouseEvent e) {
		if (editMode && tool == TOOL_DOT) {
			// translate the captured mouse coordinate
			int x = e.getX() - transX;
			int y = e.getY() - transY;
			
			// calculate bounding box to check
			Rectangle box = new Rectangle(x-(spacing/2), y-(spacing/2), spacing, spacing);
			
			// create map of points to their distance from the mouse
			Map<Point, Double> points = new HashMap<Point, Double>();
			
			// loop through the area within the box and add all points within to the map
			for (int i = box.y; i <= (box.y+box.height); i++) {
				if (i % (spacing/2) == 0) {
					for (int j = box.x; j <= (box.x+box.width); j++) {
						if (isShiftedAxis(i)) {
							if ((j-spacing/2) % spacing == 0) {
								points.put(new Point(j, i), Point.distance(x, y, j, i));
							}
						} else {
							if (j % spacing == 0) {
								points.put(new Point(j, i), Point.distance(x, y, j, i));
							}
						}
					}
				}
			}
			
			// calculate the closest Point and its distance from the mouse
			Point lowestPoint = new Point(-1, -1);
			double lowestDist = Double.MAX_VALUE;
			for (Point p : points.keySet()) {
				if (points.get(p) <= lowestDist) {
					lowestDist = points.get(p);
					lowestPoint = p;
				}
			}
			
			// ensure that the closest point is within a certain radius of the mouse,
			// the radius being a fraction of the current spacing. if the point matches
			// this condition, the hover dot will be moved to match its position
			if (lowestDist <= spacing/2.8)
				hoverDot = new Point(lowestPoint);
			else
				hoverDot = null;
			
			// repaint the screen
			repaint();
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (isShiftClick(e)) {
			int xDist = Math.abs(e.getX() - initX);
			int yDist = Math.abs(e.getY() - initY);
			
			transX = initTransX + e.getX() - initX;
			transY = initTransY + e.getY() - initY;
		} else if (editMode) {
			if (tool == TOOL_BOX) {
				selectBox.setSize(e.getX()-initX, e.getY()-initY);
			} else {
				mouseMoved(e);
			}
		}
		
		repaint();
	}
	
	private boolean isShiftClick(MouseEvent e) {
		int shift = MouseEvent.SHIFT_MASK;
		if ((e.getModifiers() & shift) == shift)
			return true;
		else
			return false;
	}
}