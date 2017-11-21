package Practice;

enum Difficulty {
	EASY ("Easy", 15);

	final String diffName; // difficulty name
	final int menuLength; // number of item rounds per menu

	Difficulty(String name, int menuLength) {
		this.diffName = name;
		this.menuLength = menuLength;
	}
}