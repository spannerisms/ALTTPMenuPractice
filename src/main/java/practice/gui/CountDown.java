package practice.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.swing.JComponent;

import practice.listeners.*;
import static practice.MenuGameConstants.*;

public class CountDown extends JComponent {
	private static final long serialVersionUID = 6892347930158631859L;

	static final BufferedImage[] COUNT_DOWN_BG = new BufferedImage[4];

	static {
		for (int i = 0; i < COUNT_DOWN_BG.length; i++) {
			COUNT_DOWN_BG[i] =
				fetchImageResource("/images/backgrounds/game countdown " + i + ".png", EMPTY_BG);
		}
	}

	static final int COUNT_DOWN_WAIT = 1000;

	// local vars
	OpTask counting;
	Timer tick;
	Task count;
	static final int COUNTS = 3;
	int curCount;
	boolean running = false;

	public CountDown() {
		this.setPreferredSize(MENU_SIZE);
		count = () -> {
			if (!running) { return; }
			curCount--;
			switch (curCount) {
				case -1 :
					tick.cancel();
					running = false;
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
		running = true;
		tick.schedule(new OpTask(count), COUNT_DOWN_WAIT);
	}

	public void kill() {
		tick.cancel();
		running = false;
	}

	public void paint(Graphics g) {
		if (curCount == -1) { return; }
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