/*
 * Goblin4k
 * Copyright (C) 2009 Daniele Olmisani (danile.olmisani@gmail.com)
 *
 * This file is part of Goblin4k.
 *
 * Goblin4k is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Goblin4k is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Original Code
 * --------------------------------------------------------------------------
 * 90 REM **COMPUTE JULY**
 * 100 PRINT "{CLR}" : POKE 52,28 : POKE 56,28 : CLR : POKE 36869,255 : POKE 36879,26
 * 110 IF S>HS THEN HS=S
 * 115 RESTORE : B=230 : Z=8152 : Z1=Z+30720 : W=0 : S=J : G=0
 * 120 FOR X=1 TO 32 : READ A : POKE X+7167,A : NEXT : FOR X=1 TO8 : READ A : POKE X+7423,A : NEXT
 * 130 PRINT "{CLR}{RVS ON}{GRN}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}G O B L I N"
 * 140 PRINT "{HOME}{RED}{DOWN}{DOWN}"SPC(12)"{RVS ON}HS="HS : PRINT"{HOME}{RVS ON}{BLK}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}O=LEFT{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}P=RIGHT"
 * 150 FOR I=1 TO 65
 * 160 X=INT(RND(1)*330)+7746
 * 170 IF PEEK(X)=B THEN 160
 * 180 POKE X,B : POKE X+30720,0 : NEXT I
 * 190 FOR I=1 TO 20
 * 200 X=INT(RND(1)*330)+7746
 * 210 IF PEEK(X)=B OR PEEK(X)=1 OR PEEK(X)=3 THEN 200
 * 220 IF PEEK(X+21)=B AND PEEK(X+22)=B AND PEEK(X+23)=B THEN POKE X,3 : POKE X+30720,0 : G=G+1 : GOTO 240
 * 230 POKE X,1 : POKE X+30720,0
 * 240 NEXT I
 * 250 POKE Z,32 : Z=Z-22 : Z1=Z1-22 : IF Z<7746 THEN Z=Z+374 : Z1=Z1+374
 * 260 GET A$ : IF A$="O" THEN Z=Z-1 : Z1=Z1-1
 * 270 IF A$="P" THEN Z=Z+1 : Z1=Z1+1
 * 280 IF PEEK(Z)=B THEN 410
 * 290 IF PEEK(Z)=1 THEN GOSUB 330
 * 300 POKE Z,0 : POKE Z1,0 : FOR T=1 TO 220 : NEXT
 * 310 IF W=20-G THEN J=S : GOSUB350 : GOTO110
 * 320 GOTO 250
 * 330 W=W+1:S=S+25:PRINT"{HOME}{BLU}{DOWN}{DOWN}{RVS ON}"S:POKE36878,15
 * 340 FOR T=235 TO 250 : POKE 36876,T : NEXT : POKE 36876,0 : RETURN
 * 350 PRINT"{HOME}{RED}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{RVS ON}******ALL RIGHT!******"
 * 355 FOR I=1 TO 10 : GET A$ : NEXT I : REM COLLECT GARBAGE
 * 360 FOR I=1 TO 25
 * 370 X=INT(RND(1)*15)+233
 * 380 POKE 36878,15 : POKE 36875,X
 * 390 FOR T=1 TO 30 : NEXT T : NEXT I
 * 400 POKE 36878,0 : POKE 36875,0 : RETURN
 * 410 POKE 36877,200 : FOR V=15 TO 0 STEP-1 : POKE 36878,V : NEXT : POKE 36877,0 : POKE Z,2
 * 420 FOR X=7746 TO 8075: IF PEEK(X)<>1 THEN NEXT X
 * 430 IF PEEK(X)=1 THEN POKE X,3 : NEXT X
 * 440 J=0
 * 445 FOR I=1 TO 10 : GET C$ : NEXT I
 * 450 PRINT "{HOME}{BLU}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{RIGHT}{RVS ON}PLAY AGAIN ? (Y/N)"
 * 465 GET C$ : IF C$="" THEN 465
 * 470 IF C$="Y" THEN 110
 * 490 POKE 36869,240 : POKE 36879,27 : POKE 52,30 : POKE 56,30 : PRINT"{CLR}SEE YA!"
 * 500 DATA 126,219,219,255,165,90,90,165,60,66,165,129,153,165,66,60
 * 510 DATA 170,85,170,85,126,219,255,189,60,66,165,129,165,153,66,60
 * 520 DATA 0,0,0,0,0,0,0,0
 */

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

import javax.swing.JEditorPane;

import sun.audio.AudioPlayer;

/**
 * Goblin4k is a remake of glorious game of the Commodore VIC20. This version is
 * developped using the Java4k coding rules.
 * 
 * @author Daniele Olmisani (daniele.olmisani@gmail.com)
 * 
 */
@SuppressWarnings("serial")
public class G extends Applet implements Runnable {

	// Game states
	private static final int STATE_NEW_GAME = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_GAME_OVER = 2;
	private static final int STATE_LEVEL_INIT = 3;
	private static final int STATE_LEVEL_BUILD = 4;

	// Game board size
	private static final int BOARD_WIDTH = 32;
	private static final int BOARD_HEIGHT = 24;

	// Game graphics dimensions
	private static final int PIXEL_SIZE = 2;
	private static final int TILE_SIZE = 8 * PIXEL_SIZE;
	
	private static final int STATUS_LINE_HEIGHT = 18;
	private static final int GAMEFIELD_WIDTH = BOARD_WIDTH * TILE_SIZE;
	private static final int GAMEFIELD_HEIGHT = BOARD_HEIGHT * TILE_SIZE + STATUS_LINE_HEIGHT;

	// Game tiles
	private static final int TILE_NOTHING = -1;

	private static final int TILE_GOBLIN_PLAY = 0;
	private static final int TILE_GOBLIN_CRASH = 1;
	private static final int TILE_ROCK = 2;
	private static final int TILE_FACE_SAD = 3;
	private static final int TILE_FACE_HAPPY = 4;

	private static final int MAX_TILES = 5;

	private static final int[][] SPRITES_DATA = {

			// Color: dark green
			{ 0xff00b200, 126, 219, 219, 255, 165, 90, 90, 165 },

			// Color: red
			{ 0xffff0000, 170, 85, 170, 85, 126, 219, 255, 189 },

			// Color: dark orange
			{ 0xffb28c00, 170, 85, 170, 85, 170, 85, 170, 85 },

			// Color: blue
			{ 0xff0000ff, 60, 66, 165, 129, 153, 165, 66, 60 },

			// Color: blue
			{ 0xff0000ff, 60, 66, 165, 129, 165, 153, 66, 60 } };

	// Number of elements on the board
	private static final int MAX_ROCKS = 65;
	private static final int MAX_FACES = 20;

	// Audio engine parameters
	private static final int SAMPLE_RATE = 16000;
	private static final double AUDIO_VOLUME = 0.3;
	
	private boolean key[] = new boolean[65535];
	private boolean keyLock[] = new boolean[65535];

	public void start() {
		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK);
		new Thread(this).start();
	}

	public void run() {

		int x = 0;
		int y = 0;
		int playerSpeed = 0;
		int stepCounter = 0;

		int facesCount = 0;

		int gameState = STATE_NEW_GAME;
		int score = 0;
		int bestScore = 0;

		int level = 0;

		int playerX = 0;
		int playerY = 0;

		int counter;

		String message;
		
		int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];

		/*
		 * // set the window size getContentPane().setPreferredSize( new
		 * Dimension(GAMEFIELD_WIDTH, GAMEFIELD_HEIGHT)); setResizable(false);
		 * pack();
		 * 
		 * // put the window at the center of the screen
		 * setLocationRelativeTo(null);
		 * 
		 * setDefaultCloseOperation(EXIT_ON_CLOSE);
		 */
		/*************************************************************
		 * GLOBAL INITIALIZATION
		 *************************************************************/

		Graphics g;

		BufferedImage[] tiles = new BufferedImage[MAX_TILES];

		for (counter = 0; counter < MAX_TILES; counter++) {
			tiles[counter] = new BufferedImage(TILE_SIZE, TILE_SIZE,
					BufferedImage.TYPE_INT_ARGB);

			g = tiles[counter].getGraphics();
			g.setColor(new Color(SPRITES_DATA[counter][0]));

			for (y = 0; y < 8; y++) {
				for (x = 0; x < 8; x++) {

					if ((SPRITES_DATA[counter][y + 1] & 1 << x) != 0) {

						g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE,
								PIXEL_SIZE);
					}
				}
			}
		}

		// TODO : Scenery
		BufferedImage scenery = new BufferedImage(GAMEFIELD_WIDTH,
				GAMEFIELD_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		g = scenery.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GAMEFIELD_WIDTH, GAMEFIELD_HEIGHT);

		enableEvents(8 | 16 | 32);
		/*
		 * createBufferStrategy(2); BufferStrategy strategy =
		 * getBufferStrategy();
		 * 
		 * // ok now on screen setIconImage(tiles[TILE_GOBLIN_PLAY]);
		 * setVisible(true);
		 */
		long nextFrameStart = System.nanoTime();
		while (true) {

			stepCounter++;
			nextFrameStart += 16666667;

			if (gameState == STATE_NEW_GAME) {

				level = 0;
				score = 0;

				playerSpeed = 30;
			}

			if (gameState == STATE_LEVEL_INIT) {

				facesCount = 0;
				counter = 0;

				level++;

				playerSpeed -= 2;
				if (playerSpeed < 0) {
					playerSpeed = 0;
				}

				for (x = 0; x < BOARD_WIDTH; x++) {
					for (y = 0; y < BOARD_HEIGHT; y++) {
						board[x][y] = TILE_NOTHING;
					}
				}
				gameState = STATE_LEVEL_BUILD;
			}

			if (gameState == STATE_LEVEL_BUILD) {
				// LEVEL INITIALIZATION

				if (counter < MAX_ROCKS) {

					do {
						x = (int) (Math.random() * BOARD_WIDTH);
						y = (int) (Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_ROCK;
					counter++;
				}

				if (facesCount < MAX_FACES) {

					do {
						x = (int) (Math.random() * BOARD_WIDTH);
						y = (int) (Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_FACE_SAD;
					facesCount++;
				}

				playerX = BOARD_WIDTH / 2;
				playerY = BOARD_HEIGHT - 1;

				if ((counter >= MAX_ROCKS) && (facesCount >= MAX_FACES)) {

					gameState = STATE_PLAYING;
				}
			}

			/*****************************************************************
			 * EVOLUTION
			 *****************************************************************/
			if (key[KeyEvent.VK_ESCAPE]) {
				System.exit(0);
			}

			if (gameState == STATE_GAME_OVER) {

				if (score > bestScore) {
					bestScore = score;
					// prefs.putInt(PREFS_NODE_NAME, bestScore);
				}

				for (y = 0; y < BOARD_HEIGHT; y++) {
					for (x = 0; x < BOARD_WIDTH; x++) {

						if (board[x][y] == TILE_FACE_SAD) {
							board[x][y] = TILE_FACE_HAPPY;
						}
					}
				}
			}

			if (gameState == STATE_PLAYING) {

				board[playerX][playerY] = TILE_NOTHING;

				if (key[KeyEvent.VK_LEFT] && !keyLock[KeyEvent.VK_LEFT]) {
					playerX -= 1;
					keyLock[KeyEvent.VK_LEFT] = true;
				}

				if (key[KeyEvent.VK_RIGHT] && !keyLock[KeyEvent.VK_RIGHT]) {
					playerX += 1;
					keyLock[KeyEvent.VK_RIGHT] = true;
				}

				if (stepCounter % playerSpeed == 0) {
					playerY -= 1;
				}

				if (playerY < 0) {
					playerY = BOARD_HEIGHT - 1;

					score -= 5;
					if (score < 0) {
						score = 0;
					}
				}
				if (playerX < 0) {

					playerX += BOARD_WIDTH;
				}

				if (playerX >= BOARD_WIDTH) {
					playerX -= BOARD_WIDTH;
				}

				if (board[playerX][playerY] == TILE_FACE_SAD) {

					facesCount--;

					score += 100;

					AudioPlayer.player.start(createSample(800, 0.08));

					if (facesCount == 0) {
						gameState = STATE_LEVEL_INIT;
					}
				}

				if (board[playerX][playerY] == TILE_ROCK) {

					gameState = STATE_GAME_OVER;

					AudioPlayer.player.start(createCrashSound());
				}
			}

			if (gameState == STATE_GAME_OVER) {

				board[playerX][playerY] = TILE_GOBLIN_CRASH;

			} else {

				board[playerX][playerY] = TILE_GOBLIN_PLAY;
			}

			if (key[KeyEvent.VK_ENTER]) {

				if (gameState == STATE_NEW_GAME) {
					gameState = STATE_LEVEL_INIT;
				}
				if (gameState == STATE_GAME_OVER) {
					gameState = STATE_NEW_GAME;
				}

			}

			/******************************************************************
			 * SCREEN UPDATE
			 ******************************************************************/
			// g = strategy.getDrawGraphics();

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.translate(getInsets().left, getInsets().top);

			// draw items

			if (gameState != STATE_NEW_GAME) {
				for (x = 0; x < BOARD_WIDTH; x++) {
					for (y = 0; y < BOARD_HEIGHT; y++) {

						if (board[x][y] != TILE_NOTHING) {

							g.drawImage(tiles[board[x][y]], x * TILE_SIZE, y
									* TILE_SIZE, null);
						}
					}
				}
			}

			/*
			 * setTitle(String.format(
			 * "Goblin4k -- Level: %02d(%02d faces) Score: %06d / %06d ", level,
			 * facesCount, score, bestScore));
			 */
			
			g.setColor(Color.BLACK);
			g.fillRect(0, GAMEFIELD_HEIGHT - STATUS_LINE_HEIGHT-2, GAMEFIELD_WIDTH, 1);
			
			g.setColor(Color.RED);
			g.setFont(new Font("sansserif", Font.BOLD | Font.ITALIC, STATUS_LINE_HEIGHT));
			g.drawString("Goblin4k" , 2, GAMEFIELD_HEIGHT);
			
			message = String.format(
					 "Level: %02d(%02d faces) Score: %06d / %06d ", level,
					 facesCount, score, bestScore);
			g.setColor(Color.BLACK);
			g.setFont(new Font("sansserif", Font.BOLD, STATUS_LINE_HEIGHT-4));
			g.drawString(message, GAMEFIELD_WIDTH-g.getFontMetrics()
					.stringWidth(message)-2, GAMEFIELD_HEIGHT);
			
			if (gameState == STATE_NEW_GAME) {

				JEditorPane titlePane = new JEditorPane(
						"text/html",
						"<font face=arial><font size=7 color=red><b>Goblin<i>4k</i></b></font><br><hr noshade color=red><br><font color=blue>a 4 Kb remake from Daniele Olmisani (daniele.olmisani@gmail.com)</font></font>");

				titlePane.setSize(getWidth(), getHeight());
				titlePane.paint(g);

				g.setColor(new Color(0xBBABCDEF, true));
				g.fillRect(0, (int) (getHeight() * 0.7), getWidth(), 30);
				g.setColor(Color.BLACK);
				message = "Goblin4k - Press ENTER to start";
				g.setFont(new Font("sansserif", Font.BOLD, STATUS_LINE_HEIGHT));
				g.drawString(message, (GAMEFIELD_WIDTH - g.getFontMetrics()
						.stringWidth(message)) / 2, (int) (getHeight() * 0.7) + 14
						+ (30 - 14) / 2);
			}

			if (gameState == STATE_GAME_OVER) {

				g.setColor(new Color(0xBBABCDEF, true));
				g.fillRect(0, (int) (getHeight() * 0.7), getWidth(), 30);
				g.setColor(Color.BLACK);
				String msg = "GAME OVER - Press ENTER to start";
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				g.drawString(msg, (GAMEFIELD_WIDTH - g.getFontMetrics()
						.stringWidth(msg)) / 2, (int) (getHeight() * 0.7) + 14
						+ (30 - 14) / 2);
			}

			getGraphics().drawImage(scenery, 0, 0, null);

			// Suspend until the next frame
			try {
				Thread
						.sleep(Math.max(0, nextFrameStart - System.nanoTime()) / 1000000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
	}

	protected void processKeyEvent(KeyEvent e) {

		key[e.getKeyCode()] = (e.getID() == KeyEvent.KEY_PRESSED);

		if (e.getID() == KeyEvent.KEY_RELEASED) {
			keyLock[e.getKeyCode()] = false;
		}
	}

	private static final InputStream createSample(double hertz, double duration) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			dos.writeInt(0x2e736e64); // magic number ".snd"
			dos.writeInt(24); // data offset
			dos.writeInt(-1); // data size (-1 unknown)
			dos.writeInt(3); // data format
			dos.writeInt(SAMPLE_RATE); // sample rate
			dos.writeInt(1); // channels

			int N = (int) (SAMPLE_RATE * duration);
			for (int i = 0; i <= N; i++) {
				// dos.writeShort((short)(Short.MAX_VALUE * (AUDIO_VOLUME *
				// Math.sin(2 * Math.PI * i * hertz / SAMPLE_RATE))));

				short s = (short) (Short.MAX_VALUE * (AUDIO_VOLUME * Math.sin(2
						* Math.PI * i * hertz / SAMPLE_RATE)));
				baos.write((s & 0xff00) >> 8);
				baos.write(s & 0xff);

				hertz += 1;
			}

		} catch (Exception e) {

		}

		return new ByteArrayInputStream(baos.toByteArray());
	}

	private static final InputStream createCrashSound() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			dos.writeInt(0x2e736e64); // magic number ".snd"
			dos.writeInt(24); // data offset
			dos.writeInt(-1); // data size (-1 unknown)
			dos.writeInt(3); // data format
			dos.writeInt(SAMPLE_RATE); // sample rate
			dos.writeInt(1); // channels

			int N = (int) (SAMPLE_RATE * 0.03);

			for (int i = 0; i <= N; i++) {

				short s = (short) (Short.MAX_VALUE * (AUDIO_VOLUME * 2.0 * (Math
						.random() - 0.5)));

				dos.writeShort(s);
			}

		} catch (Exception e) {

		}

		return new ByteArrayInputStream(baos.toByteArray());
	}

}
