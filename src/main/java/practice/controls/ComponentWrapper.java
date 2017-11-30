package practice.controls;

import net.java.games.input.Component;

public class ComponentWrapper {
	private final Component ax;
	public final int SNES_ID;

	private float d; // poll data
	boolean beingHeld = false; // held previous poll
	boolean heldDuringFrame; // caller counts up to 17 ms and held during frame is true if pressed at all
	boolean pressedThisFrame = false; // if the button was pressed (not held) during the current frame

	public ComponentWrapper(Component a, int id) {
		ax = a;
		SNES_ID = id;

	}

	public void poll(int ms) {
		if (ms == 0) { // reset every 17
			heldDuringFrame = false;
			pressedThisFrame = false;
		}
		d = ax.getPollData();
		if (beingHeld) { // if the button was down last poll
			if (d == 1.0F) { // if it's polled as pressed
				if (ms > 0) { // if still held from last poll and that last poll was not the last millisecond of a frame
					heldDuringFrame = true;
				}
			} else if (d == 0.0F) { // if it's polled as not pressed
				beingHeld = false;
			}
		} else { // if the button wasn't down last poll
			if (d == 1.0F) { // if it's polled as pressed
				beingHeld = true;
				heldDuringFrame = true;
				pressedThisFrame = true; // fire only for new presses
			} else if (d == 0.0F) {
				// do nothing
			}
		}
	}
}