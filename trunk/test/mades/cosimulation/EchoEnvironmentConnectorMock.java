/**
 * 
 */
package mades.cosimulation;


import java.util.ArrayList;

import mades.common.timing.Clock;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableFactory;
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
	protected VariableFactory factory;
	protected SignalMap signals;
	protected ArrayList<VariableAssignment> environmentParams;
	
	/**
	 * 
	 */
	public EchoEnvironmentConnectorMock(ArrayList<VariableAssignment> environmentParams) {
		this.environmentParams = environmentParams;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(mades.common.ParamMap, double)
	 */
	@Override
	public EnvironmentMemento initialize(Clock clock, VariableFactory factory) {
		this.clock = clock;
		this.factory = factory;
		signals = new SignalMap();
		return new EnvironmentMemento(clock.getCurrentTime(), environmentParams, signals);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentParams,
			SystemMemento systemParams) {
		assert(clock.getCurrentTime().getSimulationStep() - 1 == environmentParams.getTime().getSimulationStep());
		this.environmentParams = environmentParams.getParams();
		signals = environmentParams.getSignals();
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#simulateNext(double)
	 */
	@Override
	public EnvironmentMemento simulateNext() {
		return new EnvironmentMemento(clock.getCurrentTime(), environmentParams, signals);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#getCurrentParams()
	 */
	@Override
	public EnvironmentMemento getCurrentParams() {
		return new EnvironmentMemento(clock.getCurrentTime(), environmentParams, signals);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getEventsHistory()
	 */
	@Override
	public SignalMap getEventsHistory() {
		return signals;
	}

}
