/**
 *
 */
package mades.system.nuzot

import scala.collection.JavaConversions._
import mades.common.timing._
import mades.system.SystemMemento
import mades.common.variables.VariableAssignment
import mades.common.variables.VariableFactory
import mades.common.variables.{Scope => VScope}
import mades.common.variables.{Type => VType}
import it.polimi.nuzot.ltl.grammar._
import it.polimi.nuzot.ltl.LTLInterpreter
import it.polimi.nuzot.smt.grammar._
import z3.scala.Z3Model
import mades.common.variables.VariableDefinition

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 * Converts a SystemMemento into a Z3Model and vice-versa.
 */
object SystemMementoAdapter {

    /**
     * Sets the domain and k values
     */
    def generateInitScript(clock: Clock): Script = {
        var script = new Script()
                
        // Set real domain
        script = script :+ InitCommandSetInfo(
                AttributeKeyVal(
                        LTLInterpreter.domainKeyword,
                        AttributeValueSymbol(Symbol.Real)
                		)
        		)
        // Set K
        script = script :+ InitCommandSetInfo(
                AttributeKeyVal(
                        LTLInterpreter.kKeyword,
                        AttributeValueSpecConst(
                                SpecDoubleConstant(clock.getFinalStep().toDouble)
                                )
                        )
                 )  
        return script
    }
    
    
    def mementoToScript(
            clock: Clock,
            factory: VariableFactory,
            memento: SystemMemento): Script = {
        var script = new Script()
        
        // Parse all assigments
        for (time: Time <- memento.keySet()) {
            var terms: List[Term] = List()
            
            for (assignment: VariableAssignment <- memento.get(time)) {
                var definition = assignment.getVariableDefinition()
                definition.getScope() match {
                    case VScope.ENVIRONMENT_INTERNAL => {
                        // skip private environmental variables
                    }
                    case _ => {
                        var const: Term = null
		                definition.getType() match {
		                    case VType.BOOLEAN => {
		                        val realVal = assignment.getValue().toDouble
		                        if (realVal == 1.0) {
		                            const = Term.const(true)
		                        } else {
		                            const = Term.const(false)
		                        }
		                    }
		                    case VType.INTEGER => {
		                        const = Term.const(assignment.getValue().toInt)
		                    }
		                    case VType.DOUBLE => {
		                        const = Term.const(assignment.getValue().toDouble)
		                    }
		                    case _ => {
		                        // pass
		                    }
		
		        		}
		        		terms = terms :+ EQ(
		        		        Term.call(assignment.getVariableDefinition().getSystemName()),
		        		        const)
		           }
		       } 
            }
                	
            val assert = new CommandTemporalAssert(
            		// And of all the 
                    And(
                    	terms: _*	
                    ),
                    // The time
                    SpecIntConstant(time.getSimulationStep())
            )
            script = script :+ assert
        }
        
        // Parse all rolled back values
        for (time: Time <- memento.keySet()) {
            var terms: List[Term] = List()
            
            for (collection: java.util.Collection[VariableAssignment] 
                      <- memento.getUnsatConfiguration(time)) {
                for (assignment: VariableAssignment <- collection) {
	                var definition = assignment.getVariableDefinition()
	                definition.getScope() match {
	                    case VScope.ENVIRONMENT_INTERNAL => {
	                        // skip private environmental variables
	                    }
	                    case _ => {
	                        var const: Term = null
			                definition.getType() match {
			                    case VType.BOOLEAN => {
			                        val realVal = assignment.getValue().toDouble
			                        if (realVal == 1.0) {
			                            const = Term.const(true)
			                        } else {
			                            const = Term.const(false)
			                        }
			                    }
			                    case VType.INTEGER => {
			                        const = Term.const(assignment.getValue().toInt)
			                    }
			                    case VType.DOUBLE => {
			                        const = Term.const(assignment.getValue().toDouble)
			                    }
			                    case _ => {
			                        // pass
			                    }
			
			        		}
			        		terms = terms :+ EQ(
			        		        Term.call(assignment.getVariableDefinition().getSystemName()),
			        		        const)
			           }
			       } 
	            }
	                	
	            val assert = new CommandTemporalAssert(
	            		// And of all the 
	                    Not(And(
	                    	terms: _*	
	                    )),
	                    // The time
	                    SpecIntConstant(time.getSimulationStep())
	            )
	            script = script :+ assert
            }
        }
        return script
    }
    
    
    
    
    def mementoToScript(
            time: Time, clock: Clock,
            factory: VariableFactory,
            memento: SystemMemento): Script = {
        var script = new Script()   
        
        var prev =  clock.getFactory().get(time.getSimulationStep() -1)
        
        // Parse assigments at time
        // XXX(rax): duplicated code
        var terms: List[Term] = List()
        for (assignment: VariableAssignment <- memento.get(time)) {
            var definition = assignment.getVariableDefinition()
            definition.getScope() match {
                case VScope.ENVIRONMENT_SHARED => {
                    var const: Term = null
	                definition.getType() match {
	                    case VType.BOOLEAN => {
	                        val realVal = assignment.getValue().toDouble
	                        if (realVal == 1.0) {
	                            const = Term.const(true)
	                        } else {
	                            const = Term.const(false)
	                        }
	                    }
	                    case VType.INTEGER => {
	                        const = Term.const(assignment.getValue().toInt)
	                    }
	                    case VType.DOUBLE => {
	                        const = Term.const(assignment.getValue().toDouble)
	                    }
	                    case _ => {
	                        throw new IllegalArgumentException(
	                                "Unknown variable type: " +
	                                definition.getType())
	                    }
	
	        		}
	        		terms = terms :+ EQ(
	        		        Term.call(assignment.getVariableDefinition().getSystemName()),
	        		        const)
	           }
               case _ => {}
	       } 
        }
            	
        val assert = new CommandTemporalAssert(
        		// And of all the 
                And(
                	terms: _*	
                ),
                // The time
                SpecIntConstant(time.getSimulationStep())
        )
        script = script :+ assert
        
        if (time.getSimulationStep() > 0) {
            var prevTime =  clock.getFactory().get(time.getSimulationStep() -1)
            // Parse assigments at time
	        // XXX(rax): duplicated code
	        var terms: List[Term] = List()
	        for (assignment: VariableAssignment <- memento.get(prevTime)) {
	            var definition = assignment.getVariableDefinition()
	            definition.getScope() match {
	                case VScope.SYSTEM_INTERNAL | VScope.SYSTEM_SHARED => {
	                    var const: Term = null
		                definition.getType() match {
		                    case VType.BOOLEAN => {
		                        val realVal = assignment.getValue().toDouble
		                        if (realVal == 1.0) {
		                            const = Term.const(true)
		                        } else {
		                            const = Term.const(false)
		                        }
		                    }
		                    case VType.INTEGER => {
		                        const = Term.const(assignment.getValue().toInt)
		                    }
		                    case VType.DOUBLE => {
		                        const = Term.const(assignment.getValue().toDouble)
		                    }
		                    case _ => {
		                        throw new IllegalArgumentException(
		                                "Unknown variable type: " +
		                                definition.getType())
		                    }
		
		        		}
		        		terms = terms :+ EQ(
		        		        Term.call(assignment.getVariableDefinition().getSystemName()),
		        		        const)
		           }
	               case _ => {}
		       } 
	        }
	            	
	        val assert = new CommandTemporalAssert(
	        		// And of all the 
	                And(
	                	terms: _*	
	                ),
	                // The time
	                SpecIntConstant(prevTime.getSimulationStep())
	        )
	        script = script :+ assert
        }
        
        
       // Parse all rolled back values
       // XXX(rax): duplicated code
       terms = List()     
       for (collection: java.util.Collection[VariableAssignment] 
                  <- memento.getUnsatConfiguration(time)) {
            for (assignment: VariableAssignment <- collection) {
                var definition = assignment.getVariableDefinition()
                definition.getScope() match {
                    case VScope.ENVIRONMENT_INTERNAL => {
                        // skip private environmental variables
                    }
                    case _ => {
                        var const: Term = null
		                definition.getType() match {
		                    case VType.BOOLEAN => {
		                        val realVal = assignment.getValue().toDouble
		                        if (realVal == 1.0) {
		                            const = Term.const(true)
		                        } else {
		                            const = Term.const(false)
		                        }
		                    }
		                    case VType.INTEGER => {
		                        const = Term.const(assignment.getValue().toInt)
		                    }
		                    case VType.DOUBLE => {
		                        const = Term.const(assignment.getValue().toDouble)
		                    }
		                    case _ => {
		                        throw new IllegalArgumentException(
	                                "Unknown variable type: " +
	                                definition.getType())
		                    }
		        		}
		        		terms = terms :+ EQ(
		        		        Term.call(assignment.getVariableDefinition().getSystemName()),
		        		        const)
		           }
		       } 
            }
                	
            val assert = new CommandTemporalAssert(
            		// And of all the 
                    Not(And(
                    	terms: _*	
                    )),
                    // The time
                    SpecIntConstant(time.getSimulationStep())
            )
            script = script :+ assert
        }
        
        return script
    }
    
    def modelToMemento(
            time: Time, factory: VariableFactory,
            previous: SystemMemento, model: Z3Model): SystemMemento = {
        
        //print(model)
        
        val memento = new SystemMemento(previous)
        val doubleTime = time.getSimulationStep().toDouble
        
        model.getModelFuncInterpretations.foreach(x => {
            val name = x._1.getName
            if (factory.isDefinedInSystem(name.toString())) {
                val values = x._2
                values.foreach(v => {
                    val params = v._1
                    val assignment = v._2
                    params match {
                        case Seq(k) => {
                            if (k.toString().toInt == time.getSimulationStep()) {
                                val varDef = factory.getSystemVar(name.toString())
                                varDef.getType() match {
                                    case VType.BOOLEAN => {
                                        memento.put(time,
                                        new VariableAssignment(
                                                varDef,
                                                if (assignment.toString().equals("true")) 1.0.toString else 0.0.toString
                                                )
                                        )
                                    }
                                    case _ => {
                                        memento.put(time,
                                        new VariableAssignment(
                                                varDef, assignment.toString()))
                                    }
                                }
                                
                            }
                        }
                        case _ => {
                            // Pass
                        }
                    }
                })
            } else {
                //println("skipping " + name)
            }
        })
        return memento
    }
}