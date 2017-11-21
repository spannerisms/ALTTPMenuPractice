package Practice.Listeners;

import java.util.EventObject;

public class GameOverEvent extends EventObject {
	private static final long serialVersionUID = -2999263736122009428L;

	public GameOverEvent(Object o) {
		super(o);
	}
}