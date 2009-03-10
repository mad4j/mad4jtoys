

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.prefs.Preferences;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;

/**
 * @author Daniele Olmisani (daniele.olmisani@gmail.com)
 */

@SuppressWarnings("serial")
public class G extends JFrame {
	
	public static void main(String argv[]) {
		
		// this gives us better performance on windows
		System.setProperty("sun.java2d.noddraw", "");
		
		new G();
	}

	
	private static final int BOARD_WIDTH = 32;
	private static final int BOARD_HEIGHT = 24;

	private static final int PIXEL_SIZE = 2;
	private static final int CELL_SIZE = 8*PIXEL_SIZE;


	private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
	
	private int speed = 300;
	private int step = speed;

	private boolean started = false;
	
	private String msg = "";
	private int dieWait;
	
	private int level;
	
	private int score;
	private int bestScore = 0;
	
	
	private MidiChannel channel;
	

	private int facesCount;
	private int goblinX;
	private int goblinY;
	
	private int TILE_NOTHING = 0;
	private int TILE_ROCK = 1;
	private int TILE_FACE_SAD = 2;
	private int TILE_FACE_HAPPY = 3;

	private Preferences prefs = Preferences.userRoot().node("/dolmisani/games/goblin4k");
	
	private G() {
		
		super("Goblin4k");
		
		// icon creation and setting
		BufferedImage temp = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D)temp.getGraphics();
		g.setColor(Color.YELLOW);
		drawFace(g, 0, 0, true);
		setIconImage(temp);
		
		setSize(800, 600);
		setResizable(false);

		setVisible(true);
		
		
		
		bestScore = prefs.getInt("hires", 0);

		enableEvents(AWTEvent.KEY_EVENT_MASK);

		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();

		init();

		long lastLoopTime = System.currentTimeMillis();

		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();
			
			synthesizer.open();
			
			channel = synthesizer.getChannels()[0];
			Patch patch = synthesizer.getAvailableInstruments()[407].getPatch();
	        channel.programChange(patch.getBank(), patch.getProgram());
		} catch (MidiUnavailableException e) {
		}
        
		while (true) {
			int delta = (int) (System.currentTimeMillis() - lastLoopTime);
			for (int i = 0; i < delta / 10; i++) {
				logic(10);
			}
			logic(delta % 10);

			lastLoopTime = System.currentTimeMillis();

			draw((Graphics2D) strategy.getDrawGraphics());

			strategy.show();
			if (!isVisible()) {
				System.exit(0);
			}
		}
	}

	private void randomBoard(int level) {

		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				board[x][y] = TILE_NOTHING;
			}
		}

		for (int i = 0; i < 65; i++) {
			int x = (int) (Math.random() * BOARD_WIDTH);
			int y = (int) (Math.random() * BOARD_HEIGHT-1);
			board[x][y] = TILE_ROCK;
		}

		for (int i = 0; i < 20; i++) {
			int x = (int) (Math.random() * BOARD_WIDTH);
			int y = (int) (Math.random() * BOARD_HEIGHT);
			board[x][y] = TILE_FACE_SAD;
		}
		
		facesCount = 20;
		goblinX = BOARD_WIDTH / 2;
		goblinY = BOARD_HEIGHT - 1;
	}

	private void renderBoard(Graphics2D g) {

		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {


				if (board[x][y] == TILE_ROCK) {
					
					
					
					g.setColor(Color.ORANGE);
					drawRock(g, 20+x*CELL_SIZE, 15+y*CELL_SIZE);
					
					
				}
				
				if (board[x][y] == TILE_FACE_SAD) {
					

					g.setColor(Color.BLACK);
					drawFace(g, 20+x*CELL_SIZE, 15+y*CELL_SIZE, false);

				}
						
				g.setColor(Color.green.darker());
				drawGoblin(g,20+goblinX*CELL_SIZE, 15+goblinY*CELL_SIZE);
			}
		}

	}

	/**
	 * Initialise the board for the next level
	 */
	private void init() {
		if (score > bestScore) {
			bestScore = score;
			prefs.putInt("hires", bestScore);
		}
		
		level = 1;
		score = 0;
		
		randomBoard(level);

		msg = "Press Enter To Start";
	}

	private void die() {
		started = false;
		msg = "You're Dead";
		dieWait = 1000;
	}

	/**
	 * Game logic.
	 * 
	 * @param delta
	 *            The amount of time passed since last we were called.
	 */
	private void logic(int delta) {
		if (!started) {
			dieWait -= delta;
			if (dieWait <= 0) {
				init();
			}

			return;
		}



		step -= delta;
		if(step <= 0) {
			
			goblinY--;
			if(goblinY < 0) {
				goblinY = BOARD_HEIGHT-1;
				
				score = Math.max(0, score-5);
				
			}
			step = speed;
		}
		
		if(board[goblinX][goblinY] == TILE_ROCK) {
			
			//playNote(channel, 22, 80, 150);
			
			channel.noteOn(22, 80);
			channel.noteOff(22);
			
			die();
		}
		
		if(board[goblinX][goblinY] == TILE_FACE_SAD) {
			
			board[goblinX][goblinY] = TILE_NOTHING;
			facesCount--;
			score += speed;
			
			//playNote(channel, 60, 50, 150);
			
			channel.noteOn(60, 50);
			channel.noteOff(60);
			
		}
		
		if(facesCount == 0) {
			level++;
			speed -= 10;
			randomBoard(level);
		}
		
		
	}

	
	/**
	 * Draw the game screen
	 * 
	 * @param g
	 *            The graphics context on which to draw the game
	 */
	private void draw(Graphics2D g) {
		// clear the background
		g.setColor(Color.white);
		g.fillRect(0, 0, 800, 600);

		g.translate(0, 30);

		
		renderBoard(g);
		
		g.setFont(g.getFont().deriveFont(Font.BOLD, 20.0f));

		if (!started) {
			g.setPaint(Color.black);
			g.fillRect(80, 165, 330, 50);
			g.setColor(Color.black);
			g.drawRect(80, 165, 330, 50);
			g.drawString(msg, ((500 - g.getFontMetrics().stringWidth(msg)) / 2), 200);
		}

		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(Font.BOLD, 14.0f));
		g.drawString(String.format("Goblin4k Score: %06d BestScore: %06d Level: %02d", score, bestScore, level), 20, 20);
		
	}

	protected void processKeyEvent(KeyEvent e) {
		
		if (e.getID() != KeyEvent.KEY_PRESSED) {
			return;
		}

		if ((e.getKeyCode() == KeyEvent.VK_LEFT & started)) {
			
			goblinX = goblinX-1;
			if(goblinX < 0) {
				goblinX += BOARD_WIDTH;
			}
		}
		
		if ((e.getKeyCode() == KeyEvent.VK_RIGHT && started)) {
			
			goblinX++;
			if(goblinX >= BOARD_WIDTH) {
				goblinX -= BOARD_WIDTH;
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if ((!started) && (dieWait <= 0)) {
				started = true;
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	private void drawFace(Graphics2D g, int x, int y, boolean happinessFlag) {
		
		
		g.fillRect(x+2*PIXEL_SIZE, y+0*PIXEL_SIZE, 4*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+1*PIXEL_SIZE, y+1*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+6*PIXEL_SIZE, y+1*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+0*PIXEL_SIZE, y+2*PIXEL_SIZE, 1*PIXEL_SIZE, 4*PIXEL_SIZE);
		g.fillRect(x+7*PIXEL_SIZE, y+2*PIXEL_SIZE, 1*PIXEL_SIZE, 4*PIXEL_SIZE);
		g.fillRect(x+2*PIXEL_SIZE, y+2*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+5*PIXEL_SIZE, y+2*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		
		g.fillRect(x+1*PIXEL_SIZE, y+6*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+6*PIXEL_SIZE, y+6*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+2*PIXEL_SIZE, y+7*PIXEL_SIZE, 4*PIXEL_SIZE, 1*PIXEL_SIZE);
		
		if(happinessFlag) {
			g.fillRect(x+2*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
			g.fillRect(x+5*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
			g.fillRect(x+3*PIXEL_SIZE, y+5*PIXEL_SIZE, 2*PIXEL_SIZE, 1*PIXEL_SIZE);
		} else {
			g.fillRect(x+2*PIXEL_SIZE, y+5*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
			g.fillRect(x+5*PIXEL_SIZE, y+5*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
			g.fillRect(x+3*PIXEL_SIZE, y+4*PIXEL_SIZE, 2*PIXEL_SIZE, 1*PIXEL_SIZE);
		}

		
	}
	
	
	private void drawRock(Graphics2D g, int x, int y) {
		
		
		g.fillRect(x+2*PIXEL_SIZE, y+0*PIXEL_SIZE, 4*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+1*PIXEL_SIZE, y+1*PIXEL_SIZE, 6*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+0*PIXEL_SIZE, y+2*PIXEL_SIZE, 8*PIXEL_SIZE, 4*PIXEL_SIZE);
		g.fillRect(x+1*PIXEL_SIZE, y+6*PIXEL_SIZE, 6*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+2*PIXEL_SIZE, y+7*PIXEL_SIZE, 4*PIXEL_SIZE, 1*PIXEL_SIZE);
		
	}
	
	private void drawGoblin(Graphics2D g, int x, int y) {
		
		
		g.fillRect(x+1*PIXEL_SIZE, y+0*PIXEL_SIZE, 6*PIXEL_SIZE, 1*PIXEL_SIZE);
		
		g.fillRect(x+0*PIXEL_SIZE, y+1*PIXEL_SIZE, 2*PIXEL_SIZE, 3*PIXEL_SIZE);
		g.fillRect(x+3*PIXEL_SIZE, y+1*PIXEL_SIZE, 2*PIXEL_SIZE, 3*PIXEL_SIZE);
		g.fillRect(x+6*PIXEL_SIZE, y+1*PIXEL_SIZE, 2*PIXEL_SIZE, 3*PIXEL_SIZE);
		
		g.fillRect(x+0*PIXEL_SIZE, y+3*PIXEL_SIZE, 8*PIXEL_SIZE, 1*PIXEL_SIZE);
		
		g.fillRect(x+0*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+2*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+5*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+7*PIXEL_SIZE, y+4*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		
		g.fillRect(x+1*PIXEL_SIZE, y+5*PIXEL_SIZE, 1*PIXEL_SIZE, 2*PIXEL_SIZE);
		g.fillRect(x+3*PIXEL_SIZE, y+5*PIXEL_SIZE, 2*PIXEL_SIZE, 2*PIXEL_SIZE);
		g.fillRect(x+6*PIXEL_SIZE, y+5*PIXEL_SIZE, 1*PIXEL_SIZE, 2*PIXEL_SIZE);
		
		g.fillRect(x+0*PIXEL_SIZE, y+7*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+2*PIXEL_SIZE, y+7*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+5*PIXEL_SIZE, y+7*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
		g.fillRect(x+7*PIXEL_SIZE, y+7*PIXEL_SIZE, 1*PIXEL_SIZE, 1*PIXEL_SIZE);
	}
	
/*	
	private static final void playNote(final MidiChannel channel, final int noteNumber, final int velocity, final long duration) {
		
		new Thread() {
			
			public void run() {
				
				channel.noteOn(noteNumber, velocity);
				
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) { 					
				}
				
				channel.noteOff(noteNumber);				
			}
		}.start();
	}
*/	
	
}




