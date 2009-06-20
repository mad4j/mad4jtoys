package dolmisani.games.jpong;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class GameLogic extends JPanel implements Runnable {
	
	
	private static final long serialVersionUID = -677897566529512347L;
	
	private boolean gameStart; // Indicates if it is the start of the game
	private boolean roundStart; // Indicates if it is the start of the round
	private GameObject ball; // The ball
	private GameObject playerPaddle; // The player's paddle
	private GameObject cpuPaddle; // The cpu's paddle
	private int playerScore; // The player's score
	private int cpuScore; // The cpu score
	private int canvasWidth; // The width of the visible canvas
	private int canvasHeight; // The height of the visible canvas
	private Font pongFont; // Score font
	private Thread animate; // Animation thread

	
	private AudioClip clip01;
	private AudioClip clip02;
	private AudioClip clip03;
	
	public GameLogic() {
		
		setBackground(Color.black); // Set Panel to black
		setDoubleBuffered(true); // Double buffered for smooth animation
		gameStart = true; // Set game to just started
		roundStart = true; // Set round to just started
		ball = new GameObject(0, 0, 14, 14); // Create the ball
		playerPaddle = new GameObject(0, 0, 10, 80); // Create the player paddle
		cpuPaddle = new GameObject(0, 0, 10, 80); // Create the cpu paddle
		playerScore = 0; // Initialise the playerScore
		cpuScore = 0; // Initialise the cpuScore
		canvasWidth = 0; // Initialise canvasWidth
		canvasHeight = 0; // Initialise canvasHeight
		
		
		mouseListener mouseListener = new mouseListener(); // Create the mouse
					
		
		// listener
		addMouseListener(mouseListener); // Add the mouse listener to the panel
		addMouseMotionListener(mouseListener); // Add mouse motion listener to
												// the panel
		
		addMouseWheelListener(mouseListener);
		
		

		try {
			InputStream fontStream = GameLogic.class
					.getResourceAsStream("resources/pong.ttf"); // Read in PONG.TTF from
														// jar
			Font onePoint = Font.createFont(Font.TRUETYPE_FONT, fontStream); // Setup
																				// onePoint
																				// using
																				// PONG.TTF
			fontStream.close(); // Close the InputStream
			pongFont = onePoint.deriveFont(Font.PLAIN, 60); // Format pongFont
				
			
			clip01 = Applet.newAudioClip(GameLogic.class.getResource("resources/sample01.wav"));
			clip02 = Applet.newAudioClip(GameLogic.class.getResource("resources/sample02.wav"));
			clip03 = Applet.newAudioClip(GameLogic.class.getResource("resources/sample03.wav"));
			
			
			
		} catch (IOException e) // Catch Input Exception
		{
			System.err.println("ERR: IOException - Cannot read file PONG.TTF");
		} catch (FontFormatException e) // Catch Font Format Exception
		{
			System.err.println("ERR: FontFormatException");
		}
	}

	/*
	 * Create and start the animation thread
	 */
	public void addNotify() {
		super.addNotify(); // Override the addNotify method
		animate = new Thread(this); // Create the thread
		animate.start(); // Start the thread
	}
	

	/*
	 * Paints the components on the panel
	 */
	public void paint(Graphics g) {
		
		super.paint(g); // Override the default paint method
		
		Graphics2D g2 = (Graphics2D) g; // Use Graphics2D
		g2.scale(1.0, 0.89);

		// Get insets of Panel and reset drawing canvas inside insets
		Insets insets = getInsets();
				
		g2.translate(insets.left, insets.top);

		Dimension size = getSize(); // Get dimensions of Panel
		canvasWidth = (int) size.getWidth() - insets.left - insets.right; // Get
																			// width
																			// of
																			// canvas
																			// minus
																			// insets
		canvasHeight = (int) size.getHeight() - insets.top - insets.bottom; // Get
																			// height
																			// of
																			// canvas
																			// minus
																			// insets

		g2.setColor(Color.white); // Paint components in gray
		g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f)); // Set
																				// the
																				// stroke
																				// to
																				// dash
		g2.drawLine(canvasWidth / 2, 0, canvasWidth / 2, canvasHeight); // Draw
																		// the
																		// center
																		// line

		g2.setColor(Color.white); // Paint components in white

		if (gameStart) // Paint components in default positions if start of game
		{
			ball.setX((canvasWidth / 2) - (ball.getWidth() / 2)); // Position
																	// ball in
																	// middle of
																	// x axis
			ball.setY((canvasHeight / 2) - (ball.getHeight() / 2)); // Position
																	// ball in
																	// middle of
																	// y axis
			ball.setDeltaX(-4); // Set the speed of ball in the x direction
			ball.setDeltaY(-2); // Set the speed of the ball in the y direction

			playerPaddle.setX(10); // Set the x position of the playerPaddle
			playerPaddle.setY((canvasHeight / 2)
					- (playerPaddle.getHeight() / 2)); // Set the y position of
														// the playerPaddle

			playerPaddle.setDeltaX(0);
			playerPaddle.setDeltaY(0);
			
			cpuPaddle.setX(canvasWidth - (cpuPaddle.getWidth() * 2)); // Set the
																		// x
																		// position
																		// of
																		// the
																		// cpuPaddle
			cpuPaddle.setY((canvasHeight / 2) - (playerPaddle.getHeight() / 2)); // Set
																					// the
																					// y
																					// position
																					// of
																					// the
																					// cpuPaddle

			Font font = new Font("SansSerif", Font.BOLD, 12); // Set the font
			g2.setFont(font); // Use the new font
			FontMetrics defaultMetrics = g2.getFontMetrics(); // Get the metrics
																// of the font
			// Get the width of the strings
			int instructionWidth = defaultMetrics.stringWidth("Click to start");
			int instructionWidth2 = defaultMetrics
					.stringWidth("Move mouse to move paddle");
			int defaultFontHeight = defaultMetrics.getHeight(); // Get the
																// height of the
																// font
			// Draw the strings
			g2.drawString("Click to start", (canvasWidth / 2)
					- (instructionWidth / 2), (canvasHeight / 2)
					- defaultFontHeight - (ball.getHeight() * 2));
			g2.drawString("Move mouse to move paddle", (canvasWidth / 2)
					- (instructionWidth2 / 2), (canvasHeight / 2)
					- (ball.getHeight() * 2));
		}

		if (roundStart) // Re-center if start of the round
		{
			ball.setX((canvasWidth / 2) - (ball.getWidth() / 2)); // Position
																	// ball in
																	// middle of
																	// x axis
			ball.setY((canvasHeight / 2) - (ball.getHeight() / 2)); // Position
																	// ball in
																	// middle of
																	// y axis
			playerPaddle.setY((canvasHeight / 2)
					- (playerPaddle.getHeight() / 2)); // Position the
														// playerPaddle in
														// middle of y axis
			
			playerPaddle.setDeltaX(0);
			playerPaddle.setDeltaY(0);
			
			cpuPaddle.setY((canvasHeight / 2) - (playerPaddle.getHeight() / 2)); // Position
																					// the
																					// cpuPaddle
																					// in
																					// middle
																					// of
																					// y
																					// axis
		}

		ball.draw(g2);
		playerPaddle.draw(g2);
		cpuPaddle.draw(g2);


		g2.setFont(pongFont); // Use pongFont
		FontMetrics pongMetrics = g2.getFontMetrics(); // Get the metrics for
														// pongFont
		// Get the width of the score strings
		int playerScoreWidth = pongMetrics.stringWidth(Integer
				.toString(playerScore));
		int cpuScoreWidth = pongMetrics.stringWidth(Integer.toString(cpuScore));
		int pongFontHeight = pongMetrics.getHeight(); // Get the height of the
														// font.
		g2.drawString(Integer.toString(playerScore), (canvasWidth / 3)
				- (playerScoreWidth / 2), pongFontHeight); // Draw the player's
															// score
		g2.drawString(Integer.toString(cpuScore), ((canvasWidth / 3) * 2)
				- (cpuScoreWidth / 2), pongFontHeight); // Draw the cpu's score

		// Sync with toolkit (Linux) and clean up
		Toolkit.getDefaultToolkit().sync();
		g2.dispose();
	}

	public class mouseListener extends MouseInputAdapter {
		
		public void mouseReleased(MouseEvent e) // Mouse button clicked
		{
			gameStart = false; // Set game to started
			roundStart = false; // Start round
		}

		public void mouseMoved(MouseEvent e) // Mouse moved
		{
				
			//playerPaddle.setY(e.getY() - (playerPaddle.getHeight() / 2)); // Move
																			// playerPaddle
		}

		public void mouseDragged(MouseEvent e) // Mouse dragged
		{
			//playerPaddle.setY(e.getY() - (playerPaddle.getHeight() / 2)); // Move
																			// playerPaddle
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			
			playerPaddle.setDeltaY(playerPaddle.getDeltaY()+e.getWheelRotation());
			
		}
		
		
		
		
	}

	public void cpuPaddleMove() {
		if (ball.getDeltaX() < 0) // If ball is travelling away from cpuPaddle
		{
			// Center paddle
			if ((cpuPaddle.getY() + cpuPaddle.getHeight() / 2) < (canvasHeight / 2)) {
				cpuPaddle.setY(cpuPaddle.getY() + 3);
			}
			if ((cpuPaddle.getY() + cpuPaddle.getHeight() / 2) > (canvasHeight / 2)) {
				cpuPaddle.setY(cpuPaddle.getY() - 3);
			}
		}
		if (ball.getDeltaX() > 0) // If ball is travelling toward cpuPaddle
		{
			// Distance between ball and middle of cpuPaddle vertically
			int dist = Math.abs(ball.getY()
					- (cpuPaddle.getY() + cpuPaddle.getHeight() / 2));
			/*
			 * Move the paddle toward the ball at a speed of dist / 8 Changing
			 * the division changes the difficulty of the cpu
			 */
			if ((ball.getY() + ball.getHeight() / 2) < (cpuPaddle.getY() + cpuPaddle
					.getHeight() / 2)) {
				cpuPaddle.setY(cpuPaddle.getY() - dist / 8);
			}
			if ((ball.getY() + ball.getHeight() / 2) > (cpuPaddle.getY() + cpuPaddle
					.getHeight() / 2)) {
				cpuPaddle.setY(cpuPaddle.getY() + dist / 8);
			}
		}
	}

	public void collisionDetect() {
		/*
		 * Check if ball hit the left or right sides Reset the y direction of
		 * the ball Increase relevant score and set the round to start
		 */
		if (ball.getX() >= canvasWidth + ball.getWidth() * 2) {
			playerScore += 1;
			ball.setDeltaY(-2);
		
			roundStart = true;
			clip03.play();
		}
		if (ball.getX() <= ball.getWidth() * -2) {
			cpuScore += 1;
			ball.setDeltaY(-2);
			
			roundStart = true;
			clip03.play();
		}

		// Check if ball hit the top or bottom and invert the y direction
		if (ball.getY() + ball.getHeight() >= canvasHeight || ball.getY() <= 0) {
			ball.setDeltaY(ball.getDeltaY() * -1);
			clip01.play();
		}

		// Check if ball hit the playerPaddle and change y and x direction
		if (ball.getX() + ball.getDeltaX() < playerPaddle.getX()
				+ (ball.getWidth() / 2)
				&& ball.getX() >= playerPaddle.getX())
			if (ball.getY() + ball.getHeight() >= playerPaddle.getY()
					&& ball.getY() <= playerPaddle.getY()
							+ playerPaddle.getHeight()) {
				// Distance between middle of playerPaddle and ball vertically
				int playerDist = ball.getY() - playerPaddle.getY()
						+ (playerPaddle.getHeight() / 2);
				// Set y direction of ball
				ball.setDeltaY(ball.getDeltaY() + playerDist / 28);
				// Invert x direction of ball
				ball.setDeltaX(ball.getDeltaX() * -1);
				
				clip02.play();
			}

		// Check if ball hit the cpuPaddle and change y and x direction
		if (ball.getX() + ball.getDeltaX() > cpuPaddle.getX()
				- cpuPaddle.getWidth()
				&& ball.getX() <= cpuPaddle.getX())
			if (ball.getY() + ball.getWidth() >= cpuPaddle.getY()
					&& ball.getY() <= cpuPaddle.getY() + cpuPaddle.getHeight()) {
				// Distance between middle of cpuPaddle and ball vertically
				int cpuDist = ball.getY() - cpuPaddle.getY()
						+ (cpuPaddle.getHeight() / 2);
				// Set y direction of ball
				ball.setDeltaY(ball.getDeltaY() + cpuDist / 28);
				// Invert x direction of ball
				ball.setDeltaX(ball.getDeltaX() * -1);
				
				clip02.play();
			}
	}

	public void run() {
		long startTime; // startTime of current loop
		long timeDifference; // Difference between startTime and current time
		long sleep; // Amount of time to sleep

		startTime = System.currentTimeMillis(); // Get the current time in
												// milliseconds

		while (true) // Endless loop
		{
			if (!roundStart && !gameStart) // If not the start of round or game
			{
				
				playerPaddle.move();
				
				if((playerPaddle.getY() + playerPaddle.getHeight() / 2) < 0) {
					playerPaddle.setY( - playerPaddle.getHeight() /2);
					playerPaddle.setDeltaY(0);
				}
				
				if((playerPaddle.getY()  + playerPaddle.getHeight() / 2) > canvasHeight) {
					playerPaddle.setY(canvasHeight - playerPaddle.getHeight() / 2);
					playerPaddle.setDeltaY(0);
				}
				
				
				ball.move(); // Move the ball
				cpuPaddleMove(); // Move cpu paddle
				collisionDetect(); // Detect collisions
				repaint(); // Repaint everything
			}

			/*
			 * Check the difference between the startTime and the current time
			 * Calculate the difference between 10 and timeDifference Sleep for
			 * the difference in order to ensure a constant speed of animation
			 */
			timeDifference = System.currentTimeMillis() - startTime;
			sleep = 10 - timeDifference;
			if (sleep < 0) {
				sleep = 2;
			}

			try // Attempt to sleep
			{
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				
			}

			startTime = System.currentTimeMillis(); // Get the current time in
													// milliseconds
		}
	}
}
