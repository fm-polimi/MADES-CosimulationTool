/**
 * 
 */
package mades.system.zot;

import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.variables.TriggerFactory;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class ZotSystemConnector implements SystemConnector {

	protected Clock clock;
	protected Logger logger;
	protected ZotWrapper wrapper;
	
	protected SystemMemento systemMemento;
	
	public ZotSystemConnector(Logger logger) {
		this.logger = logger;
	}
	
	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(java.util.ArrayList, int)
	 */
	@Override
	public SystemMemento initialize(
			String systemPath, String systemName, 
			Clock clock,
			VariableFactory variableFactory,
			TriggerFactory triggerFactory,
			SystemMemento systemMemento) {
		this.clock = clock;
		
		wrapper = new ZotWrapper(systemPath, systemName,
				clock, variableFactory, triggerFactory,
				logger);
		systemMemento = wrapper.initialize(systemMemento);
		return systemMemento;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemMemento,
			EnvironmentMemento environmentParams) {
		this.systemMemento = new SystemMemento(systemMemento);
		this.systemMemento.update(environmentParams);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#simulateNext(int)
	 */
	@Override
	public SystemMemento simulateNext() {
		systemMemento = wrapper.executeSimulationStep(clock.getCurrentTime(), systemMemento);
		return systemMemento;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getCurrentParams()
	 */
	@Override
	public SystemMemento getCurrentParams() {
		return systemMemento;
	}

	

}
