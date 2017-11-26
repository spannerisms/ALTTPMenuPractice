package Practice.GUI;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import Practice.Listeners.SNESInputEvent;
import Practice.Listeners.SNESInputListener;

public class ControllerHandler {
	public final int T_UP;
	public final int T_DOWN;
	public final int T_RIGHT;
	public final int T_LEFT;
	public final int T_START;

	private final SNESInputListener snes;

	private final ArrayList<SNESControllable> children;
	private SNESControllable brat; // has full control and only one who receives events

	public ControllerHandler(int up, int down, int right, int left, int start) {
		T_UP = up;
		T_DOWN = down;
		T_RIGHT = right;
		T_LEFT = left;
		T_START = start;

		children = new ArrayList<SNESControllable>();

		brat = null;
		snes = null;
	}

	public ControllerHandler() {
		this(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE);
	}

	public void addChild(SNESControllable kid) {
		if (children.contains(kid)) {
			return;
		}
		children.add(kid);
	}

	public void removeChild(SNESControllable kid) {
		children.remove(kid);
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
		for (SNESControllable kid : children) {
			kid.fireSNESInputEvent(e);
		}
	}

	/*
	 * Events for snes inputs
	 */
	private List<SNESInputListener> snesListen = new ArrayList<SNESInputListener>();
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