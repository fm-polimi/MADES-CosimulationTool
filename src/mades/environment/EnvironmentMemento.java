/**
 * 
 */
package mades.environment;

import mades.common.Memento;
import mades.common.ParamMap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento extends Memento {
	
	
	/**
	 * @param time
	 * @param params
	 */
	public EnvironmentMemento(double time, ParamMap params) {
		super(time, params);
	}

	/* (non-Javadoc)
	 * @see mades.common.Memento#deleteRelatedFiles()
	 */
	@Override
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
