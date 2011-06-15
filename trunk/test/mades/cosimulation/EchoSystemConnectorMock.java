/**
 * 
 */
package mades.cosimulation;

import java.util.ArrayList;

import com.google.common.collect.TreeMultimap;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableFactory;
import mades.environment.EnvironmentMemento;
import mades.environment.SignalMap;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements an {@link SystemConnector} which returns in output
 * the same parameters it had in input.
 */
public class EchoSystemConnectorMock implements SystemConnector {

	protected Clock clock;
	TreeMultimap<Time, VariableAssignment> variablesMultimap;
	protected SignalMap signals;
	protected ArrayList<VariableAssignment> params;
	
	/**
	 * 
	 */
	public EchoSystemConnectorMock(ArrayList<VariableAssignment> params) {
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(mades.common.ParamMap, double)
	 */
	@Override
	public SystemMemento initialize(Clock clock, VariableFactory factory) {
		assert(clock.getCurrentTime().getSimulationStep() == 0);
		this.clock = clock;
		variablesMultimap = TreeMultimap.create();
		for (VariableAssignment v: params) {
			variablesMultimap.put(clock.getCurrentTime(), v);
		}
		
		return new SystemMemento(variablesMultimap);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemParams,
			EnvironmentMemento environmentParams) {
		variablesMultimap = TreeMultimap.create(systemParams.getVariablesMultimap());
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#simulateNext(double)
	 */
	@Override
	public SystemMemento simulateNext() {
		variablesMultimap.putAll(clock.getCurrentTime(), params);
		return new SystemMemento(variablesMultimap);
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getCurrentParams()
	 */
	@Override
	public SystemMemento getCurrentParams() {
		return new SystemMemento(variablesMultimap);
	}


}
