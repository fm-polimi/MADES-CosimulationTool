/**
 * 
 */
package mades.system;

import java.util.ArrayList;

import mades.common.Memento;
import mades.common.Variable;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Implements the memento patter for the system by allowing
 * the {@link SystemConnector} to save and restore its state.
 */
public class SystemMemento extends Memento {

	/**
	 * @param time
	 * @param params
	 */
	public SystemMemento(double time, ArrayList<Variable> params) {
		super(time, params);
	}

	@Override
	public void deleteRelatedFiles() {
		// TODO Auto-generated method stub
	}
		
}
