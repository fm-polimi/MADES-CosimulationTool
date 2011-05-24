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
		if (this.value > arg0.value) {
			return -1;
		} else if (this.value == arg0.value){
			return 0;
		} else {
			return 1;
		}
	}
}
