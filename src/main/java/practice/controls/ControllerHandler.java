package practice.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import practice.listeners.SNESInputEvent;
import practice.listeners.SNESInputListener;
import net.java.games.input.*;

import static practice.listeners.SNESInputEvent.*;

/*
 * TODO:
 * menu cursor move time = 2 frames after input is read ?
 */
public abstract class ControllerHandler {
	protected static final long TICK = 1;

	protected static final float ON = 1.0F;

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
	protected boolean dead = false;

	public ControllerHandler(Controller c, Component[] list) {
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

	public ControllerHandler(Controller c,
			Component up, Component down, Component right, Component left,
			Component a, Component b, Component x, Component y,
			Component r, Component l, Component start, Component select) {
		this(
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

	protected ControllerHandler(Controller c,
			ComponentWrapper up, ComponentWrapper down, ComponentWrapper right, ComponentWrapper left,
			ComponentWrapper a, ComponentWrapper b, ComponentWrapper x, ComponentWrapper y,
			ComponentWrapper r, ComponentWrapper l, ComponentWrapper start, ComponentWrapper select) {
		controller = c;
		UP = up;
		DOWN = down;
		RIGHT = right;
		LEFT = left;
		A = a;
		B = b;
		X = x;
		Y = y;
		R = r;
		L = l;
		START = start;
		SELECT = select;

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
				while (true) {
					try {
						if (running) {
							Thread.sleep(TICK);
							pollAll();
							ms++;
							if (ms == 17) {
								ms = 0;
								setUpAndFireEvents();
							}
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
				case SNES_UP :
				case SNES_DOWN :
				case SNES_RIGHT :
				case SNES_LEFT :
					if (x.pressedThisFrame) {
						pressesFiredDPad |= id;
					}
					break;
				case SNES_R :
				case SNES_L :
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
			// filter the dpad to only send 1 button per event
			// priority : UP>DOWN>LEFT>RIGHT
			int fire = 0;
			if ((pressesFiredDPad & SNES_UP) > 0) {
				fire = SNES_UP;
			} else if ((pressesFiredDPad & SNES_DOWN) > 0) {
				fire = SNES_DOWN;
			} else if ((pressesFiredDPad & SNES_LEFT) > 0) {
				fire = SNES_LEFT;
			} else if ((pressesFiredDPad & SNES_RIGHT) > 0) {
				fire = SNES_RIGHT;
			}
			System.out.println(fire);
			fireEvents(new SNESInputEvent(this, fire));
		}
		if (pressesFiredAll != 0) {
			fireEvents(new SNESInputEvent(this, pressesFiredAll));
		}
	}

	public synchronized void kill() {
		dead = true;
		running = false;
		ticker = null;
		children.clear();
	}

	public synchronized void addChild(SNESControllable kid) {
		if (dead) { return; }
		if (children.contains(kid)) { return; }
		children.add(kid);
	}

	public synchronized void removeChild(SNESControllable kid) {
		children.remove(kid);
		kidCalmed(kid);
	}

	// requests full focus of the controller
	public synchronized void kidWhined(SNESControllable kid) {
		if (children.contains(kid)) {
			brat = kid;
		}
	}

	public synchronized void kidCalmed(SNESControllable kid) {
		if (brat == kid) { // can't let some other brat steal this kid's spotlight
			brat = null;
		}
	}

	public synchronized void setRunning(boolean r) {
		if (dead) { return; }
		running = r;
		if (running) {
			synchronized (ticker) {
				ticker.notify();
			}
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