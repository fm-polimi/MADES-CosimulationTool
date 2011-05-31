/**
 * 
 */
package mades.cosimulation;


import java.util.ArrayList;

import mades.common.timing.Clock;
import mades.common.variables.VariableAssignment;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements an {@link EnvironmentConnector} which returns in output
 * the same parameters it had in input.
 */
public class EchoEnvironmentConnectorMock implements EnvironmentConnector {
	
	protected Clock clock;
	protected ArrayList<VariableAssignment> params;
	protected SignalMap signals;
	
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
	public EnvironmentMemento initialize(ArrayList<VariableAssignment> params, Clock clock) {
		this.clock = clock;
		this.params = params;
		signals = new SignalMap();
		return new EnvironmentMemento(clock.getCurrentTime(), params, signals);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentParams,
			SystemMemento systemParams) {
		assert(clock.getCurrentTime().getSimulationStep() - 1 == environmentParams.getTime().getSimulationStep());
		params = environmentParams.getParams();
		signals = environmentParams.getSignals();
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#simulateNext(double)
	 */
	@Override
	public EnvironmentMemento simulateNext() {
		return new EnvironmentMemento(clock.getCurrentTime(), params, signals);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#getCurrentParams()
	 */
	@Override
	public EnvironmentMemento getCurrentParams() {
		return new EnvironmentMemento(clock.getCurrentTime(), params, signals);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getEventsHistory()
	 */
	@Override
	public SignalMap getEventsHistory() {
		return signals;
	}

}
