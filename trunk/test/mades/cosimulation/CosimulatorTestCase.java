/**
 * 
 */
package mades.cosimulation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import mades.common.timing.Time;
import mades.common.timing.TimeFactory;
import mades.common.variables.Scope;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class CosimulatorTestCase {
	
	ArrayList<VariableAssignment> environmentParams;
	ArrayList<VariableAssignment> systemParams;
	VariableDefinition sharedVar1;
	VariableDefinition sharedVar2;
	VariableDefinition privateVar1;
	VariableDefinition privateVar2;
	
	Cosimulator cosimulator;
	
	TimeFactory timeFactory = new TimeFactory();
	VariableFactory variableFactory = new VariableFactory();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String val= "15";
		
		sharedVar1 = variableFactory.define("sharedVar1", Scope.ENVIRONMENT_SHARED, true);
		sharedVar2 = variableFactory.define("sharedVar2", Scope.SYSTEM_SHARED, true);
		privateVar1 = variableFactory.define("privateVar1", Scope.ENVIRONMENT_INTERNAL, true);
		privateVar2 = variableFactory.define("privateVar2", Scope.SYSTEM_INTERNAL, true);


		environmentParams = new ArrayList<VariableAssignment>();
		environmentParams.add(new VariableAssignment(sharedVar1, val, ""));
		environmentParams.add(new VariableAssignment(privateVar1, val, ""));
		
		systemParams = new ArrayList<VariableAssignment>();
		systemParams.add(new VariableAssignment(sharedVar2, val, ""));
		systemParams.add(new VariableAssignment(privateVar2, val, ""));
		
		
		cosimulator = new Cosimulator(
				Logger.getLogger(this.getClass().getName()));
		cosimulator.setEnvironment(new EchoEnvironmentConnectorMock(environmentParams));
		cosimulator.setSystem(new EchoSystemConnectorMock(systemParams));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link mades.cosimulation.Cosimulator#startCosimulation()}.
	 */
	@Test
	public void testStartCosimulation() {
		double initialSimulationTime = 0;
		double timeStep = 1;
		double maxCosimulationTime = 10;
		int maxCosimulationAttemptsForStep = 1;
		int maxCosimulationBacktraking = 1;
		
		cosimulator.startCosimulation(
				initialSimulationTime,
				timeStep,
				maxCosimulationTime,
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking);
		
		TreeMultimap<Time, VariableAssignment> results = cosimulator.getSharedVariablesMultimap();
		int steps = (int)maxCosimulationTime + 1;
		Set<Time> keys = results.keySet();
		assertEquals(steps, keys.size());
		for (Time key: keys) {
			Set<VariableAssignment> vars = results.get(key);
			assertEquals(2, vars.size());
			HashSet<VariableDefinition> definitions = new HashSet<VariableDefinition>();
			for (VariableAssignment v: vars) {
				definitions.add(v.getVariableDefinition());
			}
			assertTrue(definitions.contains(sharedVar1));
			assertTrue(definitions.contains(sharedVar2));
		}
	}
}
