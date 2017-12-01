package practice.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

	private static final Dimension PREF_D = new Dimension(200, 400);
	private static final Dimension TEXT_D = new Dimension(100, 17);

	private KeyWrapper[] list = new KeyWrapper[SNESButton.values().length];

	private Controller[] controllerList = refreshList();

	static final Controller[] refreshList() {
		ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();
		ArrayList<Controller> ret = new ArrayList<Controller>();
		for (Controller c : env.getControllers()) {
			Controller.Type t = c.getType();
			if (t.equals(Controller.Type.GAMEPAD) ||
					t.equals(Controller.Type.KEYBOARD)) {
				ret.add(c);
			}
		}

		Controller[] r = new Controller[ret.size()];
		int i = 0;
		for (Controller a : ret) {
			r[i++] = a;
		}
		return r;
	}

	// default controller
	public static final ControllerHandler defaultController;

	static {
		Controller[] list = refreshList();
		Controller keyboard = null;
		for (Controller c : list) {
			if (c.getType().equals(Controller.Type.KEYBOARD)) {
				keyboard = c;
				break;
			}
		}
		Component[] comp = keyboard.getComponents();
		Component[] use = new Component[12];
		int i = 0;
		defaultMappings :
		for (SNESButton s : SNESButton.values()) {
			for (Component c : comp) {
				if (c.getIdentifier() == s.getDefaultButton(ControllerType.KEYBOARD)) {
					use[i++] = c;
					continue defaultMappings;
				}
			}
		}
		defaultController = null;
	}

	JPanel comboArea = new JPanel();

	public ControlMapper(JFrame frame) {
		super(frame, "Configure");
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
		c.gridwidth = 2;
		this.add(comboArea, c);
		c.gridwidth = 1;

		int i = 0;
		for (SNESButton b : SNESButton.values()) {
			JLabel lbl = new JLabel(b.name);
			KeyWrapper k = new KeyWrapper(0);
			list[i++] = k;
			c.gridy++;
			c.gridx = 0;
			this.add(lbl, c);
			c.gridx = 1;
			this.add(k.text, c);
		}

		// apply
		JButton confirm = new JButton("Apply");
		c.gridwidth = 2;
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
		c.gridwidth = 2;
		c.gridy++;
		this.add(no, c);

		confirm.addActionListener(
			arg0 -> {
				boolean okToGo = true;
				dupeSearch :
				for (KeyWrapper e : list) {
					dupeMatch :
					for (KeyWrapper k : list) {
						if (e == k) {
							continue dupeMatch;
						}
						if (e.i == k.i) {
							okToGo = false;
							break dupeSearch;
						}
					} // end loop 1
				} // end loop 2
				if (okToGo) {
					no.setText("");
					no.setForeground(null);
					no.setBackground(null);
					fireRemapEvent();
				} else {
					System.out.println("A");
					no.setText("DUPLICATE KEYS");
					no.setForeground(Color.WHITE);
					no.setBackground(Color.RED);
				}
			});
	}

	private void newComboBox() {
		controllerList = refreshList();
		comboArea.removeAll();
		JComboBox<Controller> add = new JComboBox<Controller>(controllerList);
		comboArea.add(add);
		revalidate();
	}

	private ControllerHandler makeController() {
		return null;
	}

	static class KeyWrapper {
		int i;
		final JTextField text;

		KeyWrapper(int i) {
			this.i = i;
			text = new JTextField();
			text.setPreferredSize(TEXT_D);
			text.setMinimumSize(TEXT_D);
			text.setHorizontalAlignment(SwingConstants.CENTER);
			setKey(i);
			text.setEditable(false);
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					text.setBackground(Color.YELLOW);
				}
				public void focusLost(FocusEvent arg0) {
					text.setBackground(null);
				}});
			text.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
					int p = arg0.getKeyCode();
					switch (p) { // switch statement to just cancel invalid keys
						case KeyEvent.VK_CAPS_LOCK :
						case KeyEvent.VK_NUM_LOCK :
						case KeyEvent.VK_SCROLL_LOCK :
						case KeyEvent.VK_WINDOWS :
						case KeyEvent.VK_SHIFT :
						case KeyEvent.VK_CONTROL :
						case KeyEvent.VK_ALT :
						case KeyEvent.VK_ESCAPE :
						case KeyEvent.VK_KANA_LOCK :
							return; // TODO : better catch?
					}
					setKey(p);
				}

				public void keyReleased(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
		}

		public void setKey(int p) {
			String n = KeyEvent.getKeyText(p);
			text.setText(n.toUpperCase());
			i = p;
		}
	}

	/*
	 * Events for being done
	 */
	private List<RemapListener> doneListen = new ArrayList<RemapListener>();
	public synchronized void addRemapListener(RemapListener s) {
		doneListen.add(s);
	}

	private synchronized void fireRemapEvent() {
		RemapEvent te = new RemapEvent(this, makeController());
		Iterator<RemapListener> listening = doneListen.iterator();
		while(listening.hasNext()) {
			(listening.next()).eventReceived(te);
		}
	}
}