/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MadesMainForm.java
 *
 * Created on Jul 15, 2011, 8:13:33 PM
 */
package mades.cosimulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.comparators.Ascending;
import de.erichseifert.gral.data.filters.Convolution;
import de.erichseifert.gral.data.filters.Filter.Mode;
import de.erichseifert.gral.data.filters.Kernel;
import de.erichseifert.gral.data.filters.KernelUtils;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import mades.common.timing.Clock;
import mades.common.utils.Files;
import mades.common.variables.VariableDefinition;
import mades.cosimulation.Cosimulator;
import mades.cosimulation.OutputWriter;
import mades.environment.EnvironmentConnector;
import mades.environment.modelica.ModelicaEnvironmentConnector;
import mades.system.SystemConnector;
import mades.system.zot.ZotSystemConnector;

/**
 *
 * @author Michele Sama (m.sama@puzzledev.com)
 * 
 * Main form for MADES. Use this class to run MADES with the GUI.
 * 
 */
public class MadesMainForm extends javax.swing.JFrame
        implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 4046070019836240661L;

	// Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSlider maxAttemptJSlider;
    private javax.swing.JSlider maxBacktrakingJSlider;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JFileChooser projetFileChooser;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox selectVariablesComboBox;
    private javax.swing.JButton startJButton;
    private javax.swing.JButton stopJButton;
    private javax.swing.JTextField stopTimeJTextField;
    private javax.swing.JTextField timeStepJTextField;
    // End of variables declaration//GEN-END:variables


    private Cosimulator cosimulator;
    private OutputWriter outputWriter;
    private DrawablePanel chart;

	
    /** Creates new form MadesMainForm */
    public MadesMainForm() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // MR: changed default starting folder for search
        projetFileChooser = new javax.swing.JFileChooser(Files.getCurrentPath(MadesMainForm.class));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        timeStepJTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        stopTimeJTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        maxAttemptJSlider = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        maxBacktrakingJSlider = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        startJButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        selectVariablesComboBox = new javax.swing.JComboBox();
        chartPanel = new javax.swing.JPanel();
        stopJButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        projetFileChooser.setToolTipText("Select the project file.");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MADES Co-simulation tool");

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 36));
        jLabel1.setForeground(new java.awt.Color(254, 6, 6));
        jLabel1.setText("MADES");

        jLabel2.setText("Co-simulation tool");

        // projectTextField.setText("examples/RC/mades.xml");
        // MR: changed default
        projectTextField.setText("");

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Co-simulation parameters"));

        timeStepJTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        timeStepJTextField.setText("1");
        timeStepJTextField.setToolTipText("The discrete co-simulation time step.");

        jLabel4.setText(" Time step (s)");

        jLabel5.setText(" Stop time (s)");

        stopTimeJTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        stopTimeJTextField.setText("20");
        stopTimeJTextField.setToolTipText("The time after which the simulation will stop.");

        jLabel6.setText("Max attempts for step");

        maxAttemptJSlider.setMajorTickSpacing(2);
        maxAttemptJSlider.setMaximum(5);
        maxAttemptJSlider.setMinimum(1);
        maxAttemptJSlider.setMinorTickSpacing(1);
        maxAttemptJSlider.setPaintLabels(true);
        maxAttemptJSlider.setPaintTicks(true);
        maxAttemptJSlider.setValue(3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxAttemptJSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(maxAttemptJSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel8.setText("Max backtraking depth");

        maxBacktrakingJSlider.setMajorTickSpacing(2);
        maxBacktrakingJSlider.setMaximum(5);
        maxBacktrakingJSlider.setMinimum(1);
        maxBacktrakingJSlider.setMinorTickSpacing(1);
        maxBacktrakingJSlider.setPaintLabels(true);
        maxBacktrakingJSlider.setPaintTicks(true);
        maxBacktrakingJSlider.setValue(3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxBacktrakingJSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(maxBacktrakingJSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(81, 81, 81)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stopTimeJTextField)
                    .addComponent(timeStepJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeStepJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(stopTimeJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Select the project. The project must be a MADES \".xml\" configuration file. ");

        startJButton.setText("Start co-simulation");
        startJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startJButtonActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));

        selectVariablesComboBox.addActionListener(this);

        chartPanel.setBackground(new java.awt.Color(253, 251, 251));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectVariablesComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 690, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(selectVariablesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        stopJButton.setEnabled(false);
        stopJButton.setLabel("Stop Co-simulation");
        stopJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(projectTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(startJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopJButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(projectTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(stopJButton)
                        .addComponent(startJButton))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        try {
            File f =new File(projectTextField.getText());
            projetFileChooser.setSelectedFile(f);
        } catch(Exception ex) {}
        
        int returnValue = projetFileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File f = projetFileChooser.getSelectedFile();
            if (f.exists() && f.isFile()) {
                projectTextField.setText(f.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Selected file does not exist or it is a directory: " + 
                        f.getAbsolutePath(), 
                        "Error opening file", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void stopJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopJButtonActionPerformed
        if (cosimulator!= null) {
            cosimulator.stopCosimulation();
            cosimulator = null;
        }
        stopJButton.setEnabled(false);
        startJButton.setEnabled(true);
        progressBar.setValue(0);
    }//GEN-LAST:event_stopJButtonActionPerformed

    private void startJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startJButtonActionPerformed
        if (cosimulator == null || !cosimulator.isSimulationStarted()) {
            Thread th = new Thread(){
                @Override
                public void run() {
                    runCosimulation();
                }
            };
            th.start();
        }
    }//GEN-LAST:event_startJButtonActionPerformed

    private void runCosimulation() {
        startJButton.setEnabled(false);
        stopJButton.setEnabled(true);
        selectVariablesComboBox.removeAllItems();
        progressBar.setValue(0);
 
        String model;
        double timeStep;
        double stopTime;
        int maxAttemptsInStep;
        int maxBacktrakingDepth;

        
        try {
            Logger logger = Logger.getLogger(Cosimulator.class.getName());
		logger.setLevel(Level.ALL);
		logger.info("Starting co-simulation");
		
            SystemConnector system = new ZotSystemConnector(logger);
            EnvironmentConnector environment = 
                        new ModelicaEnvironmentConnector(logger);
            cosimulator = new Cosimulator(logger);
            cosimulator.setEnvironment(environment);
            cosimulator.setSystem(system);
		
            
            model = projectTextField.getText();
            timeStep = Double.parseDouble(timeStepJTextField.getText());
            stopTime = Double.parseDouble(stopTimeJTextField.getText());
            maxAttemptsInStep = maxAttemptJSlider.getValue();
            maxBacktrakingDepth = maxBacktrakingJSlider.getValue();
            cosimulator.addPropertyChangeListener(Cosimulator.SIMULATION_STEP_DONE, this);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                    "Co-simulation initialization failed due to the " +
                    "following error:\n" + ex.getMessage(), 
                    "Co-simulation initialization failed.",
                    JOptionPane.ERROR_MESSAGE);
            
            ex.printStackTrace();
            stopJButton.setEnabled(false);
            startJButton.setEnabled(true);
            return;
        }
            
        try {    
            cosimulator.startCosimulation(model,
                        timeStep, stopTime,
                        maxAttemptsInStep,
                        maxBacktrakingDepth);
            
            File f = new File(model);
            String output = f.getParent() + File.separator + "madesOutput.xml";
            outputWriter.writeXmlFile(output);
            
            JOptionPane.showMessageDialog(this,
            		"Co-simulation output saved in file: " + output,
            		"Co-simulation terminated succesfully.",
            		JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                    "Co-simulation failed due to the " +
                    "following error:\n" + ex.getMessage(), 
                    "Co-simulation aborted.",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            stopJButton.setEnabled(false);
            startJButton.setEnabled(true);
            return;
        }
        
        startJButton.setEnabled(true);
        stopJButton.setEnabled(false);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MadesMainForm().setVisible(true);
            }
        });
    }
   
    @Override
    public void actionPerformed(ActionEvent e) {
    	if (outputWriter == null) {
    		return;
    	}
    	Object selected=selectVariablesComboBox.getSelectedItem();
    	if (selected == null) {
    		return;
    	}
        VariableDefinition def = (VariableDefinition)selected;
        DataSource dataSource = outputWriter.getDataSource(def);
        DataTable data = new DataTable(dataSource);
        data.sort(new Ascending(0));
        //Kernel kernel = KernelUtils.getUniform(30, 15, 1.0).normalize();
        //DataSource filtered = new Convolution(data, kernel, Mode.REPEAT, 1);
        XYPlot plot = new XYPlot(data);//, filtered);
        
        //plot.setPointRenderer(filtered, null);
        DefaultLineRenderer2D lineRenderer = new DefaultLineRenderer2D();
        lineRenderer.setSetting(LineRenderer.COLOR, Color.BLUE);
        //plot.setLineRenderer(filtered, lineRenderer);
        plot.setLineRenderer(data, lineRenderer);
        plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
        if (chart != null) {
        	chartPanel.remove(chart);
        }
        chart = new InteractivePanel(plot); 
        chart.setSize(chartPanel.getSize());
        chartPanel.add(chart);
        chartPanel.repaint();
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Cosimulator.SIMULATION_STEP_DONE.equals(evt.getPropertyName())) {
			Clock clock = cosimulator.getClock();
			progressBar.setValue(
					(int) Math.floor(
					100 * clock.getCurrentTime().getSimulationTime() / clock.getFinalTime()));
			
			outputWriter = cosimulator.createOutputWriter();
            
			if (selectVariablesComboBox.getItemCount() == 0) {
				Collection<VariableDefinition> variables = outputWriter.getVariables();
	            for (VariableDefinition def: variables) {
	            	selectVariablesComboBox.addItem(def);
	            }	
			} else {
				int index = selectVariablesComboBox.getSelectedIndex();
	            selectVariablesComboBox.setSelectedIndex(index);	
			}
		}
	}
}
