/**
 *
 */
package mades.system.nuzot

import java.util.logging.Logger
import org.junit._
import Assert._

import mades.common.timing._
import mades.common.variables._
import mades.system.SystemMemento
import mades.system.nuzot._
import mades.common.variables._

import it.polimi.nuzot.smt.TypeChecker
import it.polimi.nuzot.smt.grammar._
import it.polimi.nuzot.ltl.LTLInterpreter
import it.polimi.nuzot.ltl.grammar._
import it.polimi.nuzot.shell.ShellInterpreter
import it.polimi.nuzot.Z3.Z3Interpreter;


/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
class SystemMementoAdapterTestCase {

    val varFactory = new VariableFactory()
    
    val envSharedVar = varFactory.define(
            "x_sys", "x_env", Scope.ENVIRONMENT_SHARED,
            Type.DOUBLE)
    val envPrivateVar = varFactory.define(
            "y_sys", "y_env",
            Scope.ENVIRONMENT_INTERNAL, Type.DOUBLE)
    val sysSharedVar = varFactory.define(
            "a_sys", "a_env",
            Scope.SYSTEM_SHARED, Type.BOOLEAN)
    val sysPrivateVar = varFactory.define(
            "b_sys", "b_env",
            Scope.SYSTEM_INTERNAL, Type.BOOLEAN)
    
    val memento = new SystemMemento()
    
    val clock = new Clock(
    	        Logger.getAnonymousLogger(),
    	        1, 0, 7)
    
    @Before
    def setUp(): Unit = {
    	memento.put(
    	        clock.getCurrentTime(),
    	        new VariableAssignment(envSharedVar, "123"))
    	/*memento.put(
    	        clock.getCurrentTime(),
    	        new VariableAssignment(envPrivateVar, "0"))*/
    	memento.put(
    	        clock.getCurrentTime(),
    	        new VariableAssignment(sysSharedVar, "1.0"))
    	memento.put(
    	        clock.getCurrentTime(),
    	        new VariableAssignment(sysPrivateVar, "0.0"))
    }
    
    @Test
    def testGenerateInitScript(): Unit = {
        val script = SystemMementoAdapter.generateInitScript(clock)
        val expected = 
            "(set-info :domain Real)\n" +
            "(set-info :k 7.0)"
        assertEquals(expected, script.toString())
    }
    
    @Test
    def testMementoToScript(): Unit = {
        val script = 
            	SystemMementoAdapter.generateInitScript(clock) ++
            	Script(
            	        CommandDeclareTFun(Symbol("x_sys"), List(), Sort.Real) ::
            	        CommandDeclareTFun(Symbol("a_sys"), List(), Sort.Bool) ::
            	        CommandDeclareTFun(Symbol("b_sys"), List(), Sort.Bool) ::
            	        List()
            	        ) ++
            	SystemMementoAdapter.mementoToScript(
            			clock, varFactory, memento)
        val expected = 
            "(set-info :domain Real)\n" +
            "(set-info :k 7.0)\n" +
            "(declare-tfun x_sys () Real)\n" +
            "(declare-tfun a_sys () Bool)\n" +
            "(declare-tfun b_sys () Bool)\n" +
            "(assert-t (and (= a_sys true) (= b_sys false) (= x_sys 123.0)) 0)"
        assertEquals(expected, script.toString())
    }
    
    @Test
    def testModelToMemento(): Unit = {
        val script = 
            	SystemMementoAdapter.generateInitScript(clock) ++
            	Script(
            	        CommandDeclareTFun(Symbol("x_sys"), List(), Sort.Real) ::
            	        CommandDeclareTFun(Symbol("a_sys"), List(), Sort.Bool) ::
            	        CommandDeclareTFun(Symbol("b_sys"), List(), Sort.Bool) ::
            	        List()
            	        ) ++
            	SystemMementoAdapter.mementoToScript(
            			clock, varFactory, memento)
            			
        val z3 = new Z3Interpreter();
		val typeChecker = new TypeChecker();
        typeChecker.next(z3);
        val ltl = new LTLInterpreter();
    	ltl.next(typeChecker);
		val shell = new ShellInterpreter();
        shell.next(ltl);
        shell.doVisit(script)
        assertTrue(z3.checkSat())
        val model = z3.getModel()
        println(model)
        assertEquals(memento.getVariablesMultimap(),
                SystemMementoAdapter.modelToMemento(
                        memento.getLatestSimulatedTime(),
                        varFactory,
                        new SystemMemento(),
                        model).getVariablesMultimap())
    }
}