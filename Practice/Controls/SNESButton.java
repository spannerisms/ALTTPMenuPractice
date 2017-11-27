package Practice.Controls;

import net.java.games.input.*;

public enum SNESButton {
	UP ("Up", Component.Identifier.Key.UP),
	DOWN ("Down", Component.Identifier.Key.DOWN),
	RIGHT ("Right", Component.Identifier.Key.RIGHT),
	LEFT ("Left", Component.Identifier.Key.LEFT),
	A ("A", Component.Identifier.Key.Q),
	B ("B", Component.Identifier.Key.W),
	X ("X", Component.Identifier.Key.R),
	Y ("Y", Component.Identifier.Key.E),
	R ("R", Component.Identifier.Key.S),
	L ("L", Component.Identifier.Key.A),
	START ("Start", Component.Identifier.Key.D),
	SELECT ("Select", Component.Identifier.Key.F);

	public final String name;
	public final Component.Identifier.Key defaultKeyboardKey;

	private SNESButton(String name, Component.Identifier.Key key) {
		this.name = name;
		this.defaultKeyboardKey = key;
	}
}