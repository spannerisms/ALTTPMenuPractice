package Practice.GUI;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import Practice.Difficulty;
import Practice.GameMode;
import Practice.MenuGame;
import Practice.Listeners.GameOverEvent;
import Practice.Listeners.GameOverListener;

import static Practice.MenuGameConstants.*;

public class ControlScreen extends JPanel {
	private static final long serialVersionUID = -4589871913175293600L;

	static final BufferedImage TITLE_SPLASH;
	static final BufferedImage SCORE_SPLASH;

	public static final BufferedImage[] ROUND_NUMBERS = new BufferedImage[20];
	public static final BufferedImage[] ROUND_NUMBERS_HILITE = new BufferedImage[20];

	public static final BufferedImage DIFFICULTY_WORD = makeWordImage("DIFFICULTY:", false);
	public static final BufferedImage DIFFICULTY_HILITE = makeWordImage("DIFFICULTY:", true);

	public static final BufferedImage MODE_WORD = makeWordImage("MODE:", false);
	public static final BufferedImage MODE_HILITE = makeWordImage("MODE:", true);

	public static final BufferedImage ROUNDS_WORD = makeWordImage("ROUNDS:", false);
	public static final BufferedImage ROUNDS_HILITE = makeWordImage("ROUNDS:", true);

	public static final BufferedImage START_WORD = makeWordImage("START", false);
	public static final BufferedImage START_HILITE = makeWordImage("START", true);

	public static final BufferedImage CARETS = makeWordImage("<             >", true);

	static {
		for (int i = 0, j = 1; i < 20; i++, j++) {
			String w;
			if (j < 10) {
				w = " " + j;
			} else {
				w = Integer.toString(j);
			}
			ROUND_NUMBERS[i] = makeWordImage(w, false);
			ROUND_NUMBERS_HILITE[i] = makeWordImage(w, true);
		}

		BufferedImage temp;
		try {
			temp = ImageIO.read(GameContainer.class.getResourceAsStream("/Practice/Images/title screen.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		TITLE_SPLASH = temp;

		try {
			temp = ImageIO.read(GameContainer.class.getResourceAsStream("/Practice/Images/score screen.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		SCORE_SPLASH = temp;
	}

	static enum Focus { MODE, DIFFICULTY, ROUND, START };

	private Focus selection = Focus.DIFFICULTY;
	private Difficulty diffSel = Difficulty.EASY;
	private GameMode modeSel = GameMode.STUDY;
	private int rounds = 1;
	private BufferedImage disp = TITLE_SPLASH;
	private BufferedImage score;

	public ControlScreen() {
		setPreferredSize(MENU_SIZE);
		setSize(MENU_SIZE);
		setFocusable(true);
		addKeys();
	}

	public void setScore(int s) {
		disp = SCORE_SPLASH;
		score = makeNumberImage(s, false);
	}

	private final void addKeys() {
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				switch (arg0.getKeyCode()) {
					case KeyEvent.VK_UP :
						selectionChange(-1);
						break;
					case KeyEvent.VK_DOWN :
						selectionChange(+1);
						break;
					case KeyEvent.VK_LEFT :
						switch (selection) {
							case DIFFICULTY :
								difficultyChange(-1);
								break;
							case MODE :
								modeChange(-1);
								break;
							case ROUND :
								rounds--;
								if (rounds < 1) {
									rounds = 1;
								}
								repaint();
								break;
							default:
								break;
						}
						break;
					case KeyEvent.VK_RIGHT :
						switch (selection) {
							case DIFFICULTY :
								difficultyChange(+1);
								break;
							case MODE :
								modeChange(+1);
								break;
							case ROUND :
								rounds++;
								if (rounds > 20) {
									rounds = 20;
								}
								repaint();
								break;
							default:
								break;
						}
						break;
					case KeyEvent.VK_SPACE :
						if (selection == Focus.START) {
							fireGameOverEvent();
							selection = Focus.MODE; // safety against starting a new game when done
						}
						break;
				}
			}

			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
	}

	private void selectionChange(int change) {
		int cur = selection.ordinal() + change;
		int max = Focus.values().length;
		if (cur < 0) {
			cur = max-1;
		} else if (cur >= max) {
			cur = 0;
		}
		selection = Focus.values()[cur];
		repaint();
	}

	private void difficultyChange(int change) {
		int cur = diffSel.ordinal() + change;
		int max = Difficulty.values().length;
		if (cur < 0) {
			cur = max-1;
		} else if (cur >= max) {
			cur = 0;
		}
		diffSel = Difficulty.values()[cur];
		repaint();
	}

	private void modeChange(int change) {
		int cur = modeSel.ordinal() + change;
		int max = GameMode.values().length;
		if (cur < 0) {
			cur = max-1;
		} else if (cur >= max) {
			cur = 0;
		}
		modeSel = GameMode.values()[cur];
		repaint();
	}

	public MenuGame makeThisGame(Controller c) {
		return new MenuGame(c, modeSel, diffSel, rounds);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(ZOOM, ZOOM);

		g2.drawImage(BACKGROUND, 0, 0, null);
		draw(g2, disp, 3, 1);

		if (score != null) {
			draw(g2, score, 15 - (score.getWidth() / 8), 3);
		}

		draw(g2, CARETS, 2, 7 + (selection.ordinal() * 2));
		boolean isSelected;

		isSelected = selection == Focus.MODE;
		draw(g2, isSelected ? MODE_HILITE : MODE_WORD, 2, 6);
		draw(g2, isSelected ? modeSel.wordHilite : modeSel.word, 4, 7);

		isSelected = selection == Focus.DIFFICULTY;
		draw(g2, isSelected ? DIFFICULTY_HILITE : DIFFICULTY_WORD, 2, 8);
		draw(g2, isSelected ? diffSel.wordHilite : diffSel.word, 4, 9);

		isSelected = selection == Focus.ROUND;
		draw(g2, isSelected ? ROUNDS_HILITE : ROUNDS_WORD, 2, 10);
		draw(g2, isSelected ? ROUND_NUMBERS_HILITE[rounds-1] : ROUND_NUMBERS[rounds-1], 14, 11);

		isSelected = selection == Focus.START;
		draw(g2, isSelected ? START_HILITE : START_WORD, 7, 13);
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
