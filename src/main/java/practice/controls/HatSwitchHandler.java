package practice.controls;

import net.java.games.input.Component;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;

import practice.listeners.SNESInputEvent;

public class HatSwitchHandler extends ControllerHandler {

	public HatSwitchHandler(Controller c,
			Component hat,
			Component a, Component b, Component x, Component y,
			Component r, Component l, Component start, Component select) {
		super(
			c,
			new ComponentWrapper(hat, SNESInputEvent.SNES_UP, POV.UP, POV.UP_LEFT, POV.UP_RIGHT),
			new ComponentWrapper(hat, SNESInputEvent.SNES_DOWN, POV.DOWN, POV.DOWN_LEFT, POV.DOWN_RIGHT),
			new ComponentWrapper(hat, SNESInputEvent.SNES_RIGHT, POV.RIGHT, POV.UP_RIGHT, POV.DOWN_RIGHT),
			new ComponentWrapper(hat, SNESInputEvent.SNES_LEFT, POV.LEFT, POV.UP_LEFT, POV.DOWN_LEFT),
			new ComponentWrapper(a, SNESInputEvent.SNES_A, ON),
			new ComponentWrapper(b, SNESInputEvent.SNES_B, ON),
			new ComponentWrapper(x, SNESInputEvent.SNES_X, ON),
			new ComponentWrapper(y, SNESInputEvent.SNES_Y, ON),
			new ComponentWrapper(r, SNESInputEvent.SNES_R, ON),
			new ComponentWrapper(l, SNESInputEvent.SNES_L, ON),
			new ComponentWrapper(start, SNESInputEvent.SNES_START, ON),
			new ComponentWrapper(select, SNESInputEvent.SNES_SELECT, ON)
		);
	}

	public HatSwitchHandler(Controller c, Component[] list) {
		this(
			c,
			list[0],
			list[4],
			list[5],
			list[6],
			list[7],
			list[8],
			list[9],
			list[10],
			list[11]
		);
	}
}