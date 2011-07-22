/**
 * 
 */
package mades.common.variables;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Identifies a monitored transition of a trigger.
 */
public class Transition {
	
	private Trigger trigger;
	private double time;
	private boolean upDown;
	
	/**
	 * @param trigger
	 * @param time
	 * @param step
	 * @param upDown
	 */
	public Transition(Trigger trigger, double time, boolean upDown) {
		super();
		this.trigger = trigger;
		this.time = time;
		this.upDown = upDown;
	}
	
	/**
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @return the upDown
	 */
	public boolean isUpDown() {
		return upDown;
	}
	
	/**
	 * @return the upDown negated
	 */
	public boolean isDownUp() {
		return !upDown;
	}
}
