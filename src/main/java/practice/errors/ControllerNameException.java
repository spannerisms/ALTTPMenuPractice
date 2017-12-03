package practice.errors;

public class ControllerNameException extends Exception {
	private static final long serialVersionUID = -1013591768067877663L;

	public ControllerNameException(String message) {
		super(message);
	}

	private ControllerNameException() {}
}