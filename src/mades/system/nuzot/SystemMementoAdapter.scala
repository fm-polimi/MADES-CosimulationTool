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
                                     
        // Declare tfun
        /*
        for (varDef: VariableDefinition <- factory.getDefinedVariables()) {
            varDef.getScope() match {
                case VScope.ENVIRONMENT_SHARED => {
                    script = script :+ CommandDeclareTFun(
                            Symbol(varDef.getSystemName()),
                            List(),
                            varDef.getType() match {
	                            case VType.BOOLEAN => {
			                        Sort.Bool
			                    }
			                    case VType.INTEGER => {
			                        // Z3 cannot mix real and doubles
			                        Sort.Real
			                    }
			                    case VType.DOUBLE => {
			                        Sort.Real
			                    }
			                    case _ => {
			                    	throw new IllegalArgumentException(
			                    	        "Unexpected variable type: " +
			                    	        varDef.getType())
			                    }
                            }
                            )
                }
                case _ => {
                    // System variables are already defined
                }
            }
        }*/
        
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
			                        const = Term.const(assignment.getValue().toBoolean)
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
    
    
    def modelToMemento(
            time: Time, factory: VariableFactory,
            previous: SystemMemento, model: Z3Model): SystemMemento = {
        val memento = new SystemMemento(previous)
        val doubleTime = time.getSimulationStep().toDouble
        
        model.getModelFuncInterpretations.foreach(x => {
            val name = x._1.getName
            println("@" + name)
            if (factory.isDefinedInSystem(name.toString())) {
                val values = x._2
                values.foreach(v => {
                    val params = v._1
                    val assignment = v._2
                    println("#" + params.getClass())
                    params match {
                        case Seq(k) => {
                            println(k + " " + k.getClass() + " " + assignment)
                            if (k.toString().toInt == time.getSimulationStep()) {
                                memento.put(time,
                                        new VariableAssignment(
                                                factory.getSystemVar(name.toString()),
                                                assignment.toString()))
                            }
                        }
                        case _ => {
                            // Pass
                        }
                    }
                })
            } else {
                println("skipping " + name)
            }
        })
        return memento
    }
}