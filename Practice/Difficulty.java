package Practice;

enum Difficulty {
	// flag is a map for booleans, where each bitplane represents the following
	// 1 :
	// 2 :
	// 3 :
	// 4 :
	// 5 :
	// 6 : 
	// 7 : show a blue cursor over the target item in addition to the name
	// 8 : randomize location of starting cursor each round
	EASY ("Easy", 15, (byte) 0b00000010),
	MEDIUM ("Medium", 10, (byte) 0b00000001);

	// local vars
	final String diffName; // difficulty name
	final int menuLength; // number of item rounds per menu
	final boolean randomizeStart; // for study mode
	final boolean showTargetCursor;

	Difficulty(String name, int menuLength, byte flags) {
		this.diffName = name;
		this.menuLength = menuLength;
		this.randomizeStart = ((flags >> 0) | 1) == 1;
		this.showTargetCursor = ((flags >> 1) | 1) == 1;
	}
}