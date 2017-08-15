// Author:   Max McCord
// Created:  04/02/2014
//
// Project:  2048 Game
// Desc:     Utility class for loading images from resource files, and returning the correct
//           image for the given value of tile.

import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageUtility {
	public static final int NUM_OF_TILES = 12;
	
	private static BufferedImage tileImages[];
	
	public static void loadImages(String dir) {
		tileImages = new BufferedImage[NUM_OF_TILES];
		
		try {
			tileImages[0] = ImageIO.read(new File(dir + "0.png"));
			tileImages[1] = ImageIO.read(new File(dir + "2.png"));
			tileImages[2] = ImageIO.read(new File(dir + "4.png"));
			tileImages[3] = ImageIO.read(new File(dir + "8.png"));
			tileImages[4] = ImageIO.read(new File(dir + "16.png"));
			tileImages[5] = ImageIO.read(new File(dir + "32.png"));
			tileImages[6] = ImageIO.read(new File(dir + "64.png"));
			tileImages[7] = ImageIO.read(new File(dir + "128.png"));
			tileImages[8] = ImageIO.read(new File(dir + "256.png"));
			tileImages[9] = ImageIO.read(new File(dir + "512.png"));
			tileImages[10] = ImageIO.read(new File(dir + "1024.png"));
			tileImages[11] = ImageIO.read(new File(dir + "2048.png"));
		} catch (IOException e) {
			System.out.println("Error loading images. Loading false images.");
			
			BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < NUM_OF_TILES; i++)
				if (tileImages[i] == null)
					tileImages[i] = image;
		}
	}
	
	// value must be a power of 2.
	public static BufferedImage getImageForValue(int value) {
		int index = (int)(Math.log(value) / Math.log(2));
		
		if (index < NUM_OF_TILES)
			return tileImages[index];
		else
			return tileImages[0];
	}
}