package Practice.Controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.games.input.*;
import Practice.Listeners.SNESInputEvent;
import Practice.Listeners.SNESInputListener;

/*
 * TODO:
 * Up/Down + Right/Left = Vertical
 * Up>down
 * Left>right
 * menu cursor move time = 2 frames after input is read
 * item switch delay = 16 frames
 */
public abstract class ControllerHandler {
	static final long TICK = 10;

	protected final SNESInputListener snes;

	protected final ArrayList<SNESControllable> children;
	protected SNESControllable brat; // has full control and only one who receives events

	protected Thread ticker;
	protected boolean running;

	protected ControllerHandler() {
		children = new ArrayList<SNESControllable>();

		brat = null;
		snes = null;

		ticker = new Thread(new Runnable() {
				public void run(){
					try {
						while(running){
							Thread.sleep(TICK);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}

	public abstract void setPolling();

	public void kill() {
		running = false;
		ticker = null;
		children.clear();
	}

	public void addChild(SNESControllable kid) {
		if (children.contains(kid)) {
			return;
		}
		children.add(kid);
	}

	public void removeChild(SNESControllable kid) {
		children.remove(kid);
		kidCalmed(kid);
	}

	// requests full focus of the controller
	public void kidWhined(SNESControllable kid) {
		if (children.contains(kid)) {
			brat = kid;
		}
	}

	public void kidCalmed(SNESControllable kid) {
		if (brat == kid) { // can't let some other brat steal this kid's spotlight
			brat = null;
		}
	}

	public void fireEvents(SNESInputEvent e) {
		if (brat != null) {
			brat.fireSNESInputEvent(e);
			return;
		}

		for (SNESControllable kid : children) {
			kid.fireSNESInputEvent(e);
		}
	}

	/*
	 * Events for snes inputs
	 */
	protected List<SNESInputListener> snesListen = new ArrayList<SNESInputListener>();
	public synchronized void addInputListener(SNESInputListener s) {
		snesListen.add(s);
	}

	public synchronized void fireInputEvent(int button) {
		SNESInputEvent te = new SNESInputEvent(this, button);
		Iterator<SNESInputListener> listening = snesListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}