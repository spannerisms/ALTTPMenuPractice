package Practice;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.imageio.ImageIO;

import Practice.Listeners.*;

public class CountDown extends Container {
	private static final long serialVersionUID = 6892347930158631859L;

	static final int BG_WIDTH = MenuGame.BG_WIDTH;
	static final int BG_HEIGHT = MenuGame.BG_HEIGHT;

	static final BufferedImage[] COUNT_DOWN_BG = new BufferedImage[4];

	static {
		BufferedImage temp;
		for (int i = 0; i < COUNT_DOWN_BG.length; i++) {
			try {
				temp = ImageIO.read(CountDown.class.getResourceAsStream(
						"/Practice/Images/game countdown " + i + ".png"));
			} catch (Exception e) {
				temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			}
			COUNT_DOWN_BG[i] = temp;
		}
	}

	static final int COUNT_DOWN_WAIT = 1000;

	// local vars
	OpTask counting;
	Timer tick = new Timer();

	static final int COUNTS = 4;
	int curCount;
	public CountDown() {
		counting = new OpTask(()-> {
			curCount--;
			switch (curCount) {
				case 0 : // for the count down, continue counting and make a new background
				case 1 :
				case 2 :
				case 3 :
					tick.schedule(counting, COUNT_DOWN_WAIT);
					break;
				case -1 :
					CountDown.this.fireGameOverEvent();
					break;
			}
		});
	}

	public void newGame() {
		curCount = COUNTS;
		
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(COUNT_DOWN_BG[curCount], 0, 0, null);
		this.paintComponents(g2);
	}

	/*
	 * Events for snes inputs
	 */
	private List<GameOverListener> doneListen = new ArrayList<GameOverListener>();
	public synchronized void addGameOverListener(GameOverListener s) {
		doneListen.add(s);
	}

	private synchronized void fireGameOverEvent() {
		GameOverEvent te = new GameOverEvent(this);
		Iterator<GameOverListener> listening = doneListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}