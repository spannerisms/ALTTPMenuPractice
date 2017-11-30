package practice.gui;

import java.util.TimerTask;

public class OpTask extends TimerTask {

	private final Task op;

	public OpTask(Task op) {
		this.op = op;
	}

	public void run() {
		op.doThing();
	}
}