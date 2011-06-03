/**
 * 
 */
package mades.environment.modelica;

import java.util.ArrayList;
import java.util.logging.Logger;

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
 * Environment connector to use modelica as a simulation tool.
 *
 */
public class ModelicaEnvironmentConnector implements EnvironmentConnector {

	protected Logger logger;
	protected ModelicaWrapper wrapper;
	protected VariableFactory variableFactory;
	
	private String environmentPath;
	private String environmentName;
	EnvironmentMemento environmentMemento;
	
	public ModelicaEnvironmentConnector(
			String environmentPath,
			String environmentName,
			Logger logger,
			VariableFactory variableFactory) {
		this.variableFactory = variableFactory;
		this.environmentPath = environmentPath;
		this.environmentName = environmentName;
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(java.util.ArrayList, mades.common.timing.Clock)
	 */
	@Override
	public EnvironmentMemento initialize(ArrayList<VariableAssignment> params,
			Clock clock) {
		wrapper = new ModelicaWrapper(environmentPath, environmentName, clock);
		environmentMemento = wrapper.initFromFile(variableFactory);
		environmentMemento.update(params);
		return environmentMemento;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentMemento,
			SystemMemento systemParams) {
		this.environmentMemento = environmentMemento;
		this.environmentMemento.update(systemParams);
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#simulateNext()
	 */
	@Override
	public EnvironmentMemento simulateNext() {
		return wrapper.simulateNext(environmentMemento);
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
