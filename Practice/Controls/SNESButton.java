package Practice.Controls;

import java.awt.event.KeyEvent;

public enum SNESButton {
	UP ("Up", KeyEvent.VK_UP),
	DOWN ("Down", KeyEvent.VK_DOWN),
	RIGHT ("Right", KeyEvent.VK_RIGHT),
	LEFT ("Left", KeyEvent.VK_LEFT),
	A ("A", KeyEvent.VK_Q),
	B ("B", KeyEvent.VK_W),
	X ("X", KeyEvent.VK_R),
	Y ("Y", KeyEvent.VK_E),
	R ("R", KeyEvent.VK_S),
	L ("L", KeyEvent.VK_A),
	START ("Start", KeyEvent.VK_D),
	SELECT ("Select", KeyEvent.VK_F);

	public final String name;
	public final int defaultKeyboardKey;

	private SNESButton(String name, int defaultKeyboardKey) {
		this.name = name;
		this.defaultKeyboardKey = defaultKeyboardKey;
	}
}