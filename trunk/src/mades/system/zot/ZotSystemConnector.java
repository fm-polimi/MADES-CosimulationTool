/**
 * 
 */
package mades.system.zot;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.common.collect.TreeMultimap;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
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
	protected VariableFactory variableFactory;
	protected ZotWrapper wrapper;
	
	protected SystemMemento systemMemento;
	
	private String engineFileName;
	private String systemFileName;
	private String initialVariablesFileName;
	private int maxSimulationStep;
	private ArrayList<VariableDefinition> systemVariables;
	
	public ZotSystemConnector(String engineFileName, String systemFileName,
			String initialVariablesFileName, int maxSimulationStep,
			VariableFactory variableFactory,
			ArrayList<VariableDefinition> variables,
			Logger logger) {
		this.variableFactory = variableFactory;
		this.logger = logger;
		
		this.engineFileName = engineFileName;
		this.systemFileName = systemFileName;
		this.initialVariablesFileName = initialVariablesFileName;
		this.maxSimulationStep = maxSimulationStep;
		this.systemVariables = variables;
	}
	
	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(java.util.ArrayList, int)
	 */
	@Override
	public SystemMemento initialize(ArrayList<VariableAssignment> params, Clock clock) {
		Time time = clock.getCurrentTime();
		
		this.clock = clock;
		wrapper = new ZotWrapper(engineFileName,
				systemFileName, initialVariablesFileName, 
				maxSimulationStep, clock, variableFactory,
				systemVariables);
		
		TreeMultimap<Time, VariableAssignment> variablesMultimap = TreeMultimap.create();
		for (VariableAssignment v: params) {
			variablesMultimap.put(time, v);
		}
		systemMemento = new SystemMemento(variablesMultimap);
		systemMemento = wrapper.executeSimulationStep(clock.getCurrentTime(), systemMemento);
		return systemMemento;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemMemento,
			EnvironmentMemento environmentParams) {
		this.systemMemento = systemMemento;
		// TODO(rax): update shared variables
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
