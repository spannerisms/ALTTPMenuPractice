package Practice;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import static Practice.Item.ITEM_COUNT;

// TODO: https://github.com/snes9xgit/snes9x
public class MenuGame extends Container {
	private static final long serialVersionUID = 1L;

	// location within the menu image to start for calculating
	static final int ITEM_ORIGIN_X = 24;
	static final int ITEM_ORIGIN_Y = 16;

	static final int CURSOR_OFFSET = 8; // number of pixels to shift the cursor
	static final int BLOCK_SIZE = 24; // size in pixels of an item block (for offsets, not drawing)
	static final int ITEM_SIZE = 16; // size of the image itself
	static final Dimension BLOCK_D = new Dimension(BLOCK_SIZE, BLOCK_SIZE);

	static final Item[] ALL_ITEMS = Item.values(); // for easy access
	static final int MIN_ITEMS = 4;

	static final BufferedImage BACKGROUND;
	static final BufferedImage CURSOR;
	static final int BG_WIDTH = 152;
	static final int BG_HEIGHT = 120;

	static {
		BufferedImage temp;
		try {
			temp = ImageIO.read(MenuGame.class.getResourceAsStream("/Practice/Images/menu background.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		BACKGROUND = temp;

		try {
			temp = ImageIO.read(MenuGame.class.getResourceAsStream("/Practice/Images/menu cursor.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		CURSOR = temp;
	}

	// all possible moves
	private static final ArrayList<Integer> ALL_POSSIBLE_MOVES = new ArrayList<Integer>();

	// how this works
	// each int holds 14 possible moves:
	private static final byte MOVE_UP = 0b00;
	private static final byte MOVE_DOWN = 0b01;
	private static final byte MOVE_RIGHT = 0b10;
	private static final byte MOVE_LEFT = 0b11;
	private static final byte[] MOVES = { MOVE_UP, MOVE_DOWN, MOVE_RIGHT, MOVE_LEFT };

	// the first 4 bits are the number of moves in this set
	private static final int COUNT_OFFSET = 28;

	// starting from the least significant bit
	// every 2 bits form the token for a single move
	// the moves are counted from right to left

	static {
		// do all 1 move patterns
		for (int i = 1; i < 6; i++) {
			addToPattern(0, i, i);
		}
	}

	/**
	 * @param p - pattern
	 * @param l - current nest level
	 * @param m - number of moves
	 */
	private static void addToPattern(int p, int l, int m) {
		for (int i = 0; i < 4; i++) {
			int pattern = p | (MOVES[i] << (2 * (l - 1)));
			if (l == 1) { // bottom level, just add
				pattern |= m << COUNT_OFFSET; // add number of moves
				ALL_POSSIBLE_MOVES.add(pattern);
			} else { // recursion
				addToPattern(pattern, l-1, m);
			}
		}
	}

	// local vars
	private ItemSlot[] list = new ItemSlot[20];
	private int target;
	private int loc;
	private ArrayList<Integer> pickFrom;

	private ScoreCard ref = new ScoreCard(0);

	// gameplay
	// enumerate difficulties
	enum Difficulty {
		EASY ("Easy", 15);

		final String diffName; // difficulty name
		final int menuLength; // number of item rounds per menu
		Difficulty(String name, int menuLength) {
			this.diffName = name;
			this.menuLength = menuLength;
		}
	}

	// enumerate game modes
	enum GameMode {
		STUDY ("Study mode");

		final String modeName;
		GameMode (String name) {
			modeName = name;
		}
	}

	GameMode mode; // current game mode
	Difficulty dif; // current difficulty
	int currentTurn; // current turn, based on difficulty
	
	// end gameplay
	// draw size
	private int zoom = 2;

	public MenuGame() {
		initialize();
		setGameMode(GameMode.STUDY);
		setDifficulty(Difficulty.EASY);
		addKeys();
		randomizeMenu();
	}

	private final void initialize() {
		this.setLayout(null);
		for (int i = 0; i < ITEM_COUNT; i++) {
			ItemSlot temp = new ItemSlot(ALL_ITEMS[i]);
			list[i] = temp;
			int r = i / 5;
			int c = i % 5;

			// add to container
			temp.setBounds(ITEM_ORIGIN_X + (c * BLOCK_SIZE),
					ITEM_ORIGIN_Y + (r * BLOCK_SIZE),
					ITEM_SIZE, ITEM_SIZE);
			this.add(temp);
		}
	}

	private final void addKeys() {
		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}

			public void keyPressed(KeyEvent arg0) {
				switch (arg0.getExtendedKeyCode() ) {
					case KeyEvent.VK_UP :
						loc = moveUp(loc);
						ref.moves++;
						fireInputEvent(InputEvent.SNES_UP);
						break;
					case KeyEvent.VK_DOWN :
						loc = moveDown(loc);
						ref.moves++;
						fireInputEvent(InputEvent.SNES_DOWN);
						break;
					case KeyEvent.VK_RIGHT :
						loc = moveRight(loc);
						ref.moves++;
						fireInputEvent(InputEvent.SNES_RIGHT);
						break;
					case KeyEvent.VK_LEFT :
						loc = moveLeft(loc);
						ref.moves++;
						fireInputEvent(InputEvent.SNES_LEFT);
						break;
					case KeyEvent.VK_D :
						pressStart();
						fireInputEvent(InputEvent.SNES_START);
						break;
				}
				repaint();
			}
			
		});
	}

	private final void setDifficulty(Difficulty difficulty) {
		dif = difficulty;
	}

	private final void setGameMode(GameMode mode) {
		this.mode = mode;
	}

	private void pressStart() {
		ref.startPresses++;
		if (target == loc) {
			win();
		} else {
			
		}
	}

	private void win() {
		switch (mode) {
			case STUDY :
				break;
		}
	}
	/*
	 * Movement
	 */
	private int moveUp(int s) {
		int newLoc = (s + 15) % 20;
		if (!list[newLoc].isEnabled()) {
			return moveUp(newLoc);
		}
		return newLoc;
	}

	private int moveDown(int s) {
		int newLoc = (s + 5) % 20;
		if (!list[newLoc].isEnabled()) {
			return moveDown(newLoc);
		}
		return newLoc;
	}

	private int moveRight(int s) {
		int newLoc = (s + 1) % 20;
		if (!list[newLoc].isEnabled()) {
			return moveRight(newLoc);
		}
		return newLoc;
	}

	private int moveLeft(int s) {
		int newLoc = (s + 19) % 20;
		if (!list[newLoc].isEnabled()) {
			return moveLeft(newLoc);
		}
		return newLoc;
	}

	/**
	 * Forumala used to determine if an item is on
	 * @return
	 */
	private boolean chooseOn() {
		int x = (int) (Math.random() * 3);
		return x == 0;
	}

	private void randomizeMenu() {
		boolean[] chosen = new boolean[20];

		// we need at least this many items
		int itemsChosen = 0;
		boolean notEnoughItems = true;

		// choose slots to use
		while (notEnoughItems) {
			for (int i = 0; i < ITEM_COUNT; i++) {
				if (chosen[i]) {
					continue;
				}

				if (chooseOn()) {
					chosen[i] = true;
					itemsChosen++;

					if (itemsChosen >= MIN_ITEMS) {
						notEnoughItems = false;
					}
				}
			} // end for
		} // end while

		// add items to lists
		pickFrom = new ArrayList<Integer>();

		for (int i = 0; i < ITEM_COUNT; i++) {
			if (chosen[i]) {
				list[i].setRandomItem();
				list[i].setEnabled(true);
				pickFrom.add(i);
			} else {
				list[i].setEnabled(false);
			}
		}

		// choose begin item and target item
		randomizeGoal();
	}

	private void randomizeGoal() {
		int randomIndex = (int) (Math.random() * pickFrom.size());
		loc = pickFrom.remove(randomIndex);

		randomIndex = (int) (Math.random() * pickFrom.size());
		target = pickFrom.remove(randomIndex);
		ScoreCard prevRef = ref;

		ref = new ScoreCard(calcMinMoves());

		fireTurnEvent(prevRef);
		repaint();
		this.requestFocusInWindow();
	}

	public ItemSlot getTarget() {
		return list[target];
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		g2.drawImage(BACKGROUND, 0, 0, null);
		this.paintComponents(g2);
		ItemPoint cursorLoc = ItemPoint.valueOf("SLOT_" + loc);
		g2.drawImage(CURSOR,
				ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
				ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
				null);
	}

	private int calcMinMoves() {
		for (int pattern : ALL_POSSIBLE_MOVES) {
			int moves = pattern >> COUNT_OFFSET;
			int pos = loc;
			for (int i = 0; i < moves; i++) {
				int moveToken = (pattern >> (i * 2)) & 0b11;
				int newPos;

				switch (moveToken) {
					case MOVE_UP :
						newPos = moveUp(pos);
						break;
					case MOVE_DOWN :
						newPos = moveDown(pos);
						break;
					case MOVE_RIGHT :
						newPos = moveRight(pos);
						break;
					case MOVE_LEFT :
						newPos = moveLeft(pos);
						break;
					default :
						newPos = pos;
						break;
				}

				pos = newPos;
				if (pos == target) {
					return moves;
				}
			}
		}
		// failure, just in case
		return -1;
	}

	/*
	 * Events for turn changes
	 */
	private List<TurnListener> turnListen = new ArrayList<TurnListener>();
	public synchronized void addTurnListener(TurnListener s) {
		turnListen.add(s);
	}

	private synchronized void fireTurnEvent(ScoreCard ref) {
		ref.calcScore();
		TurnEvent te = new TurnEvent(this, ref);
		Iterator<TurnListener> listening = turnListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	/*
	 * Events for snes inputs
	 */
	private List<InputListener> targListen = new ArrayList<InputListener>();
	public synchronized void addInputListener(InputListener s) {
		targListen.add(s);
	}

	private synchronized void fireInputEvent(int button) {
		InputEvent te = new InputEvent(this, button);
		Iterator<InputListener> listening = targListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}