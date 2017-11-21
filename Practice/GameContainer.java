package Practice;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GameContainer extends Container {

	// draw size
	int zoom = 2;
	CountDown counter = new CountDown();

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		paintComponents(g2);
	}
}