package Practice.Listeners;

import java.util.EventObject;

public class InputEvent extends EventObject {
	private static final long serialVersionUID = -5347950474514614608L;

	public static final int SNES_UP = 0b0000001;
	public static final int SNES_DOWN = 0b0000010;
	public static final int SNES_RIGHT = 0b0000100;
	public static final int SNES_LEFT = 0b0001000;
	public static final int SNES_START = 0b0010000;

	private final int key_pressed;

	public InputEvent(Object o, int key) {
		super(o);
		key_pressed = key;
	}

	public int getKey() {
		return key_pressed;
	}
}