package Practice;

import java.awt.Graphics;

import javax.swing.JComponent;

class ItemSlot extends JComponent {
	private static final long serialVersionUID = -4130293452712063127L;

	private final Item mine;
	private ImageNamePair i;
	public final int weight;

	ItemSlot(Item item) {
		this.setSize(MenuGame.BLOCK_D);
		this.setMinimumSize(MenuGame.BLOCK_D);
		this.setMaximumSize(MenuGame.BLOCK_D);
		this.setPreferredSize(MenuGame.BLOCK_D);
		this.mine = item;
		weight = item.weight;
	}

	public void setRandomItem() {
		i = mine.getRandomItem();
	}

	public void paint(Graphics g) {
		if (isEnabled() && i != null) {
			g.drawImage(i.img, 0, 0, null);
		}
	}

	public String getCurrentItem() {
		return i.name;
	}
}