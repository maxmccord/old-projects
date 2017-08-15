import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.text.*;

public class Program implements ActionListener, LineListener, ConnectionListener {
	// program entry point
	public static void main(String[] args) throws Exception {
		new Program();
	}
	
	// constants
	public static final int DEFAULT_PORT = 4444;
	
	// private variables
	private DotGame game; // game manager
	private Player user, opponent;
	private DotGameConnection connect;
	
	// gui variables
	private JFrame frame;
	private StyledDocument chatDoc;
	private JTextField sendField;
	private JButton hostGameButton, joinGameButton, leaveGameButton, optionsButton;
	private JButton sendButton;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Program() {
		// set up dot game
		user = PlayerDialog.showPlayerDialog(frame, true);
		opponent = new Player("Default", Color.BLACK);
		game = new DotGame(this, user);
		
		// initialize UI
		initUI();
	}
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	// initializes the UI
	private void initUI() {
		// create frame
		frame = new JFrame("The Dot Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Program ref = this;
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ref.windowClosing(e);
			}
		});
		
		// set up the chat pane and init the styled document
	    JTextPane pane = new JTextPane();
	    pane.setEditable(false);
	    chatDoc = pane.getStyledDocument();
	    
	    Style defaultStyle = chatDoc.addStyle("default", new StyleContext().getStyle(StyleContext.DEFAULT_STYLE));
	    StyleConstants.setFontFamily(defaultStyle, "Courier New");
	    StyleConstants.setFontSize(defaultStyle, 12);
	    Style player1Style = chatDoc.addStyle("name", defaultStyle);
		
		// set up the ui
		JPanel parent = new JPanel();
		parent.setLayout(new BoxLayout(parent, BoxLayout.PAGE_AXIS));
		parent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		hostGameButton = new JButton("Host Game");
		hostGameButton.addActionListener(this);
		joinGameButton = new JButton("Join Game");
		joinGameButton.addActionListener(this);
		leaveGameButton = new JButton("Leave Game");
		leaveGameButton.setEnabled(false);
		leaveGameButton.addActionListener(this);
		optionsButton = new JButton("Options");
		optionsButton.addActionListener(this);
		
		buttonPanel.add(hostGameButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(joinGameButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(leaveGameButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(optionsButton);
		buttonPanel.add(Box.createHorizontalGlue());
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		
		JPanel gamePanel = new JPanel(new BorderLayout());
		gamePanel.setBackground(Color.BLACK);
		gamePanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		gamePanel.add(game, BorderLayout.CENTER);
		
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setPreferredSize(new Dimension(150, 360));
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
			public void adjustmentValueChanged(AdjustmentEvent e) {  
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				System.out.println(e.getAdjustmentType());
			}}); 
		
		JPanel sendChatPanel = new JPanel();
		sendField = new JTextField(20);
		sendField.addActionListener(this);
		sendField.setEnabled(false);
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);
		sendChatPanel.add(sendField);
		sendChatPanel.add(sendButton);
		
		chatPanel.add(scrollPane);
		chatPanel.add(sendChatPanel);
		
		lowerPanel.add(gamePanel);
		lowerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		lowerPanel.add(chatPanel);
		
		parent.add(buttonPanel);
		parent.add(Box.createRigidArea(new Dimension(0, 10)));
		parent.add(lowerPanel);
		
		frame.add(parent, BorderLayout.CENTER);
		
		// show the frame
		//frame.setSize(700, 500);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		
		// give the chat box immediate focus
		sendField.requestFocusInWindow();
	}
	
	private void chat(String name, Color color, String msg) {
		try {
			int line = chatDoc.getLength();
			chatDoc.insertString(line, "<" + name + "> " + msg + "\n", chatDoc.getStyle("default"));
			StyleConstants.setForeground(chatDoc.getStyle("name"), color);
			chatDoc.setCharacterAttributes(line+1, name.length(), chatDoc.getStyle("name"), true);
		} catch (Exception e) { }
	}
	
	private void consoleChat(String msg) {
		chat("Console", new Color(128, 128, 128), msg);
	}
	
	private void windowClosing(WindowEvent e) {
		try {
			connect.sendStop();
			connect.close();
		} catch (Exception ex) { }
	}
	
	private void updateUI(boolean gameOn) {
		sendField.setEnabled(gameOn);
		sendButton.setEnabled(gameOn);
		hostGameButton.setEnabled(!gameOn);
		joinGameButton.setEnabled(!gameOn);
		leaveGameButton.setEnabled(gameOn);
	}
	
	/////////////////////
	// ACTION LISTENER //
	/////////////////////
	
	// processes button presses and chat messages
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		String cmd = e.getActionCommand();
		
		if (src == sendField || cmd.equals("Send")) {
			// if 'enter' is pressed in the chat text field OR the 'send' button is pressed...
			String msg = sendField.getText();
			sendField.setText("");
			sendField.requestFocusInWindow();
			connect.sendChat(msg);
			chat(user.getName(), user.getColor(), msg);
		} else if (cmd.equals("Host Game")) {
			// show dialog and set new size
			final int size = HostDialog.showHostDialog(frame);
			
			game.setMessage("Listening for clients on port " + DEFAULT_PORT + "...");
			
			final Program ref = this;
			Thread connectThread = new Thread(new Runnable() {
				public void run() {
					connect = new HostConnection(ref, DEFAULT_PORT, size);
					connect.openConnection();
					connect.sendPlayerInfo(user);
				}
			});
			connectThread.start();
		} else if (cmd.equals("Join Game")) {
			// show dialog prompting for ip address
			final String ip = JoinDialog.showJoinDialog(frame);
			
			game.setMessage("Looking for " + ip + " on port " + DEFAULT_PORT + "...");
			
			final Program ref = this;
			Thread connectThread = new Thread(new Runnable() {
				public void run() {
					connect = new ClientConnection(ref, ip, DEFAULT_PORT);
					connect.openConnection();
					connect.sendPlayerInfo(user);
				}
			});
			connectThread.start();
		} else if (cmd.equals("Leave Game")) {
			// close connection
			connect.sendStop();
			connect.close();
			game.setMessage("Host or Join a game.");
			game.setActive(false);
			
			// update the UI for 'game off'
			updateUI(false);
		} else if (cmd.equals("Options")) {
			// open player dialog, make changes, and send to opponent (if game is in progress)
			user = PlayerDialog.showPlayerDialog(frame, false);
			game.setUser(user);
			
			if (game.isEnabled())
				connect.sendPlayerInfo(user);
		}
	}
	
	///////////////////
	// LINE LISTENER //
	///////////////////
	
	// fires when user adds a line
	public void lineAdded(int r, int c) {
		connect.sendLine(r, c);
	}
	
	/////////////////////////
	// CONNECTION LISTENER //
	/////////////////////////
	
	public void playerInfoReceived(Player p) {
		opponent = p;
		game.setOpponent(opponent);
		game.repaint();
		consoleChat("Player info updated for " + opponent.getName() + ".");
	}
	
	public void lineReceived(int r, int c) {
		game.drawLine(r, c);
	}
	
	public void chatReceived(String msg) {
		chat(opponent.getName(), opponent.getColor(), msg);
	}
	
	public void startReceived(int turn, int size) {
		consoleChat("Connected. Game started.");
		
		// reset the dot game with new opponent
		game.startGame(turn, size);
		
		// update the UI for 'game off'
		updateUI(true);
	}
	
	public void stopReceived() {
		// close connection 
		connect.close();
		game.setMessage(opponent.getName() + " left the game.");
		game.setActive(false);
		
		// update the UI for 'game off'
		updateUI(false);
	}
}