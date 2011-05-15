/**
 * 
 */
package mades.environment;

import mades.common.Memento;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the environment by allowing
 * the {@link EnvironmentConnector} to save and restore its state.
 */
public class EnvironmentMemento extends Memento {
	
	/**
	 * Default constructor.
	 */
	EnvironmentMemento () {
	}
	
	@Override
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
