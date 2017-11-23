package Practice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static Practice.MenuGameConstants.*;

public class ScoreCard {
	int startPresses;
	int moves;
	final long startTime;
	long endTime;
	int finalScore;
	int finalTime;
	final int minMoves;
	final Difficulty d;
	final ItemSlot[] itemsPlaced;
	final PlayerMovement[] bestPath;
	PlayerMovement[] yourMoves;
	int turn;

	public ScoreCard(Difficulty d, int minMoves, PlayerMovement[] bestPath, ItemSlot[] itemsPlaced) {
		this.d = d;
		this.minMoves = minMoves;
		startPresses = 0;
		moves = 0;
		this.bestPath = bestPath;
		this.itemsPlaced = itemsPlaced;
		startTime = System.currentTimeMillis();
	}

	int calcScore() {
		// score for how long it took
		endTime = System.currentTimeMillis(); // calculate end time on score request
		long timeDiff = endTime - startTime;
		finalTime = (int) (timeDiff);
		int timeScore = (4000 - finalTime);

		// difference between moves made and optimal moves
		int diffScore = ( 500 * ( (minMoves + 1) - moves) );
		int moveBonus = (moves == minMoves) ? 1000 : 0; // bonus for being optimal

		// penalty for pressing start on the wrong item
		int startPenalty = 0;
		if (startPresses > 1) {
			startPenalty = startPresses * 1000;
		}

		finalScore = timeScore + diffScore + moveBonus - startPenalty + d.bonus;
		return finalScore;
	}

	void setPlayerPath(PlayerMovement[] yourMoves) {
		this.yourMoves = yourMoves;
	}

	void setTurn(int t) {
		turn = t;
	}

	public BufferedImage drawTurn() {
		BufferedImage ret = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = ret.getGraphics();
		g.drawImage(BACKGROUND, 0, 0, null);
		int i = 0;
		ItemPoint cursorLoc;
		for (ItemSlot s : itemsPlaced) {
			int pos = i++;
			if (s == null) {
				continue;
			}
			cursorLoc = ItemPoint.valueOf("SLOT_" + pos);
			g.drawImage(s.getCurrentImage(),
					ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
					ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
					null);
		}
		g.drawImage(PlayerMovement.drawOptimalPath(bestPath, true), 0, 0, null);
		g.drawImage(PlayerMovement.drawPlayerPath(yourMoves), 0, 0, null);

		return ret;
	}

	public int[] toArray() {
		return new int[] {
				turn,
				finalScore,
				moves,
				minMoves,
				finalTime,
				startPresses - 1
		};
	}
}