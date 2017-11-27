package Practice.GUI;

import Practice.Listeners.SNESInputEvent;

public interface SNESControllable {
	default void addToController(ControllerHandler c) {
		c.addChild(this);
	};

	default void removeFromController(ControllerHandler c) {
		c.removeChild(this);
	};

	/**
	 * Function should add SNES control behavior to itself
	 */
	void addSNESInput();

	// 
	void fireSNESInputEvent(SNESInputEvent e);

	void whineToMommy();

	void shutUp();
}
