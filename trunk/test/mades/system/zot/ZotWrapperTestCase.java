/**
 * 
 */
package mades.system.zot;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.timing.TimeFactory;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
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
	
	protected VariableFactory factory = new VariableFactory();
	protected Clock clock;
	
	protected VariableDefinition cond1;
	protected VariableDefinition react1;
	protected VariableDefinition act1;
	protected ZotWrapper wrapper;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		clock = new Clock(Logger.getLogger(ZotWrapperTestCase.class.getName()), 
				1, 0, 30);
		
		cond1 = factory.define("cond1", Scope.ENVIRONMENT_SHARED);
		react1 = factory.define("react1", Scope.SYSTEM_SHARED);
		act1 = factory.define("act1", Scope.SYSTEM_INTERNAL);
		
		ArrayList<VariableDefinition> vars = new ArrayList<VariableDefinition>();
		vars.add(cond1);
		vars.add(react1);
		vars.add(act1);
		
		wrapper = new ZotWrapper(engine, SYSTEM, variables, 30, clock, factory, vars);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File engineFile = new File(engine);
		if (engineFile.exists()) {
			engineFile.deleteOnExit();
		}
		File variablesFile = new File(variables);
		if (variablesFile.exists()) {
			variablesFile.deleteOnExit();
		}
	}

	/**
	 * Test method for {@link mades.system.zot.ZotWrapper#runZot()}.
	 */
	@Test
	public void testExecuteSimulationStep() {
		SystemMemento memento = new SystemMemento();
		memento.put(clock.getCurrentTime(), new VariableAssignment(cond1, 0));
		memento = wrapper.executeSimulationStep(clock.tickForward(), memento);
	}

}
