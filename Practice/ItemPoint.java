package Practice;

enum ItemPoint {
	SLOT_0 (0),
	SLOT_1 (1),
	SLOT_2 (2),
	SLOT_3 (3),
	SLOT_4 (4),
	SLOT_5 (5),
	SLOT_6 (6),
	SLOT_7 (7),
	SLOT_8 (8),
	SLOT_9 (9),
	SLOT_10 (10),
	SLOT_11 (11),
	SLOT_12 (12),
	SLOT_13 (13),
	SLOT_14 (14),
	SLOT_15 (15),
	SLOT_16 (16),
	SLOT_17 (17),
	SLOT_18 (18),
	SLOT_19 (19);

	public final int x;
	public final int y;

	ItemPoint(int l) {
		x = (l % 5) * MenuGame.BLOCK_SIZE;
		y = (l / 5) * MenuGame.BLOCK_SIZE;
	}
}