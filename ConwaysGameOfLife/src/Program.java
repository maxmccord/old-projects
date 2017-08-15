import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Program {
	static MainPanel mainPanel = new MainPanel();
	
	public static void go() {
		// set up the frame
			JFrame frame = new JFrame("Conway's Game of Life");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800+8, 600+34);
			frame.add(mainPanel);
		
		// set up the menubar
			frame.setJMenuBar(setUpMenu());
		
		// display the frame
			frame.setVisible(true);
	}
	
	public static void main(String[] args) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() { go(); }
			});
	}
	
	// sets up the menu gui
	private static JMenuBar setUpMenu() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		
		menuBar = new JMenuBar();
		
		// file menu
			menu = new JMenu("File");
				menu.setMnemonic(KeyEvent.VK_F);
			menuBar.add(menu);
			
			// New - ctrl + n
				menuItem = new JMenuItem("New");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
				menu.add(menuItem);
			
			// Open - ctrl + o
				menuItem = new JMenuItem("Open...");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
				menu.add(menuItem);
				
			// separator
				menu.addSeparator();
			
			// Save - ctrl + s
				menuItem = new JMenuItem("Save");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
				menu.add(menuItem);
			
			// Save as - ctrl + alt + s
				menuItem = new JMenuItem("Save As...");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
				menu.add(menuItem);
		
		// view menu
			menu = new JMenu("View");
				menu.setMnemonic(KeyEvent.VK_V);
			menuBar.add(menu);
			
			// Zoom In - ctrl + +
				menuItem = new JMenuItem("Zoom In");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("zoomIn");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
			
			// Zoom Out - ctrl + -
				menuItem = new JMenuItem("Zoom Out");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("zoomOut");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
			
			// separator
				menu.addSeparator();
			
			// Set View Options...
				menuItem = new JMenuItem("Set View Options...");
				menu.add(menuItem);
				
		// insert menu
			menu = new JMenu("Insert");
				menu.setMnemonic(KeyEvent.VK_I);
			menuBar.add(menu);
			
			// load library here...
		
		// control menu
			menu = new JMenu("Control");
				menu.setMnemonic(KeyEvent.VK_C);
			menuBar.add(menu);
			
			// Play/Pause - ctrl + enter
				menuItem = new JMenuItem("Play/Pause");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("playPause");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
			
			// Step Forward - ctrl + \
				menuItem = new JMenuItem("Step Forward");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("stepForward");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
		
		// grid menu
			menu = new JMenu("Grid");
				menu.setMnemonic(KeyEvent.VK_G);
			menuBar.add(menu);
			
			// Clear Grid - ctrl + x
				menuItem = new JMenuItem("Clear Grid");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("clearGrid");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
			
			// Set Grid Properties
				menuItem = new JMenuItem("Set Grid Properties...");
				menu.add(menuItem);
		
		// tool menu
			menu = new JMenu("Tools");
				menu.setMnemonic(KeyEvent.VK_T);
			menuBar.add(menu);
			
			// Pencil - ctrl + w
				menuItem = new JMenuItem("Pencil");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("pencil");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
			
			// Eraser - ctrl + e
				menuItem = new JMenuItem("Eraser");
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
					menuItem.setActionCommand("eraser");
					menuItem.addActionListener(mainPanel);
				menu.add(menuItem);
		
		// help menu
			menu = new JMenu("Help");
				menu.setMnemonic(KeyEvent.VK_H);
			menuBar.add(menu);
			
			// How This Works... - F1
				menuItem = new JMenuItem("How This Works...");
				menu.add(menuItem);
				
			// separator
				menu.addSeparator();
				
			// About Conway's Game of Life...
				menuItem = new JMenuItem("About Conway's Game of Life");
				menu.add(menuItem);
				
		return menuBar;
	}
}