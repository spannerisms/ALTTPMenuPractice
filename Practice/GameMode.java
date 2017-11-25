package Practice;

import java.awt.image.BufferedImage;

public enum GameMode {
	STUDY ("Study"),
	BLITZ ("Blitz"),
	COLLECT ("Collections");

	public final String modeName;
	public final BufferedImage word;
	public final BufferedImage wordHilite;

	private GameMode (String name) {
		modeName = name;
		this.word = MenuGameConstants.makeWordImage(modeName, false);
		this.wordHilite = MenuGameConstants.makeWordImage(modeName, true);
	}
}