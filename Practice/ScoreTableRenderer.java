package Practice;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ScoreTableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -6441444195312498636L;
	private static final Color SEL = new Color(0, 128, 255);

	public int useColors;
	public ScoreTableRenderer(boolean useColors) {
		if (useColors) {
			this.useColors = 0x0000;
		} else {
			this.useColors = 0x111111;
		}
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
			super.getTableCellRendererComponent(table, (int) value, isSelected, hasFocus, row, col);
		setForeground(isSelected ? Color.WHITE : Color.BLACK);
		this.setHorizontalAlignment(SwingConstants.RIGHT);
		int ensity;
		int val = (int) table.getModel().getValueAt(row, col);
		Color c = null;
		switch (col | useColors) {
			case 1 : // score
				ensity = ensity(val, 5000);
				if (val > 0) {
					c = new Color(0, 255, 0, ensity);
				} else {
					c = new Color(255, 0, 0, ensity);
				}
				break;
			case 2 : // moves
			case 3 : // optimal path
				int moves = (int) table.getModel().getValueAt(row, 2);
				int opt = (int) table.getModel().getValueAt(row, 3);
				ensity = ensity(opt, moves);
				if (moves == opt) {
					c = new Color(0, 255, 0, 200);
				} else {
					c = new Color(255, 0, 0, 255 - ensity);
				}
				break;
			case 4 : // time
				ensity = 255 - ensity(val, 2000);
				if (val <= 2000) {
					c = new Color(0, 255, 0, ensity);
				} else {
					c = new Color(255, 0, 0, ensity);
				}
				break;
			case 5 : // penalties
				if (val == 0) {
					c = new Color(0, 255, 0, 200);
				} else {
					c = new Color(255, 0, 0, 200);
				}
				break;
			case 0 :
			default :
				c = table.getBackground();
				break;
		}

		if (isSelected) {
			setBackground(SEL);
		} else if (c != null) {
			setBackground(c);
		}

		return this;
	}

	private int ensity(int n, int d) {
		double d2 = (double) d;
		return Math.min(Math.abs((int) (255 * (n / d2))), 200);
	}
}