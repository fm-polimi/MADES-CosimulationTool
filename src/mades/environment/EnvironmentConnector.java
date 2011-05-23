/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.Variable;
import mades.cosimulation.Cosimulator;
import mades.system.SystemMemento;

/**
 * 
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Defines the interface between the {@link Cosimulator} and the environment.
 * Implementations of this interface allow the {@link Cosimulator} to initialize
 * the environment simulation tool and to control it during the simulation. 
 */
public interface EnvironmentConnector {

	/**
	 * Initializes the environment simulator with a set of configuration
	 * parameters given by the Cosimulator. This initialization has to be done
	 * once at the beginning of the simulation.
	 * 
	 * @param params a collection of initialization parameters.
	 * @param initialTime the initial time of the simulation.
	 * @return an instance representing the configuration of
	 *         this connector at the initial state.
	 */
	public EnvironmentMemento initialize(ArrayList<Variable> params, double initialTime);
	
	/**
	 * Loads a given initial state to the environment. This method is supposed
	 * to be invoked ad each step of the co-simulation.
	 * 
	 * @param environmentParams the configuration of the environment to use. 
	 *         If <code>null</code> the last simulated step is used.
	 * @param systemParams the system configuration to use.
	 * @throws IllegalArgumentException: If <code>environment</code> is
	 *         <code>null</code> and if no simulation steps have been 
	 *         performed.
	 */
	public void load(EnvironmentMemento environmentParams, SystemMemento systemParams);
	
	/**
	 * Loads a given initial state to the environment. This method is supposed
	 * to be invoked ad each step of the co-simulation.
	 * 
	 * @param systemParams the system configuration to use.
	 * @throws AssertionError if no simulation steps have been 
	 *         performed.
	 */
	public void load(SystemMemento systemParams);
	
	/**
	 * Performs the next step of the simulation.
	 * 
	 * @param time: The time to simulate.
	 * @return The result of the simulation.
	 * @throws IllegalArgumentException: If time is negative or lesser 
	 *         than the last simulated step.
	 */
	public EnvironmentMemento simulateNext(double time);
	
	/**
	 * Returns the value of the current step of the simulation. The returned
	 * value is the same as the last simulated step.
	 * 
	 * @return The result of the last performed step of the simulation. If no
	 *         simulation has been performed it returns an empty collection.
	 * @throws AssertionError: If no simulation step has been performed.
	 */
	public EnvironmentMemento getCurrentParams();
	
	/**
	 * Returns a collection of all the events which have occurred during
	 * the current step of the simulation.  
	 *  
	 * @return A collection of event name, array of times in which the 
	 *         event has occurred.
	 */
	public SignalMap getEventsHistory();
}
