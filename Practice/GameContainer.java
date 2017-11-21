package Practice;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

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
				playing.start();
				playing.requestFocusInWindow();
				GameContainer.this.repaint();
			});
		counter.addTurnListener(arg0 -> GameContainer.this.repaint());
		initialize();
		newGame();
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		paintComponents(g2);
	}

	private final void initialize() {
		SpringLayout l = new SpringLayout();
		this.setLayout(l);
		
		holder.setPreferredSize(MenuGame.MENU_SIZE);
		holder.setSize(MenuGame.MENU_SIZE);

		l.putConstraint(SpringLayout.NORTH, holder, 0,
				SpringLayout.NORTH, this);
		l.putConstraint(SpringLayout.SOUTH, holder, MenuGame.BG_HEIGHT * 2 + 10,
				SpringLayout.NORTH, this);
		this.add(holder);
		revalidate();
	}

	public void newGame() {
		playing = new MenuGame(GameMode.STUDY, Difficulty.EASY);
		counter.newGame();
		holder.add(counter);
		playing.addGameOverListener(
				arg0 -> {
					holder.remove(playing);
					GameContainer.this.repaint();
					playing.transferFocus();
				});
		playing.addTurnListener(arg0 -> GameContainer.this.fireTurnEvent(arg0) ); // just relay it to MenuPractice
		playing.addInputListener(arg0 -> GameContainer.this.repaint() );
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