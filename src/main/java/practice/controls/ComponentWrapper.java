package practice.controls;

import net.java.games.input.Component;

public class ComponentWrapper {
	private final Component ax;
	public final int SNES_ID;

	private float d; // poll data
	private final float[] a; // values to check for if held
	boolean beingHeld = false; // held previous poll
	boolean heldDuringFrame; // caller counts up to 17 ms and held during frame is true if pressed at all
	boolean pressedThisFrame = false; // if the button was pressed (not held) during the current frame

	public ComponentWrapper(Component axis, int id, float... active) {
		ax = axis;
		SNES_ID = id;
		a = active;
	}

	public void poll(int ms) {
		if (ms == 0) { // reset every 17
			heldDuringFrame = false;
			pressedThisFrame = false;
		}
		d = ax.getPollData();
		if (beingHeld) { // if the button was down last poll
			if (held(d)) { // if it's polled as pressed
				if (ms > 0) { // if still held from last poll and that last poll was not the last millisecond of a frame
					heldDuringFrame = true;
				}
			} else { // if it's polled as not pressed
				beingHeld = false;
			}
		} else { // if the button wasn't down last poll
			if (held(d)) { // if it's polled as pressed
				beingHeld = true;
				heldDuringFrame = true;
				pressedThisFrame = true; // fire only for new presses
			} else {
				// do nothing
			}
		}
	}

	private boolean held(float f) {
		boolean held = false;
		for (float t : a) {
			if (f == t) {
				held = true;
				break;
			}
		}
		return held;
	}
}