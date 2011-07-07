/**
 * 
 */
package mades.system.zot;

import java.util.logging.Logger;

import mades.common.timing.Clock;
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
	
	private String path;
	private int maxSimulationStep;
	
	public ZotSystemConnector(String path, int maxSimulationStep,
			Logger logger) {
		this.logger = logger;
		
		this.path = path;
		this.maxSimulationStep = maxSimulationStep;
	}
	
	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(java.util.ArrayList, int)
	 */
	@Override
	public SystemMemento initialize(Clock clock,
			VariableFactory variableFactory, SystemMemento systemMemento) {
		this.clock = clock;
		wrapper = new ZotWrapper(path, 
				maxSimulationStep, clock, variableFactory,
				logger);
		wrapper.initialize(systemMemento);
		// TODO(rax) shall we do other initialization steps?
		/*
		ArrayList<VariableAssignment> variables = wrapper.parseInit();
		TreeMultimap<Time, VariableAssignment> variablesMultimap = TreeMultimap.create();
		for (VariableAssignment v: variables) {
			variablesMultimap.put(clock.getCurrentTime(), v);
		}
		systemMemento = new SystemMemento(variablesMultimap);
		//systemMemento = wrapper.executeSimulationStep(clock.getCurrentTime(), systemMemento);
		*/
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
