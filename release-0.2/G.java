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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage; 
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;

/**
 *  Goblin4k is a remake of glorious game of the Commodore Vic20.
 *  This version is developped using the Java4k coding rules.
 *  
 * @author Daniele Olmisani (daniele.olmisani@gmail.com)
 *
 */
@SuppressWarnings("serial")
public class G extends JFrame {

	private static final int STATE_NEW_GAME = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_GAME_OVER = 2;
	private static final int STATE_LEVEL_INIT = 3;
	private static final int STATE_LEVEL_BUILD = 4;

	private static final int BOARD_WIDTH = 32;
	private static final int BOARD_HEIGHT = 24;

	private static final int PIXEL_SIZE = 2;
	private static final int TILE_SIZE = 8 * PIXEL_SIZE;

	private static final int GAMEFILED_WIDTH = BOARD_WIDTH * TILE_SIZE;
	private static final int GAMEFILED_HEIGHT = BOARD_HEIGHT * TILE_SIZE;

	private static final int TILE_NOTHING = -1;
	private static final int TILE_ROCK = 0;
	private static final int TILE_FACE_SAD = 1;
	private static final int TILE_FACE_HAPPY = 2;

	private static final int MAX_TILES = 3;

	private static final int MAX_ROCKS = 65;
	private static final int MAX_FACES = 20;
	

	private boolean key[] = new boolean[65535];
	private boolean keyLock[] = new boolean[65535];

	public static void main(String[] args) {
		new G();
	}

	public G() {

		super("Goblin4k");

		int x = 0;
		int y = 0;
		int playerSpeed = 0;
		int stepCounter = 0;

		int facesCount = 0;
		int rocksCount = 0;

		int gameState = STATE_NEW_GAME;
		int score = 0;
		int bestScore = 0;

		int level = 0;

		int playerX = 0;
		int playerY = 0;
		
		int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];


		// set the window size
		getContentPane().setPreferredSize(
				new Dimension(GAMEFILED_WIDTH, GAMEFILED_HEIGHT));
		setResizable(false);
		pack();

		// put the window at the center of the screen
		setLocationRelativeTo(null);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/*************************************************************
		 * GLOBAL INITIALIZATION
		 *************************************************************/

		Graphics2D g;

		BufferedImage playerImage = new BufferedImage(TILE_SIZE, TILE_SIZE,
				BufferedImage.TYPE_INT_ARGB);
		drawGoblin(playerImage.createGraphics());

		BufferedImage[] tiles = new BufferedImage[MAX_TILES];

		tiles[TILE_ROCK] = new BufferedImage(TILE_SIZE, TILE_SIZE,
				BufferedImage.TYPE_INT_ARGB);
		drawRock(tiles[TILE_ROCK].createGraphics());
		
		tiles[TILE_FACE_SAD] = new BufferedImage(TILE_SIZE, TILE_SIZE,
				BufferedImage.TYPE_INT_ARGB);
		drawFace(tiles[TILE_FACE_SAD].createGraphics(), false);

		tiles[TILE_FACE_HAPPY] = new BufferedImage(TILE_SIZE, TILE_SIZE,
				BufferedImage.TYPE_INT_ARGB);
		drawFace(tiles[TILE_FACE_HAPPY].createGraphics(), true);

		MidiChannel channel = null;
		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();

			synthesizer.open();

			channel = synthesizer.getChannels()[0];
			Patch patch = synthesizer.getAvailableInstruments()[407].getPatch();
			channel.programChange(patch.getBank(), patch.getProgram());
		} catch (Exception e) {
		}

	

		// TODO : Scenery
		BufferedImage scenery = new BufferedImage(GAMEFILED_WIDTH,
				GAMEFILED_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		g = scenery.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GAMEFILED_WIDTH, GAMEFILED_HEIGHT);

		enableEvents(8 | 16 | 32);

		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();

		// ok now on screen
		setIconImage(playerImage);
		setVisible(true);

		long nextFrameStart = System.nanoTime();
		while (true) {
			
			stepCounter++;
			nextFrameStart += 16666667;

			if (gameState == STATE_NEW_GAME) {
				
				level = 0;
				score = 0;
				
				playerSpeed = 30;
			}

			if(gameState == STATE_LEVEL_INIT) {

				facesCount = 0;
				rocksCount = 0;

				level++;
				playerSpeed -= 2;
				if(playerSpeed < 0) {
					playerSpeed = 0;
				}

				for (x = 0; x < BOARD_WIDTH; x++) {
					for (y = 0; y < BOARD_HEIGHT; y++) {
						board[x][y] = TILE_NOTHING;
					}
				}
				gameState = STATE_LEVEL_BUILD;
			}

			if(gameState == STATE_LEVEL_BUILD) {
				// LEVEL INITIALIZATION

				if (rocksCount < MAX_ROCKS) {

					do {
						x = (int) (Math.random() * BOARD_WIDTH);
						y = (int) (Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_ROCK;
					rocksCount++;
				}

				if (facesCount < MAX_FACES) {

					do {
						x = (int)(Math.random() * BOARD_WIDTH);
						y = (int)(Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_FACE_SAD;
					facesCount++;
				}

				playerX = BOARD_WIDTH / 2;
				playerY = BOARD_HEIGHT - 1;

				if ((rocksCount >= MAX_ROCKS) && (facesCount >= MAX_FACES)) {
					
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

					board[playerX][playerY] = TILE_NOTHING;
					facesCount--;

					score += 100;

					channel.noteOn(60, 50);
					channel.noteOff(60);

					if (facesCount == 0) {
						gameState = STATE_LEVEL_INIT;
					}
				}
				
				if (board[playerX][playerY] == TILE_ROCK) {

					channel.noteOn(22, 80);
					channel.noteOff(22);

					gameState = STATE_GAME_OVER;
				}

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
			g = (Graphics2D)strategy.getDrawGraphics();
			g.translate(getInsets().left, getInsets().top);
			g.drawImage(scenery, 0, 0, null);

			// draw items
			for (x = 0; x < BOARD_WIDTH; x++) {
				for (y = 0; y < BOARD_HEIGHT; y++) {

					if (board[x][y] != TILE_NOTHING) {

						g.drawImage(tiles[board[x][y]], x * TILE_SIZE, y
								* TILE_SIZE, null);
					}
				}
			}

			// Draw player
			g.drawImage(playerImage, playerX * TILE_SIZE, playerY * TILE_SIZE,
					null);

			setTitle(String.format(
					"Goblin4k -- Level: %02d(%02d faces) Score: %06d / %06d ",
					level, facesCount, score, bestScore));

			if(gameState == STATE_NEW_GAME) {
				g.setColor(new Color(0xBBABCDEF, true));
				g.fillRect(0, (int) (getHeight() * 0.7), getWidth(), 30);
				g.setColor(Color.BLACK);
				String msg = "Goblin4k - Press ENTER to start";
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				g.drawString(msg, (GAMEFILED_WIDTH - g.getFontMetrics()
						.stringWidth(msg)) / 2, (int) (getHeight() * 0.7)
						+ 14+(30 - 14) / 2);
			}

			if(gameState == STATE_GAME_OVER) {

				g.setColor(new Color(0xBBABCDEF, true));
				g.fillRect(0, (int) (getHeight() * 0.7), getWidth(), 30);
				g.setColor(Color.BLACK);
				String msg = "GAME OVER - Press ENTER to start";
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				g.drawString(msg, (GAMEFILED_WIDTH - g.getFontMetrics()
						.stringWidth(msg)) / 2, (int) (getHeight() * 0.7)
						+ 14+(30 - 14) / 2);
			}

			strategy.show();

			// Suspend until the next frame
			// TODO: what happen if sleep parameter is negative??
			try {
				Thread.sleep((nextFrameStart - System.nanoTime()) / 1000000);
			} catch (Throwable t) {
			}
		}
	}

	protected void processKeyEvent(KeyEvent e) {

		key[e.getKeyCode()] = (e.getID() == KeyEvent.KEY_PRESSED);

		if (e.getID() == KeyEvent.KEY_RELEASED) {
			keyLock[e.getKeyCode()] = false;
		}
		
	}

	private static final void drawGoblin(Graphics2D g) {

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

	private static final void drawRock(Graphics2D g) {

		g.setColor(Color.ORANGE);

		g.fillRect(1 * PIXEL_SIZE, 1 * PIXEL_SIZE, 6 * PIXEL_SIZE,
				6 * PIXEL_SIZE);

	}

	private static final void drawFace(Graphics2D g, boolean happinessFlag) {

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
