import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel implements ActionListener {
	// declare variables
		int width = 50;           // width of the life grid
		int height = 50;          // height of the life grid
		int boxSize = 10;         // size (pixels) of each box
		LifeGrid grid;            // the grid
		JLifeGridViewer gridView; // grid viewer
		
	public MainPanel() {
		// set layout
			setLayout(new BorderLayout());
		
		// set up the grid
			grid = new LifeGrid(30, 30);
		
		// set the Grid View inside a JScrollPane and add it to the panel
			gridView = new JLifeGridViewer(grid, 10, 20);
				gridView.setColorScheme(Color.white, Color.black, new Color(128, 128, 255));
			JTextArea textArea = new JTextArea(5, 30);
			JScrollPane scrollPane = new JScrollPane(gridView);
				scrollPane.setPreferredSize(new Dimension(450, 110));
			
			add(scrollPane, BorderLayout.CENTER);
	}
	
	////////////////////////////
	// ACTIONLISTENER METHODS //
	////////////////////////////
	
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		
		// view menu
			if (c.equals("zoomIn"))
				gridView.zoomIn();
				
			if (c.equals("zoomOut"))
				gridView.zoomOut();
		
		// control menu
			if (c.equals("playPause"))
				if (gridView.playing)
					gridView.pause();
				else
					gridView.play();
					
			if (c.equals("stepForward"))
				gridView.step();
		
		// grid menu
			if(c.equals("clearGrid"))
				gridView.clear();
		
		// tools menu
			if(c.equals("pencil"))
				gridView.setCurrentTool(JLifeGridViewer.PENCIL);
			if(c.equals("eraser"))
				gridView.setCurrentTool(JLifeGridViewer.ERASER);
	}
}