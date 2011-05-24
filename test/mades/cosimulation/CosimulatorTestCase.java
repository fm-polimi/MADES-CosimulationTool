/**
 * 
 */
package mades.cosimulation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;

import mades.common.Variable;

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
		Variable sharedVar1 = new Variable("sharedVar1", val, true);
		Variable sharedVar2 = new Variable("sharedVar2", val, true);
		Variable privateVar1 = new Variable("privateVar1", val, false);
		Variable privateVar2 = new Variable("privateVar2", val, false);


		ArrayList<Variable> environmentParams = new ArrayList<Variable>();
		environmentParams.add(sharedVar1);
		environmentParams.add(privateVar1);
		
		ArrayList<Variable> systemParams = new ArrayList<Variable>();
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
		TreeMultimap<Double, Variable> results = cosimulator.getSharedVariablesMultimap();
		int steps = (int)maxCosimulationTime;
		Set<Double> keys = results.keySet();
		assertEquals(steps, keys.size());
		for (Double key: keys) {
			Set<Variable> vars = results.get(key);
			assertEquals(2, vars.size());
			assertTrue(vars.contains(sharedVar1));
			assertTrue(vars.contains(sharedVar2));
		}
	}
}
