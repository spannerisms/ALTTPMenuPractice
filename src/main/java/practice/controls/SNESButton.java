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
		switch(this.name()) {
			case "UP" :
				ID = SNESInputEvent.SNES_UP;
				break;
			case "DOWN" :
				ID = SNESInputEvent.SNES_DOWN;
				break;
			case "RIGHT" :
				ID = SNESInputEvent.SNES_RIGHT;
				break;
			case "LEFT" :
				ID = SNESInputEvent.SNES_LEFT;
				break;
			case "A" :
				ID = SNESInputEvent.SNES_A;
				break;
			case "B" :
				ID = SNESInputEvent.SNES_B;
				break;
			case "X" :
				ID = SNESInputEvent.SNES_X;
				break;
			case "Y" :
				ID = SNESInputEvent.SNES_Y;
				break;
			case "R" :
				ID = SNESInputEvent.SNES_R;
				break;
			case "L" :
				ID = SNESInputEvent.SNES_L;
				break;
			case "START" :
				ID = SNESInputEvent.SNES_START;
				break;
			case "SELECT" :
				ID = SNESInputEvent.SNES_SELECT;
				break;
			default :
				ID = 0;
				break;
		}
	}

	public Identifier getDefaultButton(ControllerType t) {
		return t.getDefaultButton(this);
	}
}