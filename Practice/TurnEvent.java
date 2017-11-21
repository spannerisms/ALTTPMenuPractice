package Practice;

import java.util.EventObject;

public class TurnEvent extends EventObject {
	private static final long serialVersionUID = -1486020225726052678L;

	public final ScoreCard score;

	public TurnEvent(Object o, ScoreCard score) {
		super(o);
		this.score = score;
	}
}