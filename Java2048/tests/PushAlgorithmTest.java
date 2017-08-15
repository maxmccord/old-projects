import static java.lang.System.*;

public class PushAlgorithmTest {
	// program entry point
	public static void main(String[] args) {
		int data[][] = { { 16,  4,  4,  4 },
		                 { 32, 16,  2,  0 },
		                 {  0,  4,  0,  0 },
		                 {  2,  0,  2,  0 } };
		                 
		printData(data);
		out.println();
		pushLeft(data);
		printData(data);
	}
	
	// EASY PRINTING METHODS
	
	private static void printData(int data[][]) {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++)
				out.printf("%5d", data[i][j]);
			out.println();
		}
	}
	
	private static void printRow(int row[]) {
		for (int i = 0; i < row.length; i++)
			out.printf("%5d", row[i]);
		out.println();
	}
	
	// PUSHING THE ARRAY AROUND
	
	private static void pushLeft(int data[][]) {
		for (int i = 0; i < data.length; i++)
			pushLeft(data[i]);
	}
	
	private static void pushRight(int data[][]) {
		for (int i = 0; i < data.length; i++)
			pushRight(data[i]);
	}
	
	private static void pushUp(int data[][]) {
		for (int i = 0; i < 4; i++) {
			int col[] = new int[4];
			
			for (int j = 0; j < 4; j++)
				col[j] = data[j][i];
			
			pushUp(col);
			
			for (int j = 0; j < 4; j++)
				data[j][i] = col[j];
		}
	}
	
	private static void pushDown(int data[][]) {
		for (int i = 0; i < 4; i++) {
			int col[] = new int[4];
			
			for (int j = 0; j < 4; j++)
				col[j] = data[j][i];
			
			pushDown(col);
			
			for (int j = 0; j < 4; j++)
				data[j][i] = col[j];
		}
	}
	
	// THE FOLLOWING METHODS WORK ON SINGLE ROWS/COLS
	
	// pushes the row to the left - other methods should call this one with transposed rows/cols
	private static void push(int row[]) {
		// count empty spaces
		int emptySpaces = 0;
		while (emptySpaces < row.length && row[emptySpaces] == 0) emptySpaces++;
		
		if (emptySpaces == row.length) {
			// if row is empty, do nothing
			return;
		} else if (emptySpaces > 0) {
			// otherwise, shift everything to the left
			for (int i = 0; i < row.length; i++) {
				if (i + emptySpaces < row.length)
					row[i] = row[i + emptySpaces];
				else
					row[i] = 0;
			}
		}
		
		for (int pos = 0; pos < row.length; pos++) {
			// row[pos] must be a number at this point - find the next number to the right
			int next = pos + 1;
			while (next < row.length && row[next] == 0) next++;
			
			if (next == row.length) {
				// if we got here, there isn't another number in the row
				break;
			} else {
				if (row[pos] == row[next]) {
					// the numbers match. combine the two and reove row[next]
					row[pos] *= 2;
					row[next] = 0;
				}
			}
			
			// count empty spaces to the right
			emptySpaces = 0;
			int j = pos + 1;
			while (j < row.length && row[j] == 0) {
				 emptySpaces++;
				 j++;
			}
			
			if (j == row.length) {
				// if row is empty, stop algorithm
				break;
			} else if (emptySpaces > 0) {
				// otherwise, shift everything to the left
				for (int i = pos+1; i < row.length; i++) {
					if (i + emptySpaces < row.length)
						row[i] = row[i + emptySpaces];
					else
						row[i] = 0;
				}
			}
		}
	}
	
	// simply calls push, to push the tiles left
	private static void pushLeft(int row[]) {
		push(row);
	}
	
	// reverses the data and pushes left - reverses again to get pushed right
	public static void pushRight(int row[]) {
		int reverse[] = new int[row.length];
		
		for (int i = 0; i < row.length; i++)
			reverse[row.length-1-i] = row[i];
		
		push(reverse);
		
		for (int i = 0; i < row.length; i++)
			row[i] = reverse[row.length-1-i];
	}
	
	// calls push to push the tiles left - works because rows/cols are indexed the same
	public static void pushUp(int col[]) {
		push(col);
	}
	
	// calls push right, because rows/cols are indexed the same
	public static void pushDown(int col[]) {
		pushRight(col);
	}
}









