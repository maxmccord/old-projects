import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Program extends JFrame implements ActionListener, ChangeListener {
	public static void main(String[] args) {
		// create and display the frame
		new Program().setVisible(true);
	}
	
	// title variables
	private final String TITLE = "Ribboning is fun!";
	private final String DEFAULT_FILE = "new file";
	private String currentFile = DEFAULT_FILE;
	
	// components
	private RibboningCanvas canvas;
	private JCheckBoxMenuItem editModeItem;
	private JToolBar toolBar;
	private JSlider spacingSlider;
	private JLabel spacingLabel;
	private JSlider sizeSlider;
	private JLabel sizeLabel;
	private JToggleButton editModeButton;
	private JToggleButton dotToolButton;
	private JToggleButton boxToolButton;
	
	public Program() {
		// set properties
		setTitle(TITLE + " - [" + DEFAULT_FILE + "]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(650+8, 500+34);
		
		// create menu system
		JMenuBar menuBar = new JMenuBar();
		
		// construct file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem newItem = makeMenuItem("New", KeyEvent.VK_N, "control N");
		fileMenu.add(newItem);
		
		JMenuItem openItem = makeMenuItem("Open...", KeyEvent.VK_O, "control O");
		fileMenu.add(openItem);
		
		JMenuItem saveItem = makeMenuItem("Save", KeyEvent.VK_S, "control S");
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = makeMenuItem("Save As...", KeyEvent.VK_A);
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exitItem = makeMenuItem("Exit", KeyEvent.VK_X);
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		
		// construct view menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		editModeItem = new JCheckBoxMenuItem("Edit Mode", true);
		editModeItem.setMnemonic(KeyEvent.VK_E);
		editModeItem.setAccelerator(KeyStroke.getKeyStroke("E"));
		editModeItem.addActionListener(this);
		viewMenu.add(editModeItem);
		
		menuBar.add(viewMenu);
		
		// set the frame's menu bar
		setJMenuBar(menuBar);
		
		// construct toolbar
		toolBar = new JToolBar();
		toolBar.setBackground(new Color(238, 238, 238));
		
		spacingLabel = new JLabel("Spacing: 32");
		toolBar.add(spacingLabel);
		
		spacingSlider = new JSlider(16, 256, 32);
		spacingSlider.setMajorTickSpacing(80);
		spacingSlider.setMinorTickSpacing(16);
		spacingSlider.setPaintTicks(true);
		spacingSlider.setSnapToTicks(true);
		spacingSlider.addChangeListener(this);
		toolBar.add(spacingSlider);
		
		sizeLabel = new JLabel("Size: 8");
		toolBar.add(sizeLabel);
		
		sizeSlider = new JSlider(0, spacingSlider.getValue(), 8);
		sizeSlider.setMajorTickSpacing(spacingSlider.getValue());
		sizeSlider.setMinorTickSpacing(4);
		sizeSlider.setPaintTicks(true);
		sizeSlider.addChangeListener(this);
		toolBar.add(sizeSlider);
		
		toolBar.addSeparator();
		
		// create the edit mode button with custom icon
		Icon editModeIcon = new Icon() {
			public int getIconWidth() {
				return 16;
			}
			
			public int getIconHeight() {
				return 16;
			}
			
			// draw a pencil, for editing
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.translate(x, y);
				
				// eraser
				g.setColor(new Color(255, 179, 255)); // light pink
				g.fillRect(2, 2, 12, 8);
				
				// body
				g.setColor(Color.yellow);
				g.fillRect(2, 10, 12, 6);
				
				// outlining
				g.setColor(Color.black);
				g.drawLine(2, 1, 13, 1);
				g.drawLine(1, 2, 1, 15);
				g.drawLine(14, 2, 14, 15);
				g.drawLine(2, 2, 2, 3);
				g.drawLine(13, 2, 13, 3);
				g.drawLine(3, 4, 12, 4);
				g.drawLine(2, 9, 2, 9);
				g.drawLine(13, 9, 13, 9);
				g.drawLine(3, 10, 12, 10);
				g.drawLine(5, 11, 5, 15);
				g.drawLine(10, 11, 10, 15);
				
				g.translate(-x, -y);
			}
		};
		
		editModeButton = new JToggleButton(editModeIcon, true);
		editModeButton.setActionCommand("Edit Mode");
		editModeButton.addActionListener(this);
		toolBar.add(editModeButton);
		
		toolBar.addSeparator();
		
		// create the dot tool button with custom icon
		Icon dotIcon = new Icon() {
			public int getIconWidth() {
				return 16;
			}
			
			public int getIconHeight() {
				return 16;
			}
			
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(new Color(160, 190, 225)); // light blue
				g.fillOval(x+3, y+3, 10, 10);
				g.setColor(new Color(122, 138, 153)); // dark blue
				g.drawOval(x+3, y+3, 9, 9);
			}
		};
		
		dotToolButton = new JToggleButton(dotIcon, true);
		dotToolButton.setActionCommand("Dot Tool");
		dotToolButton.addActionListener(this);
		toolBar.add(dotToolButton);
		
		// create the box tool button with custom icon
		Icon boxIcon = new Icon() {
			public int getIconWidth() {
				return 16;
			}
			
			public int getIconHeight() {
				return 16;
			}
			
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(new Color(160, 190, 225)); // light blue
				g.fillRect(x, y, 14, 14);
				g.setColor(new Color(122, 138, 153)); // dark blue
				g.drawRect(x, y, 14, 14);
			}
		};
		
		boxToolButton = new JToggleButton(boxIcon, false);
		boxToolButton.setActionCommand("Box Tool");
		boxToolButton.addActionListener(this);
		toolBar.add(boxToolButton);
		
		add(toolBar, BorderLayout.PAGE_START);
		
		// create the ribboning canvas and add it to the center of the frame
		canvas = new RibboningCanvas();
		canvas.addChangeListener(this);
		add(canvas, BorderLayout.CENTER);
	}
	
	private JMenuItem makeMenuItem(String name, int mnemonic) {
		JMenuItem item = new JMenuItem(name);
		item.setMnemonic(mnemonic);
		item.addActionListener(this);
		return item;
	}
	
	private JMenuItem makeMenuItem(String name, int mnemonic, String accelerator) {
		JMenuItem item = makeMenuItem(name, mnemonic);
		item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		return item;
	}
	
	private void save(String path) {
		try {
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
			
			w.print(canvas.getSpacing() + " ");
			w.print(canvas.getRibbonSize() + " ");
			w.print(canvas.getDots().length);
			w.println();
			
			for (Dot d : canvas.getDots()) {
				Point p = d.getLocation();
				w.println(p.x + " " + p.y);
			}
			
			w.close();
			
			currentFile = new File(path).getName();
			setTitle(TITLE + " - [" + currentFile + "]");
		} catch (Exception e) { }
	}
	
	private void open(String path) {
		try {
			Scanner file = new Scanner(new File(path));
			
			String[] header = file.nextLine().trim().split(" ");
			spacingSlider.setValue(Integer.parseInt(header[0]));
			sizeSlider.setValue(Integer.parseInt(header[1]));
			
			int length = Integer.parseInt(header[2]);
			canvas.clear();
			for (int i = 0; i < length; i++) {
				String[] point = file.nextLine().trim().split(" ");
				int x = Integer.parseInt(point[0]);
				int y = Integer.parseInt(point[1]);
				canvas.add(x, y);
			}
			
			file.close();
			
			currentFile = new File(path).getName();
			setTitle(TITLE + " - [" + currentFile + "]");
		} catch (Exception e) { }
	}
	
	/////////////////////
	// ACTION LISTENER //
	/////////////////////
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		// file menu
		if (s.equals("New")) {
			canvas.clear();
			setTitle(TITLE + " - [" + DEFAULT_FILE + "]");
		} else if (s.equals("Open...")) {
			open("hello.rib");
		} else if (s.indexOf("Save") == 0) {
			String path;
			boolean pathNeeded = (s.equals("Save As...") || currentFile.equals(DEFAULT_FILE));
			
			if (pathNeeded) {
				path = "NO PATH.rib";
			} else {
				path = currentFile;
			}
			
			save(path);
		} else if (s.equals("Exit")) {
			System.exit(0);
		}
		
		// view menu / edit mode button
		else if (s.equals("Edit Mode")) {
			boolean editMode = !canvas.isInEditMode();
			canvas.setEditMode(editMode);
			editModeItem.setSelected(editMode);
			editModeButton.setSelected(editMode);
			dotToolButton.setEnabled(editMode);
			boxToolButton.setEnabled(editMode);
		}
		
		// toolbar buttons
		else if (s.equals("Dot Tool")) {
			canvas.setTool(RibboningCanvas.TOOL_DOT);
			dotToolButton.setSelected(true);
			boxToolButton.setSelected(false);
		} else if (s.equals("Box Tool")) {
			canvas.setTool(RibboningCanvas.TOOL_BOX);
			boxToolButton.setSelected(true);
			dotToolButton.setSelected(false);
		}
	}
	
	/////////////////////
	// CHANGE LISTENER //
	/////////////////////
	
	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();
		
		if (spacingSlider.equals(o)) {
			if (!spacingSlider.getValueIsAdjusting()) {
				canvas.setSpacing(spacingSlider.getValue());
				
				sizeSlider.setValue(spacingSlider.getValue()/4);
				sizeSlider.setMaximum(spacingSlider.getValue()/2);
				sizeSlider.setMajorTickSpacing(spacingSlider.getValue());
			}
			
			spacingLabel.setText("Spacing: " + String.valueOf(spacingSlider.getValue()));
		} else if (sizeSlider.equals(o)) {
			if (!sizeSlider.getValueIsAdjusting())
				canvas.setRibbonSize(sizeSlider.getValue());
				
			sizeLabel.setText("Size: " + String.valueOf(sizeSlider.getValue()));
		} else if (canvas.equals(o)) {
			setTitle(TITLE + " - [" + currentFile + "]*");
		}
	}
}