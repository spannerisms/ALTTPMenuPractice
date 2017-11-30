package practice;

enum Item {
	BOW (0, 1,
		new ImageNamePair[] {
			new ImageNamePair("Bow", "bow-empty"),
			new ImageNamePair("Bow", "bow-normal"),
			new ImageNamePair("Bow", "bow-silvers")
		}
	),
	BOOMERANG (1, 2,
		new ImageNamePair[] {
				new ImageNamePair("Blue boomerang", "boomerang-blue"),
				new ImageNamePair("Red boomerang", "boomerang-red")
		}
	),
	HOOKSHOT (2, 1,
		new ImageNamePair("Hookshot")
	),
	BOMBS (3, 9,
			new ImageNamePair("Bombs")
		),
	WITCH (4, 2,
			new ImageNamePair[] {
					new ImageNamePair("Mushroom"),
					new ImageNamePair("Powder")
			}
		),
	FIRE_ROD (5, 1,
			new ImageNamePair("Fire rod", "firerod")
		),
	ICE_ROD (6, 1,
			new ImageNamePair("Ice rod", "icerod")
		),
	BOMBOS (7, 1,
			new ImageNamePair("Bombos")
		),
	ETHER (8, 1,
			new ImageNamePair("Ether")
		),
	QUAKE (9, 1,
			new ImageNamePair("Quake")
		),
	LAMP (10, 1,
			new ImageNamePair("Lamp")
		),
	HAMMER (11, 1,
			new ImageNamePair("Hammer")
		),
	STUMPY (12, 2,
			new ImageNamePair[] {
					new ImageNamePair("Flute"),
					new ImageNamePair("Shovel")
			}
		),
	NET (13, 1,
			new ImageNamePair("Net")
		),
	BOOK (14, 1,
			new ImageNamePair("Book of Mudora", "book")
		),
	BOTTLES (15, 4,
			new ImageNamePair[] {
					new ImageNamePair("Bottle", "bottle-empty"),
					new ImageNamePair("Green potion", "bottle-green"),
					new ImageNamePair("Blue potion", "bottle-blue"),
					new ImageNamePair("Red potion", "bottle-red"),
					new ImageNamePair("Fairy", "bottle-fairy"),
					new ImageNamePair("Bee", "bottle-bee"),
			}
		),
	SOMARIA (16, 1,
			new ImageNamePair("Cane of Somaria", "somaria")
		),
	BYRNA (17, 1,
			new ImageNamePair("Cane of Byrna", "byrna")
		),
	CAPE (18, 1,
			new ImageNamePair("Magic cape", "cape")
		),
	MIRROR (19, 1,
			new ImageNamePair("Magic mirror", "mirror")
		);

	// number of slots
	public static final int ITEM_COUNT = 20;

	// local vars
	public final int index;
	public final int weight;
	private final ImageNamePair[] items;

	private Item(int index, int weight, ImageNamePair[] items) {
		this.index = index;
		this.weight = weight;
		this.items = items;
	}

	private Item(int index, int weight, ImageNamePair items) {
		this(index, weight, new ImageNamePair[] { items });
	}

	public ImageNamePair getRandomItem() {
		int p = (int) (Math.random() * items.length);
		return items[p];
	}
}