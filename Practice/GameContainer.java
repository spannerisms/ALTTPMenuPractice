package Practice;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import Practice.Listeners.*;
import static Practice.MenuGameConstants.*;

public class GameContainer extends Container {
	private static final long serialVersionUID = -2890787797874712957L;

	private static final String NOTHING = "";
	static final Font SANS = new Font("SANS", Font.PLAIN, 20);

	CountDown counter = new CountDown();
	MenuGame playing;
	final JPanel holder = new JPanel(new SpringLayout());
	final JPanel lower = new JPanel(new SpringLayout());
	final JLabel targ = new JLabel(NOTHING);
	Controller controller = new Controller();
	ControlScreen splash = new ControlScreen();

	public GameContainer() {
		counter.addGameOverListener(
			arg0 -> {
				playing.start();
				setHolder(playing);
				playing.requestFocusInWindow();
				GameContainer.this.repaint();
			});
		counter.addTurnListener(arg0 -> GameContainer.this.repaint());
		initialize();
	}

	private final void initialize() {
		SpringLayout l = new SpringLayout();
		this.setLayout(l);

		holder.setPreferredSize(MENU_SIZE);
		holder.setSize(MENU_SIZE);

		l.putConstraint(SpringLayout.NORTH, holder, 0,
				SpringLayout.NORTH, this);
		l.putConstraint(SpringLayout.SOUTH, holder, BG_HEIGHT * ZOOM,
				SpringLayout.NORTH, this);
		this.add(holder);

		setHolder(splash);
		targ.setFont(SANS);
		targ.setFocusable(false);
		targ.setVerticalTextPosition(SwingConstants.TOP);

		l.putConstraint(SpringLayout.EAST, lower, 0,
				SpringLayout.EAST, holder);
		l.putConstraint(SpringLayout.WEST, lower, 0,
				SpringLayout.WEST, holder);
		l.putConstraint(SpringLayout.NORTH, lower, 5,
				SpringLayout.SOUTH, holder);
		l.putConstraint(SpringLayout.SOUTH, lower, 0,
				SpringLayout.SOUTH, this);
		this.add(lower);

		splash.requestFocus();
		splash.addGameOverListener(
			arg0 -> {
				newGame(splash.makeThisGame(controller));
				splash.transferFocus();
			});
		revalidate();
	}

	void setHolder(Component c) {
		holder.removeAll();
		SpringLayout l = (SpringLayout) holder.getLayout();
		l.putConstraint(SpringLayout.EAST, c, 0,
				SpringLayout.EAST, holder);
		l.putConstraint(SpringLayout.WEST, c, 0,
				SpringLayout.WEST, holder);
		l.putConstraint(SpringLayout.NORTH, c, 0,
				SpringLayout.NORTH, holder);
		l.putConstraint(SpringLayout.SOUTH, c, 0,
				SpringLayout.SOUTH, holder);
		holder.add(c);
		revalidate();
	}

	void setLower(Component c) {
		lower.removeAll();
		SpringLayout l = (SpringLayout) lower.getLayout();
		l.putConstraint(SpringLayout.EAST, c, 0,
				SpringLayout.EAST, lower);
		l.putConstraint(SpringLayout.WEST, c, 0,
				SpringLayout.WEST, lower);
		l.putConstraint(SpringLayout.NORTH, c, 0,
				SpringLayout.NORTH, lower);
		lower.add(c);
		revalidate();
	}

	public synchronized void newGame(MenuGame game) {
		playing = game;
		playing.addTurnListener(
			arg0 -> {
				targ.setText(playing.getTarget());
				GameContainer.this.fireTurnEvent(arg0);
			}); // just relay it to MenuPractice
		playing.addInputListener(arg0 -> GameContainer.this.repaint() );
		counter.newGame();
		setHolder(counter);
		repaint();
		playing.addGameOverListener(
				arg0 -> {
					targ.setText(NOTHING);
					setLower(null);
					splash.setScore(playing.getScore());
					setHolder(splash);
					splash.requestFocus();
					GameContainer.this.repaint();
					playing.transferFocus();
					fireGameOverEvent(arg0);
				});
		fireGameStartEvent();
		setLower(targ);
		revalidate();
	}

	public void forfeit() {
		playing.forfeit();
		counter.kill();
	}

	public void setController(Controller c) {
		controller = c;
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

	/*
	 * Events for game changes
	 */
	private List<GameOverListener> startListen = new ArrayList<GameOverListener>();
	public synchronized void addGameStartListener(GameOverListener s) {
		startListen.add(s);
	}

	private synchronized void fireGameStartEvent() {
		GameOverEvent te = new GameOverEvent(this);
		Iterator<GameOverListener> listening = startListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	private List<GameOverListener> endListen = new ArrayList<GameOverListener>();
	public synchronized void addGameOverListener(GameOverListener s) {
		endListen.add(s);
	}

	private synchronized void fireGameOverEvent(GameOverEvent te) {
		Iterator<GameOverListener> listening = endListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}