package Practice.Listeners;

import java.util.EventObject;

public class SNESInputEvent extends EventObject {
	private static final long serialVersionUID = -5347950474514614608L;

	public static final int SNES_UP =
			0b0000_0000_0001;
	public static final int SNES_DOWN =
			0b0000_0000_0010;
	public static final int SNES_RIGHT =
			0b0000_0000_0100;
	public static final int SNES_LEFT =
			0b0000_0000_1000;

	public static final int SNES_A =
			0b0000_0001_0000;
	public static final int SNES_B =
			0b0000_0010_0000;
	public static final int SNES_X =
			0b0000_0100_0000;
	public static final int SNES_Y =
			0b0000_1000_0000;
	
	public static final int SNES_R =
			0b0001_0000_0000;
	public static final int SNES_L =
			0b0010_0000_0000;
	public static final int SNES_START =
			0b0100_0000_0000;
	public static final int SNES_SELECT =
			0b1000_0000_0000;

	private final int key_pressed;

	public SNESInputEvent(Object o, int key) {
		super(o);
		key_pressed = key;
	}

	public int getKey() {
		return key_pressed;
	}
}