/**
 * 
 */
package mades.common.variables;

import java.util.ArrayList;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class Trigger {
	private String variableName;
	private VariableDefinition variable;
	
	private String signalName;
	private VariableDefinition signal; 
	
	private String thresholdName;
	private VariableDefinition threshold;
	
	private Scope scope;
	private double value;
	
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	/**
	 * @param variable
	 * @param signal
	 * @param threshold
	 * @param value
	 */
	protected Trigger(String variable, String signal, String threshold,
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
	
	public Transition addTransition(double time, boolean upDown) {
		Transition t = new Transition(this, time, upDown);
		transitions.add(t);
		return t;
	}
	
	public boolean validate(double minSignalDelta) {
		for (int i = transitions.size() - 1; i > 1; i--) {
			Transition end = transitions.get(i);
			Transition begin = transitions.get(i - 1);
			
			if ((end.getTime() - begin.getTime()) < minSignalDelta) {
				return false;
			}
		}
		return true;
	}
	
	public Transition getLatestTransition() {
		// If this trigger has no transition, yet, null is returned
		if (transitions.isEmpty()) {
			return null;
		}
		return transitions.get(transitions.size() - 1);
	}
	
	public boolean removeTransition(Transition t) {
		return transitions.remove(t);
	}
}
