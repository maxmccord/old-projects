import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class LazorsSim {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Lazors Sim");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		
		SimCanvas sim = new SimCanvas();
		
		panel.add(sim, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		sim.requestFocus();
	}
}