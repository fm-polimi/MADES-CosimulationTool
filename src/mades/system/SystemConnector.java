/**
 * 
 */
package mades.system;

import mades.common.timing.Clock;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableFactory;
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
	 * @param systemPath the folder containing systems files
	 * @param systemName the system name
	 * @param clock the timer for this co-simulation.
	 * @param variableFactory the variable factory.
	 * @param systemMemento a memento representing the initial state.
	 * @return an instance representing the configuration of
	 *         this connector at the initial state.
	 */
	public SystemMemento initialize(String systemPath, String systemName, 
			Clock clock, VariableFactory variableFactory,
			TriggerFactory triggerFactory,
			SystemMemento systemMemento);
	
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
	 * Save the current state in a stack.
	 */
	public void push();
	
	
	/**
	 * Remove the current state from the top of the stack.
	 */
	public void pop();
	
	/**
	 * Performs the next step of the simulation using the time of the internal clock.
	 * 
	 * @return The result of the simulation.
	 * @throws IllegalArgumentException: If time is negative or lesser 
	 *         than the last simulated step.
	 */
	public SystemMemento simulateNext();
	
	/**
	 * Returns the value of the current step of the system. The returned
	 * value is the same as the last simulated step.
	 * 
	 * @return The result of the last performed step of the simulation. If no
	 *         simulation has been performed it returns an empty collection.
	 * @throws AssertionError: If no simulation step has been performed.
	 */
	public SystemMemento getCurrentParams();
	
}
