package dolmisani.toys.demos.wordworms;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.swingset3.DemoProperties;

import dolmisani.toys.wordworms.WordWorm;
import dolmisani.toys.wordworms.customs.BacoDiDaniele;
import dolmisani.toys.wordworms.customs.CalabroneCoatto;
import dolmisani.toys.wordworms.originals.CimiceMaiofaga;
import dolmisani.toys.wordworms.originals.Farfalo;
import dolmisani.toys.wordworms.originals.MoscerinoApocopio;
import dolmisani.toys.wordworms.originals.RagnoUniverbo;
import dolmisani.toys.wordworms.originals.TermiteDiDublino;
import dolmisani.toys.wordworms.originals.VermeDisicio;

/**
 * Demo for Swing's JFrame top-level component.
 * 
 * @author aim
 */
@DemoProperties(value = "Word Worms Demo", category = "Word Worms", description = "Demonstrates JFrame, Swing's top-level primary window container.", sourceFiles = {
		"com/sun/swingset3/demos/frame/BusyGlass.java",
		"com/sun/swingset3/demos/frame/FrameDemo.java",
		"com/sun/swingset3/demos/DemoUtilities.java",
		"com/sun/swingset3/demos/frame/resources/FrameDemo.html",
		"com/sun/swingset3/demos/frame/resources/images/FrameDemo.gif" })
public class WordWormsDemo extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5610457825299331903L;

	static {
		// Property must be set *early* due to Apple Bug#3909714
		// ignored on other platforms
		if (System.getProperty("os.name").equals("Mac OS X")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
	}

	private static WordWorm[] WORMS = {
		new Farfalo(),
		new TermiteDiDublino(),
		new CimiceMaiofaga(),
		new RagnoUniverbo(),
		new MoscerinoApocopio(),
		new BacoDiDaniele(),
		new CalabroneCoatto(),
		new VermeDisicio()
	};
	
	private JTextArea sourceTextArea;
	private JTextArea targetTextArea;
	private JComboBox wormsList;	
	
	
	public WordWormsDemo() {
		initComponents();
	}

	protected void initComponents() {

		setLayout(new BorderLayout());
		add(createControlPanel(), BorderLayout.NORTH);
		add(createDataPanel(), BorderLayout.CENTER);
	}

	protected JComponent createDataPanel() {

		Box dataPanel = Box.createVerticalBox();
		dataPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		dataPanel.add(new JLabel("Source:", JLabel.LEFT));
		
		sourceTextArea = new JTextArea(12, 40);
		sourceTextArea.setLineWrap(true);
		sourceTextArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
			
				textUpdate();				
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				
				textUpdate();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
			
				textUpdate();				
			}
			
		});
		dataPanel.add(new JScrollPane(sourceTextArea));

		JLabel targetLabel = new JLabel("Target:");
		dataPanel.add(targetLabel);
		
		targetTextArea = new JTextArea(12, 40);
		targetTextArea.setEditable(false);
		targetTextArea.setLineWrap(true);
		dataPanel.add(new JScrollPane(targetTextArea));

		return dataPanel;
	}

	protected JComponent createControlPanel() {
		Box controlPanel = Box.createHorizontalBox();
		controlPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		wormsList = new JComboBox(WORMS);
		wormsList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				textUpdate();				
			}			
		});
		controlPanel.add(wormsList);
		
		//controlPanel.add(Box.createGlue());
		
		return controlPanel;
	}

	public void start() {
	}

	public void stop() {
	}
	
	private void textUpdate() {
		String text = sourceTextArea.getText();
		text = ((WordWorm)(wormsList.getSelectedItem())).eat(text);
		targetTextArea.setText(text);
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				WordWormsDemo demo = new WordWormsDemo();
				frame.add(demo);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
				demo.start();
			}
		});
	}
}
