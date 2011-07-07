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
	private String systemName;
	private String environmentName;
	private Scope scope;
	private Type type;
	
	/**
	 * Defines a new variable. 
	 * 
	 * @param name
	 * @param scope
	 */
	protected VariableDefinition(String systemName, String environmentName,
			Scope scope, Type type) {
		this.systemName = systemName;
		this.environmentName = environmentName;
		this.scope = scope;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VariableDefinition arg0) {
		return this.systemName.compareTo(arg0.systemName);
	}

	/**
	 * @return the variable's scope.
	 */
	public Scope getScope() {
		return scope;
	}

	public Type getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return systemName;
	}

	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return systemName;
	}

	/**
	 * @return the environmentName
	 */
	public String getEnvironmentName() {
		return environmentName;
	}
}
