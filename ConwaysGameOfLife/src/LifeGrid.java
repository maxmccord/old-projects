public class LifeGrid implements Comparable {
	// alive/dead variables, makes it easier to set the space values
		public static final boolean ALIVE = true;
		public static final boolean DEAD = false;
	
	// declare variables
		public boolean[][] spaces; // public array where cells are stored
		public int width, height;  // width and height of the grid
	
	public LifeGrid(int w, int h) {
		// set width and height
			width = w;
			height = h;
		
		// initiate the spaces array
			spaces = new boolean[w][h];
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++)
					spaces[x][y] = DEAD;
	}
	
	// this method goes forward one generation
	public void step() {
		// declare array to store next generation
			boolean[][] nextGen = new boolean[width][height];
		
		// for every cell, determine the state it will be in for the next generation
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					nextGen[x][y] = getNextGen(x, y);
		
		// assign the next generation states to the current generation
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					spaces[x][y] = nextGen[x][y];
	}
	
	// creates a live cell
	public void create(int x, int y) {
		spaces[x][y] = ALIVE;
	}
	
	// kills a cell
	public void destroy(int x, int y) {
		spaces[x][y] = DEAD;
	}
	
	// clears the grid
	public void clear() {
		for (int x = 0; x < spaces.length; x++)
			for (int y = 0; y < spaces[0].length; y++)
				spaces[x][y] = DEAD;
	}
	
	// returns whether or not a cell is alive
	public boolean checkAlive(int x, int y) {
		if (x < 0)
			x = width - 1;
			
		if (x >= width)
			x = 0;
			
		if (y < 0)
			y = height - 1;
			
		if (y >= height)
			y = 0;
			
		return spaces[x][y];
	}
	
	// returns the height and width of the grid
	public int getWidth() {
		return spaces.length;
	}
	
	public int getHeight() {
		return spaces[0].length;
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	// determines the state of a specific cell in the next generation
	private boolean getNextGen(int x, int y) {
		int neighborNum = 0; // variable to store the cell's number of neighbors
		
		// check west
			if (checkAlive(x - 1, y))
				neighborNum++;
				
		// check northwest
			if (checkAlive(x - 1, y - 1))
				neighborNum++;
		
		// check north
			if (checkAlive(x, y - 1))
				neighborNum++;
		
		// check northeast
			if (checkAlive(x + 1, y - 1))
				neighborNum++;
		
		// check east
			if (checkAlive(x + 1, y))
				neighborNum++;
		
		// check southeast
			if (checkAlive(x + 1, y + 1))
				neighborNum++;
		
		// check south
			if (checkAlive(x, y + 1))
				neighborNum++;
		
		// check southwest
			if (checkAlive(x - 1, y + 1))
				neighborNum++;
		
		if (checkAlive(x, y)) {
			// 1. If cell has less than two neighbors, it dies
				if (neighborNum < 2)
					return DEAD;
			// 2. If cell has more than three neighbors, it dies
				if (neighborNum > 3)
					return DEAD;
			// 3. If cell has 2-3 neighbors, it lives
				if (neighborNum == 2 || neighborNum == 3)
					return ALIVE;
		} else {
			// 4. If a dead cell has 3 neighbors, it comes to life
				if (neighborNum == 3)
					return ALIVE;
				else
					return DEAD;
		}
		
		return DEAD;
	}
	
	////////////////////////
	// COMPARABLE METHODS //
	////////////////////////
	
	public int compareTo(Object obj) {
		if (obj instanceof LifeGrid) {
			LifeGrid grid = (LifeGrid)obj;
			return compareTo(grid);
		}
		
		return -1;
	}
	
	public int compareTo(LifeGrid grid) {
		if (getWidth() != grid.getWidth() || getHeight() != grid.getHeight()) {
			int size0 = getWidth() * getHeight();
			int size1 = grid.getWidth() * grid.getHeight();
			return (-1 * Math.abs(size0 - size1));
		}
		
		int diff = 0;
		
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
				if (checkAlive(x, y) ^ grid.checkAlive(x, y))
					diff++;
		
		return diff;
	}
}