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
	private double value;
	
	/**
	 * @param name
	 * @param value
	 * @param visible
	 */
	public VariableAssignment(VariableDefinition definition, 
			double value) {
		this.definition = definition;
		this.value = value;
	}

	/**
	 * Gets the value of this assignment
	 * 
	 * @return the value of this assignment.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Reassign this value.
	 * 
	 * @param value
	 * @throws AssertionError if a boolean variable is not assigned 0 or 1.
	 */
	public void setValue(double value) {
		if (getVariableDefinition().isBoolean() && value != 0 && value != 1) {
			throw new AssertionError("Boolean variable " + 
					getVariableDefinition().getName() +
					" can only be assigned with 0 or 1: found " + 
					value +".");
		}
		this.value = value;
	}
	
	/**
	 * Reassign this value.
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		if (!getVariableDefinition().isBoolean()) {
			throw new AssertionError("This variable is not boolean.");
		}
		this.value = value?1:0;
	}

	/**
	 * @return the variable definition of this assignment.
	 */
	public VariableDefinition getVariableDefinition() {
		return definition;
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
}
