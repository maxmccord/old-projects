import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class HostDialog extends JDialog {
	// static variables
	public static final int HOST_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	
	// private variables
	private JComboBox comboBox;
	private int status = -1;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	//////////////////////////
	
	public int getBoardSize() {
		return comboBox.getSelectedIndex()*2+3;
	}
	
	public int getCloseStatus() {
		return status;
	}
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public HostDialog(Frame parent) {
		super(parent, "Select Board Size", true);
		setResizable(false);
		
		// set 'CANCEL_PRESSED' status when dialog is x-ed out
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { status = CANCEL_PRESSED; } });
		
		// create top-level panel with border
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// create panel with label and combo box
		JPanel comboPanel = new JPanel();
		JLabel label = new JLabel("Choose Board Size:");
		
		String[] options = { "Extra Small Board (3x3)",
			"Small Board (5x5)",
			"Medium Board (7x7)",
			"Large Board(9x9)",
			"Extra Large Board (11x11)",
			"Huge Board (13x13)",
			"Gigantic Board (15x15)" };
		comboBox = new JComboBox(options);
		
		comboPanel.add(label);
		comboPanel.add(comboBox);
		
		// create panel with external ip loaded from whatsmyip.org
		JPanel addressPanel = new JPanel();
		JLabel ipLabel = new JLabel("Give this IP to your friend:");
		JTextField ipField = new JTextField(10);
		
		// attempt to read external ip
		try {
			URL ip = new URL("http://automation.whatismyip.com/n09230945.asp");
			BufferedReader in = new BufferedReader(new InputStreamReader(ip.openStream()));
			ipField.setText(in.readLine());
			in.close();
		} catch (Exception e) {
			ipField.setText("Couldn't load External IP");
		}
		
		ipField.setEditable(false);
		addressPanel.add(ipLabel);
		addressPanel.add(ipField);
		
		// create panel with 'host' and 'cancel' buttons
		JPanel buttonPanel = new JPanel();
		JButton hostButton = new JButton("Host");
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = HOST_PRESSED;
				setVisible(false);
			} });
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = CANCEL_PRESSED;
				setVisible(false);
			} });
		buttonPanel.add(hostButton);
		buttonPanel.add(cancelButton);
		
		// add the three panels to the top-level panel
		panel.add(comboPanel);
		panel.add(addressPanel);
		panel.add(buttonPanel);
		
		// 'host' button will be activated when 'enter' is pressed
		getRootPane().setDefaultButton(hostButton);
		
		// add the top-level panel to the content pane
		add(panel, BorderLayout.CENTER);
		
		// resize the frame according to components
		pack();
	}
	
	////////////////////
	// STATIC METHODS //
	////////////////////
	
	// creates and shows a new JoinDialog, returning the host's IP
	public static int showHostDialog(Frame parent) {
		HostDialog host = new HostDialog(parent);
		host.setVisible(true);
		
		if (host.getCloseStatus() == HOST_PRESSED)
			return host.getBoardSize();
		else
			return -1;
	}
}