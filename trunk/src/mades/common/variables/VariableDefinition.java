/**
 * 
 */
package mades.common.variables;


/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Defines a new variable. New instances of this class
 * can only be obtained by calling 
 * {@link VariableFactory#defineOrGet(String)}
 * 
 */
public class VariableDefinition implements Comparable<VariableDefinition>{
	private String name;
	private Scope scope;
	private boolean bool;
	
	/**
	 * Defines a new variable. 
	 * 
	 * @param name
	 * @param scope
	 */
	protected VariableDefinition(String name, Scope scope, boolean bool) {
		this.name = name;
		this.scope = scope;
		this.bool = bool;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VariableDefinition arg0) {
		return this.name.compareTo(arg0.name);
	}

	/**
	 * @return the variable's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the variable's scope.
	 */
	public Scope getScope() {
		return scope;
	}

	public boolean isBoolean() {
		return bool;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
