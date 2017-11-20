package Practice;

public enum Item {
	BOW (0,
		new ImageNamePair[] {
			new ImageNamePair("Bow", "bow-empty"),
			new ImageNamePair("Bow", "bow-normal"),
			new ImageNamePair("Bow", "bow-silvers")
		}
	),
	BOOMERANG (1,
		new ImageNamePair[] {
				new ImageNamePair("Blue boomerang", "boomerang-blue"),
				new ImageNamePair("Red boomarang", "boomerang-red")
		}
	),
	HOOKSHOT (2,
		new ImageNamePair("Hookshot")
	),
	BOMBS (3,
			new ImageNamePair("Bombs")
		),
	WITCH (4,
			new ImageNamePair[] {
					new ImageNamePair("Mushroom"),
					new ImageNamePair("Powder")
			}
		),
	FIRE_ROD (5,
			new ImageNamePair("Fire rod", "firerod")
		),
	ICE_ROD (6,
			new ImageNamePair("Ice rod", "icerod")
		),
	BOMBOS (7,
			new ImageNamePair("Bombos")
		),
	ETHER (8,
			new ImageNamePair("Ether")
		),
	QUAKE (9,
			new ImageNamePair("Quake")
		),
	LAMP (10,
			new ImageNamePair("Lamp")
		),
	HAMMER (11,
			new ImageNamePair("Hammer")
		),
	STUMPY (12,
			new ImageNamePair[] {
					new ImageNamePair("Flute"),
					new ImageNamePair("Shovel")
			}
		),
	NEW (13,
			new ImageNamePair("Net")
		),
	BOOK (14,
			new ImageNamePair("Book of Mudora", "book")
		),
	BOTTLES (15,
			new ImageNamePair[] {
					new ImageNamePair("Bottle", "bottle-empty"),
					new ImageNamePair("Green potion", "bottle-green"),
					new ImageNamePair("Blue potion", "bottle-blue"),
					new ImageNamePair("Red potion", "bottle-red"),
					new ImageNamePair("Fairy", "bottle-fairy"),
					new ImageNamePair("Bee", "bottle-bee"),
			}
		),
	SOMARIA (16,
			new ImageNamePair("Cane of Somaria", "somaria")
		),
	BYRNA (17,
			new ImageNamePair("Cane of Byrna", "byrna")
		),
	CAPE (18,
			new ImageNamePair("Magic cape", "cape")
		),
	MIRROR (19,
			new ImageNamePair("Magic mirror", "mirror")
		);

	// number of slots
	public static final int ITEM_COUNT = 20;

	// local vars
	public final int index;
	private final ImageNamePair[] items;

	private Item(int index, ImageNamePair[] items) {
		this.index = index;
		this.items = items;
	}

	private Item(int index, ImageNamePair items) {
		this(index, new ImageNamePair[] { items });
	}

	public ImageNamePair getRandomItem() {
		int p = (int) (Math.random() * items.length);
		return items[p];
	}
}