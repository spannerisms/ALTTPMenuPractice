package practice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static practice.MenuGameConstants.*;

public class PlayerMovement {
	final int location;
	final int movement;

	PlayerMovement(int loc, int dir) {
		location = loc;
		movement = dir;
	}

	public static BufferedImage drawOptimalPath(PlayerMovement[] moves, boolean includeGoal) {
		BufferedImage ret = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = ret.getGraphics();
		int size = moves.length + (includeGoal ? 0 : -1);
		for (int i = 0; i < size; i++) {
			PlayerMovement p = moves[i];
			ItemPoint cursorLoc = ItemPoint.valueOf("SLOT_" + p.location);
			g.drawImage(OPTIMAL_MOVES[p.movement],
					ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
					ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
					null);
		}
		return ret;
	}

	public static BufferedImage drawPlayerPath(PlayerMovement[] moves) {
		BufferedImage ret = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = ret.getGraphics();
		int size = moves.length;
		for (int i = 0; i < size; i++) {
			PlayerMovement p = moves[i];
			ItemPoint cursorLoc = ItemPoint.valueOf("SLOT_" + p.location);
			g.drawImage(PLAYER_MOVES[p.movement],
					ITEM_ORIGIN_X + cursorLoc.x - CURSOR_OFFSET,
					ITEM_ORIGIN_Y + cursorLoc.y - CURSOR_OFFSET,
					null);
		}
		return ret;
	}
}