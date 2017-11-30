package practice.listeners;

import java.util.EventObject;
import practice.controls.ControllerHandler;

public class RemapEvent extends EventObject {
	private static final long serialVersionUID = -7276244234710323979L;

	public final ControllerHandler map;

	public RemapEvent(Object o, ControllerHandler c) {
		super(o);
		this.map = c;
	}
}