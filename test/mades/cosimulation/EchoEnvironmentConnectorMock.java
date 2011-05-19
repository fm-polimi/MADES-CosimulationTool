/**
 * 
 */
package mades.cosimulation;

import mades.common.ParamMap;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements an {@link EnvironmentConnector} which returns in output
 * the same parameters it had in input.
 */
public class EchoEnvironmentConnectorMock implements EnvironmentConnector {

	protected double currentSimulationTime;
	protected ParamMap params;
	
	/**
	 * 
	 */
	public EchoEnvironmentConnectorMock() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(mades.common.ParamMap, double)
	 */
	@Override
	public void initialize(ParamMap params, double initialTime) {
		assert(initialTime > 0);
		currentSimulationTime = initialTime;
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentParams,
			SystemMemento systemParams) {
		assert(environmentParams.getTime() != systemParams.getTime());
		currentSimulationTime = environmentParams.getTime();
		params = environmentParams.getParams();
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.system.SystemMemento)
	 */
	@Override
	public void load(SystemMemento systemParams) {
		assert(currentSimulationTime < systemParams.getTime());
		currentSimulationTime = systemParams.getTime();
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#simulateNext(double)
	 */
	@Override
	public EnvironmentMemento simulateNext(double time) {
		assert(currentSimulationTime < time);
		currentSimulationTime = time;
		return new EnvironmentMemento(currentSimulationTime, params);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#getCurrentParams()
	 */
	@Override
	public EnvironmentMemento getCurrentParams() {
		return new EnvironmentMemento(currentSimulationTime, params);
	}

}
