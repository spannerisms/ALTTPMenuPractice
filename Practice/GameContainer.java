package Practice;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import Practice.Listeners.TurnEvent;
import Practice.Listeners.TurnListener;

public class GameContainer extends Container {
	private static final long serialVersionUID = -2890787797874712957L;

	private static final String NOTHING = "";
	static final Font SANS = new Font("SANS", Font.BOLD, 10);
	static final Dimension ffs = new Dimension(100, 20);

	// draw size
	static final int ZOOM = 2;
	CountDown counter = new CountDown();
	MenuGame playing;
	final JPanel holder = new JPanel(new SpringLayout());
	final JPanel lower = new JPanel(new SpringLayout());
	final JLabel targ = new JLabel(NOTHING);
	final JPanel controls = new JPanel();

	public GameContainer() {
		counter.addGameOverListener(
			arg0 -> {
				playing.start();
				holder.remove(counter);
				GameContainer.this.repaint();
				holder.add(playing);
				playing.requestFocusInWindow();
				GameContainer.this.repaint();
			});
		counter.addTurnListener(arg0 -> GameContainer.this.repaint());
		initialize();
	}

	private final void initialize() {
		SpringLayout l = new SpringLayout();
		this.setLayout(l);

		holder.setPreferredSize(MenuGame.MENU_SIZE);
		holder.setSize(MenuGame.MENU_SIZE);

		l.putConstraint(SpringLayout.NORTH, holder, 0,
				SpringLayout.NORTH, this);
		l.putConstraint(SpringLayout.SOUTH, holder, MenuGame.BG_HEIGHT * 2 + 5,
				SpringLayout.NORTH, this);
		this.add(holder);

		targ.setFont(SANS);
		targ.setFocusable(false);

		l.putConstraint(SpringLayout.EAST, lower, 0,
				SpringLayout.EAST, holder);
		l.putConstraint(SpringLayout.WEST, lower, 0,
				SpringLayout.WEST, holder);
		l.putConstraint(SpringLayout.NORTH, lower, 5,
				SpringLayout.SOUTH, holder);
		l.putConstraint(SpringLayout.SOUTH, lower, 0,
				SpringLayout.SOUTH, this);
		this.add(lower);

		// set control panel stuff
		controls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		controls.setPreferredSize(MenuGame.MENU_SIZE);
		controls.setSize(MenuGame.MENU_SIZE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = -1;

		// some blank space
		c.gridx ++;
		c.ipadx = 5;
		controls.add(new JLabel(""), c);
		c.ipadx = 0;

		// difficulty radio buttons
		c.gridy = 0;
		c.gridx++;
		controls.add(new JLabel("Difficulty"), c);
		ButtonGroup difficultyGroup = new ButtonGroup();
		for (Difficulty d : Difficulty.values()) {
			JRadioButton btn = new JRadioButton(d.diffName);
			btn.setActionCommand(d.name());
			difficultyGroup.add(btn);
			c.gridy++;
			controls.add(btn, c);
			if (d.ordinal() == 0) {
				btn.setSelected(true);
			}
		}

		// some blank space
		c.gridx++;
		c.ipadx = 50;
		controls.add(new JLabel(""), c);
		c.ipadx = 0;

		// game mode radio buttons
		c.gridy = 0;
		c.gridx++;
		controls.add(new JLabel("Mode"), c);
		ButtonGroup gameModeGroup = new ButtonGroup();
		for (GameMode m : GameMode.values()) {
			JRadioButton btn = new JRadioButton(m.modeName);
			btn.setActionCommand(m.name());
			gameModeGroup.add(btn);
			c.gridy++;
			controls.add(btn, c);
			if (m.ordinal() == 0) {
				btn.setSelected(true);
			}
		}

		// some blank space
		c.gridx++;
		c.ipadx = 20;
		controls.add(new JLabel(""), c);
		c.ipadx = 0;

		// go button
		JButton play = new JButton("Play");
		c.gridy = 0;
		c.gridx++;
		controls.add(play, c);
		play.addActionListener(
			arg0 -> {
				Difficulty d = Difficulty.valueOf(difficultyGroup.getSelection().getActionCommand());
				GameMode m = GameMode.valueOf(gameModeGroup.getSelection().getActionCommand());
				newGame(m, d, 2);
			});

		setLower(controls);

		revalidate();
	}

	void setLower(Component c) {
		SpringLayout l = (SpringLayout) lower.getLayout();
		l.putConstraint(SpringLayout.EAST, c, 0,
				SpringLayout.EAST, lower);
		l.putConstraint(SpringLayout.WEST, c, 0,
				SpringLayout.WEST, lower);
		l.putConstraint(SpringLayout.NORTH, c, 0,
				SpringLayout.NORTH, lower);
		l.putConstraint(SpringLayout.SOUTH, c, 0,
				SpringLayout.SOUTH, lower);
		lower.add(c);
		revalidate();
	}

	public synchronized void newGame(GameMode g, Difficulty d, int rounds) {
		holder.removeAll();
		playing = new MenuGame(g, d, rounds);
		playing.addTurnListener(
			arg0 -> {
				targ.setText(playing.getTarget().getCurrentItem());
				GameContainer.this.fireTurnEvent(arg0);
			}); // just relay it to MenuPractice
		playing.addInputListener(arg0 -> GameContainer.this.repaint() );
		counter.newGame();
		holder.add(counter);
		playing.addGameOverListener(
				arg0 -> {
					holder.remove(playing);
					targ.setText(NOTHING);
					GameContainer.this.repaint();
					playing.transferFocus();
				});
		setLower(targ);
		revalidate();
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