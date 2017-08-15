import static java.lang.System.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class NoiseGenerator3D extends JFrame implements ActionListener {
	// program entry point
	public static void main(String[] args) {
		NoiseGenerator3D noiseGen = new NoiseGenerator3D();
		noiseGen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		noiseGen.setLocationRelativeTo(null);
		noiseGen.setVisible(true);
	}
	
	// constants
	private final int IMG_SIZE = 256;
	
	// private members
	private NoiseCanvas noiseCanvas;
	private JTextField persistField;
	private JSpinner octaveSpin;
	private JSpinner framesSpin;
	private JToggleButton cloudToggle;
	
	/////////////////
	// CONSTRUCTOR //
	
	public NoiseGenerator3D() {
		setTitle("3D Noise Generator");
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
		
		JLabel persistLabel = new JLabel("Persistence:");
		persistField = new JTextField(6);
		persistField.setText("0.25");
		interfacePanel.add(persistLabel);
		interfacePanel.add(persistField);
		
		JLabel octaveLabel = new JLabel("Octaves:");
		SpinnerModel octaveModel = new SpinnerNumberModel(1, 1, 6, 1);
		octaveSpin = new JSpinner(octaveModel);
		interfacePanel.add(octaveLabel);
		interfacePanel.add(octaveSpin);
		
		JLabel framesLabel = new JLabel("# of Frames:");
		SpinnerModel framesModel = new SpinnerNumberModel(1, 1, 20, 1);
		framesSpin = new JSpinner(framesModel);
		interfacePanel.add(framesLabel);
		interfacePanel.add(framesSpin);
		
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
	}
	
	/////////////////////
	// PRIVATE METHODS //
	
	private void genPerlinNoise3D(int[][][] data, double persist, int octaves, int frames) {
		int w = data.length;
		int h = data[0].length;
		int d = frames;
		
		// clear data
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				for (int z = 0; z < d; z++)
					data[x][y][z] = 0;
		
		// compute data for each octave, adding to the total
		double amplitudeSum = 0; // used for normalization
		for (int n = 0; n < octaves; n++) {
			int freq = (int)Math.pow(2, n);
			double amplitude = Math.pow(persist, n);
			amplitudeSum += amplitude;
			int factor = 64/freq;
			
			// calculate unit vectors for each grid point
			double[][][][] gridGrads = new double[w/factor+2][h/factor+2][d/factor+2][3];
			for (int i = 0; i < gridGrads.length; i++) {
				for (int j = 0; j < gridGrads[0].length; j++) {
					for (int k = 0; k < gridGrads[0][0].length; k++) {
						double a1 = Math.random()*2*Math.PI;
						double a2 = Math.random()*2*Math.PI;
						gridGrads[i][j][k][0] = Math.cos(a1)*Math.sin(a2);
						gridGrads[i][j][k][1] = Math.sin(a1)*Math.sin(a2);
						gridGrads[i][j][k][2] = Math.cos(a2);
					}
				}
			}
			
			double max = Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			
			// compute each cell of noise
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					for (int k = 0; k < d; k++) {
						// rescale to match grid coords
						double x = i/(double)factor;
						double y = j/(double)factor;
						double z = d/(double)factor;
						
						// find corner grid coords
						int x0 = i/factor;
						int y0 = j/factor;
						int z0 = d/factor;
						int x1 = x0+1;
						int y1 = y0+1;
						int z1 = z0+1;
						
						// compute dot products between 4 corners and vectors to center
						double x0y0z0 = gridGrads[x0][y0][z0][0] * (x-x0) + gridGrads[x0][y0][z0][1] * (y-y0) + gridGrads[x0][y0][z0][2] * (z-z0);
						double x0y0z1 = gridGrads[x0][y0][z1][0] * (x-x0) + gridGrads[x0][y0][z1][1] * (y-y0) + gridGrads[x0][y0][z1][2] * (z-z0);
						double x1y0z0 = gridGrads[x1][y0][z0][0] * (x-x1) + gridGrads[x1][y0][z0][1] * (y-y0) + gridGrads[x0][y0][z0][2] * (z-z0);
						double x1y0z1 = gridGrads[x1][y0][z1][0] * (x-x1) + gridGrads[x1][y0][z1][1] * (y-y0) + gridGrads[x0][y0][z1][2] * (z-z0);
						double x0y1z0 = gridGrads[x0][y1][z0][0] * (x-x0) + gridGrads[x0][y1][z0][1] * (y-y1) + gridGrads[x0][y0][z0][2] * (z-z0);
						double x0y1z1 = gridGrads[x0][y1][z1][0] * (x-x0) + gridGrads[x0][y1][z1][1] * (y-y1) + gridGrads[x0][y0][z1][2] * (z-z0);
						double x1y1z0 = gridGrads[x1][y1][z0][0] * (x-x1) + gridGrads[x1][y1][z0][1] * (y-y1) + gridGrads[x0][y0][z0][2] * (z-z0);
						double x1y1z1 = gridGrads[x1][y1][z1][0] * (x-x1) + gridGrads[x1][y1][z1][1] * (y-y1) + gridGrads[x0][y0][z1][2] * (z-z0);
						
						// interpolate
						double sX = 3*Math.pow(x-x0, 2) - 2*Math.pow(x-x0, 3);
						double a = x0y0z0 + sX*(x1y0z0-x0y0z0);
						double b = x0y1z0 + sX*(x1y1z0-x0y1z0);
						double sY = 3*Math.pow(y-y0, 2) - 2*Math.pow(y-y0, 3);
						double out1 = a + sY*(b-a);
						
						sX = 3*Math.pow(x-x0, 2) - 2*Math.pow(x-x0, 3);
						a = x0y0z1 + sX*(x1y0z1-x0y0z1);
						b = x0y1z1 + sX*(x1y1z1-x0y1z1);
						sY = 3*Math.pow(y-y0, 2) - 2*Math.pow(y-y0, 3);
						double out2 = a + sY*(b-a);
						
						double sZ = 3*Math.pow(z-z0, 2) - 2*Math.pow(z-z0, 3);
						double result = out1 + sZ*(out2-out1);
						
						max = Math.max(max, result);
						min = Math.min(min, result);
						
						// z is between -sqrt(2)/2 and +sqrt(2)/2, adjust scale
						if (result > 255)
							result = 255;
						else if (result < 0)
							result = 0;
						
						// add to noise
						data[i][j][k] += (int)(result*256*amplitude);
					}
				}
			}
			
			out.println("max = " + max);
			out.println("min = " + min);
			out.println();
		}
		
		// normalize data
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
				for (int z = 0; z < d; z++)
					data[x][y][z] = (int)(data[x][y][z]/amplitudeSum);
	}
	
	/////////////////////
	// ACTION LISTENER //
	
	public void actionPerformed(ActionEvent e) {
		String com = e.getActionCommand();
		
		if (com.equals("generate")) {
			double persist = Double.parseDouble(persistField.getText());
			Integer octave = (Integer)octaveSpin.getValue();
			Integer frames = (Integer)framesSpin.getValue();
			int[][][] data = new int[IMG_SIZE][IMG_SIZE][frames.intValue()];
			genPerlinNoise3D(data, persist, octave, frames);
			
			int[][] frame1 = new int[IMG_SIZE][IMG_SIZE];
			for (int x = 0; x < IMG_SIZE; x++)
				for (int y = 0; y < IMG_SIZE; y++)
					frame1[x][y] = data[x][y][0];
			noiseCanvas.setData(frame1);
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