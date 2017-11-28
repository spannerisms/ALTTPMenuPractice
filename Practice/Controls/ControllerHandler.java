package Practice.Controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Practice.Listeners.SNESInputEvent;
import Practice.Listeners.SNESInputListener;
import net.java.games.input.*;

/*
 * TODO:
 * Up/Down + Right/Left = Vertical
 * Up>down
 * Left>right
 * menu cursor move time = 2 frames after input is read
 * item switch delay = 16 frames
 */
public class ControllerHandler {
	protected static final long TICK = 1;

	protected final SNESInputListener snes;

	protected final ArrayList<SNESControllable> children;
	protected SNESControllable brat; // has full control and only one who receives events

	protected Thread ticker;
	protected boolean running = true;

	protected final Controller controller;
	protected ComponentWrapper[] axes = new ComponentWrapper[12];

	protected final ComponentWrapper UP;
	protected final ComponentWrapper DOWN;
	protected final ComponentWrapper RIGHT;
	protected final ComponentWrapper LEFT;

	protected final ComponentWrapper A;
	protected final ComponentWrapper B;
	protected final ComponentWrapper X;
	protected final ComponentWrapper Y;

	protected final ComponentWrapper R;
	protected final ComponentWrapper L;
	protected final ComponentWrapper START;
	protected final ComponentWrapper SELECT;

	protected int ms; // counts to 16 then resets, to simulate doing an action once every frame

	public ControllerHandler(Controller c, Component[] comps) {
		this(
			c,
			comps[0],
			comps[1],
			comps[2],
			comps[3],
			comps[4],
			comps[5],
			comps[6],
			comps[7],
			comps[8],
			comps[9],
			comps[10],
			comps[11]
		);
	}

	public ControllerHandler(Controller c,
			Component up, Component down, Component right, Component left,
			Component a, Component b, Component x, Component y,
			Component r, Component l, Component start, Component select) {
		controller = c;
		UP = new ComponentWrapper(up, SNESInputEvent.SNES_UP);
		DOWN = new ComponentWrapper(down, SNESInputEvent.SNES_DOWN);
		RIGHT = new ComponentWrapper(right, SNESInputEvent.SNES_RIGHT);
		LEFT = new ComponentWrapper(left, SNESInputEvent.SNES_LEFT);
		A = new ComponentWrapper(a, SNESInputEvent.SNES_A);
		B = new ComponentWrapper(b, SNESInputEvent.SNES_B);
		X = new ComponentWrapper(x, SNESInputEvent.SNES_X);
		Y = new ComponentWrapper(y, SNESInputEvent.SNES_Y);
		R = new ComponentWrapper(r, SNESInputEvent.SNES_R);
		L = new ComponentWrapper(l, SNESInputEvent.SNES_L);
		START = new ComponentWrapper(start, SNESInputEvent.SNES_START);
		SELECT = new ComponentWrapper(select, SNESInputEvent.SNES_SELECT);

		int i = 0;
		axes[i++] = UP;
		axes[i++] = DOWN;
		axes[i++] = RIGHT;
		axes[i++] = LEFT;
		axes[i++] = A;
		axes[i++] = B;
		axes[i++] = X;
		axes[i++] = Y;
		axes[i++] = R;
		axes[i++] = L;
		axes[i++] = START;
		axes[i++] = SELECT;

		children = new ArrayList<SNESControllable>();

		brat = null;
		snes = null;

		ticker = new Thread(new Runnable() {
				public void run(){
					try {
						while(running) {
							pollAll();
							ms++;
							if (ms == 17) {
								ms = 0;
								setUpAndFireEvents();
							}
							Thread.sleep(TICK);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		ticker.start();
	}

	private synchronized void pollAll() {
		controller.poll();

		for (ComponentWrapper x : axes) {
			x.poll(ms);
		}
	}

	private synchronized void setUpAndFireEvents() {
		int pressesFiredAll = 0;
		int pressesFiredDPad = 0;
		for (ComponentWrapper x : axes) {
			int id = x.SNES_ID;
			switch (id) {
				case SNESInputEvent.SNES_UP :
				case SNESInputEvent.SNES_DOWN :
				case SNESInputEvent.SNES_RIGHT :
					case SNESInputEvent.SNES_LEFT :
					if (x.pressedThisFrame) {
						pressesFiredDPad |= id;
					}
					break;
				case SNESInputEvent.SNES_R :
				case SNESInputEvent.SNES_L :
					if (x.heldDuringFrame) {
						pressesFiredAll |= id;
					}
					break;
				default :
					if (x.pressedThisFrame) {
						pressesFiredAll |= id;
					}
					break;
			}
		}
		if (pressesFiredDPad != 0) {
			fireEvents(new SNESInputEvent(this, pressesFiredDPad));
		}
		if (pressesFiredAll != 0) {
			fireEvents(new SNESInputEvent(this, pressesFiredAll));
		}
	}

	public void kill() {
		running = false;
		ticker = null;
		children.clear();
	}

	public synchronized void addChild(SNESControllable kid) {
		if (children.contains(kid)) {
			return;
		}
		children.add(kid);
	}

	public synchronized void removeChild(SNESControllable kid) {
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

	public synchronized void fireEvents(SNESInputEvent e) {
		if (brat != null) {
			brat.fireSNESInputEvent(e);
			return;
		}

		ArrayList<SNESControllable> firing = new ArrayList<SNESControllable>();
		firing.addAll(children);
		Iterator<SNESControllable> i = firing.iterator();
		while (i.hasNext()) {
			(i.next()).fireSNESInputEvent(e);
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