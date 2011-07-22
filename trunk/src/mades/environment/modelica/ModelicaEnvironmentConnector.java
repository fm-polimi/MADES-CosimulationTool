/**
 * 
 */
package mades.environment.modelica;

import java.util.ArrayList;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.variables.Transition;
import mades.common.variables.Trigger;
import mades.common.variables.TriggerFactory;
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

	EnvironmentMemento environmentMemento;
	
	public ModelicaEnvironmentConnector(Logger logger) {
		this.logger = logger;
	}
	
	/* (non-Javadoc)
	 * @see mades.environment.EnvironmentConnector#initialize(java.util.ArrayList, mades.common.timing.Clock)
	 */
	@Override
	public EnvironmentMemento initialize(
			String environmentPath,
			String environmentFileName,
			String environmentName,
			Clock clock,
			VariableFactory variableFactory, 
			TriggerFactory triggerFactory,
			EnvironmentMemento environmentMemento,
			ArrayList<Trigger> triggers) {
		wrapper = new ModelicaWrapper(
				environmentPath, environmentFileName, environmentName, 
				clock, variableFactory, triggerFactory, 
				triggers);
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
	public ArrayList<Transition> getTransitions() {
		return environmentMemento.getTransitions();
	}

}
