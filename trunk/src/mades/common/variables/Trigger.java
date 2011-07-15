/**
 * 
 */
package mades.common.variables;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class Trigger {
	String variableName;
	VariableDefinition variable;
	
	String signalName;
	VariableDefinition signal; 
	
	String thresholdName;
	VariableDefinition threshold;
	
	Scope scope;
	double value;
	
	/**
	 * @param variable
	 * @param signal
	 * @param threshold
	 * @param value
	 */
	public Trigger(String variable, String signal, String threshold,
			Scope scope, double value) {
		this.variableName = variable;
		this.signalName = signal;
		this.thresholdName = threshold;
		this.value = value;
		if (scope == Scope.ENVIRONMENT_INTERNAL ||
				scope == Scope.SYSTEM_INTERNAL) {
			this.scope = scope;
		}
	}

	/**
	 * @return the variable
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @return the trigger
	 */
	public String getSignalName() {
		return signalName;
	}

	/**
	 * @return the threshold
	 */
	public String getThresholdName() {
		return thresholdName;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @return the variable
	 */
	public VariableDefinition getVariable() {
		return variable;
	}

	/**
	 * @param variable the variable to set
	 */
	public void setVariable(VariableDefinition variable) {
		this.variable = variable;
	}

	/**
	 * @return the signal
	 */
	public VariableDefinition getSignal() {
		return signal;
	}

	/**
	 * @param signal the signal to set
	 */
	public void setSignal(VariableDefinition signal) {
		this.signal = signal;
	}

	/**
	 * @return the threshold
	 */
	public VariableDefinition getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(VariableDefinition threshold) {
		this.threshold = threshold;
	}
	
}
