package dolmisani.toys.demos.wordworms;


import java.awt.BorderLayout;
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

import com.sun.swingset3.DemoProperties;

import dolmisani.toys.wordworms.CimiceMaiofaga;
import dolmisani.toys.wordworms.Farfalo;
import dolmisani.toys.wordworms.TermiteDiDublino;
import dolmisani.toys.wordworms.WordWorm;

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
		new CimiceMaiofaga()
	};
	
	private JTextArea sourceTextArea;
	private JTextArea targetTextArea;
	
	
	
	public WordWormsDemo() {
		initComponents();
	}

	protected void initComponents() {

		setLayout(new BorderLayout());
		add(createControlPanel(), BorderLayout.WEST);
		add(createDataPanel(), BorderLayout.CENTER);
	}

	protected JComponent createDataPanel() {

		Box dataPanel = Box.createVerticalBox();
		dataPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		dataPanel.add(new JLabel("Source:", JLabel.LEFT));
		
		sourceTextArea = new JTextArea(12, 40);
		dataPanel.add(new JScrollPane(sourceTextArea));

		dataPanel.add(new JLabel("Target:"));
		
		targetTextArea = new JTextArea(12, 40);
		targetTextArea.setEditable(false);
		dataPanel.add(new JScrollPane(targetTextArea));

		return dataPanel;
	}

	protected JComponent createControlPanel() {
		Box controlPanel = Box.createVerticalBox();
		controlPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		final JComboBox wormsList = new JComboBox(WORMS);
		controlPanel.add(wormsList);
		
		final JButton feedButton = new JButton("Feed!!");
		feedButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String text = sourceTextArea.getText();
				text = ((WordWorm)(wormsList.getSelectedItem())).eat(text);
				targetTextArea.setText(text);				
			}									
		});
		controlPanel.add(feedButton);

		return controlPanel;
	}

	public void start() {
	}

	public void stop() {
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
