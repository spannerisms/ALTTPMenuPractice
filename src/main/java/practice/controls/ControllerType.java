package practice.controls;

import static net.java.games.input.Component.*;
import static net.java.games.input.Component.Identifier.Button.*;

public enum ControllerType {
	KEYBOARD ( DirectionType.DPAD,
			Key.UP, Key.DOWN, Key.RIGHT, Key.LEFT,
			Key.Q, Key.W, Key.R, Key.E, Key.S, Key.A, Key.D, Key.F,
			"Keyboard"),
	MAYFLASH_GAMECUBE ( DirectionType.DPAD,
			_12, _14, _13, _15,
			_1, _2, _0, _3, _5, _4, _9, _7,
			"Mayflash GameCube Controller Adapter"),
	WINDOWS_XBOW ( DirectionType.HAT,
			Identifier.Axis.POV, Identifier.Axis.POV, Identifier.Axis.POV, Identifier.Axis.POV,
			_1, _0, _3, _2, _4, _5, _9, _6,
			"XBOX 360 For Windows (Controller)");

	private final String[] names;

	public final Identifier hatSwitch;

	public final Identifier defaultUp;
	public final Identifier defaultDown;
	public final Identifier defaultRight;
	public final Identifier defaultLeft;

	public final Identifier defaultA;
	public final Identifier defaultB;
	public final Identifier defaultX;
	public final Identifier defaultY;

	public final Identifier defaultR;
	public final Identifier defaultL;

	public final Identifier defaultStart;
	public final Identifier defaultSelect;

	public final Class<?> buttonType;

	private ControllerType(
			DirectionType d,
			Identifier defaultUp, Identifier defaultDown, Identifier defaultRight, Identifier defaultLeft,
			Identifier defaultA, Identifier defaultB, Identifier defaultX, Identifier defaultY,
			Identifier defaultR, Identifier defaultL,
			Identifier defaultStart, Identifier defaultSelect,
			String... names) {

		if (defaultUp instanceof Identifier.Axis) {
			this.hatSwitch = defaultUp;
		} else {
			this.hatSwitch = null;
		}

		this.defaultUp = defaultUp;
		this.defaultDown = defaultDown;
		this.defaultRight = defaultRight;
		this.defaultLeft = defaultLeft;

		this.defaultA = defaultA;
		this.defaultB = defaultB;
		this.defaultX = defaultX;
		this.defaultY = defaultY;

		this.defaultR = defaultR;
		this.defaultL = defaultL;

		this.defaultStart = defaultStart;
		this.defaultSelect = defaultSelect;

		this.names = names;

		buttonType = defaultStart.getClass();
	}

	public ControllerType getTypeFromName(String n) {
		ControllerType ret = null;
		typeSearch :
		for (ControllerType t : values()) {
			for (String s : t.names) {
				if (n.equalsIgnoreCase(s)) {
					ret = t;
					break typeSearch;
				}
			}
		}
		return ret;
	}

	public Identifier getDefaultButton(SNESButton b) {
		Identifier ret = null;
		switch(b) {
			case UP :
				ret = defaultUp;
				break;
			case DOWN :
				ret = defaultDown;
				break;
			case RIGHT :
				ret = defaultRight;
				break;
			case LEFT :
				ret = defaultLeft;
				break;
			case A :
				ret = defaultA;
				break;
			case B :
				ret = defaultB;
				break;
			case X :
				ret = defaultX;
				break;
			case Y :
				ret = defaultY;
				break;
			case R :
				ret = defaultR;
				break;
			case L :
				ret = defaultL;
				break;
			case START :
				ret = defaultStart;
				break;
			case SELECT :
				ret = defaultSelect;
				break;
		}
		return ret;
	}
}