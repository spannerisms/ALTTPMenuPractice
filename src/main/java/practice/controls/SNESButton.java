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
	private int k;

	private SNESButton(String name) {
		this.name = name;
		setKey();
	}

	private final void setKey() {
		switch(this) {
			case UP :
				k = SNESInputEvent.SNES_UP;
				break;
			case DOWN :
				k = SNESInputEvent.SNES_DOWN;
				break;
			case RIGHT :
				k = SNESInputEvent.SNES_RIGHT;
				break;
			case LEFT :
				k = SNESInputEvent.SNES_LEFT;
				break;
			case A :
				k = SNESInputEvent.SNES_A;
				break;
			case B :
				k = SNESInputEvent.SNES_B;
				break;
			case X :
				k = SNESInputEvent.SNES_X;
				break;
			case Y :
				k = SNESInputEvent.SNES_Y;
				break;
			case R :
				k = SNESInputEvent.SNES_R;
				break;
			case L :
				k = SNESInputEvent.SNES_L;
				break;
			case START :
				k = SNESInputEvent.SNES_START;
				break;
			case SELECT :
				k = SNESInputEvent.SNES_SELECT;
				break;
		}
	}

	public int ID() {
		return k;
	}

	public Identifier getDefaultButton(ControllerType t) {
		return t.getDefaultButton(this);
	}
}