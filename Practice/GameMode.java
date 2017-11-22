package Practice;

enum GameMode {
	STUDY ("Study"),
	BLITZ ("Blitz"),
	COLLECT ("Collections");

	final String modeName;
	GameMode (String name) {
		modeName = name;
	}
}