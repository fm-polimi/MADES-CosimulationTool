/**
 * 
 */
package mades.system;

import mades.common.ParamMap;
import mades.cosimulation.Cosimulator;
import mades.environment.EnvironmentMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Defines the interface to connect the system simulator to the 
 * {@link Cosimulator}. Implementation of this interface allows the 
 * {@link SystemConnector} to initialize and control the simulated
 * system during the simulation.
 */
public interface SystemConnector {

	/**
	 * Initializes the system simulator with a set of configuration
	 * parameters given by the Cosimulator. This initialization has to be done
	 * once and only once.
	 * 
	 * @param params: A collection of key,value initialization parameters.
	 * @param initialTime the initial time of the simulation.
	 */
	public void initialize(ParamMap params, double initialTime);
	
	/**
	 * Loads a given initial state to the system. This method is supposed
	 * to be invoked ad each step of the co-simulation.
	 * 
	 * @param systemParams: The initial configuration of the system. 
	 *         If <code>null</code> the last simulated step is used.
	 * @throws IllegalArgumentException: If <code>system</code> is
	 *         <code>null</code> and if no simulation steps have been 
	 *         performed.
	 */
	public void load(SystemMemento systemParams, EnvironmentMemento environmentParams);
	
	/**
	 * Performs the next step of the simulation.
	 * 
	 * @param time: The time to simulate.
	 * @return The result of the simulation.
	 * @throws IllegalArgumentException: If time is negative or lesser 
	 *         than the last simulated step.
	 */
	public SystemMemento simulateNext(double time);
	
	/**
	 * Returns the value of the current step of the system. The returned
	 * value is the same as the last simulated step.
	 * 
	 * @return The result of the last performed step of the simulation. If no
	 *         simulation has been performed it returns an empty collection.
	 * @throws AssertionError: If no simulation step has been performed.
	 */
	public SystemMemento getCurrentParams();
	
	/**
	 * Returns a collection of all the events which have occurred during
	 * the current step of the simulation.  
	 *  
	 * @return A collection of event name, array of times in which the 
	 *         event has occurred.
	 */
	public SignalMap getEventsHistory();
	
}
