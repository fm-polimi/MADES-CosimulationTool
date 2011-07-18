/**
 * 
 */
package mades.cosimulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import mades.common.timing.Time;
import mades.common.variables.VariableAssignment;
import mades.common.variables.VariableDefinition;
import mades.common.variables.VariableFactory;

import com.google.common.collect.TreeMultimap;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class OutputWriter {
	private TreeMultimap<Time, VariableAssignment> sharedVariablesMultimap;
	private ArrayList<VariableDefinition> variables;

	/**
	 * @param sharedVariablesMultimap
	 */
	public OutputWriter(VariableFactory variableFactory,
			TreeMultimap<Time, VariableAssignment> sharedVariablesMultimap) {
		this.sharedVariablesMultimap = sharedVariablesMultimap;
		variables = new ArrayList<VariableDefinition>(
				variableFactory.getDefinedVariables());
		
	}
	
	public void writeXmlFile(String filename) {
		try {
			PrintWriter printWriter = new PrintWriter(filename);
			XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();
			XMLStreamWriter xmlWriter = xmlFactory.createXMLStreamWriter(printWriter);
			
			xmlWriter.writeStartDocument();
			Set<Time> keys = sharedVariablesMultimap.keySet();
			xmlWriter.writeStartElement("Mades:cosimulation");
			for (Time time: keys) {
				xmlWriter.writeStartElement("Mades:step");
				xmlWriter.writeAttribute("continuous", "" + time.getSimulationTime());
				xmlWriter.writeAttribute("discrete", "" + time.getSimulationStep());
				Set<VariableAssignment> vars = sharedVariablesMultimap.get(time);
				for(VariableAssignment v: vars) {
					xmlWriter.writeEmptyElement("Mades:variable");
					xmlWriter.writeAttribute("systemName", 
							v.getVariableDefinition().getSystemName());
					xmlWriter.writeAttribute("environmentName", 
							v.getVariableDefinition().getEnvironmentName());
					xmlWriter.writeAttribute("value", 
							v.getValue());
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
			xmlWriter.close();
			
		} catch(XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the variables
	 */
	public ArrayList<VariableDefinition> getVariables() {
		return variables;
	}
}
