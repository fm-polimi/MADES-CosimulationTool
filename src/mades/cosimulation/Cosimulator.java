/**
 * 
 */
package mades.cosimulation;

import java.util.HashMap;

import mades.environment.EnvironmentConnector;
import mades.system.SystemConnector;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Defines the main controller for the co-simulation. The cosimulator
 * is responsible for initializing the system and the environment and
 * for supervising the simulation. 
 *
 */
public class Cosimulator {

	private boolean simulationStarted;
	
	/**
	 * The environment simulator used in this co-simulation.
	 */
	private EnvironmentConnector environment;
	
	/**
	 * The system simulator used in this co-simulation.
	 */
	private SystemConnector system;
	
	/**
	 * Default constructor.
	 */
	public Cosimulator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the current {@link EnvironmentConnector}.
	 * 
	 * @return the environment
	 */
	public EnvironmentConnector getEnvironment() {
		return environment;
	}

	/**
	 * Sets a new {@link EnvironmentConnector}.
	 * 
	 * @param environment the environment to set
	 */
	public void setEnvironment(EnvironmentConnector environment) {
		this.environment = environment;
	}

	/**
	 * Gets the current {@link SystemConnector}.
	 * 
	 * @return the system
	 */
	public SystemConnector getSystem() {
		return system;
	}

	/**
	 * Sets a new {@link SystemConnector}.
	 * 
	 * @param system the system to set
	 */
	public void setSystem(SystemConnector system) {
		this.system = system;
	}
	
	/**
	 * Gets if the co-simulation is running.
	 * 
	 * @return <code>true</code> if the co-simulation is already running,
	 *         <code>false</code> otherwise.
	 */
	public boolean isSimulationStarted() {
		return simulationStarted;
	}

	/**
	 * Starts a new co-simulation.
	 * 
	 * @param params the parameters for this co-simulation. Parameters will be 
	 *         forwarded both to the {@link SystemConnector} and to the 
	 *         {@link EnvironmentConnector}.
	 * @param initialTime the initial time of this simulation.
	 * @throws IllegalStateException if the simulation is already running.
	 * @throws AssertionError if either the {@link EnvironmentConnector} or the
	 *         {@link SystemConnector} are <code>null</code> or not properly initialized.
	 */
	public void startCosimulation(HashMap<String, String> params, double initialTime) {
		if (simulationStarted) {
			throw new IllegalStateException("Simulation is already running"); 
		}
		if (environment == null) {
			throw new AssertionError("EnvironmentConnector cannot be null.");
		}
		if (system == null) {
			throw new AssertionError("SystemConnector cannot be null.");
		}
		environment.initialize(params, initialTime);
		system.initialize(params, initialTime);
		simulationStarted = true;
	}

	/**
	 * Stops the current simulation.
	 */
	public void stopCosimulation() {
		if (!simulationStarted) {
			throw new IllegalStateException("Simulation is not running"); 
		}
		simulationStarted = false;
	}

}
