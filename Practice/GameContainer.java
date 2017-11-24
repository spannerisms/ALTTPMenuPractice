package Practice;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import Practice.Listeners.*;
import static Practice.MenuGameConstants.*;

public class GameContainer extends Container {
	private static final long serialVersionUID = -2890787797874712957L;

	private static final String NOTHING = "";
	static final Font SANS = new Font("SANS", Font.PLAIN, 20);

	static final BufferedImage TITLE_SCREEN;
	static final BufferedImage SCORE_SCREEN;

	static {
		BufferedImage temp;
		try {
			temp = ImageIO.read(GameContainer.class.getResourceAsStream("/Practice/Images/title screen.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		TITLE_SCREEN = temp;

		try {
			temp = ImageIO.read(GameContainer.class.getResourceAsStream("/Practice/Images/score screen.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		SCORE_SCREEN = temp;
	}

	CountDown counter = new CountDown();
	MenuGame playing;
	final JPanel holder = new JPanel(new SpringLayout());
	final JPanel lower = new JPanel(new SpringLayout());
	final JLabel targ = new JLabel(NOTHING);
	final JPanel controls = new JPanel();
	Controller controller = new Controller();

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
		l.putConstraint(SpringLayout.SOUTH, holder, BG_HEIGHT * 2,
				SpringLayout.NORTH, this);
		this.add(holder);

		setHolder(new HoldScreen(TITLE_SCREEN));
		targ.setFont(SANS);
		targ.setFocusable(false);
		targ.setVerticalTextPosition(SwingConstants.TOP);
		targ.setBackground(Color.BLACK);

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
		controls.setPreferredSize(MENU_SIZE);
		controls.setSize(MENU_SIZE);
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
		c.gridwidth = 2;
		controls.add(play, c);

		// round number
		SpinnerModel roundModel = new SpinnerNumberModel(1,1,20,1);
		JSpinner roundSpinner = new JSpinner(roundModel);
		roundSpinner.setFocusable(false);
		c.gridwidth = 1;
		c.gridy++;
		JLabel roundLabel = new JLabel("Rounds:");
		controls.add(roundLabel, c);
		c.gridx++;
		controls.add(roundSpinner, c);

		play.addActionListener(
				arg0 -> {
					Difficulty d = Difficulty.valueOf(difficultyGroup.getSelection().getActionCommand());
					GameMode m = GameMode.valueOf(gameModeGroup.getSelection().getActionCommand());
					int rounds = (int) roundModel.getValue();
					newGame(m, d, rounds);
				});

		setLower(controls);
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

	public synchronized void newGame(GameMode g, Difficulty d, int rounds) {
		playing = new MenuGame(controller, g, d, rounds);
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
					setLower(controls);
					setHolder(new HoldScreen(playing.getScore(), SCORE_SCREEN));
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

	static final String CHARS = "0123456789,";
	public static BufferedImage makeNumberImage(int i) {
		String num = "";
		char[] temp = Integer.toString(i).toCharArray();
		int pos = 1;

		for (int j = temp.length - 1; j >= 0; j--, pos++ ) {
			num = temp[j] + num;
			if ((pos % 3 == 0) && (j != 0)) {
				num = ',' + num;
			}
		}
		BufferedImage ret = new BufferedImage(num.length() * 8, 8, BufferedImage.TYPE_INT_ARGB);
		temp = num.toCharArray();
		Graphics g = ret.getGraphics();
		for (int j = 0; j < temp.length; j++) {
			int loc = CHARS.indexOf(temp[j]);
			BufferedImage t = NUMBER_SPRITES.getSubimage(loc * 8, 0, 8, 8);
			g.drawImage(t, j * 8, 0, null);
		}
		return ret;
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

	@SuppressWarnings("serial")
	private static class HoldScreen extends JComponent {
		BufferedImage num;
		BufferedImage bg;
		int numOffset = 0;

		HoldScreen(BufferedImage num, BufferedImage bg) {
			this.num = num;
			this.bg = bg;
			if (num != null) {
				numOffset = (BG_WIDTH - num.getWidth()) / 2;
			}
			setPreferredSize(MENU_SIZE);
			setSize(MENU_SIZE);
		}

		HoldScreen(int i, BufferedImage bg) {
			this(makeNumberImage(i), bg);
		}
		
		HoldScreen(BufferedImage bg) {
			this(null, bg);
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.scale(ZOOM, ZOOM);
			g2.drawImage(bg, 0, 0, null);
			if (num != null) {
				g2.drawImage(num, numOffset, 31, null);
			}
		}
	}
}