package practice.controls;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import practice.listeners.SNESInputEvent;

public class AxisHandler extends ControllerHandler {

	public AxisHandler(Controller c,
			Component yaxis, Component xaxis,
			Component a, Component b, Component x, Component y,
			Component r, Component l, Component start, Component select) {
		super(
			c,
			new ComponentWrapper(yaxis, SNESInputEvent.SNES_UP, -ON),
			new ComponentWrapper(yaxis, SNESInputEvent.SNES_DOWN, ON),
			new ComponentWrapper(xaxis, SNESInputEvent.SNES_RIGHT, ON),
			new ComponentWrapper(xaxis, SNESInputEvent.SNES_LEFT, -ON),
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

	public AxisHandler(Controller c, Component[] list) {
		this(
				c,
				list[0],
				list[2],
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