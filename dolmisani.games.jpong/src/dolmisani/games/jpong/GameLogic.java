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

@SuppressWarnings("serial")
public class GameLogic extends JPanel implements Runnable {

	private ColorTheme[] THEMES = { new ColorTheme(Color.BLACK, Color.WHITE),
			new ColorTheme(Color.WHITE, Color.BLACK),
			new ColorTheme(Color.WHITE, Color.ORANGE) };

	private boolean gameStart; // Indicates if it is the start of the game
	private boolean roundStart; // Indicates if it is the start of the round
	private GameObject ball; // The ball
	private AIController aiController;
	private GameObject playerPaddle; // The player's paddle
	private GameObject cpuPaddle; // The cpu's paddle
	private GameObject gameField;
	private int playerScore; // The player's score
	private int cpuScore; // The cpu score

	private Font pongFont; // Score font
	private Thread animate; // Animation thread

	private int activeThemeIntdex;

	private AudioClip clip01;
	private AudioClip clip02;
	private AudioClip clip03;

	public GameLogic() {

		activeThemeIntdex = 0;

		// setBackground(activeTheme.getBackgroundColor()); // Set Panel to
		// black

		setDoubleBuffered(true); // Double buffered for smooth animation
		gameStart = true; // Set game to just started
		roundStart = true; // Set round to just started

		ball = new GameObject(0, 0, 14, 14); // Create the ball

		playerPaddle = new GameObject(0, 0, 10, 80); // Create the player paddle
		cpuPaddle = new GameObject(0, 0, 10, 80); // Create the cpu paddle
		aiController = new AIController(cpuPaddle, this);
		playerScore = 0; // Initialise the playerScore
		cpuScore = 0; // Initialise the cpuScore

		gameField = new GameObject(0, 0, 0, 0);

		mouseListener mouseListener = new mouseListener(); // Create the mouse

		// listener
		addMouseListener(mouseListener); // Add the mouse listener to the panel
		addMouseMotionListener(mouseListener); // Add mouse motion listener to
		// the panel

		addMouseWheelListener(mouseListener);

		try {
			InputStream fontStream = GameLogic.class
					.getResourceAsStream("resources/pong.ttf"); // Read in
																// PONG.TTF from
			// jar
			Font onePoint = Font.createFont(Font.TRUETYPE_FONT, fontStream); // Setup
			// onePoint
			// using
			// PONG.TTF
			fontStream.close(); // Close the InputStream
			pongFont = onePoint.deriveFont(Font.PLAIN, 60); // Format pongFont

			clip01 = Applet.newAudioClip(GameLogic.class
					.getResource("resources/sample01.wav"));
			clip02 = Applet.newAudioClip(GameLogic.class
					.getResource("resources/sample02.wav"));
			clip03 = Applet.newAudioClip(GameLogic.class
					.getResource("resources/sample03.wav"));

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

		ColorTheme activeTheme = THEMES[activeThemeIntdex % THEMES.length];

		setBackground(activeTheme.getBackgroundColor());

		super.paint(g); // Override the default paint method

		Graphics2D g2 = (Graphics2D) g; // Use Graphics2D

		// Get insets of Panel and reset drawing canvas inside insets
		Insets insets = getInsets();

		g2.translate(insets.left, insets.top);

		Dimension size = getSize(); // Get dimensions of Panel

		gameField.setWidth((int) size.getWidth() - insets.left - insets.right);
		gameField
				.setHeight((int) size.getHeight() - insets.top - insets.bottom);

		g2.setColor(activeTheme.getForegroundColor()); // Paint components in
														// gray
		g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f)); 
		g2.drawLine(gameField.getCenterX(), gameField.getY(), gameField
				.getCenterX(), gameField.getHeight());

		g2.setColor(activeTheme.getForegroundColor()); // Paint components in
														// white
		if (gameStart) // Paint components in default positions if start of game
		{

			ball.setCenterX(gameField.getCenterX());
			ball.setCenterY(gameField.getCenterY());

			ball.setDeltaX(-4); // Set the speed of ball in the x direction
			ball.setDeltaY(-2); // Set the speed of the ball in the y direction

			playerPaddle.setX(gameField.getX() + 2 * playerPaddle.getWidth());
			playerPaddle.setCenterY(gameField.getCenterY());

			playerPaddle.setDeltaX(0);
			playerPaddle.setDeltaY(0);

			cpuPaddle.setX(gameField.getWidth() - 2 * cpuPaddle.getWidth());
			cpuPaddle.setCenterY(gameField.getCenterY());

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
			g2.drawString("Click to start", gameField.getCenterX()
					- (instructionWidth / 2), gameField.getCenterY()
					- defaultFontHeight - (ball.getHeight() * 2));
			g2.drawString("Move mouse to move paddle", gameField.getCenterX()
					- (instructionWidth2 / 2), gameField.getCenterY()
					- (ball.getHeight() * 2));
		}

		if (roundStart) // Re-center if start of the round
		{
			ball.setCenterX(gameField.getCenterX());
			ball.setCenterY(gameField.getCenterY());

			playerPaddle.setCenterY(gameField.getCenterY());

			playerPaddle.setDeltaX(0);
			playerPaddle.setDeltaY(0);

			cpuPaddle.setCenterY(gameField.getCenterY());
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
		g2.drawString(Integer.toString(playerScore), (gameField.getWidth() / 3)
				- (playerScoreWidth / 2), pongFontHeight); // Draw the player's
		// score
		g2.drawString(Integer.toString(cpuScore),
				((gameField.getWidth() / 3) * 2) - (cpuScoreWidth / 2),
				pongFontHeight); // Draw the cpu's score

		// Sync with toolkit (Linux) and clean up
		Toolkit.getDefaultToolkit().sync();
		g2.dispose();
	}

	public class mouseListener extends MouseInputAdapter {

		@Override
		public void mouseReleased(MouseEvent e) // Mouse button clicked
		{
			gameStart = false; // Set game to started
			roundStart = false; // Start round
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {

			playerPaddle.setDeltaY(playerPaddle.getDeltaY()
					+ e.getWheelRotation());

		}

	}

	public void collisionDetect() {
		/*
		 * Check if ball hit the left or right sides Reset the y direction of
		 * the ball Increase relevant score and set the round to start
		 */
		if (ball.getX() >= gameField.getWidth() + ball.getWidth() * 2) {
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
		if (ball.getY() + ball.getHeight() >= gameField.getHeight()
				|| ball.getY() <= 0) {
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

				if ((playerPaddle.getCenterY()) < gameField.getY()) {
					playerPaddle.setCenterY(gameField.getY());
					playerPaddle.setDeltaY(0);
				}

				if ((playerPaddle.getCenterY()) > gameField.getHeight()) {
					playerPaddle.setCenterY(gameField.getHeight());
					playerPaddle.setDeltaY(0);
				}

				ball.move(); // Move the ball

				// cpuPaddleMove(); // Move cpu paddle
				aiController.control();

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

	public void switchColorTheme() {

		activeThemeIntdex++;
		repaint();
	}

	public GameObject getBallObject() {

		return ball;
	}

	public GameObject getGameFieldObject() {

		return gameField;
	}

}
