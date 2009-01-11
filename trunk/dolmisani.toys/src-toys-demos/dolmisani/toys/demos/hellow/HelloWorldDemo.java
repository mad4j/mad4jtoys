package dolmisani.toys.demos.hellow;

/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.sun.swingset3.DemoProperties;

/**
 * Demo for Swing's JFrame top-level component.
 * 
 * @author aim
 */
@DemoProperties(value = "HelloWorld Demo", category = "Hello World", description = "Demonstrates JFrame, Swing's top-level primary window container.", sourceFiles = {
		"com/sun/swingset3/demos/frame/BusyGlass.java",
		"com/sun/swingset3/demos/frame/FrameDemo.java",
		"com/sun/swingset3/demos/DemoUtilities.java",
		"com/sun/swingset3/demos/frame/resources/FrameDemo.html",
		"com/sun/swingset3/demos/frame/resources/images/FrameDemo.gif" })
public class HelloWorldDemo extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 755456253118253848L;

	static {
		// Property must be set *early* due to Apple Bug#3909714
		// ignored on other platforms
		if (System.getProperty("os.name").equals("Mac OS X")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
	}

	public HelloWorldDemo() {
		initComponents();
	}

	protected void initComponents() {

		setLayout(new BorderLayout());
		add(createControlPanel(), BorderLayout.WEST);
	}

	protected JComponent createControlPanel() {
		Box controlPanel = Box.createVerticalBox();
		controlPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		JLabel helloworldLabel = new JLabel("HelloWorld!!");
		controlPanel.add(helloworldLabel);
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
				HelloWorldDemo demo = new HelloWorldDemo();
				frame.add(demo);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
				demo.start();
			}
		});
	}
}
