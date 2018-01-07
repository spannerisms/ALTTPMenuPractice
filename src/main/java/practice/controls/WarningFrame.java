package practice.controls;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class WarningFrame extends JFrame {
	private static final long serialVersionUID = -5699427980293829834L;

	public WarningFrame(String title, String... message) {
		super(title);
		JLabel warnText = new JLabel(String.join("\n",
				new String[] {
						"<html><div style=\"padding: 4px;\">",
						String.join("\n", message),
						"</div></html>"
				}));
		warnText.setVerticalAlignment(SwingConstants.NORTH);
		this.add(warnText);
		this.setMinimumSize(new Dimension(400, 200));
		this.setLocation(500, 500);
		this.setVisible(true);
	}
}