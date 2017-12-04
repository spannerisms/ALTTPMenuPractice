package practice.controls;

public class ControllerException extends Exception {
	private static final long serialVersionUID = -1013591768067877663L;

	public ControllerException(String message) {
		super(message);
	}

	private ControllerException() {}
}