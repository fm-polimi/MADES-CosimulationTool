/**
 * 
 */
package mades.common.variables;

import java.util.HashMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class TriggerFactory {
	
	private HashMap<String, Trigger> definedTriggers =
		new HashMap<String, Trigger>();


	public Trigger getOrDefine(String variable, String signal, 
			String threshold, Scope scope, double value) {
		Trigger t = definedTriggers.get(variable);
		if (t == null) {
			t = new Trigger(variable, signal, threshold,
					scope, value);
			definedTriggers.put(variable, t);
		}
		return t;
	}
	
	public Trigger get(String variable) {
		Trigger t = definedTriggers.get(variable);
		if (t == null) {
			throw new AssertionError("A trigger for variable: " +
					variable + " is not defined.");
		}
		return t;
	}
}
