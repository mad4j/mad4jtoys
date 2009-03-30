/*
 * Template
 * Copyright (C) 2009 Daniele Olmisani (daniele.olmisani@gmail.com)
 *
 * This file is part of Template.
 *
 * Invaders is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Invaders is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;

@SuppressWarnings("serial")
public class T extends JFrame {

	final boolean[] K = new boolean[65535]; // pressed keys

	public T() {

		BufferedImage image = new BufferedImage(320, 240,
				BufferedImage.TYPE_INT_RGB);
		Graphics imageGraphics = image.getGraphics();

		JPanel panel = (JPanel)getContentPane();
		panel.setPreferredSize(new Dimension(640, 480));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(null);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		long nextFrameStart = System.nanoTime();
		while (true) {
			do {
				// -- UPDATE MODEL BEGIN
				// -------------------------------------------------------

				// -- UPDATE MODEL END
				// ---------------------------------------------------------
				nextFrameStart += 16666667;
			} while (nextFrameStart < System.nanoTime());
			// -- RENDER FRAME BEGIN
			// -------------------------------------------------------

			imageGraphics.setColor(Color.WHITE);
			imageGraphics.fillRect(0, 0, 320, 240);

			Graphics panelGraphics = panel.getGraphics();
			if (panelGraphics != null) {
				panelGraphics.drawImage(image, 0, 0, 640, 480, null);
				panelGraphics.dispose();
			}

			// -- RENDER FRAME END
			// ---------------------------------------------------------
			long remaining = nextFrameStart - System.nanoTime();
			if (remaining > 0) {
				try {
					Thread.sleep(remaining / 1000000);
				} catch (Throwable t) {
				}
			}
		}
	}

	protected void processKeyEvent(KeyEvent e) {
		K[e.getKeyCode()] = e.getID() == 401;
	}

	public static void main(String[] args) {
		new T();
	}
}
