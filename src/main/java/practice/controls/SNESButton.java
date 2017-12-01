package practice.controls;

import net.java.games.input.Component.Identifier;
import practice.listeners.SNESInputEvent;;

public enum SNESButton {
	UP ("Up"),
	DOWN ("Down"),
	RIGHT ("Right"),
	LEFT ("Left"),
	A ("A"),
	B ("B"),
	X ("X"),
	Y ("Y"),
	R ("R"),
	L ("L"),
	START ("Start"),
	SELECT ("Select");

	public final String name;
	public final int ID;

	private SNESButton(String name) {
		this.name = name;
		ID = getKey();
	}

	private final int getKey() {
		int ret = -1;
		switch(this) {
			case UP :
				ret = SNESInputEvent.SNES_UP;
				break;
			case DOWN :
				ret = SNESInputEvent.SNES_DOWN;
				break;
			case RIGHT :
				ret = SNESInputEvent.SNES_RIGHT;
				break;
			case LEFT :
				ret = SNESInputEvent.SNES_LEFT;
				break;
			case A :
				ret = SNESInputEvent.SNES_A;
				break;
			case B :
				ret = SNESInputEvent.SNES_B;
				break;
			case X :
				ret = SNESInputEvent.SNES_X;
				break;
			case Y :
				ret = SNESInputEvent.SNES_Y;
				break;
			case R :
				ret = SNESInputEvent.SNES_R;
				break;
			case L :
				ret = SNESInputEvent.SNES_L;
				break;
			case START :
				ret = SNESInputEvent.SNES_START;
				break;
			case SELECT :
				ret = SNESInputEvent.SNES_SELECT;
				break;
		}
		return ret;
	}

	public Identifier getDefaultButton(ControllerType t) {
		return t.getDefaultButton(this);
	}
}