/**
 * 
 */
package mades.system.zot;

import java.util.ArrayList;

import mades.common.Variable;
import mades.environment.EnvironmentMemento;
import mades.system.SystemConnector;
import mades.system.SystemMemento;

/**
 * @author rax
 *
 */
public class ZotSystemConnector implements SystemConnector {

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#initialize(java.util.ArrayList, double)
	 */
	@Override
	public SystemMemento initialize(ArrayList<Variable> params,
			double initialTime) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#load(mades.system.SystemMemento, mades.environment.EnvironmentMemento)
	 */
	@Override
	public void load(SystemMemento systemParams,
			EnvironmentMemento environmentParams) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#simulateNext(double)
	 */
	@Override
	public SystemMemento simulateNext(double time) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mades.system.SystemConnector#getCurrentParams()
	 */
	@Override
	public SystemMemento getCurrentParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
