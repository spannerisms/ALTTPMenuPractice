package Practice;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImageNamePair {
	public final String name;
	public final BufferedImage img;

	ImageNamePair(String name, String img) {
		this.name = name;
		BufferedImage temp;
		try {
			temp = readImage(img);
		} catch (Exception e) {
			temp = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		}
		this.img = temp;
	}

	ImageNamePair(String name) {
		this(name, name.toLowerCase());
	}

	private static BufferedImage readImage(String n) throws IOException {
		String filename = "/Practice/Images/icon-" + n + ".png";
		BufferedImage img = ImageIO.read(ImageNamePair.class.getResourceAsStream(filename));
		return img;
	}
}