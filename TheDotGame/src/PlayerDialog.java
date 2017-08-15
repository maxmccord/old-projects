import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class PlayerDialog extends JDialog implements WindowListener {
	// static variables
	public static final int OK_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	
	// private variables
	private JTextField nameField;
	private JButton colorButton;
	private boolean firstRun;
	private int status = -1;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public String getName() {
		return nameField.getText();
	}
	
	public Color getColor() {
		return colorButton.getBackground();
	}
	
	public int getCloseStatus() {
		return status;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public PlayerDialog(Frame parent, boolean firstRun) {
		super(parent, "Edit Player Details", true);
		setResizable(false);
		
		// if this is the first run, cancelling the window will
		// make the application exit
		this.firstRun = firstRun;
		
		// set 'CANCEL_PRESSED' status when dialog is x-ed out (if not first run)
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		// create top-level panel with border
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// create panel with label and text field
		JPanel namePanel = new JPanel();
		JLabel label = new JLabel("Name:");
		nameField = new JTextField(20);
		namePanel.add(label);
		namePanel.add(nameField);
		
		// create panel with label and choose color button
		JPanel colorPanel = new JPanel();
		JLabel colorLabel = new JLabel("Color:");
		
		colorButton = new JButton("Choose");
		colorButton.setBackground(Color.RED);
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(colorButton, "Choose Player Color", colorButton.getBackground());
				colorButton.setBackground(newColor);
			}
		});
		
		colorPanel.add(colorLabel);
		colorPanel.add(colorButton);
		
		// create panel with 'ok' and 'cancel' buttons
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPressed();
			} });
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closePressed();
			} });
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		// add the three panels to the top-level panel
		panel.add(namePanel);
		panel.add(colorPanel);
		panel.add(buttonPanel);
		
		// 'join' button will be activated when 'enter' is pressed
		getRootPane().setDefaultButton(okButton);
		
		// add the top-level panel to the content pane
		add(panel, BorderLayout.CENTER);
		
		// resize the frame according to components
		pack();
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	// validates the name and closes if a name has been entered
	private void okPressed() {
		if (getName().equals("")) {
			JOptionPane.showMessageDialog(this, "You must enter a name for yourself.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
		} else {
			status = OK_PRESSED;
			setVisible(false);
		}
	}
	
	// closes the window if this instance wasn't opened from the 'options' button
	private void closePressed() {
		if (!firstRun) {
			status = CANCEL_PRESSED;
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this, "You must enter player details before continuing.", "Can't Close Window", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/////////////////////
	// WINDOW LISTENER //
	/////////////////////
	
	public void windowClosing(WindowEvent e) { closePressed(); }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	////////////////////
	// STATIC METHODS //
	////////////////////
	
	// creates and shows a new JoinDialog, returning the host's IP
	public static Player showPlayerDialog(Frame parent, boolean firstRun) {
		PlayerDialog player = new PlayerDialog(parent, firstRun);
		player.setVisible(true);
		
		if (player.getCloseStatus() == OK_PRESSED)
			return new Player(player.getName(), player.getColor());
		else
			return null;
	}
}