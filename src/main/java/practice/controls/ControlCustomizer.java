package practice.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.games.input.*;

import practice.listeners.*;

public class ControlCustomizer {
	private static final long TICK = 5;

	private boolean running = false;

	private Thread ticker;
	private Controller cur = null;

	public ControlCustomizer() {

		ticker = new Thread(new Runnable() {
			public void run(){
				while (true) {
					try {
						if (running) {
							Thread.sleep(TICK);
							pollAll();
						} else {
							synchronized (ticker) {
								ticker.wait();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ticker.start();
	}

	public synchronized void setController(Controller c) {
		cur = c;
	}

	public synchronized void setRunning(boolean r) {
		running = r;
		if (running) {
			synchronized (ticker) {
				ticker.notify();
			}
		}
	}

	private synchronized void pollAll() {
		if (cur == null) { return; }

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
			fireComponentPollEvent(pressed);
		}
	}

	/*
	 * Events for remaps
	 */
	private List<ComponentPollListener> mapListen = new ArrayList<ComponentPollListener>();
	public synchronized void addComponentPollListener(ComponentPollListener s) {
		mapListen.add(s);
	}

	private synchronized void fireComponentPollEvent(Component c) {
		ComponentPollEvent te = new ComponentPollEvent(this, c);
		Iterator<ComponentPollListener> listening = mapListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}