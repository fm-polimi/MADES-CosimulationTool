/**
 * 
 */
package mades.common.variables;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class Trigger {
	String variable;
	String signal;
	String threshold;
	double value;
	
	/**
	 * @param variable
	 * @param signal
	 * @param threshold
	 * @param value
	 */
	public Trigger(String variable, String signal, String threshold,
			double value) {
		super();
		this.variable = variable;
		this.signal = signal;
		this.threshold = threshold;
		this.value = value;
	}

	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * @return the trigger
	 */
	public String getSignal() {
		return signal;
	}

	/**
	 * @return the threshold
	 */
	public String getThreshold() {
		return threshold;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	
}
