/**
 * 
 */
package mades.environment.modelica;

import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentConnector;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Environment connector to use modelica as a simulation tool.
 *
 */
public class ModelicaEnvironmentConnector implements EnvironmentConnector {

	protected Logger logger;
	protected ModelicaWrapper wrapper;
	
	private String environmentPath;
	EnvironmentMemento environmentMemento;
	
	public ModelicaEnvironmentConnector(
			String environmentPath,
			Logger logger
			) {
		this.logger = logger;
		this.environmentPath = environmentPath;
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(java.util.ArrayList, mades.common.timing.Clock)
	 */
	@Override
	public EnvironmentMemento initialize(Clock clock,
			VariableFactory variableFactory, EnvironmentMemento environmentMemento) {
		wrapper = new ModelicaWrapper(environmentPath, clock, variableFactory);
		environmentMemento = wrapper.initialize(environmentMemento);
		return environmentMemento;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentMemento,
			SystemMemento systemParams) {
		this.environmentMemento = new EnvironmentMemento(environmentMemento);
		this.environmentMemento.update(systemParams);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#simulateNext()
	 */
	@Override
	public EnvironmentMemento simulateNext() {
		environmentMemento = wrapper.simulateNext(environmentMemento);
		return environmentMemento;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#getCurrentParams()
	 */
	@Override
	public EnvironmentMemento getCurrentParams() {
		return environmentMemento;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#getEventsHistory()
	 */
	@Override
	public SignalMap getEventsHistory() {
		return environmentMemento.getSignals();
	}

}
