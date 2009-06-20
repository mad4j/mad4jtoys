package dolmisani.games.jpong;

import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;

public class JPong extends JFrame {

	private static final long serialVersionUID = -6016934053312319564L;

	/*
	 * Create the window and add the table.
	 */
	public JPong() {

		setTitle("JPong - Daniele Olmisani (daniele.olmisani@gmail.com)");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(640, 480);
		setResizable(false);

		add(new GameLogic());

		setLocationRelativeTo(null); // Center on screen

		setUndecorated(true);

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		DisplayMode dm = new DisplayMode(720, 576, 32,
				DisplayMode.REFRESH_RATE_UNKNOWN);

		
		gd.setFullScreenWindow(this);
		gd.setDisplayMode(dm);
		setVisible(true);

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
				
				
			}

		});
		
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(
		        new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor =
		        Toolkit.getDefaultToolkit().createCustomCursor
		             (image, new Point(0, 0), "invisibleCursor");
		

		setCursor(transparentCursor);
		
	}

	/*
	 * Start the game.
	 */
	public static void main(String[] args) {
		
		new JPong();
	}
}
