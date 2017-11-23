package Practice;

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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Practice.Listeners.*;

public class ControlMapper extends JDialog {
	private static final long serialVersionUID = -3293305934784136424L;

	private static final Dimension PREF_D = new Dimension(200, 200);
	private static final Dimension TEXT_D = new Dimension(100, 17);

	private KeyWrapper up;
	private KeyWrapper down;
	private KeyWrapper right;
	private KeyWrapper left;
	private KeyWrapper start;

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
		// up
		JLabel upLbl = new JLabel("Up");
		up = new KeyWrapper(KeyEvent.VK_UP);
		c.gridy++;
		c.gridx = 0;
		this.add(upLbl, c);
		c.gridx = 1;
		this.add(up.text, c);

		// down
		JLabel downLbl = new JLabel("Down");
		down = new KeyWrapper(KeyEvent.VK_DOWN);
		c.gridy++;
		c.gridx = 0;
		this.add(downLbl, c);
		c.gridx = 1;
		this.add(down.text, c);

		// right
		JLabel rightLbl = new JLabel("Right");
		right = new KeyWrapper(KeyEvent.VK_RIGHT);
		c.gridy++;
		c.gridx = 0;
		this.add(rightLbl, c);
		c.gridx = 1;
		this.add(right.text, c);

		// left
		JLabel leftLbl = new JLabel("Left");
		left = new KeyWrapper(KeyEvent.VK_LEFT);
		c.gridy++;
		c.gridx = 0;
		this.add(leftLbl, c);
		c.gridx = 1;
		this.add(left.text, c);

		// start
		JLabel startLbl = new JLabel("Start");
		start = new KeyWrapper(KeyEvent.VK_SPACE);
		c.gridy++;
		c.gridx = 0;
		this.add(startLbl, c);
		c.gridx = 1;
		this.add(start.text, c);

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
				KeyWrapper[] list = new KeyWrapper[] { up, down, right, left, start };
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

	private Controller makeController() {
		return new Controller(up.i, down.i, right.i, left.i, start.i);
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