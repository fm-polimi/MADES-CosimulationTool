/**
 * 
 */
package mades.cosimulation;

import java.util.ArrayList;

import com.google.common.collect.TreeMultimap;

import mades.common.Variable;
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

	protected int currentSimulationStep;
	TreeMultimap<Integer, Variable> variablesMultimap;
	protected SignalMap signals;
	
	/**
	 * 
	 */
	public EchoSystemConnectorMock() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(mades.common.ParamMap, double)
	 */
	@Override
	public SystemMemento initialize(ArrayList<Variable> params, int initialStep) {
		assert(initialStep >= 0);
		currentSimulationStep = initialStep;
		variablesMultimap = TreeMultimap.create();
		for (Variable v: params) {
			variablesMultimap.put(initialStep, v);
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
	public SystemMemento simulateNext(int step) {
		assert(currentSimulationStep < step);
		variablesMultimap.putAll(step, variablesMultimap.get(currentSimulationStep));
		currentSimulationStep = step;
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
