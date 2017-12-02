package practice.controls;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import practice.listeners.SNESInputEvent;

public class DPadHandler extends ControllerHandler {

	public DPadHandler(Controller c,
			Component up, Component down, Component right, Component left,
			Component a, Component b, Component x, Component y,
			Component r, Component l, Component start, Component select) {
		super(
			c,
			new ComponentWrapper(up, SNESInputEvent.SNES_UP, ON),
			new ComponentWrapper(down, SNESInputEvent.SNES_DOWN, ON),
			new ComponentWrapper(right, SNESInputEvent.SNES_RIGHT, ON),
			new ComponentWrapper(left, SNESInputEvent.SNES_LEFT, ON),
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

	public DPadHandler(Controller c, Component[] list) {
		this(
			c,
			list[0],
			list[1],
			list[2],
			list[3],
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