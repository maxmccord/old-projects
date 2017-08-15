import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BrowsePanel extends JPanel implements ActionListener {
	// private members
	private boolean finished;
	private String filePath;
	
	JTextField filePathField;
	JButton browseButton;
	JButton okButton;
	
	//////////////////////////
	// ACCESSORS / MUTATORS //
	
	public boolean isFinished() {
		return finished;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	/////////////////
	// CONSTRUCTOR //
	
	public BrowsePanel() {
		setPreferredSize(new Dimension(Constants.CANVAS_SIZE, Constants.CANVAS_SIZE));
		
		finished = false;
		
		filePathField = new JTextField(20);
		
		browseButton = new JButton("Browse...");
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);
		
		okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		add(filePathField);
		add(browseButton);
		add(okButton);
	}
	
	/////////////////////
	// ACTION LISTENER //
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("browse")) {
			JFrame parentFrame = (JFrame)SwingUtilities.getRoot(this);
			
			FileDialog fileDialog = new FileDialog(parentFrame, "Locate Image", FileDialog.LOAD);
			
			fileDialog.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						String lowercase = name.toLowerCase();
						return false;
						//return (lowercase.endsWith(".png"));
					}
				});
			
			fileDialog.setVisible(true);
			
			filePathField.setText(fileDialog.getDirectory() + fileDialog.getFile());
		} else if (e.getActionCommand().equals("ok")) {
			filePath = filePathField.getText();
			finished = true;
		}
	}
}




