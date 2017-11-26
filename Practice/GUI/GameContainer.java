package Practice.GUI;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import Practice.MenuGame;
import Practice.Listeners.*;

import static Practice.MenuGameConstants.*;

public class GameContainer extends Container {
	private static final long serialVersionUID = -2890787797874712957L;

	private static final String WAIT = "CHOOSE SETTINGS";


	CountDown counter = new CountDown();
	MenuGame playing;
	final JPanel holder = new JPanel(new SpringLayout());

	final PrettyLabel targ = new PrettyLabel(BOARD_SIZE_NO_BORDER);
	final PrettyButton forfeit = new PrettyButton(3);

	final PrettyLabel gameCount = new PrettyLabel(BOARD_SIZE_NO_BORDER - 5);
	final PrettyLabel roundCount = new PrettyLabel(BOARD_SIZE_NO_BORDER);
	final PrettyLabel turnCount = new PrettyLabel(BOARD_SIZE_NO_BORDER);

	ControllerHandler controller = new ControllerHandler();
	ControlScreen splash = new ControlScreen(controller);

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
		this.setBackground(null);

		holder.setPreferredSize(MENU_SIZE);
		holder.setSize(MENU_SIZE);
		holder.setBackground(null);

		l.putConstraint(SpringLayout.NORTH, holder, 0,
				SpringLayout.NORTH, this);
		l.putConstraint(SpringLayout.SOUTH, holder, BG_HEIGHT * ZOOM,
				SpringLayout.NORTH, this);
		this.add(holder);

		// status text
		targ.setText(WAIT);
		targ.setIcon(COMPASS);

		l.putConstraint(SpringLayout.WEST, targ, 0,
				SpringLayout.WEST, holder);
		l.putConstraint(SpringLayout.NORTH, targ, 0,
				SpringLayout.SOUTH, holder);
		this.add(targ);

		// game count
		gameCount.setText("GAME :");
		gameCount.setRightText("--");

		l.putConstraint(SpringLayout.WEST, gameCount, 0,
				SpringLayout.WEST, targ);
		l.putConstraint(SpringLayout.NORTH, gameCount, 0,
				SpringLayout.SOUTH, targ);
		this.add(gameCount);

		// forfeit button
		forfeit.setText("END");

		l.putConstraint(SpringLayout.EAST, forfeit, 0,
				SpringLayout.EAST, targ);
		l.putConstraint(SpringLayout.NORTH, forfeit, 0,
				SpringLayout.SOUTH, targ);
		this.add(forfeit);
		forfeit.setEnabled(false);
		forfeit.addActionListener(
			arg0 -> {
				forfeit();
			});

		// round count
		roundCount.setText("ROUND:");
		roundCount.setRightText("--");

		l.putConstraint(SpringLayout.WEST, roundCount, 0,
				SpringLayout.WEST, targ);
		l.putConstraint(SpringLayout.NORTH, roundCount, 0,
				SpringLayout.SOUTH, gameCount);
		this.add(roundCount);

		// turn count
		turnCount.setText("TURN :");
		turnCount.setRightText("--");

		l.putConstraint(SpringLayout.WEST, turnCount, 0,
				SpringLayout.WEST, targ);
		l.putConstraint(SpringLayout.NORTH, turnCount, 0,
				SpringLayout.SOUTH, roundCount);
		this.add(turnCount);

		// splash
		setHolder(splash);
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

	public synchronized void newGame(MenuGame game) {
		playing = game;
		playing.addTurnListener(
			arg0 -> {
				targ.setText(playing.getTarget());
				targ.setIcon(playing.getTargetImage());
				gameCount.setRightText(playing.getGame() + "/" + playing.getMaxGame());
				roundCount.setRightText(playing.getRound() + "/" + playing.getMaxRound());
				turnCount.setRightText(playing.getTurn() + "/" + playing.getMaxTurn());
				GameContainer.this.fireTurnEvent(arg0);
			}); // just relay it to MenuPractice
		playing.addSNESInputListener(arg0 -> GameContainer.this.repaint() );
		counter.newGame();
		setHolder(counter);
		repaint();
		playing.addGameOverListener(
				arg0 -> {
					forfeit.setEnabled(false);
					targ.setText(WAIT);
					targ.setIcon(COMPASS);
					gameCount.setRightText("--");
					roundCount.setRightText("--");
					turnCount.setRightText("--");
					splash.setScore(playing.getScore());
					setHolder(splash);
					splash.requestFocus();
					GameContainer.this.repaint();
					playing.transferFocus();
					fireGameOverEvent(arg0);
				});
		targ.setText("GET READY");
		forfeit.setEnabled(true);
		fireGameStartEvent();
		revalidate();
	}

	public void forfeit() {
		playing.forfeit();
		counter.kill();
	}

	public void setController(ControllerHandler c) {
		splash.setController(controller);
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