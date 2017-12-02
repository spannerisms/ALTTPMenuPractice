package practice.controls;

import net.java.games.input.*;

public class ControlCustomizer {
	private static final long TICK = 5;

	private boolean running = false;

	private Thread ticker;
	private Controller cur = null;

	public ControlCustomizer() {
		ticker = new Thread(new Runnable() {
			public void run(){
				try {
					Thread.sleep(TICK);
					synchronized(this) {
						while(!running) {
							wait();
						}
					}
				} catch (Exception e) {}
				pollAll();
			}
		});
	}

	public void setRunning(boolean r) {
		running = r;
		if (running) {
			ticker.notify();
		}
	}

	private synchronized void pollAll() {
		cur.poll();

		Component pressed = null;

		// just find the first button pressed
		for (Component x : cur.getComponents()) {
			if (x.getPollData() == 1.0F) {
				pressed = x;
				break;
			}
		}

		if (pressed != null) {
			
		}
	}
}