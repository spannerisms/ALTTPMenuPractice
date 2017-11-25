package Practice;

import java.awt.event.KeyEvent;

public class Controller {
	public final int T_UP;
	public final int T_DOWN;
	public final int T_RIGHT;
	public final int T_LEFT;
	public final int T_START;

	public Controller(int up, int down, int right, int left, int start) {
		T_UP = up;
		T_DOWN = down;
		T_RIGHT = right;
		T_LEFT = left;
		T_START = start;
	}

	public Controller() {
		T_UP = KeyEvent.VK_UP;
		T_DOWN = KeyEvent.VK_DOWN;
		T_RIGHT = KeyEvent.VK_RIGHT;
		T_LEFT = KeyEvent.VK_LEFT;
		T_START = KeyEvent.VK_SPACE;
	}
}
