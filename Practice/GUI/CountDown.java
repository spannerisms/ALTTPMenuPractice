package Practice;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import Practice.Listeners.*;
import static Practice.MenuGameConstants.*;

public class CountDown extends JComponent {
	private static final long serialVersionUID = 6892347930158631859L;

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
	Timer tick;
	Task count;
	static final int COUNTS = 3;
	int curCount;

	public CountDown() {
		this.setPreferredSize(MENU_SIZE);
		count = () -> {
			curCount--;
			switch (curCount) {
				case -1 :
					tick.cancel();
					fireGameOverEvent();
					break;
				// for the count down, continue counting and make a new background
				case 0 : // make GO appear briefly
					tick.schedule(new OpTask(count), COUNT_DOWN_WAIT / 4);
					fireTurnEvent();
					break;
				case 1 :
				case 2 :
				case 3 :
					tick.schedule(new OpTask(count), COUNT_DOWN_WAIT);
					fireTurnEvent();
					break;
				}
		};
	}

	public void newGame() {
		tick = new Timer();
		curCount = COUNTS;
		tick.schedule(new OpTask(count), COUNT_DOWN_WAIT);
	}

	public void kill() {
		tick.cancel();
	}

	public void paint(Graphics g) {
		if (curCount == -1) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(ZOOM, ZOOM);
		g2.drawImage(BACKGROUND, 0, 0, null);
		g2.drawImage(COUNT_DOWN_BG[curCount], 0, 0, null);
	}

	/*
	 * Events for turn changes
	 */
	private List<TurnListener> turnListen = new ArrayList<TurnListener>();
	public synchronized void addTurnListener(TurnListener s) {
		turnListen.add(s);
	}

	private synchronized void fireTurnEvent() {
		TurnEvent te = new TurnEvent(this, null);
		Iterator<TurnListener> listening = turnListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	/*
	 * Events for being done
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
