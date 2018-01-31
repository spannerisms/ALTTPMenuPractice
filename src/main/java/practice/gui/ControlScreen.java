package practice.gui;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import practice.Difficulty;
import practice.GameMode;
import practice.MenuGame;
import practice.MenuGameConstants;
import practice.controls.ControllerHandler;
import practice.controls.SNESControllable;
import practice.listeners.*;

import static practice.MenuGameConstants.*;

public class ControlScreen extends JPanel implements SNESControllable {
	private static final long serialVersionUID = -4589871913175293600L;

	static final BufferedImage TITLE_SPLASH = fetchImageResource("/images/meta/title screen.png", EMPTY_BG);
	static final BufferedImage SCORE_SPLASH = fetchImageResource("/images/meta/score screen.png", EMPTY_BG);

	static final int MAX_GAMES = 20;
	public static final BufferedImage[] GAME_NUMBERS = new BufferedImage[MAX_GAMES];
	public static final BufferedImage[] GAME_NUMBERS_HILITE = new BufferedImage[MAX_GAMES];

	public static final BufferedImage DIFFICULTY_WORD = makeWordImage("DIFFICULTY:", WHITE);
	public static final BufferedImage DIFFICULTY_HILITE = makeWordImage("DIFFICULTY:", YELLOW);

	public static final BufferedImage MODE_WORD = makeWordImage("MODE:", WHITE);
	public static final BufferedImage MODE_HILITE = makeWordImage("MODE:", YELLOW);

	public static final BufferedImage GAMES_WORD = makeWordImage("GAMES:", WHITE);
	public static final BufferedImage GAMES_HILITE = makeWordImage("GAMES:", YELLOW);

	public static final BufferedImage START_WORD = makeWordImage("START", WHITE);
	public static final BufferedImage START_HILITE = makeWordImage("START", YELLOW);

	public static final BufferedImage CARETS = makeWordImage("<             >", YELLOW);

	static {
		for (int i = 0, j = 1; i < MAX_GAMES; i++, j++) {
			String w;
			if (j < 10) {
				w = " " + j;
			} else {
				w = Integer.toString(j);
			}
			GAME_NUMBERS[i] = makeWordImage(w, WHITE);
			GAME_NUMBERS_HILITE[i] = makeWordImage(w, YELLOW);
		}

		// write the year to title
		Graphics g = TITLE_SPLASH.getGraphics();
		BufferedImage year = makeWordImage("2K18", WHITE);
		MenuGameConstants.draw(g, year, 0, 2);
	}

	static enum Focus { MODE, DIFFICULTY, GAME, START };
	private Focus selection = Focus.DIFFICULTY;

	private Difficulty diffSel = Difficulty.EASY;
	private GameMode modeSel = GameMode.STUDY;
	private int games = 1;

	private BufferedImage disp = TITLE_SPLASH;
	private BufferedImage score;

	private ControllerHandler controls;
	private boolean playingGame;

	public ControlScreen() {
		setPreferredSize(MENU_SIZE);
		setSize(MENU_SIZE);
		setFocusable(true);
		addSNESInput();
	}

	public void setScore(int s) {
		disp = SCORE_SPLASH;
		score = makeNumberImage(s, WHITE);
	}

	public final void setController(ControllerHandler c) {
		if (controls != null) {
			removeFromController(c);
		}
		controls = c;
		addToController(controls);
	}

	public void whineToMommy() {
		controls.kidWhined(this);
	}

	public void shutUp() {
		controls.kidCalmed(this);
	}

	public final void addSNESInput() {
		this.addSNESInputListener(
			arg0 -> {
				if (arg0.getSource() == this || playingGame) {
					return;
				}
				switch (arg0.getKey()) {
					case SNESInputEvent.SNES_UP :
						selectionChange(-1);
						break;
					case SNESInputEvent.SNES_DOWN :
						selectionChange(+1);
						break;
					case SNESInputEvent.SNES_LEFT :
						switch (selection) {
							case DIFFICULTY :
								difficultyChange(-1);
								break;
							case MODE :
								modeChange(-1);
								break;
							case GAME :
								games--;
								if (games < 1) {
									games = 1;
								}
								repaint();
								break;
							default:
								break;
						}
						break;
					case SNESInputEvent.SNES_RIGHT :
						switch (selection) {
							case DIFFICULTY :
								difficultyChange(+1);
								break;
							case MODE :
								modeChange(+1);
								break;
							case GAME :
								games++;
								if (games > MAX_GAMES) {
									games = MAX_GAMES;
								}
								repaint();
								break;
							default:
								break;
						}
						break;
					case SNESInputEvent.SNES_START :
					case SNESInputEvent.SNES_A :
						if (selection == Focus.START) {
							playingGame = false;
							fireGameOverEvent();
							selection = Focus.MODE; // safety against starting a new game when done
						}
						break;
				}
		});
	}

	public void comeBack() {
		playingGame = false;
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

	public MenuGame makeThisGame(ControllerHandler c) {
		return new MenuGame(c, modeSel, diffSel, games);
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

		isSelected = selection == Focus.GAME;
		draw(g2, isSelected ? GAMES_HILITE : GAMES_WORD, 2, 10);
		draw(g2, isSelected ? GAME_NUMBERS_HILITE[games-1] : GAME_NUMBERS[games-1], 14, 11);

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

	/*
	 * Events for snes inputs
	 */
	private List<SNESInputListener> snesListen = new ArrayList<SNESInputListener>();
	public synchronized void addSNESInputListener(SNESInputListener s) {
		snesListen.add(s);
	}

	public synchronized void fireSNESInputEvent(SNESInputEvent e) {
		Iterator<SNESInputListener> listening = snesListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(e);
		}
	}
}