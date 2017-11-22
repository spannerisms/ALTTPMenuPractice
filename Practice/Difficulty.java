package Practice;

enum Difficulty {
	// flag is a map for booleans, where each bitplane represents the following
	// 1 :
	// 2 :
	// 3 :
	// 4 :
	// 5 :
	// 6 : randomize location of starting cursor each round in collections mode
	// 7 : show a cursor over the target item in addition to the name
	// 8 : randomize location of starting cursor each round in study mode
	EASY ("Easy", 15, 20, 7, (byte) 0b00000010),
	MEDIUM ("Medium", 10, 30, 5, (byte) 0b00000011),
	HARD ("Hard", 5, 50, 3, (byte) 0b00000000),
	EXPERT ("Expert", 2, 100, 1, (byte) 0b00000101);

	// local vars
	final String diffName; // difficulty name
	final int studyRoundLength; // number of item rounds per menu in study mode
	final int burstRounds; // number of rounds in burst mode
	final int collectionRoundLength;
	final boolean randomizeStartStudy;
	final boolean randomizeStartCollections;
	final boolean showTargetCursor;

	Difficulty(String name, int studyRoundLength, int burstRounds, int collectionRoundLength, byte flags) {
		this.diffName = name;
		this.studyRoundLength = studyRoundLength;
		this.burstRounds = burstRounds;
		this.collectionRoundLength = collectionRoundLength;
		this.randomizeStartStudy = ((flags >> 0) & 1) == 1;
		this.showTargetCursor = ((flags >> 1) & 1) == 1;
		this.randomizeStartCollections = ((flags >> 2) & 1) == 1;
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

	public int roundCount(int rounds, GameMode m) {
		int ret = 1;
		switch (m) {
			case STUDY :
				ret = rounds;
				break;
			case BLITZ :
				ret = rounds * burstRounds;
				break;
			case COLLECT :
				ret = rounds * 20;
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