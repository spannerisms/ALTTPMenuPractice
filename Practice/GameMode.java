package Practice;

import java.awt.image.BufferedImage;

enum GameMode {
	STUDY ("Study"),
	BLITZ ("Blitz"),
	COLLECT ("Collections");

	final String modeName;
	final BufferedImage word;
	final BufferedImage wordHilite;

	GameMode (String name) {
		modeName = name;
		this.word = ControlScreen.makeWordImage(modeName, false);
		this.wordHilite = ControlScreen.makeWordImage(modeName, true);
	}
}