import java.awt.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SnakePanel snake = new SnakePanel();
		snake.setPreferredSize(new Dimension(625, 419));
		frame.add(snake, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		snake.requestFocus();
	}
}