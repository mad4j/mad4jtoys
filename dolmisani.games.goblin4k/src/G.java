/*
 * Goblin4k
 * Copyright (C) 2009 Daniele Olmisani (danile.olmisani@gmail.com)
 *
 * This file is part of Goblin4k.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage; //import java.util.prefs.Preferences;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class G extends JFrame {

	private static final int STATE_NEW_GAME = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_GAME_OVER = 2;
	private static final int STATE_INIT = 3;
	private static final int STATE_INIT_LEVEL = 4;

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

	//private static final String PREFS_NODE_ROOT = "/dolmisani/games/goblin4k";
	//private static final String PREFS_NODE_NAME = "hires";

	private boolean key[] = new boolean[65535];
	private boolean keyLock[] = new boolean[65535];

	public static void main(String[] args) {
		new G();
	}

	public G() {

		super("Goblin4k");

		int x, y, i;
		int speed = 30;
		int stepCounter = 0;

		int facesCount = 0;
		int rocksCount = 0;

		int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];

		// Preferences prefs = Preferences.userRoot().node(PREFS_NODE_ROOT);

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
		} catch (MidiUnavailableException e) {
		}

		int state = STATE_NEW_GAME;
		int score = 0;
		// int bestScore = prefs.getInt(PREFS_NODE_NAME, 0);
		int bestScore = 0;

		int level = 0;

		int playerX = 0;
		int playerY = 0;

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

			if (state == STATE_NEW_GAME) {

				level = 0;
				score = 0;
				state = STATE_INIT;
			}

			if (state == STATE_INIT) {

				facesCount = 0;
				rocksCount = 0;

				level++;

				for (x = 0; x < BOARD_WIDTH; x++) {
					for (y = 0; y < BOARD_HEIGHT; y++) {
						board[x][y] = TILE_NOTHING;
					}
				}

				state = STATE_INIT_LEVEL;
			}

			if (state == STATE_INIT_LEVEL) {
				// LEVEL INITIALIZATION

				if (rocksCount < 65) {

					// for (i = 0; i < 65; i++) {
					do {
						x = (int) (Math.random() * BOARD_WIDTH);
						y = (int) (Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_ROCK;
					rocksCount++;

				}

				if (facesCount < 20) {
					// for (i = 0; i < 20; i++) {
					do {
						x = (int) (Math.random() * BOARD_WIDTH);
						y = (int) (Math.random() * BOARD_HEIGHT - 1);
					} while (board[x][y] != TILE_NOTHING);
					board[x][y] = TILE_FACE_SAD;
					facesCount++;
				}

				// facesCount = 20;
				playerX = BOARD_WIDTH / 2;
				playerY = BOARD_HEIGHT - 1;

				// if(facesCount >= 20) {
				//if (state == STATE_INIT) {
					// GAME INITIALIZATION
				//	level = 1;
				//	score = 0;
				//}
				// if (state != STATE_NEW_GAME) {
				// state = STATE_PLAYING;
				// }
				// }
			}

			if ((rocksCount >= 65) && (facesCount >= 20)) {
				state = STATE_PLAYING;
			}

			/*****************************************************************
			 * EVOLUTION
			 *****************************************************************/
			if (key[KeyEvent.VK_ESCAPE]) {
				System.exit(0);
			}

			if (key[KeyEvent.VK_ENTER]) {
				if (state == STATE_NEW_GAME) {
					state = STATE_INIT;
				}
				if (state == STATE_GAME_OVER) {
					state = STATE_NEW_GAME;
				}

			}

			if (state == STATE_GAME_OVER) {

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

			if (state == STATE_PLAYING) {

				// Player evolution
				// if (key[KeyEvent.VK_UP]) { playerY -= PLAYER_SPEED; }
				// if (key[KeyEvent.VK_DOWN]) { playerY += PLAYER_SPEED; }

				if (key[KeyEvent.VK_LEFT] && !keyLock[KeyEvent.VK_LEFT]) {

					playerX -= 1;
					keyLock[KeyEvent.VK_LEFT] = true;
				}

				if (key[KeyEvent.VK_RIGHT] && !keyLock[KeyEvent.VK_RIGHT]) {

					playerX += 1;
					keyLock[KeyEvent.VK_RIGHT] = true;
				}

				if (stepCounter % speed == 0) {
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

				if (board[playerX][playerY] == TILE_ROCK) {

					channel.noteOn(22, 80);
					channel.noteOff(22);

					state = STATE_GAME_OVER;
					// state = STATE_NEW_GAME;
				}

				if (board[playerX][playerY] == TILE_FACE_SAD) {

					board[playerX][playerY] = TILE_NOTHING;
					facesCount--;

					score += 100;

					channel.noteOn(60, 50);
					channel.noteOff(60);

					if (facesCount == 0) {
						state = STATE_INIT;
					}
				}
			}

			/******************************************************************
			 * SCREEN UPDATE
			 ******************************************************************/
			g = (Graphics2D) strategy.getDrawGraphics();
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

			if (state == STATE_NEW_GAME) {
				g.setColor(Color.red);
				g.drawString("GAME OVER. Press 'Enter' To Start ", 140, 300);
			}

			if (state == STATE_GAME_OVER) {

				g.setColor(new Color(0xaa000000));
				g.fillRect(0, (int) (getHeight() * 0.7), getWidth(), 30);
				g.setColor(Color.WHITE);
				String msg = "Game Over";
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				g.drawString(msg, (GAMEFILED_WIDTH - g.getFontMetrics()
						.stringWidth(msg)) / 2, (int) (getHeight() * 0.7)
						+ (30 - 14) / 2);

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

		key[e.getKeyCode()] = e.getID() == KeyEvent.KEY_PRESSED;

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
