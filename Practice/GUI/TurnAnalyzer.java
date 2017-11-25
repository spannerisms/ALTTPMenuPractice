package Practice.GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import Practice.ScoreCard;

import static Practice.MenuGameConstants.*;

public class TurnAnalyzer extends JDialog {
	private static final long serialVersionUID = -5679165578072012375L;

	static final Dimension PREF_D =
			new Dimension(BG_WIDTH * 2 + 20, BG_HEIGHT * 2 + 140);

	ScoreCard curRef;
	BufferedImage refImg;
	final JPanel menuPainter;
	final JLabel turnVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel scoreVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel timeVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel movesVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel optVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel penaltyVal = new JLabel("-", SwingConstants.RIGHT);
	final JLabel diffName = new JLabel("-", SwingConstants.RIGHT);
	final SpringLayout l = new SpringLayout();

	@SuppressWarnings("serial")
	public TurnAnalyzer(JFrame frame) {
		super(frame,"Turn analysis");
		menuPainter = new JPanel() {
			public void paint(Graphics g) {
				if (refImg == null) {
					return;
				}
				Graphics2D g2 = (Graphics2D) g;
				g2.scale(2, 2);

				g2.drawImage(refImg, 0, 0, null);
			}
		};
		menuPainter.setPreferredSize(MENU_SIZE_X2);
		menuPainter.setMinimumSize(MENU_SIZE_X2);
		menuPainter.setSize(MENU_SIZE_X2);
		initialize();
	}

	private final void initialize() {
		this.setPreferredSize(PREF_D);
		this.setMinimumSize(PREF_D);
		this.setResizable(false);
		this.setLayout(new GridBagLayout());
		JPanel wrap = (JPanel) this.getContentPane();
		wrap.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		wrap.setPreferredSize(PREF_D);
		wrap.setMinimumSize(PREF_D);
		c.gridy = -1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 4;
		JLabel colon;

		// turn
		JLabel turnLbl = new JLabel("Turn", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(turnLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(turnVal, c);
		c.gridx = 3;
		c.weightx = 20;
		wrap.add(new JLabel(""), c); // blank
		c.weightx = 1;

		// difficulty
		JLabel diffLbl = new JLabel("Difficulty", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(diffLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(diffName, c);

		// score
		JLabel scoreLbl = new JLabel("Score", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(scoreLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(scoreVal, c);

		// moves
		JLabel movesLbl = new JLabel("Moves", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(movesLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(movesVal, c);

		// optimal path
		JLabel optLbl = new JLabel("Best path", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(optLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(optVal, c);

		// time
		JLabel timeLbl = new JLabel("Time (ms)", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(timeLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(timeVal, c);

		// penal
		JLabel penalLbl = new JLabel("Penalties", SwingConstants.LEFT);
		colon = new JLabel(":");
		c.gridy++;
		c.gridx = 0;
		wrap.add(penalLbl, c);
		c.gridx = 1;
		wrap.add(colon, c);
		c.gridx = 2;
		wrap.add(penaltyVal, c);

		// image
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 4;
		wrap.add(menuPainter, c);
	}

	public void setRef(ScoreCard ref) {
		curRef = ref;
		refImg = ref.drawTurn();
		turnVal.setText(Integer.toString(ref.turn));
		diffName.setText(ref.d.diffName);
		scoreVal.setText(Integer.toString(ref.finalScore));
		timeVal.setText(Integer.toString(ref.finalTime));
		optVal.setText(Integer.toString(ref.minMoves));
		movesVal.setText(Integer.toString(ref.moves));
		penaltyVal.setText(Integer.toString(ref.startPresses-1));
		revalidate();
		repaint();
	}
}