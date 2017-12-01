package practice.controls;

public enum DirectionType {
	DPAD (DPadHandler.class),
	HAT (HatSwitchHandler.class);

	public final Class<?> handler;
	private DirectionType(Class<?> c) {
		handler = c;
	}
}