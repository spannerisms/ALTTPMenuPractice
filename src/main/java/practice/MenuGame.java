package practice;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import practice.listeners.*;
import practice.gui.OpTask;
import practice.controls.ControllerHandler;
import practice.controls.SNESControllable;

import static practice.Item.ITEM_COUNT;
import static practice.MenuGameConstants.*;

public class MenuGame extends Container implements SNESControllable {
	private static final long serialVersionUID = -4474643068621537992L;

	// local vars
	private ItemSlot[] list = new ItemSlot[20];
	private ItemSlot[] listAtTurn;
	private ArrayList<Integer> pickFrom;

	private int target;
	private int loc;

	private ScoreCard ref;

	final GameMode mode;
	final Difficulty dif;

	int currentTurn;
	final int maxTurn;
	int currentRound;
	final int maxRound;
	int currentGame;
	final int maxGame;

	int scoreForGame = 0;
	ArrayList<PlayerMovement> movesMade = new ArrayList<PlayerMovement>();
	PlayerMovement[] bestMoves;

	final boolean randoAllStarts;
	final boolean showOpt;
	final boolean showIcon;
	final boolean showStartStudy;
	final ControllerHandler controls;
	BufferedImage minMoveOverlay;

	ItemLister chosen;
	final Timer waiter = new Timer();
	boolean studying = false;

	// constructor
	public MenuGame(ControllerHandler controls, GameMode gameMode, Difficulty difficulty, int games) {
		initialize();
		mode = gameMode;
		dif = difficulty;

		maxTurn = dif.roundLength(mode);
		currentTurn = maxTurn;

		currentRound = dif.roundsPerGame(mode);
		maxRound = currentRound;

		currentGame = games;
		maxGame = games;

		randoAllStarts = dif.randomizesStart(mode);
		showOpt = dif.showOptimalPath;
		showIcon = dif.showItemIcon;
		showStartStudy = dif.showStartDuringStudy;

		this.controls = controls;
		addToController(this.controls);
		this.whineToMommy();
		addSNESInput();
	}

	private void makeNewCard() {
		int min = calcMinMoves();
		ref = new ScoreCard(dif, min, bestMoves, listAtTurn);
	}

	public void start() {
		randomizeMenu();
		if (mode == GameMode.STUDY) {
			holdOn();
		} else {
			randomizeGoal();
			fireTurnEvent(null);
			makeNewCard();
		}
	}

	public void holdOn() {
		ref = null;
		studying = true;
		fireTurnEvent(null);
		randomizeGoal();
		waiter.schedule(new OpTask(
			() -> {
					studying = false;
					fireTurnEvent(null);
					makeNewCard();
				}),
			dif.studyTime);
	}

	private final void initialize() {
		this.setPreferredSize(MENU_SIZE);
		this.setLayout(null);
		this.setBackground(null);

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

	public void whineToMommy() {
		controls.kidWhined(this);
	}

	public void shutUp() {
		controls.kidCalmed(this);
	}

	public final void addSNESInput() {
		this.addSNESInputListener(
			arg0 -> {
				if (arg0.getSource() == this) {
					return;
				}

				int key = arg0.getKey();

				// forfeit controls need to operate outside of the game controls
				if (key == (SNESInputEvent.SNES_L | SNESInputEvent.SNES_R)) { // bitwise or
						forfeit();
				}

				if (ref == null) { // don't do anything related to playing unless we have a scoring object
					return;
				}

				switch(key) {
					case SNESInputEvent.SNES_UP :
						movesMade.add(new PlayerMovement(loc, MOVE_UP));
						loc = moveUp(loc);
						ref.moves++;
						break;
					case SNESInputEvent.SNES_DOWN :
						movesMade.add(new PlayerMovement(loc, MOVE_DOWN));
						loc = moveDown(loc);
						ref.moves++;
						break;
					case SNESInputEvent.SNES_RIGHT :
						movesMade.add(new PlayerMovement(loc, MOVE_RIGHT));
						loc = moveRight(loc);
						ref.moves++;
						break;
					case SNESInputEvent.SNES_LEFT :
						movesMade.add(new PlayerMovement(loc, MOVE_LEFT));
						loc = moveLeft(loc);
						ref.moves++;
						break;
					case SNESInputEvent.SNES_START :
						movesMade.add(new PlayerMovement(loc, PRESS_START));
						pressStart();
						break;
				}
		});
	}

	private void pressStart() {
		ref.startPresses++;
		if (target == loc) {
			nextTurn();
		}
	}

	public void forfeit() {
		waiter.cancel();
		removeFromController(controls);
		fireGameOverEvent();
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
		ref.setPlayerPath(movesMade.toArray(new PlayerMovement[movesMade.size()]));
		movesMade.clear();
		ScoreCard prevRef = ref;
		fireTurnEvent(prevRef);
		makeNewCard();
	}

	private void nextRound() {
		currentRound--;
		currentTurn = maxTurn;
		if (currentRound == 0) {
			nextGame();
			return;
		}
		newRound();
	}

	private void newRound() {
		switch (mode) {
			case STUDY :
				randomizeMenu();
				newTurn();
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

	private void nextGame() {
		currentGame--;
		currentRound = maxRound;
		if (currentGame == 0) {
			newTurn();
			fireGameOverEvent();
			return;
		}
		currentRound = maxRound;
		newRound();
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
		listAtTurn = new ItemSlot[20];

		switch (mode) {
			case STUDY :
				currentTurn = maxTurn;
				break;
			case COLLECT :
				currentTurn = maxTurn;
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
				listAtTurn[i] = list[i].clone();
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
		ItemSlot[] temp = new ItemSlot[20];
		System.arraycopy(listAtTurn, 0, temp, 0, 20);
		listAtTurn = temp;
		listAtTurn[i] = list[i].clone();
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

	public BufferedImage getTargetImage() {
		if (studying) {
			return MAP;
		} else if (!showIcon) {
			return null;
		}
		return list[target].getCurrentImage();
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(ZOOM, ZOOM);

		g2.drawImage(BACKGROUND, 0, 0, null);
		paintComponents(g2);
		ItemPoint cursorPos;

		if (!studying || showStartStudy) {
			cursorPos = ItemPoint.valueOf("SLOT_" + loc);
			g2.drawImage(CURSOR,
					ITEM_ORIGIN_X + cursorPos.x - CURSOR_OFFSET,
					ITEM_ORIGIN_Y + cursorPos.y - CURSOR_OFFSET,
					null);
		}

		if (!studying) {
			if (dif.showTargetCursor) {
				cursorPos = ItemPoint.valueOf("SLOT_" + target);
				g2.drawImage(TARGET_CURSOR,
						ITEM_ORIGIN_X + cursorPos.x - CURSOR_OFFSET,
						ITEM_ORIGIN_Y + cursorPos.y - CURSOR_OFFSET,
						null);
			}
			if (showOpt) {
				g2.drawImage(minMoveOverlay, 0, 0, null);
			}
		}
	}

	private int calcMinMoves() {
		int[] arrowPlacement = new int[1];
		int moves = -1; // default to return for failure, just in case
		int goodPattern = -1;
		for (int pattern : ALL_POSSIBLE_MOVES) {
			int pos;
			moves = pattern >> COUNT_OFFSET;
			pos = loc;
			int newPos = pos;
			arrowPlacement = new int[moves];

			for (int i = 0; i < moves; i++) {
				int moveToken = (pattern >> (i * 2)) & 0b11;
				arrowPlacement[i] = newPos; // add to list of positions
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
				} // end switch
				pos = newPos;
			} // end moves for

			if (pos == target) {
				goodPattern = pattern;
				break;
			}
		}

		// make the best moves pattern
		if (goodPattern != -1) {
			bestMoves = new PlayerMovement[moves+1];
			for (int i = 0; i < moves; i++) {
				int moveToken = (goodPattern >> (i * 2)) & 0b11;
				bestMoves[i] = new PlayerMovement(arrowPlacement[i], moveToken);
			}
			bestMoves[moves] = new PlayerMovement(target, PRESS_START);
		}

		// make a single image of the optimal path, to be overlayed
		if (showOpt) {
			minMoveOverlay = PlayerMovement.drawOptimalPath(bestMoves, false);
		}
		// failure, just in case
		return moves;
	}

	// turn
	public int getTurn() {
		return maxTurn - currentTurn + 1;
	}

	public int getMaxTurn() {
		return maxTurn;
	}

	// round
	public int getRound() {
		return maxRound - currentRound + 1;
	}

	public int getMaxRound() {
		return maxRound;
	}

	// game
	public int getGame() {
		return maxGame - currentGame + 1;
	}

	public int getMaxGame() {
		return maxGame;
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