package Practice;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public final class MenuGameConstants {
	private MenuGameConstants() {};

	// location within the menu image to start for calculating
	static final int ITEM_ORIGIN_X = 24;
	static final int ITEM_ORIGIN_Y = 16;

	static final int CURSOR_OFFSET = 8; // number of pixels to shift the cursor
	static final int BLOCK_SIZE = 24; // size in pixels of an item block (for offsets, not drawing)
	static final int ITEM_SIZE = 16; // size of the image itself
	static final Dimension BLOCK_D = new Dimension(BLOCK_SIZE, BLOCK_SIZE);

	static final Item[] ALL_ITEMS = Item.values(); // for easy access
	static final int MIN_ITEMS = 4;

	// all possible moves
	static final ArrayList<Integer> ALL_POSSIBLE_MOVES = new ArrayList<Integer>();

	// how this works
	// each int holds 14 possible moves:
	static final byte MOVE_UP = 0b000;
	static final byte MOVE_DOWN = 0b001;
	static final byte MOVE_RIGHT = 0b010;
	static final byte MOVE_LEFT = 0b011;
	static final byte PRESS_START = 0b100;
	static final byte[] MOVES = { MOVE_UP, MOVE_DOWN, MOVE_RIGHT, MOVE_LEFT };

	// the first 4 bits are the number of moves in this set
	static final int COUNT_OFFSET = 28;

	// starting from the least significant bit
	// every 2 bits form the token for a single move
	// the moves are counted from right to left

	static {// do all move patterns
		for (int i = 1; i < 6; i++) {
			addToPattern(0, i, i);
		}
	}

	// display
	static final int BG_WIDTH = 152;
	static final int BG_HEIGHT = 120;
	static final Dimension MENU_SIZE = new Dimension(BG_WIDTH * 2 + 5, BG_HEIGHT * 2 + 5);

	// images
	static final int ZOOM = 2;
	static final BufferedImage BACKGROUND;
	static final BufferedImage CURSOR;
	static final BufferedImage TARGET_CURSOR;
	static final BufferedImage NUMBER_SPRITES;
	static final BufferedImage[] OPTIMAL_MOVES = new BufferedImage[5];
	static final BufferedImage[] PLAYER_MOVES = new BufferedImage[5];

	static {
		BufferedImage temp;

		// main background
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/menu background.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		BACKGROUND = temp;

		// menu cursors
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/menu cursor.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		CURSOR = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/target cursor.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		TARGET_CURSOR = temp;

		// optimal path arrows
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/optimal up.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		OPTIMAL_MOVES[MOVE_UP] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/optimal down.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		OPTIMAL_MOVES[MOVE_DOWN] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/optimal right.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		OPTIMAL_MOVES[MOVE_RIGHT] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/optimal left.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		OPTIMAL_MOVES[MOVE_LEFT] = temp;

		OPTIMAL_MOVES[PRESS_START] = TARGET_CURSOR;

		// player path arrows
		// optimal path arrows
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/player up.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		PLAYER_MOVES[MOVE_UP] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/player down.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		PLAYER_MOVES[MOVE_DOWN] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/player right.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		PLAYER_MOVES[MOVE_RIGHT] = temp;

		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/player left.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		PLAYER_MOVES[MOVE_LEFT] = temp;
		
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/player start.png"));
		} catch (Exception e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		}
		PLAYER_MOVES[PRESS_START] = temp;

		// sprite sheet for numbers
		try {
			temp = ImageIO.read(MenuGameConstants.class.getResourceAsStream("/Practice/Images/number sprites.png"));
		} catch (Exception e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		}
		NUMBER_SPRITES = temp;
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
	static final ArrayList<Integer> ITEM_CHOOSER = new ArrayList<Integer>();

	static {
		for (Item i : Item.values()) { // for each item
			int tag = i.index;
			for (int j = 0; j < i.weight; j++) { // add X times based on weight
				ITEM_CHOOSER.add(tag);
			}
		}
	}
}
