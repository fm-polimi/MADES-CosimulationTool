/**
 * 
 */
package mades.common;

/**
 * @author rax
 *
 */
public class Variable implements Comparable<Variable>{

	private String name;
	private double value;
	
	private boolean visible;
	
	/**
	 * @param name
	 * @param value
	 * @param visible
	 */
	public Variable(String name, double value, boolean visible) {
		super();
		this.name = name;
		this.value = value;
		this.visible = visible;
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	@Override
	public int compareTo(Variable arg0) {		
		return this.name.compareTo(arg0.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + ": " + value;
	}
}
