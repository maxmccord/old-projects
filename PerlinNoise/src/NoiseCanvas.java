import java.awt.*;
import java.awt.image.*;

public class NoiseCanvas extends Canvas {
	// private members
	private int width, height;
	private int[][] data;
	private BufferedImage image;
	private int minR, minG, minB;
	private int maxR, maxG, maxB;
	
	/////////////////
	// CONSTRUCTOR //
	
	public NoiseCanvas(int width, int height) {
		this.width = width;
		this.height = height;
		
		data = new int[width][height];
		/*for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (x != y && x != (height-1-y))
					data[x][y] = 255;*/
					
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		minR = minG = minB = 0;
		maxR = maxG = maxB = 255;
	}
	
	////////////////////
	// PUBLIC METHODS //
	
	public void paint(Graphics g) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// use linear interpolation to choose between the min and max color
				double val = data[x][y]/255.0;
				int red = (int)(minR*(1-val) + maxR*val);
				int green = (int)(minG*(1-val) + maxG*val);
				int blue = (int)(minB*(1-val) + maxB*val);
				
				image.setRGB(x, y, 0xff000000 | red << 16 | green << 8 | blue);
			}
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
	
	public void setData(int[][] newData) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				data[x][y] = newData[x][y];
		repaint();
	}
	
	public void setMin(int r, int g, int b) {
		this.minR = r;
		this.minG = g;
		this.minB = b;
	}
	
	public void setMax(int r, int g, int b) {
		this.maxR = r;
		this.maxG = g;
		this.maxB = b;
	}
}