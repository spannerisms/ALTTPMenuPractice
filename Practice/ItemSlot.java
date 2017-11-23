package Practice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import static Practice.MenuGameConstants.BLOCK_D;

class ItemSlot extends JComponent {
	private static final long serialVersionUID = -4130293452712063127L;

	private final Item mine;
	private ImageNamePair i;
	public final int weight;

	private ItemSlot(Item item, ImageNamePair i) {
		this.setSize(BLOCK_D);
		this.setMinimumSize(BLOCK_D);
		this.setMaximumSize(BLOCK_D);
		this.setPreferredSize(BLOCK_D);
		this.mine = item;
		this.weight = item.weight;
		this.i = i;
	}

	public ItemSlot(Item item) {
		this.setSize(BLOCK_D);
		this.setMinimumSize(BLOCK_D);
		this.setMaximumSize(BLOCK_D);
		this.setPreferredSize(BLOCK_D);
		this.mine = item;
		this.weight = item.weight;
	}

	private ItemSlot(ItemSlot c) {
		this(c.mine, c.i);
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

	public BufferedImage getCurrentImage() {
		return i.img;
	}

	public ItemSlot clone() {
		return new ItemSlot(this);
	}
}