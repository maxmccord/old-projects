import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class NoiseGenerator extends JFrame implements ActionListener {
	// program entry point
	public static void main(String[] args) {
		NoiseGenerator noiseGen = new NoiseGenerator();
		noiseGen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		noiseGen.setLocationRelativeTo(null);
		noiseGen.showWindow();
	}
	
	// constants
	private final int IMG_SIZE = 256;
	
	// private members
	private NoiseCanvas noiseCanvas;
	private JComboBox noiseSelect;
	private JTextField persistField;
	private JSpinner octaveSpin;
	private JToggleButton cloudToggle;
	
	/////////////////
	// CONSTRUCTOR //
	
	public NoiseGenerator() {
		setTitle("Noise Generator");
		setLayout(new BorderLayout());
		
		// panel containing the noise panel
		JPanel canvasPanel = new JPanel(new BorderLayout());
		noiseCanvas = new NoiseCanvas(IMG_SIZE, IMG_SIZE);
		noiseCanvas.setPreferredSize(new Dimension(512, 512));
		canvasPanel.add(noiseCanvas, BorderLayout.CENTER);
		canvasPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		add(canvasPanel, BorderLayout.CENTER);
		
		// user interface - noise type
		JPanel interfacePanel = new JPanel();
		
		String[] noiseTypes = { "Monochrome", "Grayscale", "Perlin" };
		noiseSelect = new JComboBox(noiseTypes);
		noiseSelect.setActionCommand("noiseSelect");
		noiseSelect.addActionListener(this);
		interfacePanel.add(noiseSelect);
		
		JLabel persistLabel = new JLabel("Persistence:");
		persistField = new JTextField(6);
		persistField.setText("0.25");
		persistField.setEnabled(false);
		interfacePanel.add(persistLabel);
		interfacePanel.add(persistField);
		
		JLabel octaveLabel = new JLabel("Octaves:");
		SpinnerModel octaveModel = new SpinnerNumberModel(1, 1, 6, 1);
		octaveSpin = new JSpinner(octaveModel);
		octaveSpin.setEnabled(false);
		interfacePanel.add(octaveLabel);
		interfacePanel.add(octaveSpin);
		
		JButton genButton = new JButton("Generate");
		genButton.setActionCommand("generate");
		genButton.addActionListener(this);
		interfacePanel.add(genButton);
		
		cloudToggle = new JToggleButton("Cloud Mode");
		cloudToggle.setActionCommand("clouds");
		cloudToggle.addActionListener(this);
		interfacePanel.add(cloudToggle);
		
		add(interfacePanel, BorderLayout.PAGE_END);
		
		setResizable(false);
		pack();
		
		// generate initial noise
		int[][] data = new int[IMG_SIZE][IMG_SIZE];
		genMonochromeNoise(data);
		noiseCanvas.setData(data);
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void showWindow() {
		setVisible(true);
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private void genMonochromeNoise(int[][] data) {
		for (int x = 0; x < data.length; x++)
			for (int y = 0; y < data[0].length; y++)
				data[x][y] = (int)Math.round(Math.random()) * 255;
	}
	
	private void genGrayscaleNoise(int[][] data) {
		for (int x = 0; x < data.length; x++)
			for (int y = 0; y < data[0].length; y++)
				data[x][y] = (int)(Math.random()*256);
	}
	
	private void genPerlinNoise(int[][] data, double persist, int octaves) {
		int w = data.length;
		int h = data[0].length;
		
		// clear data
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				data[x][y] = 0;
		
		// compute data for each octave, adding to the total
		double amplitudeSum = 0; // used for normalization
		for (int n = 0; n < octaves; n++) {
			int freq = (int)Math.pow(2, n);
			double amplitude = Math.pow(persist, n);
			amplitudeSum += amplitude;
			int factor = 64/freq;
			
			// calculate unit vectors for each grid point
			double[][][] gridGrads = new double[w/factor+2][h/factor+2][2];
			for (int i = 0; i < gridGrads.length; i++) {
				for (int j = 0; j < gridGrads[0].length; j++) {
					double angle = Math.random()*2*Math.PI;
					gridGrads[i][j][0] = Math.cos(angle);
					gridGrads[i][j][1] = Math.sin(angle);
				}
			}
			
			// compute each cell of noise
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					// rescale to match grid coords
					double x = i/(double)factor;
					double y = j/(double)factor;
					
					// find corner grid coords
					int x0 = i/factor;
					int y0 = j/factor;
					int x1 = x0+1;
					int y1 = y0+1;
					
					// compute dot products between 4 corners and vectors to center
					double x0y0 = gridGrads[x0][y0][0] * (x-x0) + gridGrads[x0][y0][1] * (y-y0);
					double x1y0 = gridGrads[x1][y0][0] * (x-x1) + gridGrads[x1][y0][1] * (y-y0);
					double x0y1 = gridGrads[x0][y1][0] * (x-x0) + gridGrads[x0][y1][1] * (y-y1);
					double x1y1 = gridGrads[x1][y1][0] * (x-x1) + gridGrads[x1][y1][1] * (y-y1);
					
					// interpolate
					double sX = 3*Math.pow(x-x0, 2) - 2*Math.pow(x-x0, 3);
					double a = x0y0 + sX*(x1y0-x0y0);
					double b = x0y1 + sX*(x1y1-x0y1);
					double sY = 3*Math.pow(y-y0, 2) - 2*Math.pow(y-y0, 3);
					double z = a + sY*(b-a);
					
					// z is between -sqrt(2)/2 and +sqrt(2)/2, adjust scale
					z /= Math.sqrt(2);
					z += 0.5;
					
					// add to noise
					data[i][j] += (int)(z*256*amplitude);
				}
			}
		}
		
		// normalize data
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				data[x][y] = (int)(data[x][y]/amplitudeSum);
	}
	
	/////////////////////
	// ACTION LISTENER //
	
	public void actionPerformed(ActionEvent e) {
		String com = e.getActionCommand();
		
		if (com.equals("noiseSelect")) {
			// enable or disable Perlin-specific fields
			if (noiseSelect.getSelectedIndex() == 2) {
				persistField.setEnabled(true);
				octaveSpin.setEnabled(true);
			} else {
				persistField.setEnabled(false);
				octaveSpin.setEnabled(false);
			}
		} else if (com.equals("generate")) {
			// generate noise data based on selection
			int[][] data = new int[IMG_SIZE][IMG_SIZE];
			
			switch (noiseSelect.getSelectedIndex()) {
				case 0:
					genMonochromeNoise(data);
					break;
				case 1:
					genGrayscaleNoise(data);
					break;
				case 2:
					double persist = Double.parseDouble(persistField.getText());
					Integer octave = (Integer)octaveSpin.getValue();
					genPerlinNoise(data, persist, octave);
					break;
			}
			
			noiseCanvas.setData(data);
		} else if (com.equals("clouds")) {
			if (cloudToggle.isSelected()) {
				noiseCanvas.setMin(80, 120, 200);
				noiseCanvas.setMax(255, 255, 255);
			} else {
				noiseCanvas.setMin(0, 0, 0);
				noiseCanvas.setMax(255, 255, 255);
			}
			
			noiseCanvas.repaint();
		}
	}
}