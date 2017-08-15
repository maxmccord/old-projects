import java.awt.*;
import javax.swing.*;

public class Program {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Dots!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		CardLayout cardLayout = new CardLayout();
		JPanel masterPanel = new JPanel(cardLayout);
		final String BROWSE_PANEL = "Browse Panel";
		final String DOTS_PANEL   = "Dots Panel";
		
		BrowsePanel browsePanel = new BrowsePanel();
		masterPanel.add(browsePanel, BROWSE_PANEL);
		
		// show the frame
		frame.add(masterPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		// wait for the user to press okay
		int i = 0;
		while (!browsePanel.isFinished()) {
			try { Thread.sleep(20); }
			catch (InterruptedException ex) { }
		}
		
		// set up the dots panel and show it
		JPanel dotsPanel = new JPanel(new BorderLayout());
		DotCanvas canvas = new DotCanvas(browsePanel.getFilePath());
		dotsPanel.add(canvas, BorderLayout.CENTER);
		masterPanel.add(dotsPanel, DOTS_PANEL);
		
		// switch to the dots panel
		cardLayout.show(masterPanel, DOTS_PANEL);
		
		canvas.requestFocusInWindow();
	}
}