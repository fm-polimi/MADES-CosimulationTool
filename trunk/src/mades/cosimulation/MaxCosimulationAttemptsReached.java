/**
 * 
 */
package mades.cosimulation;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Defines an {@link RuntimeException} indicating that the
 * {@link Cosimulator} has reached the maximum number of re-try
 * without reaching a suitable configuration. As a result of this
 * exception the co-simulation should abort or the {@link Cosimulator}
 * should roll back to the previous state.
 */
public class MaxCosimulationAttemptsReached extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5326148776176144478L;

	/**
	 * Constructor from superclass.
	 */
	public MaxCosimulationAttemptsReached() {
	}

	/**
	 * Constructor from superclass.
	 * @param message the message.
	 */
	public MaxCosimulationAttemptsReached(String message) {
		super(message);
	}

	/**
	 * Constructor from superclass.
	 * @param cause the cause.
	 */
	public MaxCosimulationAttemptsReached(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor from superclass.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public MaxCosimulationAttemptsReached(String message, Throwable cause) {
		super(message, cause);
	}

}
