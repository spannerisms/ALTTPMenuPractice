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
import java.util.Timer;

import javax.imageio.ImageIO;

import Practice.Listeners.*;

import static Practice.Item.ITEM_COUNT;

// TODO: https://github.com/snes9xgit/snes9x
public class MenuGame extends Container {
	private static final long serialVersionUID = -4474643068621537992L;

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
	static final BufferedImage TARGET_CURSOR;
	static final int BG_WIDTH = 152;
	static final int BG_HEIGHT = 120;
	static final Dimension MENU_SIZE = new Dimension(BG_WIDTH * 2 + 5, BG_HEIGHT * 2 + 5);

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

		try {
			temp = ImageIO.read(MenuGame.class.getResourceAsStream("/Practice/Images/target cursor.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		TARGET_CURSOR = temp;
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

	// list of items with repeats to give some items higher chance of appearing
	// only needs to be defined once
	private static final ArrayList<Integer> ITEM_CHOOSER = new ArrayList<Integer>();

	static {
		for (Item i : Item.values()) { // for each item
			int tag = i.index;
			for (int j = 0; j < i.weight; j++) { // add X times based on weight
				ITEM_CHOOSER.add(tag);
			}
		}
	}

	// local vars
	private ItemSlot[] list = new ItemSlot[20];
	private int target;
	private int loc;
	private ArrayList<Integer> pickFrom;

	private ScoreCard ref;

	// gameplay
	final GameMode mode; // current game mode
	final Difficulty dif; // current difficulty
	int currentTurn; // current turn, based on difficulty
	int currentRound;
	final int maxTurn;
	final int maxRound;
	final boolean randoAllStarts;
	ItemLister chosen; // list of chosen items
	final Timer waiter = new Timer();
	boolean studying = false;
	int scoreForGame = 0;

	// end gameplay

	public MenuGame(GameMode gameMode, Difficulty difficulty, int rounds) {
		initialize();
		mode = gameMode;
		dif = difficulty;
		maxTurn = dif.roundLength(mode);
		currentRound = dif.roundCount(rounds, mode);
		maxRound = currentRound;
		currentTurn = maxTurn;
		randoAllStarts = dif.randomizesStart(mode);
		addKeys();
	}

	public void start() {
		randomizeMenu();
		if (mode == GameMode.STUDY) {
			holdOn();
		} else {
			randomizeGoal();
			fireTurnEvent(null);
			ref = new ScoreCard(dif, calcMinMoves());
		}
	}

	public void holdOn() {
		ref = null;
		studying = true;
		fireTurnEvent(null);
		waiter.schedule(new OpTask(
			() -> {
					randomizeGoal();
					studying = false;
					fireTurnEvent(null);
					ref = new ScoreCard(dif, calcMinMoves());
				}),
			dif.studyTime);
	}

	private final void initialize() {
		this.setPreferredSize(MENU_SIZE);
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
				if (ref == null) { // don't do anything unless we have a scoring object
					return;
				}
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
					case KeyEvent.VK_SPACE :
						pressStart();
						fireInputEvent(InputEvent.SNES_START);
						break;
				}
			}
		});
	}

	private void pressStart() {
		ref.startPresses++;
		if (target == loc) {
			nextTurn();
		} else {

		}
	}

	private void nextTurn() {
		currentTurn--;
		if (currentTurn == 0) {
			nextRound();
			return;
		}
		newTurn();
	}

	private void newTurn() {
		randomizeGoal();
		ScoreCard prevRef = ref;

		ref = new ScoreCard(dif, calcMinMoves());
		fireTurnEvent(prevRef);
	}

	private void nextRound() {
		currentRound--;
		if (currentRound == 0) {
			fireTurnEvent(ref);
			fireGameOverEvent();
			return;
		}
		currentTurn = maxTurn;
		switch (mode) {
			case STUDY :
				randomizeMenu();
				holdOn();
				break;
			case BLITZ :
				randomizeMenu();
				newTurn();
				break;
			case COLLECT :
				addToMenu();
				newTurn();
				break;
		}
	}

	public int getScore() {
		return scoreForGame;
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

	private void randomizeMenu() {
		chosen = new ItemLister();

		switch (mode) {
			case STUDY :
				currentTurn = dif.studyRoundLength;
				break;
			case COLLECT :
				currentTurn = dif.collectionRoundLength;
				break;
			default :
				currentTurn = 1;
				break;
		}

		// we need at least this many items
		final int itemsWanted;

		switch (mode) {
			case STUDY :
			case BLITZ :
			default :
				itemsWanted = (int) (Math.random() * 17); // choose between 0 and 16 items to add
				chosen.addRandomItems(itemsWanted);
				break;
			case COLLECT :
				// do nothing
				break;
		}

		// add items to lists
		pickFrom = new ArrayList<Integer>();

		for (int i = 0; i < ITEM_COUNT; i++) {
			if (chosen.get(i)) {
				list[i].setRandomItem();
				list[i].setEnabled(true);
				pickFrom.add(i);
			} else {
				list[i].setEnabled(false);
			}
		}
	}

	/**
	 * Adds a single item to the menu
	 */
	private void addToMenu() {
		int i = chosen.addOneItem();
		if (i == -1) {
			randomizeMenu();
			randomizeGoal();
			return;
		}
		list[i].setRandomItem();
		list[i].setEnabled(true);
		pickFrom.add(i);
	}

	private void randomizeGoal() {
		int randomIndex;
		if (
				randoAllStarts // check to see if we're changing start location each time
				// also randomize on the first turn
				|| (
					(currentTurn == maxTurn) &&
						(
						// when we're on the first turn of the first round, which should always randomize the goal
							(currentRound == maxRound) ||
						// or the first turn of a round not in collections mode
							(mode != GameMode.COLLECT) )
						)
			) {
			randomIndex = (int) (Math.random() * pickFrom.size());
			loc = pickFrom.get(randomIndex);
		}

		do {
			randomIndex = (int) (Math.random() * pickFrom.size());
			target = pickFrom.get(randomIndex);
		} while (target == loc);
	}

	public String getTarget() {
		if (studying) {
			return "Study the menu";
		}
		return list[target].getCurrentItem();
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(GameContainer.ZOOM, GameContainer.ZOOM);
		g2.drawImage(BACKGROUND, 0, 0, null);
		paintComponents(g2);
		if (!studying) {
			ItemPoint cursorLoc = ItemPoint.valueOf("SLOT_" + loc);
			g2.drawImage(CURSOR,
					ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
					ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
					null);
	
			if (dif.showTargetCursor) {
				cursorLoc = ItemPoint.valueOf("SLOT_" + target);
				g2.drawImage(TARGET_CURSOR,
						ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
						ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
						null);
			}
		}
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
		if (ref != null) {
			scoreForGame += ref.calcScore();
		}
		TurnEvent te = new TurnEvent(this, ref);
		Iterator<TurnListener> listening = turnListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	// for the first initialization
	synchronized void refresh() {
		TurnEvent te = new TurnEvent(this, null);
		Iterator<TurnListener> listening = turnListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	/*
	 * Events for snes inputs
	 */
	private List<InputListener> snesListen = new ArrayList<InputListener>();
	public synchronized void addInputListener(InputListener s) {
		snesListen.add(s);
	}

	private synchronized void fireInputEvent(int button) {
		InputEvent te = new InputEvent(this, button);
		Iterator<InputListener> listening = snesListen.iterator();
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

	static class ItemLister {
		final boolean[] list = new boolean[20];
		int count = 0;
		int itemsChosen = 0;
		static final int C_SIZE = ITEM_CHOOSER.size();
		public ItemLister() {
			addRandomItems(4);
		}

		/**
		 * Adds items
		 * @param x
		 * @return Number of items actually added; will be less on overflows
		 */
		public int addRandomItems(int x) {
			int itemsWanted = itemsChosen + x;
			int i = 0;
			while (itemsChosen != itemsWanted) {
				if (itemsChosen == 20) {
					break;
				}
				addOneItem();
				i++;
			}
			return i;
		}

		/**
		 * Adds 1 item
		 * @return Index of item added
		 */
		public int addOneItem() {
			if (itemsChosen == 20) {
				return -1;
			}

			int rand;
			int toAdd;
			do {
				rand = (int) (Math.random() * C_SIZE);
				toAdd = ITEM_CHOOSER.get(rand);				
			} while (list[toAdd] == true);

			list[toAdd] = true;
			itemsChosen++;
			return toAdd;
		}
		public boolean get(int x) {
			return list[x];
		}
	}
}