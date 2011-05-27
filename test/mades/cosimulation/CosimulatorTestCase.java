/**
 * 
 */
package mades.cosimulation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;

import mades.common.variables.VariableAssignment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class CosimulatorTestCase {
	
	Cosimulator cosimulator;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cosimulator = new Cosimulator();
		cosimulator.setEnvironment(new EchoEnvironmentConnectorMock());
		cosimulator.setSystem(new EchoSystemConnectorMock());
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
		int val= 15;
		VariableAssignment sharedVar1 = new VariableAssignment("sharedVar1", val, true);
		VariableAssignment sharedVar2 = new VariableAssignment("sharedVar2", val, true);
		VariableAssignment privateVar1 = new VariableAssignment("privateVar1", val, false);
		VariableAssignment privateVar2 = new VariableAssignment("privateVar2", val, false);


		ArrayList<VariableAssignment> environmentParams = new ArrayList<VariableAssignment>();
		environmentParams.add(sharedVar1);
		environmentParams.add(privateVar1);
		
		ArrayList<VariableAssignment> systemParams = new ArrayList<VariableAssignment>();
		systemParams.add(sharedVar2);
		systemParams.add(privateVar2);
		
		double startTime = 0;
		int startStep = 0;
		double timeStep = 1;
		double maxCosimulationTime = 10;
		int maxCosimulationAttemptsForStep = 1;
		int maxCosimulationBacktraking = 1;
		
		cosimulator.startCosimulation(
				startTime,
				startStep,
				timeStep,
				maxCosimulationTime, 
				maxCosimulationAttemptsForStep,
				maxCosimulationBacktraking, 
				environmentParams,
				systemParams);
		
		assertTrue(maxCosimulationTime <= cosimulator.getSimulationTime());
		TreeMultimap<Double, VariableAssignment> results = cosimulator.getSharedVariablesMultimap();
		int steps = (int)maxCosimulationTime;
		Set<Double> keys = results.keySet();
		assertEquals(steps, keys.size());
		for (Double key: keys) {
			Set<VariableAssignment> vars = results.get(key);
			assertEquals(2, vars.size());
			assertTrue(vars.contains(sharedVar1));
			assertTrue(vars.contains(sharedVar2));
		}
	}
}
