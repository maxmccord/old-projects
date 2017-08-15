import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;

class DoubleBufferedCanvas extends Canvas {
	public void update(Graphics g) {
		Graphics offgc;
		Image offscreen = null;

		// create the offscreen buffer and associated Graphics
		offscreen = createImage(getWidth(), getHeight());
		offgc = offscreen.getGraphics();

		// clear the exposed area
		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, getWidth(), getHeight());
		offgc.setColor(getForeground());

		// do normal redraw
		paint(offgc);

		// transfer offscreen to window
		g.drawImage(offscreen, 0, 0, this);
	}
}
