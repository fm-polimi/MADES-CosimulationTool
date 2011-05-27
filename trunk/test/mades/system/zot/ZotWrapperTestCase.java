/**
 * 
 */
package mades.system.zot;

import java.io.File;

import mades.common.variables.VariableAssignment;
import mades.system.SystemMemento;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Test case for {@link ZotWrapper}.
 */
public class ZotWrapperTestCase {

	public static String SYSTEM = "/home/rax/workspace-mades/mades/tools/zot/SimulationToyExample_system.zot";
	protected String engine = "/tmp/MadesZotEngine.zot";
	protected String variables = "/tmp/MadesZotVariables.zot";
	
	protected ZotWrapper wrapper;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		wrapper = new ZotWrapper(engine, SYSTEM, variables, 30);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File engineFile = new File(engine);
		if (engineFile.exists()) {
			//engineFile.deleteOnExit();
		}
		File variablesFile = new File(variables);
		if (variablesFile.exists()) {
			//variablesFile.deleteOnExit();
		}
	}

	/**
	 * Test method for {@link mades.system.zot.ZotWrapper#runZot()}.
	 */
	@Test
	public void testExecuteSimulationStep() {
		SystemMemento memento = new SystemMemento();
		memento.put(0, new VariableAssignment("cond1", 0, true));
		wrapper.executeSimulationStep(1, memento);
	}

}
