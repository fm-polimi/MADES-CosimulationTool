/**
 * 
 */
package mades.common.variables;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class Trigger {

	VariableAssignment variable;
	VariableAssignment threshold;
	VariableAssignment signal;

	/**
	 * @param variable
	 * @param threshold
	 * @param trigger
	 */
	public Trigger(VariableAssignment variable,
			VariableAssignment threshold, VariableAssignment trigger) {
		this.variable = variable;
		this.threshold = threshold;
		this.signal = trigger;
	}

	/**
	 * @return the variable
	 */
	public VariableAssignment getVariable() {
		return variable;
	}

	/**
	 * @return the threshold
	 */
	public VariableAssignment getThreshold() {
		return threshold;
	}

	/**
	 * @return the trigger
	 */
	public VariableAssignment getSignal() {
		return signal;
	}
}
