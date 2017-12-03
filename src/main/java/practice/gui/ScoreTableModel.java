package practice.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import practice.ScoreCard;

class ScoreTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 5841696898807444151L;

	String[] columnNames = { "Turn",
			"Score",
			"Moves",
			"Optimal",
			"Time (ms)",
			"Penalties" };
	ArrayList<ScoreCard> data = new ArrayList<ScoreCard>();

	public ScoreTableModel() {
		super();
	}

	public ScoreCard getRow(int i) {
		return data.get(i);
	}

	public void addRow(int turn, ScoreCard s) {
		int l = this.data.size();
		s.setTurn(turn);
		this.data.add(s);
		this.fireTableRowsInserted(l, l);
	}

	public void clear() {
		int oldLength = data.size();
		data.clear();
		this.fireTableRowsDeleted(0, oldLength-1);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getRowCount() {
		return data.size();
	}

	public Class<?> getColumnClass(int c) {
		Object o = getValueAt(0, c);
		if (o == null) {
			return Number.class;
		}
		return o.getClass();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= data.size()) {
			return null;
		}
		return data.get(rowIndex).toArray()[columnIndex];
	}
}