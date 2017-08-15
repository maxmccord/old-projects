import java.awt.*;
import java.util.*;


public class DotCollection {
	// variables
	private Set<Dot> dots = new HashSet<Dot>();
	private int spacing;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public Dot[] getDots() {
		Dot[] ret = new Dot[dots.size()];
		
		int i = 0;
		for (Dot d : dots)
			ret[i++] = d;
		
		return ret;
	}
	
	public int getSize() {
		return dots.size();
	}
	
	public void setSpacing(int spacing) {
		int old = this.spacing;
		this.spacing = spacing;
		
		for (Dot d : dots) {
			int x = (int)((double)spacing/old * d.getLocation().x);
			int y = (int)((double)spacing/old * d.getLocation().y);
			d.setLocation(x, y);
		}
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public DotCollection(int spacing) {
		this.spacing = spacing;
	}
	
	////////////////////
	// PUBLIC METHODS //
	////////////////////
	
	public void clear() {
		dots.clear();
	}
	
	public boolean add(int x, int y) {
		Dot add = new Dot(x, y);
		
		if (contains(x, y))
			return false;
		
		int space = spacing/2;
		Point ne = new Point(x + space, y - space);
		Point nw = new Point(x - space, y - space);
		Point sw = new Point(x - space, y + space);
		Point se = new Point(x + space, y + space);
		
		for (Dot d : dots) {
			if (d.getLocation().equals(ne)) {
				add.setNE(d);
				d.setSW(add);
				ne.setLocation(-1, -1);
				continue;
			}
			
			if (d.getLocation().equals(nw)) {
				add.setNW(d);
				d.setSE(add);
				nw.setLocation(-1, -1);
				continue;
			}
			
			if (d.getLocation().equals(sw)) {
				add.setSW(d);
				d.setNE(add);
				sw.setLocation(-1, -1);
				continue;
			}
			
			if (d.getLocation().equals(se)) {
				add.setSE(d);
				d.setNW(add);
				se.setLocation(-1, -1);
				continue;
			}
		}
		
		dots.add(add);
		calculateTypes();
		
		return true;
	}
	
	public boolean remove(int x, int y) {
		if (!contains(x, y))
			return false;
		
		Point p = new Point(x, y);
		
		Dot remove = new Dot(-1, -1);
		boolean found = false;
		for (Dot d : dots) {
			if (p.equals(d.getLocation())) {
				remove = d;
				found = true;
				break;
			}
		}
		
		if (found) {
			if (!remove.getNE().isNull())
				remove.getNE().setSW(Dot.DEFAULT_DOT);
			if (!remove.getNW().isNull())
				remove.getNW().setSE(Dot.DEFAULT_DOT);
			if (!remove.getSW().isNull())
				remove.getSW().setNE(Dot.DEFAULT_DOT);
			if (!remove.getSE().isNull())
				remove.getSE().setNW(Dot.DEFAULT_DOT);
			dots.remove(remove);
			calculateTypes();
		}
		
		return true;
	}
	
	public boolean contains(int x, int y) {
		Point p = new Point(x, y);
		
		for (Dot d : dots)
			if (p.equals(d.getLocation()))
				return true;
		
		return false;
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private void calculateTypes() {
		for (Dot d : dots) {
			switch (d.getNeighborCount()) {
				case 1:
					d.setType(Dot.CORNER);
					break;
				case 2:
					d.setType(Dot.EDGE);
					break;
				case 3:
					d.setType(Dot.INNER_CORNER);
					break;
				case 4:
					d.setType(Dot.INSIDE);
					break;
			}
		}
	}
}