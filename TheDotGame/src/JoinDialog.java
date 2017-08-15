import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JoinDialog extends JDialog {
	// static variables
	public static final int JOIN_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	
	// private variables
	private JTextField ipField;
	private int status = -1;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public String getIP() {
		return ipField.getText();
	}
	
	public int getCloseStatus() {
		return status;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public JoinDialog(Frame parent) {
		super(parent, "Enter Host's IP", true);
		setResizable(false);
		
		// set 'CANCEL_PRESSED' status when dialog is x-ed out
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { status = CANCEL_PRESSED; } });
		
		// create top-level panel with border
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// create panel with label and text field
		JPanel fieldPanel = new JPanel();
		JLabel label = new JLabel("Host's IP:");
		ipField = new JTextField(20);
		fieldPanel.add(label);
		fieldPanel.add(ipField);
		
		// create panel with 'join' and 'cancel' buttons
		JPanel buttonPanel = new JPanel();
		JButton joinButton = new JButton("Join");
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = JOIN_PRESSED;
				setVisible(false);
			} });
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = CANCEL_PRESSED;
				setVisible(false);
			} });
		buttonPanel.add(joinButton);
		buttonPanel.add(cancelButton);
		
		// add the two panels to the top-level panel
		panel.add(fieldPanel);
		panel.add(buttonPanel);
		
		// 'join' button will be activated when 'enter' is pressed
		getRootPane().setDefaultButton(joinButton);
		
		// add the top-level panel to the content pane
		add(panel, BorderLayout.CENTER);
		
		// resize the frame according to components
		pack();
	}
	
	////////////////////
	// STATIC METHODS //
	////////////////////
	
	// creates and shows a new JoinDialog, returning the host's IP
	public static String showJoinDialog(Frame parent) {
		JoinDialog join = new JoinDialog(parent);
		join.setVisible(true);
		
		if (join.getCloseStatus() == JOIN_PRESSED)
			return join.getIP();
		else
			return "";
	}
}