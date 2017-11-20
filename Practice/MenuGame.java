package Practice;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import static Practice.Item.ITEM_COUNT;

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
			temp = ImageIO.read(MenuGame.class.getResourceAsStream("/Practice/images/menu background.png"));
		} catch (IOException e) {
			temp = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			e.printStackTrace();
		}
		BACKGROUND = temp;

		try {
			temp = ImageIO.read(MenuGame.class.getResourceAsStream("/Practice/images/menu cursor.png"));
		} catch (IOException e) {
			temp = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
			e.printStackTrace();
		}
		CURSOR = temp;
	}

	// local vars
	private ItemSlot[][] matrix = new ItemSlot[4][5];
	private ItemSlot[] list = new ItemSlot[20];
	private int target;
	private int loc;

	public static int CURRENT_SCALE = 2;

	public MenuGame() {
		initialize();
		addKeys();
		randomize();
	}

	private final void initialize() {
		this.setLayout(null);
		for (int i = 0; i < ITEM_COUNT; i++) {
			ItemSlot temp = new ItemSlot(ALL_ITEMS[i]);
			list[i] = temp;
			int r = i / 5;
			int c = i % 5;
			matrix[r][c] = temp;

			// add to container
			temp.setBounds(ITEM_ORIGIN_X + (c * BLOCK_SIZE),
					ITEM_ORIGIN_Y + (r * BLOCK_SIZE),
					ITEM_SIZE, ITEM_SIZE);
			this.add(temp);
		}
	}

	private final void addKeys() {
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}

			public void keyTyped(KeyEvent arg0) {
				switch (arg0.getExtendedKeyCode() ) {
				
				}
				
			}
			
		});
	}
	/**
	 * Forumala used to determine if an item is on
	 * @return
	 */
	private boolean chooseOn() {
		int x = (int) (Math.random() * 3);
		return x == 0;
	}

	private void randomize() {
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
		ArrayList<Integer> pickFrom = new ArrayList<Integer>();

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
		int randomIndex = (int) (Math.random() * pickFrom.size());
		loc = pickFrom.remove(randomIndex);

		randomIndex = (int) (Math.random() * pickFrom.size());
		target = pickFrom.remove(randomIndex);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(2, 2);
		g2.drawImage(BACKGROUND, 0, 0, null);
		this.paintComponents(g2);
		ItemPoint cursorLoc = ItemPoint.valueOf("SLOT_" + loc);
		g2.drawImage(CURSOR,
				ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
				ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
				null);
	}

	private int calcMinMoves() {
		return 3;
	}

	// main
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doTheGUI();
			}
		});
	}

	// GUI
	public static void doTheGUI() {
		// try to set LaF
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e2) {
				// do nothing
		} //end System

		// fast and long-lasting tooltips
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // 596:31:23.647

		// main window
		final Dimension d = new Dimension(300,400);
		final JFrame frame = new JFrame("Menu Simulator 2K17");

		MenuGame instance = new MenuGame();
		instance.setSize(d);
		frame.add(instance);
		frame.setSize(d);
		frame.setLocation(150, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static enum ItemPoint {
		SLOT_0 (0),
		SLOT_1 (1),
		SLOT_2 (2),
		SLOT_3 (3),
		SLOT_4 (4),
		SLOT_5 (5),
		SLOT_6 (6),
		SLOT_7 (7),
		SLOT_8 (8),
		SLOT_9 (9),
		SLOT_10 (0),
		SLOT_11 (1),
		SLOT_12 (2),
		SLOT_13 (3),
		SLOT_14 (4),
		SLOT_15 (5),
		SLOT_16 (6),
		SLOT_17 (7),
		SLOT_18 (8),
		SLOT_19 (9);

		public final int x;
		public final int y;
		ItemPoint(int l) {
			x = (l % 5) * BLOCK_SIZE;
			y = (l / 5) * BLOCK_SIZE;
		}
	}
}