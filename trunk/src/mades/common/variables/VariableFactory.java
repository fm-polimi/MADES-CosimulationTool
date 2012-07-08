/**
 * 
 */
package mades.common.variables;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Factory class for {@link VariableDefinition}.
 *
 */
public class VariableFactory {

	private HashMap<String, VariableDefinition> systemDefinedVariables =
			new HashMap<String, VariableDefinition>();
	
	private HashMap<String, VariableDefinition> environmentDefinedVariables =
		new HashMap<String, VariableDefinition>();
	
	/**
	 * Get a variable by name.
	 * 
	 * @param name the variable name
	 * @return a variable with the given name.
	 * @throws AssertionError if a variable with the given name does
	 *         not exist.
	 */
	public VariableDefinition getSystemVar(String name) {
		VariableDefinition var =  systemDefinedVariables.get(name);
		if (var == null) {
			throw new AssertionError(
					"A variable named: " + 
					name + 
					" does not exist.");
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
	public VariableDefinition getEnvironmentVar(String name) {
		VariableDefinition var =  environmentDefinedVariables.get(name);
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
	 * @param systemName the variable's name in the system scope.
	 * @param environmentName the variable's name in the environment scope.
	 * @param scope the desired variable's scope.
	 * @return the new instance of variable.
	 * @throws AssertionError if a variable with the same name
	 *         already exists.
	 */
	public VariableDefinition define(String systemName, String environmentName,
			Scope scope, Type type) {
		if (systemDefinedVariables.containsKey(systemName)) {
			throw new AssertionError(
					"A variable named: " + 
					systemName + 
					" already exist.");
		}
		if (environmentDefinedVariables.containsKey(environmentName)) {
			throw new AssertionError(
					"A variable named: " + 
					environmentName + 
					" already exist.");
		} 
		
		VariableDefinition var = new VariableDefinition(
				systemName, environmentName,
				scope, type);
		systemDefinedVariables.put(systemName, var);
		environmentDefinedVariables.put(environmentName, var);
		return var;
	}
	
	/**
	 * Check if a variable with a given name has been defined.
	 * 
	 * @param name the name to check.
	 * @return <code>true</code> if a variable with the given name already
	 *        exists <code>false</code> otherwise.
	 */
	public boolean isDefinedInSystem(String name) {
		return systemDefinedVariables.containsKey(name);
		// Lisp zot wants upper case variables
		//return systemDefinedVariables.containsKey(name.toUpperCase());
	}
	
	/**
	 * Check if a variable with a given name has been defined.
	 * 
	 * @param name the name to check.
	 * @return <code>true</code> if a variable with the given name already
	 *        exists <code>false</code> otherwise.
	 */
	public boolean isDefinedInEnvironment(String name) {
		return environmentDefinedVariables.containsKey(name);
		// Lisp zot wants upper case variables...
		//return environmentDefinedVariables.containsKey(name.toUpperCase());
	}
	
	/**
	 * Get all the defined variables.
	 * 
	 * @return a collection with all the defined variables.
	 */
	public Collection<VariableDefinition> getDefinedVariables() {
		return systemDefinedVariables.values();
	}
}
