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
	
	EnvironmentMemento environmentMemento;
	
	public ModelicaEnvironmentConnector(
			String environmentPath,
			String environmentName,
			Logger logger,
			Clock clock,
			VariableFactory variableFactory) {
		wrapper = new ModelicaWrapper(environmentPath, environmentName, clock);
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(java.util.ArrayList, mades.common.timing.Clock)
	 */
	@Override
	public EnvironmentMemento initialize(ArrayList<VariableAssignment> params,
			Clock clock) {
		environmentMemento = wrapper.initFromFile(variableFactory);
		// TODO(rax): merge the loaded variable with params
		return environmentMemento;
	}

	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#load(mades.environment.EnvironmentMemento, mades.system.SystemMemento)
	 */
	@Override
	public void load(EnvironmentMemento environmentMemento,
			SystemMemento systemParams) {
		this.environmentMemento = environmentMemento;
		// TODO(rax) update the shared variables
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
