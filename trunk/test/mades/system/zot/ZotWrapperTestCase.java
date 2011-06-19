/**
 * 
 */
package mades.system.zot;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.logging.Logger;

import mades.common.timing.Clock;
import mades.common.timing.Time;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;
import mades.system.SystemMemento;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.TreeMultimap;

/**
 * 
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Test case for {@link ZotWrapper}.
 */
public class ZotWrapperTestCase {

	static final String SYSTEM = "./examples/RC/";

	
	VariableFactory factory = new VariableFactory();
	Clock clock;

	ZotWrapper wrapper;
	
	int step = 15;
	ArrayList<VariableDefinition> vars;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		clock = new Clock(Logger.getLogger(ZotWrapperTestCase.class.getName()), 
				1, 0, 30);
		
		wrapper = new ZotWrapper(SYSTEM, step, clock, factory, Logger.getAnonymousLogger());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	/**
	 * Test method for {@link mades.system.zot.ZotWrapper#parseInit()}.
	 * 
	 * The init file contains:
	 *     COND1 env shared boolean 1
	 *     REACT1 sys shared boolean 0
	 *     NUM_REACT1 sys shared real 0
	 *     ACT1 sys private boolean 0
	 */
	@Test
	public void testParseInit() {
		ArrayList<VariableAssignment> variables = wrapper.parseInit();
		assertEquals(4, variables.size());
		assertVariableExists("s", Scope.ENVIRONMENT_SHARED, true, "1", variables);
		assertVariableExists("REACT1", Scope.SYSTEM_SHARED, false, "0", variables);
		assertVariableExists("SIGREACT1", Scope.SYSTEM_SHARED, true, "0", variables);
		assertVariableExists("ACT1", Scope.SYSTEM_INTERNAL, true, "0", variables);
	}
	
	private void assertVariableExists(String name, Scope scope, boolean isBool,
			String value, ArrayList<VariableAssignment> variables) {
		VariableDefinition def;
		assertTrue("Variable " + name + " was not defined. Please check init file.",
				factory.isDefined(name));
		def = factory.get(name);
		assertEquals(scope, def.getScope());
		assertEquals("Wrong type for variable " + name + ". Expected: " + 
				isBool + ", found: " + def.isBoolean(), 
				isBool, def.isBoolean());
		boolean found = false;
		for (VariableAssignment v: variables) {
			if (v.getVariableDefinition().equals(def)) {
				found = true;
				assertEquals(value, v.getValue());
				break;
			}
		}
		assertTrue(found);
	}
	
	/**
	 * Test method for {@link mades.system.zot.ZotWrapper#runZot()}.
	 */
	@Test
	public void testExecuteSimulationStep() {
		ArrayList<VariableAssignment> variables = wrapper.parseInit();
		TreeMultimap<Time, VariableAssignment> variablesMultimap = TreeMultimap.create();
		for (VariableAssignment v: variables) {
			variablesMultimap.put(clock.getCurrentTime(), v);
		}
		SystemMemento systemMemento = new SystemMemento(variablesMultimap);
		
		systemMemento = wrapper.executeSimulationStep(clock.tickForward(), systemMemento);
	}

}
