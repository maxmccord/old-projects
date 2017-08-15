import java.awt.*;
import java.awt.image.*;

public class ImageUtility {
	
	// Processes an image by taking 2x2 areas of pixels and averaging their
	// colors into 1 pixel. The resulting image will have half the
	// dimensions of the original image.
	public static BufferedImage simplifyImage(BufferedImage image) {
		BufferedImage ret = new BufferedImage(
			image.getWidth() / 2,
			image.getHeight() / 2,
			image.getType()
			);
		
		// loop through the pixels of the original image and average the colors
		for (int x = 0; x < image.getWidth(); x += 2) {
			for (int y = 0; y < image.getHeight(); y += 2) {
				// grab the colors of 4 adjacent pixels
				Color[] colors = new Color[4];
				colors[0] = new Color(image.getRGB( x    , y     ));
				colors[1] = new Color(image.getRGB( x + 1, y     ));
				colors[2] = new Color(image.getRGB( x    , y + 1 ));
				colors[3] = new Color(image.getRGB( x + 1, y + 1 ));
				
				// average all colors
				int r = 0, g = 0, b = 0;
				for (int i = 0; i < 4; i++) {
					r += colors[i].getRed();
					g += colors[i].getGreen();
					b += colors[i].getBlue();
				}
				
				r /= 4;
				g /= 4;
				b /= 4;
				
				// create the new color and apply it to our new image
				Color newColor = new Color(r, g, b);
				ret.setRGB(x / 2, y / 2, newColor.getRGB());
			}
		}
		
		return ret;
	}
}