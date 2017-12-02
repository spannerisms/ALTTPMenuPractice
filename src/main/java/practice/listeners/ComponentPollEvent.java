package practice.listeners;

import java.util.EventObject;
import net.java.games.input.Component;

public class ComponentPollEvent extends EventObject {
	private static final long serialVersionUID = 7881080520791052443L;

	public final Component comp;

	public ComponentPollEvent(Object o, Component comp) {
		super(o);
		this.comp = comp;
	}
}