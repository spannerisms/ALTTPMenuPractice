package Practice;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import Practice.Listeners.TurnEvent;
import Practice.Listeners.TurnListener;

public class GameContainer extends Container {
	private static final long serialVersionUID = -2890787797874712957L;

	// draw size
	int zoom = 2;
	CountDown counter = new CountDown();
	MenuGame playing;
	final JPanel holder = new JPanel();

	public GameContainer() {
		counter.addGameOverListener(
			arg0 -> {
				holder.remove(counter);
				holder.add(playing);
				holder.revalidate();
				playing.start();
				playing.requestFocusInWindow();
			});
		counter.addTurnListener(arg0 -> repaint());
		holder.setPreferredSize(MenuGame.MENU_SIZE);
		holder.setSize(MenuGame.MENU_SIZE);
		initialize();
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		paintComponents(g2);
	}

	private final void initialize() {
		this.add(holder);
		newGame();
	}

	public void newGame() {
		counter.newGame();
		holder.add(counter);
		playing = new MenuGame(GameMode.STUDY, Difficulty.EASY);
		playing.addGameOverListener(
				arg0 -> {
					holder.remove(playing);
					holder.revalidate();
					playing.transferFocus();
				});
		playing.addTurnListener(
				arg0 -> {
					fireTurnEvent(arg0); // just relay it to MenuPractice
				});
	}

	public MenuGame getInstance() {
		return playing;
	}

	/*
	 * Events for turn changes
	 */
	private List<TurnListener> turnListen = new ArrayList<TurnListener>();
	public synchronized void addTurnListener(TurnListener s) {
		turnListen.add(s);
	}

	private synchronized void fireTurnEvent(TurnEvent te) {
		Iterator<TurnListener> listening = turnListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}