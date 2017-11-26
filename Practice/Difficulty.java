package Practice;

import java.awt.image.BufferedImage;

public enum Difficulty {
	// flag is a map for booleans, where each bitplane represents the following
	// 1 :
	// 2 :
	// 3 : show start position while studying
	// 4 : show item icons
	// 5 : show optimal path
	// 6 : randomize location of starting cursor each round in collections mode
	// 7 : show a cursor over the target item in addition to the name
	// 8 : randomize location of starting cursor each round in study mode
	BEGINNER (
			"Beginner", // name
			0, 10, 7000, // bonus, mult, time thresh
			20, 5000, // study: round length, time
			20, // blitz : rounds
			10, // collections: round length
			(byte) 0b0011_1010
		),
	EASY (
			"Easy",
			0, 20, 5000,
			15, 5000,
			20,
			7, (byte)
			0b0001_0010
		),
	MEDIUM (
			"Medium",
			100, 50, 2500,
			10, 3000,
			30,
			5,
			(byte) 0b0001_0011
		),
	HARD (
			"Hard",
			200, 90, 1200,
			5, 1000,
			50,
			3,
			(byte) 0b0001_0000
		),
	EXPERT (
			"Expert",
			500, 150, 750,
			2, 500,
			100,
			1,
			(byte) 0b000_00101
		);

	// local vars
	public final String diffName;

	public final int bonus;
	public final int multiplier;
	public final int timeThresh;

	public final int studyRoundLength;
	public final int studyTime;
	public final int blitzRounds;
	public final int collectionRoundLength;

	// values determined by flag
	public final boolean randomizeStartStudy;
	public final boolean randomizeStartCollections;
	public final boolean showTargetCursor;
	public final boolean showOptimalPath;
	public final boolean showItemIcon;
	public final boolean showStartDuringStudy;

	// graphical display constants
	public final BufferedImage word;
	public final BufferedImage wordHilite;

	private Difficulty(String name,
			int difficultyBonus, int difficultyMultiplier, int timeThreshold,
			int studyRoundLength, int studyTime,
			int burstRounds,
			int collectionRoundLength,
			byte flags) {
		this.diffName = name;
		this.word = MenuGameConstants.makeWordImage(diffName, 0);
		this.wordHilite = MenuGameConstants.makeWordImage(diffName, 1);

		this.bonus = difficultyBonus;
		this.multiplier = difficultyMultiplier;
		this.timeThresh = timeThreshold;
		this.studyRoundLength = studyRoundLength;
		this.studyTime = studyTime;
		this.blitzRounds = burstRounds;
		this.collectionRoundLength = collectionRoundLength;

		this.randomizeStartStudy = ((flags >> 0) & 1) == 1;
		this.showTargetCursor = ((flags >> 1) & 1) == 1;
		this.randomizeStartCollections = ((flags >> 2) & 1) == 1;
		this.showOptimalPath = ((flags >> 3) & 1) == 1;
		this.showItemIcon = ((flags >> 4) & 1) == 1;
		this.showStartDuringStudy = ((flags >> 5) & 1) == 1;
	}

	public int roundLength(GameMode m) {
		int ret = 1;
		switch (m) {
			case STUDY :
				ret = studyRoundLength;
				break;
			case BLITZ :
				ret = 1;
				break;
			case COLLECT :
				ret = collectionRoundLength;
				break;
		}
		return ret;
	}

	public int roundsPerGame(GameMode m) {
		int ret = 1;
		switch (m) {
			case STUDY :
				if (this == HARD) {
					ret = 5;
				} else if (this == EXPERT) {
					ret = 10;
				}
				break;
			case BLITZ :
				ret = blitzRounds;
				break;
			case COLLECT :
				ret = 17;
				break;
		}
		return ret;
	}

	public boolean randomizesStart(GameMode m) {
		boolean ret = false;
		switch (m) {
			case STUDY :
				ret = randomizeStartStudy;
				break;
			case BLITZ :
				ret = true;
				break;
			case COLLECT :
				ret = randomizeStartCollections;
				break;
		}
		return ret;
	}
}