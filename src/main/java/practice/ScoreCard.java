package practice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static practice.MenuGameConstants.*;

public class ScoreCard {
	public int startPresses;
	public int moves;
	public final long startTime;
	public long endTime;
	public int finalScore;
	public int finalTime;
	public final int minMoves;
	public final Difficulty d;
	public final ItemSlot[] itemsPlaced;
	public final PlayerMovement[] bestPath;
	public PlayerMovement[] yourMoves;
	public int turn;

	public ScoreCard(Difficulty d, int minMoves, PlayerMovement[] bestPath, ItemSlot[] itemsPlaced) {
		this.d = d;
		this.minMoves = minMoves;
		startPresses = 0;
		moves = 0;
		this.bestPath = bestPath;
		this.itemsPlaced = itemsPlaced;
		startTime = System.currentTimeMillis();
	}

	public int calcScore() {
		// score for how long it took
		endTime = System.currentTimeMillis(); // calculate end time on score request
		long timeDiff = endTime - startTime;
		finalTime = (int) (timeDiff);
		int penalizedTime = finalTime + ((startPresses - 1) * 1000); // add 1 second for every start button penalty
		int timeScore = (d.timeThresh - penalizedTime);
		timeScore = (timeScore * d.multiplier) / 100;

		// difference between moves made and optimal moves
		int moveScore =
					(
						(
							(500 * d.multiplier) / 100
						) * (
								(minMoves + 1) - moves
							)
					);
		int perfectBonus = (1000 * d.multiplier) / 100;
		int moveBonus = (moves == minMoves) ? perfectBonus : 0; // bonus for being optimal

//		System.out.println("--------------------");
//		System.out.println("Time: " + timeScore);
//		System.out.println("Moves: " + moveScore);
//		System.out.println("Bonus: " + moveBonus);
//		System.out.println("Difficulty: " + d.bonus);
		finalScore = timeScore + moveScore + moveBonus + d.bonus;
		return finalScore;
	}

	public void setPlayerPath(PlayerMovement[] yourMoves) {
		this.yourMoves = yourMoves;
	}

	public void setTurn(int t) {
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
					ITEM_ORIGIN_X + cursorLoc.x,
					ITEM_ORIGIN_Y + cursorLoc.y,
					null);
		}
		cursorLoc = ItemPoint.valueOf("SLOT_" + yourMoves[0].LOCATION);
		g.drawImage(CURSOR,
				ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
				ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
				null);

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