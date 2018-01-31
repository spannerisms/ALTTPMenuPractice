package practice;

import java.awt.image.BufferedImage;

class ImageNamePair {
	public final String name;
	public final BufferedImage img;

	ImageNamePair(String name, String img) {
		this.name = name;
		this.img = MenuGameConstants.fetchImageResource(
				"/images/game-icons/icon-" + img + ".png", MenuGameConstants.EMPTY_ITEM);
	}

	ImageNamePair(String name) {
		this(name, name.toLowerCase());
	}
}