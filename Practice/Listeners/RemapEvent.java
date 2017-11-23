package Practice.Listeners;

import java.util.EventObject;

import Practice.Controller;

public class RemapEvent extends EventObject {
	private static final long serialVersionUID = -7276244234710323979L;

	public final Controller map;

	public RemapEvent(Object o, Controller c) {
		super(o);
		this.map = c;
	}
}