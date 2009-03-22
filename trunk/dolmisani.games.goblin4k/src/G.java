import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.prefs.Preferences;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;

/*
 * Template for a 4K Game.
 * Copyright 2007 - Alexander Hristov. http://www.ahristov.com/tutorial
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307 USA 
 */


@SuppressWarnings("serial")
public class G extends JFrame {
	
	
	
	private static final int STATE_NEW_GAME = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_GAME_OVER = 2;
	private static final int STATE_INIT = 3;
	private static final int STATE_INIT_LEVEL = 4;
	private static final int STATE_LIFE_LOST = 5;
	
	
	
	
	
	private static final int BOARD_WIDTH = 32;
	private static final int BOARD_HEIGHT = 24;

	private static final int PIXEL_SIZE = 2;
	private static final int TILE_SIZE = 8 * PIXEL_SIZE;
	
	private static final int GAMEFILED_WIDTH = BOARD_WIDTH * TILE_SIZE;
	private static final int GAMEFILED_HEIGHT = BOARD_HEIGHT * TILE_SIZE;


	private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];

	private int speed = 30;
	private int step = speed;

	private boolean started = false;

	private String msg = "";
	private int dieWait;


	private int bestScore = 0;


	private int facesCount;
	private int goblinX;
	private int goblinY;

	private int TILE_NOTHING = -1;
	private int TILE_ROCK = 0;
	private int TILE_FACE_SAD = 1;
	private int TILE_FACE_HAPPY = 2;
	
	private int MAX_TILES = 3;
	
	private int stepCounter = 0;
	

	private Preferences prefs = Preferences.userRoot().node(
			"/dolmisani/games/goblin4k");
	
	
	
	
	
	
	/**
	 * states
	 * 0 = Waiting for new game (moves to 3)
	 * 1 = Playing   (moves to 2)
	 * 2 = Game over (moves to 3)
	 * 3 = Initialization request (moves to 1)
	 * 4 = Level initialization request(moves to 1)
	 * 5 = Life lost (moves to 4 on keypress)
	 */	
	private boolean key[] = new boolean[65535];
	private boolean keyLock[] = new boolean[65535];
	
	
	public static final int ITEM_GOLD = 1;
	public static final int ITEM_BOULDER = 2;
	public static final int ITEM_DIAMOND = 3;
	public static final int MAX_ITEM_TYPES = ITEM_DIAMOND+1;
	public static final int MAX_ITEMS = 100;
	public static final int PLAYER_SIZE = 64;
	public static final int ITEM_SIZE = 64;
	public static final int PLAYER_SPEED = 10;
	
	public static final int SCORE_POSITION = 400;
	public static final int LIVES_POSITION = SCORE_POSITION + 150;
	
		
	public static void main(String[] args) {
		new G();
	}
	
	
	public G() {
		
		super("Goblin4k");
		
		//set the window size
		getContentPane().setPreferredSize(new Dimension(GAMEFILED_WIDTH, GAMEFILED_HEIGHT));
		setResizable(false);
		pack();
		
		/*************************************************************
		 * GLOBAL INITIALIZATION
		 *************************************************************/
		
		
		Graphics2D g;
		
		BufferedImage playerImage = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		drawGoblin(playerImage.createGraphics());
		
		
		
		BufferedImage[] tiles = new BufferedImage[MAX_TILES];
		
		//tiles[0];
		
		tiles[TILE_ROCK] = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		drawRock(tiles[TILE_ROCK].createGraphics());
		
		tiles[TILE_FACE_SAD] = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		drawFace(tiles[TILE_FACE_SAD].createGraphics(), false);
		
		tiles[TILE_FACE_HAPPY] = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		drawFace(tiles[TILE_FACE_HAPPY].createGraphics(), true);
		
		MidiChannel channel = null;
		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();

			synthesizer.open();

			channel = synthesizer.getChannels()[0];
			Patch patch = synthesizer.getAvailableInstruments()[407].getPatch();
			channel.programChange(patch.getBank(), patch.getProgram());
		} catch (MidiUnavailableException e) {
		}
		
		
		Random random = new Random();
		int state = STATE_NEW_GAME;
		int score = 0;
		
		int level = 1;
		

		int playerX = 0;
		int playerY = 0;
		
		// TODO : Scenery
		BufferedImage scenery = new BufferedImage(GAMEFILED_WIDTH, GAMEFILED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		g = scenery.createGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GAMEFILED_WIDTH, GAMEFILED_HEIGHT);
		
	
		enableEvents(8 | 16 | 32);
		
		
		
		//put the window at the center of the screen
		setLocationRelativeTo(null);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();
		
		long nextFrameStart = System.nanoTime();
		while(true) {
			
			nextFrameStart += 16666667;
			//nextFrameStart   += 33333333;
			//nextFrameStart     += 66666667;
			
			//long t=System.currentTimeMillis();
			//while (System.currentTimeMillis()-t < 20) Thread.yield();
			
			if (state == STATE_INIT || state == STATE_INIT_LEVEL) {
				// LEVEL INITIALIZATION
				
				
				for (int x = 0; x < BOARD_WIDTH; x++) {
					for (int y = 0; y < BOARD_HEIGHT; y++) {
						board[x][y] = TILE_NOTHING;
					}
				}

				for (int i = 0; i < 65; i++) {
					int x = (int) (Math.random() * BOARD_WIDTH);
					int y = (int) (Math.random() * BOARD_HEIGHT - 1);
					board[x][y] = TILE_ROCK;
				}

				for (int i = 0; i < 20; i++) {
					int x = random.nextInt(BOARD_WIDTH);
					int y = random.nextInt(BOARD_HEIGHT);
					board[x][y] = TILE_FACE_SAD;
				}

				facesCount = 20;
				playerX = BOARD_WIDTH / 2;
				playerY = BOARD_HEIGHT - 1;	
				
				
				if (state == STATE_INIT) {
					// GAME INITIALIZATION
					score = 0;
				}
				state = STATE_PLAYING;
			}
			
			/*****************************************************************
			 * EVOLUTION
			 *****************************************************************/
			if(key[KeyEvent.VK_ESCAPE]) {
				System.exit(0);
			}
			
			if (key[KeyEvent.VK_ENTER]) {
				if (state == STATE_LIFE_LOST) {
					state = STATE_INIT_LEVEL;
				}
				if (state == STATE_NEW_GAME || state == STATE_GAME_OVER) {
					state = STATE_INIT ;
				}
			}			
			if (state == STATE_PLAYING) {
				
				// TODO: Level update formula
				level = 1+score/1000; 
				
				// Player evolution
				//if (key[KeyEvent.VK_UP]) { playerY -= PLAYER_SPEED; }
				//if (key[KeyEvent.VK_DOWN]) { playerY += PLAYER_SPEED; }
				
				stepCounter++;
				
				
				if (key[KeyEvent.VK_LEFT] && !keyLock[KeyEvent.VK_LEFT]) {
					
					playerX -= 1;
					keyLock[KeyEvent.VK_LEFT] = true;					
				}
				
				if (key[KeyEvent.VK_RIGHT] && !keyLock[KeyEvent.VK_RIGHT]) {
					
					playerX += 1;
					keyLock[KeyEvent.VK_RIGHT] = true;
				}
								
				if(stepCounter%speed == 0) {				
					playerY -= 1;
				}
				
				if (playerY < 0) {
					playerY = BOARD_HEIGHT-1;
				}
				if (playerX < 0) {
					
					playerX += BOARD_WIDTH;
				}
				
				if (playerX >= BOARD_WIDTH) {
					playerX -= BOARD_WIDTH;
				}
				
				
				if (board[playerX][playerY] == TILE_ROCK) {

					channel.noteOn(22, 80);
					channel.noteOff(22);

					state = STATE_GAME_OVER;
				}
				
				if(board[playerX][playerY] == TILE_FACE_SAD) {
					
					board[playerX][playerY] = TILE_NOTHING;
					facesCount--;
					
					channel.noteOn(60, 50);
					channel.noteOff(60);
				}
				
	
			}

			
			/******************************************************************
			 * SCREEN UPDATE
			 ******************************************************************/
			g = (Graphics2D)strategy.getDrawGraphics();
			g.translate(getInsets().left, getInsets().top);
			g.drawImage(scenery, 0, 0, null);
			
			
			//draw items
			for (int x = 0; x < BOARD_WIDTH; x++) {
				for (int y = 0; y < BOARD_HEIGHT; y++) {

					if (board[x][y] != TILE_NOTHING) {
						
						g.drawImage(tiles[board[x][y]], x*TILE_SIZE, y*TILE_SIZE, null);
					}
				}
			}
			
			// Draw player
			g.drawImage(playerImage, playerX*TILE_SIZE, playerY*TILE_SIZE, null);
			
			
			setTitle(String.format(
					"Goblin4k -- Level: %02d(%02d faces) Score: %06d / %06d ",
					1, facesCount, score, bestScore));
			
			
			if (state == STATE_GAME_OVER || state == STATE_NEW_GAME) {
				g.setColor(Color.red);
				g.drawString("GAME OVER. Press 'Enter' To Start ",140,300);
			}
			if (state == STATE_LIFE_LOST) {
				g.setColor(Color.red);
				g.drawString("Booom ",140,300);
			}
			
			
			strategy.show();
			
			long remaining = nextFrameStart - System.nanoTime();
			System.out.println(remaining);
			if (remaining > 0) {
				
				try {
					Thread.sleep(remaining / 1000000);
				} catch (Throwable t) {
				}
			}
			
		}
	}

	
	
	protected void processKeyEvent(KeyEvent e) {
		
		key[e.getKeyCode()] = e.getID() == KeyEvent.KEY_PRESSED;
			
		if(e.getID() == KeyEvent.KEY_RELEASED) {
			keyLock[e.getKeyCode()] = false;
		}
	}

	
	
	private void drawGoblin(Graphics2D g) {
		
		g.setColor(Color.green.darker());

		g.fillRect(1 * PIXEL_SIZE, 0 * PIXEL_SIZE, 6 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(0 * PIXEL_SIZE, 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);
		g.fillRect(3 * PIXEL_SIZE, 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);
		g.fillRect(6 * PIXEL_SIZE, 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);

		g.fillRect(0 * PIXEL_SIZE, 3 * PIXEL_SIZE, 8 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(0 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(2 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(5 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(7 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(1 * PIXEL_SIZE, 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				2 * PIXEL_SIZE);
		g.fillRect(3 * PIXEL_SIZE, 5 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				2 * PIXEL_SIZE);
		g.fillRect(6 * PIXEL_SIZE, 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				2 * PIXEL_SIZE);

		g.fillRect(0 * PIXEL_SIZE, 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(2 * PIXEL_SIZE, 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(5 * PIXEL_SIZE, 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(7 * PIXEL_SIZE, 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
	}

	private void drawRock(Graphics2D g) {

		g.setColor(Color.ORANGE);
		
		g.fillRect(1 * PIXEL_SIZE, 1 * PIXEL_SIZE, 6 * PIXEL_SIZE,
				6 * PIXEL_SIZE);

	}
	
	private void drawFace(Graphics2D g, boolean happinessFlag) {

		g.setColor(Color.BLACK);
		
		g.fillRect(2 * PIXEL_SIZE, 0 * PIXEL_SIZE, 4 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(1 * PIXEL_SIZE, 1 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(6 * PIXEL_SIZE, 1 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(0 * PIXEL_SIZE, 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				4 * PIXEL_SIZE);
		g.fillRect(7 * PIXEL_SIZE, 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				4 * PIXEL_SIZE);
		g.fillRect(2 * PIXEL_SIZE, 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(5 * PIXEL_SIZE, 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(1 * PIXEL_SIZE, 6 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(6 * PIXEL_SIZE, 6 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(2 * PIXEL_SIZE, 7 * PIXEL_SIZE, 4 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		if (happinessFlag) {
			g.fillRect(2 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(5 * PIXEL_SIZE, 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(3 * PIXEL_SIZE, 5 * PIXEL_SIZE, 2 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
		} else {
			g.fillRect(2 * PIXEL_SIZE, 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(5 * PIXEL_SIZE, 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(3 * PIXEL_SIZE, 4 * PIXEL_SIZE, 2 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
		}

	}
	

}
	

