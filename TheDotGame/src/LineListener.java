import java.awt.event.*;
import java.util.*;

public interface LineListener extends EventListener {
	// fired when a line is added
	public void lineAdded(int row, int col);
}