/**
 * 
 */
package mades.common.variables;

import java.util.WeakHashMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Factory class for {@link VariableDefinition}.
 *
 */
public class VariableFactory {

	private WeakHashMap<String, VariableDefinition> definedVariables =
			new WeakHashMap<String, VariableDefinition>();
	
	/**
	 * Get an already defined variable by name or defines a new one if
	 * it does not exists.
	 * 
	 * @param name the variable name.
	 * @param scope the variable scope.
	 * @return a variable with the given name.
	 * @throws AssertionError if a variable with the given name already
	 *         exists but with a different scope.
	 */
	public VariableDefinition getOrDefine(String name, Scope scope) {
		VariableDefinition var = definedVariables.get(name);
		if (var == null) {
			var = new VariableDefinition(name, scope);
			definedVariables.put(name, var);
		} else {
			if (var.getScope() != scope) {
				throw new AssertionError(
						"A variable named: " + 
						name + 
						" already exists but with a different scope.");
			}
		}
		return var;
	}
	
	/**
	 * Get a variable by name.
	 * 
	 * @param name the variable name
	 * @return a variable with the given name.
	 * @throws AssertionError if a variable with the given name does
	 *         not exist.
	 */
	public VariableDefinition get(String name) {
		VariableDefinition var =  definedVariables.get(name);
		if (var == null) {
			throw new AssertionError(
					"A variable named: " + 
					name + 
					" does not exist.");
		}
		return var;
	} 
	
	/**
	 * Defines a new variable.
	 * 
	 * @param name the desired variable's name.
	 * @param scope the desired variable's scope.
	 * @return the new instance of variable.
	 * @throws AssertionError if a variable with the same name
	 *         already exists.
	 */
	public VariableDefinition define(String name, Scope scope) {
		if (definedVariables.containsKey(name)) {
			throw new AssertionError(
					"A variable named: " + 
					name + 
					" does not exist.");
		} 
		
		VariableDefinition var = new VariableDefinition(name, scope);
		definedVariables.put(name, var);
		return var;
	}
	
	/**
	 * Check if a variable with a given name has been defined.
	 * 
	 * @param name the name to check.
	 * @return <code>true</code> if a variable with the given name already
	 *        exists <code>false</code> otherwise.
	 */
	public boolean isDefined(String name) {
		return definedVariables.containsKey(name);
	}
}
