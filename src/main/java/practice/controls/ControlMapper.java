package practice.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import practice.listeners.*;
import net.java.games.input.*;

public class ControlMapper extends JDialog {
	private static final long serialVersionUID = -3293305934784136424L;

	private static final Dimension PREF_D = new Dimension(300, 400);
	private static final Dimension TEXT_D = new Dimension(100, 17);

	private static ContWrapper[] controllerList = refreshList();

	private CompWrapper[] list = new CompWrapper[SNESButton.values().length];

	static final ContWrapper[] refreshList() {
		ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();
		ArrayList<ContWrapper> ret = new ArrayList<ContWrapper>();

		// keep track of gamepads that don't don't have default configs
		ArrayList<String> badControllers = new ArrayList<>();
		boolean badStuffHappened = false;

		controllerReading :
		for (Controller c : env.getControllers()) {
			Controller.Type t = c.getType();

			if (t.equals(Controller.Type.GAMEPAD) ||
					t.equals(Controller.Type.KEYBOARD)) {
				Component[] comp = c.getComponents();
				CompWrapper[] use = new CompWrapper[12];
				int i = 0;

				ControllerType type;

				try {
					type = ControllerType.inferType(c);
				} catch (ControllerException e) {
					badControllers.add(c.getName());
					badStuffHappened = true;
					continue controllerReading; // skip this controller
				}

				defaultMappings :
				for (SNESButton s : SNESButton.values()) {
					for (Component x : comp) {
						if (x.getIdentifier() == s.getDefaultButton(type)) {
							use[i++] = new CompWrapper(x);
							continue defaultMappings;
						}
					} // end components
				} // end buttons
				ret.add(new ContWrapper(c, use, type));
			} // end valid type if
		} // end controller loop

		ContWrapper[] r = new ContWrapper[ret.size()];

		int i = 0;
		for (ContWrapper a : ret) {
			r[i++] = a;
		}

		if (badStuffHappened) {
			WarningFrame oops =
				new WarningFrame("Unrecognized controllers",
					"The following controllers were not recognized as having default configurations:",
					"<br />",
					String.join("<br />", badControllers)
				);
			oops.setVisible(true);
		}

		return r;
	}

	// default controller
	public static final ControllerHandler defaultController;
	private static ContWrapper keyboard;

	static {
		for (ContWrapper c : controllerList) {
			if (c.c.getType().equals(Controller.Type.KEYBOARD)) {
				keyboard = c;
				break;
			}
		}

		ControllerHandler temp = null;

		try {
			temp = makeControllerHandler(keyboard);
		} catch (ControllerException e) {
			temp = null;
			e.printStackTrace();
		}

		defaultController = temp;
	}

	JComboBox<ContWrapper> curBox;
	JPanel comboArea = new JPanel();
	JPanel compArea = new JPanel();
	ControlCustomizer customizer = new ControlCustomizer();
	ContWrapper activeController;

	public ControlMapper(JFrame frame) {
		super(frame, "Configure");
		activeController = controllerList[0];
		initialize();
	}

	private final void initialize() {
		this.setPreferredSize(PREF_D);
		this.setMinimumSize(PREF_D);
		this.setResizable(false);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 4;
		c.ipady = 2;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridy = -1;

		newComboBox();
		c.gridy++;
		c.gridx = 0;
		this.add(comboArea, c);

		compArea.setLayout(new GridBagLayout());
		setComponentArea();
		c.gridy++;
		c.gridx = 0;
		this.add(compArea, c);

		// apply
		JButton confirm = new JButton("Apply");
		c.gridy++;
		c.gridx = 0;
		this.add(confirm, c);

		JTextField no = new JTextField("");
		no.setEditable(false);
		no.setHighlighter(null);
		no.setForeground(null);
		no.setBackground(null);
		no.setBorder(null);
		no.setHorizontalAlignment(SwingConstants.CENTER);
		no.setSize(TEXT_D);
		c.gridy++;
		this.add(no, c);

		confirm.addActionListener(
			arg0 -> {
				boolean okToGo = true;
				dupeSearch :
				for (CompWrapper e : list) {
					if (e.isHatSwitch) { // hat switch has to be the same
						continue dupeSearch;
					}
					dupeMatch :
					for (CompWrapper k : list) {
						if (e == k) {
							continue dupeMatch;
						}
						if (e.c == k.c) {
							okToGo = false;
							break dupeSearch;
						}
					} // end loop 1
				} // end loop 2
				if (okToGo) {
					no.setText("");
					no.setForeground(null);
					no.setBackground(null);
					try {
						fireRemapEvent();
					} catch (ControllerException e1) {
						WarningFrame oops = new WarningFrame("Oops");
						e1.printStackTrace();
					}
				} else {
					no.setText("DUPLICATE KEYS");
					no.setForeground(Color.WHITE);
					no.setBackground(Color.RED);
				}
			});

		customizer.addComponentPollListener(
			arg0 -> {
				CompWrapper f = focusedDude();
				if (f != null &&
						!f.isHatSwitch &&
						(arg0.comp.getIdentifier() instanceof Component.Identifier.Button) ) {
					f.setComp(arg0.comp);
				}
			});
		customizer.setController(activeController.c);
	}

	private void setComponentArea() {
		compArea.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 4;
		c.ipady = 2;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridy = -1;

		int i = 0;
		for (SNESButton b : SNESButton.values()) {
			JLabel lbl = new JLabel(b.name);
			CompWrapper k = activeController.list[i];
			list[i++] = k;
			c.gridy++;
			c.gridx = 0;
			compArea.add(lbl, c);
			c.gridx = 1;
			compArea.add(k.text, c);
		}
		revalidate();
		repaint();
	}

	private ItemListener boxRead = arg0 -> {
		setControlWrapper((ContWrapper) curBox.getSelectedItem());
	};

	private void newComboBox() {
		if (curBox != null) {
			curBox.removeItemListener(boxRead);
		}
		comboArea.removeAll();
		curBox = new JComboBox<ContWrapper>(controllerList);
		comboArea.add(curBox);
		setControlWrapper(curBox.getItemAt(0));
		curBox.addItemListener(boxRead);
		revalidate();
	}

	private static ControllerHandler makeControllerHandler(ContWrapper w) throws ControllerException {
		ControllerHandler ret = null;
		Class<? extends ControllerHandler> hClass = w.t.dType.handler;
		try {
			Constructor<? extends ControllerHandler> ctor = hClass.getDeclaredConstructor(Controller.class, Component[].class);
			ret = ctor.newInstance((Controller) w.c, (Component[]) w.getList());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ControllerException("There was a problem finalizing the controller configuration: " + e.getMessage());
		}
		return ret;
	}

	private void setControlWrapper(ContWrapper c) {
		activeController = c;
		customizer.setController(activeController.c);
		setComponentArea();
		repaint();
	}

	// get the focused component wrapper, only need to look at current controller
	private CompWrapper focusedDude() {
		if (activeController == null) {
			return null;
		}
		CompWrapper ret = null;
		for (CompWrapper c : activeController.list) {
			if (c.active) {
				ret = c;
				break;
			}
		}
		return ret;
	}

	public void setRunning(boolean r) {
		customizer.setRunning(r);
	}

	/*
	 * Events for being done
	 */
	private List<RemapListener> doneListen = new ArrayList<RemapListener>();
	public synchronized void addRemapListener(RemapListener s) {
		doneListen.add(s);
	}

	private synchronized void fireRemapEvent() throws ControllerException {
		RemapEvent te = new RemapEvent(this, makeControllerHandler(activeController));
		Iterator<RemapListener> listening = doneListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}

	// wrap a controller and the components that we care about
	static class ContWrapper {
		final Controller c;
		final CompWrapper[] list;
		final ControllerType t;

		ContWrapper(Controller c, CompWrapper[] list, ControllerType t) {
			this.c = c;
			this.list = list;
			this.t = t;
		}

		public String toString() {
			return c.getName();
		}

		Component[] getList() {
			Component[] ret = new Component[12];
			for (int i = 0; i < 12; i++) {
				ret[i] = list[i].c;
			}
			return ret;
		}
	}

	// wrap a component and a text box for easy pairing
	static class CompWrapper {
		Component c;
		final JTextField text;
		boolean active;
		final boolean isHatSwitch;

		CompWrapper(Component c) {
			this.c = c;

			isHatSwitch = c.getIdentifier() instanceof Component.Identifier.Axis; // need to keep track of hat switch components

			text = new JTextField();
			text.setPreferredSize(TEXT_D);
			text.setMinimumSize(TEXT_D);
			text.setHorizontalAlignment(SwingConstants.CENTER);

			setComp(c);

			Color focusColor = isHatSwitch ? Color.RED : Color.YELLOW; // ban editing on hat switch

			text.setEditable(false);
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					text.setBackground(focusColor);
					active = true;
				}

				public void focusLost(FocusEvent arg0) {
					text.setBackground(null);
					active = false;
				}});
		}

		public void setComp(Component c) {
			this.c = c;
			text.setText(c.getName());
		}
	}
}