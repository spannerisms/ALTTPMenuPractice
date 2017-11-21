package Practice;

class ScoreCard {
	int startPresses;
	int moves;
	final long startTime;
	long endTime;
	int finalScore;
	int finalTime;
	final int minMoves;

	public ScoreCard(int minMoves) {
		this.minMoves = minMoves;
		startPresses = 0;
		moves = 0;
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

		finalScore = timeScore + diffScore + moveBonus - startPenalty;
		return finalScore;
	}
}