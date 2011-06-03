/**
 * 
 */
package mades.cosimulation;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * @author Michele Sama (m.sama@puzzledev.com)
 *
 */
public class MadesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JLabel labelFolder = null;
	private JTextField jTextField = null;
	private JButton jButtonbuttonOpenFolder = null;
	private JButton buttonRun = null;
	private JTextArea textareaOutput = null;

	/**
	 * @throws HeadlessException
	 */
	public MadesFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
		super();
		initialize();
	}

	/**
	 * @param gc
	 */
	public MadesFrame(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public MadesFrame(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * @param title
	 * @param gc
	 */
	public MadesFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			labelFolder = new JLabel();
			labelFolder.setText("ProjectFolder");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(labelFolder, gridBagConstraints);
			jPanel.add(getJTextField(), gridBagConstraints2);
			jPanel.add(getJButtonbuttonOpenFolder(), gridBagConstraints3);
			jPanel.add(getButtonRun(), gridBagConstraints4);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	public JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(250, 19));
			jTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		return jTextField;
	}
	

	/**
	 * This method initializes jButtonbuttonOpenFolder	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonbuttonOpenFolder() {
		if (jButtonbuttonOpenFolder == null) {
			jButtonbuttonOpenFolder = new JButton();
			jButtonbuttonOpenFolder.setText("...");
		}
		return jButtonbuttonOpenFolder;
	}

	/**
	 * This method initializes buttonRun	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonRun() {
		if (buttonRun == null) {
			buttonRun = new JButton();
			buttonRun.setText("Run");
			buttonRun.setToolTipText("Run the co-simulation");
			buttonRun.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		return buttonRun;
	}

	/**
	 * This method initializes textareaOutput	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getTextareaOutput() {
		if (textareaOutput == null) {
			textareaOutput = new JTextArea();
			textareaOutput.setForeground(Color.white);
			textareaOutput.setLineWrap(true);
			textareaOutput.setBackground(Color.black);
		}
		return textareaOutput;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MadesFrame thisClass = new MadesFrame();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(786, 343);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
			jContentPane.add(getTextareaOutput(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
