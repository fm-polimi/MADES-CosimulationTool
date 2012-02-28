/**
 * 
 */
package mades.common.variables;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class TriggerFactory {
	
	private ArrayListMultimap<String, Trigger> definedTriggers =
			ArrayListMultimap.create();

	public Trigger getOrDefine(String variable, String signal, 
			String threshold, Scope scope, double value) {
		List<Trigger> triggers = definedTriggers.get(variable);
		Trigger t = null;
		// Check if one of the existing triggers matches
		for(Trigger tx: triggers) {
			if (tx.getSignal().equals(signal) &&
					tx.getThreshold().equals(threshold) &&
					tx.getScope().equals(scope) &&
					tx.getValue().equals(value)) {
				t = tx;
				break;
			}
		}
		
		// Define a new trigger if none of the existing matches
		if (t == null) {
			t = new Trigger(variable, signal, threshold,
					scope, value);
			definedTriggers.put(variable, t);
		}
		
		return t;
	}
	
	public List<Trigger> get(String variable) {
		List<Trigger> triggers = definedTriggers.get(variable);
		if (triggers.isEmpty()) {
			throw new AssertionError("A trigger for variable: " +
					variable + " is not defined.");
		}
		return triggers;
	}
}
