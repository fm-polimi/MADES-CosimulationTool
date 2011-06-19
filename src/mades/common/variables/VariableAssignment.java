/**
 * 
 */
package mades.common.variables;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Defines a variable assignment.
 *
 */
public class VariableAssignment implements Comparable<VariableAssignment>{

	private VariableDefinition definition;
	private String value;
	private String annotation;
	
	/**
	 * @param name
	 * @param value
	 * @param visible
	 */
	public VariableAssignment(VariableDefinition definition, 
			String value, String annotation) {
		if (definition == null) {
			throw new AssertionError("Variable definition cannot be null.");
		}
		if (value == null) {
			throw new AssertionError("Variable value cannot be null.");
		}
		if (annotation == null) {
			throw new AssertionError("Variable annotation cannot be null.");
		}
		this.definition = definition;
		this.value = value;
		this.annotation = annotation;
	}

	/**
	 * @return the variable definition of this assignment.
	 */
	public VariableDefinition getVariableDefinition() {
		return definition;
	}

	/**
	 * Gets the value of this assignment
	 * 
	 * @return the value of this assignment.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VariableAssignment arg0) {		
		return this.definition.compareTo(arg0.definition);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return definition.getScope() + " " + 
				definition.getName() + ": " +
				value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public VariableAssignment clone() {
		return new VariableAssignment(definition, value, annotation);
	}

}
