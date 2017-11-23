package Practice;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

// TODO: Meme names? Byran; IT'S GOTTA BE; etc
// TODO: credits
// feedback: candine
public class MenuPractice {
	static final String VERSION = "v0.6-beta";

	static final Dimension d = new Dimension(800, 550);
	static final Font CONSOLAS = new Font("Consolas", Font.PLAIN, 12);

	static final String HOW_TO_PLAY;
	static final String DATA_PATH = "/Practice/howtoplay.html";
	static {
		StringBuilder ret = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							MenuPractice.class.getResourceAsStream(DATA_PATH),
							StandardCharsets.UTF_8)
					);
			String line;
			while ((line = br.readLine()) != null) {
				ret.append(line);
			}
			br.close();
		} catch (Exception e) {
			ret.append("OOPS");
		}
		HOW_TO_PLAY = ret.toString();
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doTheGUI();
			}
		});
	}

	// GUI
	public static void doTheGUI() {
		// try to set LaF
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e2) {
				// do nothing
		} //end System

		// fast and long-lasting tooltips
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // 596:31:23.647

		final IntHolder turn = new IntHolder();
		turn.increment();
		final IntHolder totalScore = new IntHolder();

		// main window
		final JFrame frame = new JFrame("Menu Simulator 2K17 " + VERSION);

		final Container wrap = frame.getContentPane();
		SpringLayout l = new SpringLayout();
		wrap.setLayout(l);

		// game
		GameContainer gamePlayer = new GameContainer();

		l.putConstraint(SpringLayout.WEST, gamePlayer, 5,
				SpringLayout.WEST, wrap);
		l.putConstraint(SpringLayout.EAST, gamePlayer, 0,
				SpringLayout.HORIZONTAL_CENTER, wrap);
		l.putConstraint(SpringLayout.NORTH, gamePlayer, 5,
				SpringLayout.NORTH, wrap);
		l.putConstraint(SpringLayout.SOUTH, gamePlayer, -50,
				SpringLayout.SOUTH, wrap);
		frame.add(gamePlayer);

		// scores
		JTable scores = new JTable();
		ScoreTableModel model = new ScoreTableModel();
		scores.setModel(model);

		// scroll pane for score
		JScrollPane scoreScroll = new JScrollPane(scores,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scoreScroll.setFocusable(false);
		scoreScroll.setViewportBorder(null);
		scoreScroll.setBorder(null);
		scoreScroll.getViewport().setBorder(null);

		scores.setBorder(null);
		scores.setFont(CONSOLAS);
		scores.setBackground(null);
		scores.setFocusable(false);

		// hiscore
		JLabel scoreTotal = new JLabel("Total score:", SwingConstants.RIGHT);
		JLabel hiscore = new JLabel("0", SwingConstants.RIGHT);
		hiscore.setFont(CONSOLAS);

		l.putConstraint(SpringLayout.WEST, scoreTotal, 0,
				SpringLayout.HORIZONTAL_CENTER, scoreScroll);
		l.putConstraint(SpringLayout.SOUTH, scoreTotal, -5,
				SpringLayout.SOUTH, wrap);
		wrap.add(scoreTotal);

		l.putConstraint(SpringLayout.EAST, hiscore, -10,
				SpringLayout.EAST, scoreScroll);
		l.putConstraint(SpringLayout.VERTICAL_CENTER, hiscore, 0,
				SpringLayout.VERTICAL_CENTER, scoreTotal);
		wrap.add(hiscore);

		// clear data
		JButton clear = new JButton("Reset");
		clear.setFocusable(false);
		clear.addActionListener(
			arg0 -> {
				hiscore.setText("0");
				model.clear();
			});

		l.putConstraint(SpringLayout.EAST, clear, -10,
				SpringLayout.WEST, scoreTotal);
		l.putConstraint(SpringLayout.VERTICAL_CENTER, clear, 0,
				SpringLayout.VERTICAL_CENTER, scoreTotal);
		wrap.add(clear);

		// analysis
		TurnAnalyzer analysis = new TurnAnalyzer(frame);
		JButton analyze = new JButton("Recap");

		analyze.setFocusable(false);
		analyze.addActionListener(
			arg0 -> {
				int selectedRow = scores.getSelectedRow();
				if (selectedRow != -1) {
					analysis.setRef(model.getRow(selectedRow));
				}
				if (!analysis.isVisible()) {
					analysis.setVisible(true);
					analysis.setLocation(hiscore.getLocationOnScreen().x,
							scoreScroll.getLocationOnScreen().y);
				}
			});

		scores.getSelectionModel().addListSelectionListener(
			arg0 -> {
				int selectedRow = scores.getSelectedRow();
				if (selectedRow != -1 && analysis.isVisible()) {
					analysis.setRef(model.getRow(selectedRow));
				}
			});

		l.putConstraint(SpringLayout.EAST, analyze, -10,
				SpringLayout.WEST, clear);
		l.putConstraint(SpringLayout.VERTICAL_CENTER, analyze, 0,
				SpringLayout.VERTICAL_CENTER, scoreTotal);
		wrap.add(analyze);

		// scores in wrap
		l.putConstraint(SpringLayout.WEST, scoreScroll, 10,
				SpringLayout.EAST, gamePlayer);
		l.putConstraint(SpringLayout.EAST, scoreScroll, -5,
				SpringLayout.EAST, wrap);
		l.putConstraint(SpringLayout.NORTH, scoreScroll, 5,
				SpringLayout.NORTH, wrap);
		l.putConstraint(SpringLayout.SOUTH, scoreScroll, -5,
				SpringLayout.NORTH, scoreTotal);
		wrap.add(scoreScroll);

		// how to play
		Dimension helpD = new Dimension(400, 450);
		JDialog howPlayFrame = new JDialog(frame, "How to play");
		SpringLayout hhh = new SpringLayout();

		howPlayFrame.setLayout(hhh);
		howPlayFrame.setPreferredSize(helpD);
		howPlayFrame.setMinimumSize(helpD);
		Container howWrap = howPlayFrame.getContentPane();

		// HTML in java is dumb
		StyleSheet styleSheet = new StyleSheet();
		HTMLDocument htmlDocument;
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		styleSheet.addRule(HOW_TO_PLAY);

		htmlEditorKit.setStyleSheet(styleSheet);
		htmlDocument = (HTMLDocument) htmlEditorKit.createDefaultDocument();
		JTextPane helpPane = new JTextPane();

		helpPane.setBackground(null);
		helpPane.setEditorKit(htmlEditorKit);
		helpPane.setDocument(htmlDocument);
		helpPane.setEditable(false);

		try {
			htmlDocument.insertBeforeEnd(htmlDocument.getRootElements()[0].getElement(0), HOW_TO_PLAY);
		} catch (Exception e) {}
		// actual display
		JScrollPane helpScroll = new JScrollPane(helpPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		hhh.putConstraint(SpringLayout.WEST, helpScroll, 0,
				SpringLayout.WEST, howWrap);
		hhh.putConstraint(SpringLayout.EAST, helpScroll, 0,
				SpringLayout.EAST, howWrap);
		hhh.putConstraint(SpringLayout.NORTH, helpScroll, 0,
				SpringLayout.NORTH, howWrap);
		hhh.putConstraint(SpringLayout.SOUTH, helpScroll, 0,
				SpringLayout.SOUTH, howWrap);
		howWrap.add(helpScroll);

		// menu
		final JMenuBar menu = new JMenuBar();
		frame.setJMenuBar(menu);

		// file menu
		final JMenu fileMenu = new JMenu("File");
		menu.add(fileMenu);

		// exit
		final JMenuItem exit = new JMenuItem("Exit");
		ImageIcon mirror = new ImageIcon(
				MenuGame.class.getResource("/Practice/Images/Meta/Mirror.png")
			);
		exit.setIcon(mirror);
		fileMenu.add(exit);
		exit.addActionListener(arg0 -> System.exit(0));

		// help menu
		final JMenu helpMenu = new JMenu("Help");
		menu.add(helpMenu);

		final JMenuItem howToPlay = new JMenuItem("How to play");
		ImageIcon compass = new ImageIcon(
				MenuGame.class.getResource("/Practice/Images/Meta/Compass.png")
			);
		howToPlay.setIcon(compass);
		helpMenu.add(howToPlay);
		howToPlay.addActionListener(arg0 -> howPlayFrame.setVisible(true));

		// set icon
		ImageIcon ico =
			new ImageIcon(MenuPractice.class.getResource("/Practice/Images/Meta/ico big.png"));
		ImageIcon icoTask =
			new ImageIcon(MenuPractice.class.getResource("/Practice/Images/Meta/ico small.png"));

		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(ico.getImage());
		icons.add(icoTask.getImage());
		frame.setIconImages(icons);

		// frame display
		frame.setSize(d);
		frame.setMinimumSize(d);
		frame.setLocation(150, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		gamePlayer.addTurnListener(
			arg0 -> {
					ScoreCard tempRef = arg0.score;
					if (tempRef != null) {
						int tempScore = tempRef.finalScore;
						model.addRow(turn.val, tempRef);
						totalScore.add(tempScore);
						hiscore.setText(Integer.toString(totalScore.val));
						turn.increment();
					}
					gamePlayer.repaint();
			});
		frame.setVisible(true);
	}

	static class IntHolder {
		int val;

		IntHolder() {
			val = 0;
		}

		void set(int a) {
			val = a;
		}

		void add(int a) {
			val += a;
		}

		void increment() {
			val++;
		}
	}
}