import static java.lang.System.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.net.*;

public class FloodFill extends JPanel implements ActionListener, KeyListener {
	// program entry point
	public static void main(String[] args) {
		JFrame frame = new JFrame("Flood Fill Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new FloodFill(), BorderLayout.CENTER);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	private JButton resetButton;
	private BufferedImage[] icons;
	private JButton[] iconButtons;
	private JLabel movesLabel;
	private GameCanvas game;
	private int movesMade = 0;

	//////////////////
	// CONSTRUCTORS //
	//////////////////

	public FloodFill() {
		setLayout(new BorderLayout());

		icons = new BufferedImage[6];

		try {
			icons[0] = ImageIO.read(getClass().getResource("img/A.bmp"));
			icons[1] = ImageIO.read(getClass().getResource("img/B.bmp"));
			icons[2] = ImageIO.read(getClass().getResource("img/C.bmp"));
			icons[3] = ImageIO.read(getClass().getResource("img/D.bmp"));
			icons[4] = ImageIO.read(getClass().getResource("img/E.bmp"));
			icons[5] = ImageIO.read(getClass().getResource("img/F.bmp"));
		} catch (IOException e) {
			out.println("Error reading the images.");
		}

		JPanel buttonPanel = new JPanel();

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { reset(); } });
		buttonPanel.add(resetButton);

		iconButtons = new JButton[6];
		for (int i = 0; i < 6; i++) {
			ImageIcon icon = new ImageIcon(icons[i]);
			iconButtons[i] = new JButton(icon);
			iconButtons[i].setActionCommand(String.valueOf(i));
			iconButtons[i].addActionListener(this);
			buttonPanel.add(iconButtons[i]);
		}

		movesLabel = new JLabel("Moves Used: 0/25");
		buttonPanel.add(movesLabel);

		game = new GameCanvas(icons);
		game.addKeyListener(this);

		add(buttonPanel, BorderLayout.PAGE_START);
		add(game, BorderLayout.CENTER);

		reset();
	}

	/////////////////////
	// PRIVATE METHODS //
	/////////////////////

	private void reset() {
		movesMade = 0;
		movesLabel.setText("Moves Used: 0/25");
		game.randomize();
	}

	/////////////////////
	// ACTION LISTENER //
	/////////////////////

	public void actionPerformed(ActionEvent e) {
		int icon = Integer.parseInt(e.getActionCommand());
		game.fill(icon);
		movesMade++;

		movesLabel.setText("Moves Used: " + movesMade + "/25");

		if (game.checkWin()) {
			JOptionPane.showMessageDialog(this, "You win! Congrats :)");
			reset();
		} else if (movesMade == 25) {
			JOptionPane.showMessageDialog(this, "You lose... :(");
			reset();
		}
	}

	//////////////////
	// KEY LISTENER //
	//////////////////

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				movesMade--;
				if (movesMade < 0)
					movesMade = 0;
				movesLabel.setText("Moves Used: " + movesMade + "/25");
				break;
		}
	}

	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
}