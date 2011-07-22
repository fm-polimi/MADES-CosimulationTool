/**
 * 
 */
package mades.environment;

import java.util.ArrayList;

import mades.common.timing.Clock;
import mades.common.variables.Transition;
import mades.common.variables.Trigger;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableFactory;
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
	 * @param environmentPath the environment path
	 * @param environmentFileName the model's file name
	 * @param environmentName the model's name
	 * @param clock the timer for this co-simulation
	 * @param variableFactory the variable factory
	 * @param environmentMemento a memento representing the initial state
	 * @param triggers an array containing all the triggers which should be 
	 *         added to the environment.
	 * @return an instance representing the configuration of
	 *         this connector at the initial state.
	 */
	public EnvironmentMemento initialize(
			String environmentPath,
			String environmentFileName,
			String environmentName,
			Clock clock, 
			VariableFactory variableFactory,
			TriggerFactory triggerFactory,
			EnvironmentMemento environmentMemento, ArrayList<Trigger> triggers);
	
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
	 * Performs the next step of the simulation using the time given by the internal clock.
	 * 
	 * @return The result of the simulation.
	 * @throws IllegalArgumentException: If time is negative or lesser 
	 *         than the last simulated step.
	 */
	public EnvironmentMemento simulateNext();
	
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
	public ArrayList<Transition> getTransitions();
}
