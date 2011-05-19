/**
 * 
 */
package mades.cosimulation;

import mades.common.ParamMap;
import mades.environment.EnvironmentMemento;
import mades.system.SignalMap;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class EchoSystemConnectorMock implements SystemConnector {

	protected double currentSimulationTime;
	protected ParamMap params;
	protected SignalMap signals;
	
	/**
	 * 
	 */
	public EchoSystemConnectorMock() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(mades.common.ParamMap, double)
	 */
	@Override
	public void initialize(ParamMap params, double initialTime) {
		assert(initialTime > 0);
		currentSimulationTime = initialTime;
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemParams,
			EnvironmentMemento environmentParams) {
		assert(environmentParams.getTime() != systemParams.getTime());
		currentSimulationTime = systemParams.getTime();
		params = systemParams.getParams();
		signals = systemParams.getSignals();
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#simulateNext(double)
	 */
	@Override
	public SystemMemento simulateNext(double time) {
		assert(currentSimulationTime < time);
		currentSimulationTime = time;
		return new SystemMemento(currentSimulationTime, params, signals);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getCurrentParams()
	 */
	@Override
	public SystemMemento getCurrentParams() {
		return new SystemMemento(currentSimulationTime, params, signals);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getEventsHistory()
	 */
	@Override
	public SignalMap getEventsHistory() {
		return signals;
	}

}
