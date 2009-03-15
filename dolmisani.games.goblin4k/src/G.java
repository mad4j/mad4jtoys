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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
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

	private static final int BOARD_WIDTH = 32;
	private static final int BOARD_HEIGHT = 24;

	private static final int PIXEL_SIZE = 2;
	private static final int CELL_SIZE = 8 * PIXEL_SIZE;
	
	private static final int GAMEFILED_WIDTH = BOARD_WIDTH * CELL_SIZE;
	private static final int GAMEFILED_HEIGHT = BOARD_HEIGHT * CELL_SIZE;


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
	

	private Preferences prefs = Preferences.userRoot().node(
			"/dolmisani/games/goblin4k");

	private G() {

		super("Goblin4k");

		// icon creation and setting
		BufferedImage temp = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D)temp.getGraphics();
		g.setColor(Color.YELLOW);
		drawFace(g, 0, 0, true);
		setIconImage(temp);

		
	    //leave enough space to contain the game field
	    getContentPane().setPreferredSize(new Dimension(GAMEFILED_WIDTH, GAMEFILED_HEIGHT));
	    pack();
	    
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setResizable(false);

		//put the window at the center of the screen
		setLocationRelativeTo(null);
		
		setVisible(true);

		bestScore = prefs.getInt("hires", 0);

		enableEvents(AWTEvent.KEY_EVENT_MASK);

		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();

		init();

		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();

			synthesizer.open();

			channel = synthesizer.getChannels()[0];
			Patch patch = synthesizer.getAvailableInstruments()[407].getPatch();
			channel.programChange(patch.getBank(), patch.getProgram());
		} catch (MidiUnavailableException e) {
		}

		
		long lastLoopTime = System.currentTimeMillis();
		
		
		while(true) {
			
			int delta = (int)(System.currentTimeMillis() - lastLoopTime);
			for (int i = 0; i < delta / 10; i++) {
				logic(10);
			}
			logic(delta % 10);

			lastLoopTime = System.currentTimeMillis();
			draw((Graphics2D)strategy.getDrawGraphics());
			strategy.show();
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
			int y = (int) (Math.random() * BOARD_HEIGHT - 1);
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

		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);

		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {

				if (board[x][y] == TILE_ROCK) {
					g.setColor(Color.ORANGE);
					drawRock(g, x * CELL_SIZE, y * CELL_SIZE);
				}

				if (board[x][y] == TILE_FACE_SAD) {

					g.setColor(Color.BLACK);
					drawFace(g, x * CELL_SIZE, y * CELL_SIZE, false);

				}

				
				if (board[x][y] == TILE_FACE_HAPPY) {

					g.setColor(Color.BLACK);
					drawFace(g, x * CELL_SIZE, y * CELL_SIZE, true);

				}

				g.setColor(Color.green.darker());
				drawGoblin(g, goblinX * CELL_SIZE, goblinY * CELL_SIZE);
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
		dieWait = 2000;
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
		if (step <= 0) {

			goblinY--;
			if (goblinY < 0) {
				goblinY = BOARD_HEIGHT - 1;

				score = Math.max(0, score - 5);

			}
			step = speed;
		}

		if (board[goblinX][goblinY] == TILE_ROCK) {

			// playNote(channel, 22, 80, 150);

			channel.noteOn(22, 80);
			channel.noteOff(22);

			die();
		}

		if (board[goblinX][goblinY] == TILE_FACE_SAD) {

			board[goblinX][goblinY] = TILE_NOTHING;
			facesCount--;
			score += speed;

			channel.noteOn(60, 50);
			channel.noteOff(60);

		}

		if (facesCount == 0) {
			level++;
			speed -= 10;
			randomBoard(level);
			
			dieWait = 1000;
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

		g.translate(getInsets().left, getInsets().top);
		
		g.setColor(Color.white);
		g.fillRect(0, 0,GAMEFILED_WIDTH, GAMEFILED_HEIGHT);

		renderBoard(g);

		g.setFont(g.getFont().deriveFont(Font.BOLD, 20.0f));

		if (!started) {
			g.setColor(Color.BLACK);
			g.fillRect(0, GAMEFILED_HEIGHT/2-20, GAMEFILED_WIDTH, 20+20);
			g.setColor(Color.WHITE);
			g.drawString(msg,
					((GAMEFILED_WIDTH - g.getFontMetrics().stringWidth(msg)) / 2), GAMEFILED_HEIGHT/2+10);
		}

		setTitle(String.format(
				"Goblin4k -- Level: %02d(%02d faces) Score: %06d / %06d ",
				level, facesCount, score, bestScore));
		
	}

	protected void processKeyEvent(KeyEvent e) {

		if (e.getID() != KeyEvent.KEY_PRESSED) {
			return;
		}

		if ((e.getKeyCode() == KeyEvent.VK_LEFT & started)) {

			goblinX = goblinX - 1;
			if (goblinX < 0) {
				goblinX += BOARD_WIDTH;
			}
		}

		if ((e.getKeyCode() == KeyEvent.VK_RIGHT && started)) {

			goblinX++;
			if (goblinX >= BOARD_WIDTH) {
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

		g.fillRect(x + 2 * PIXEL_SIZE, y + 0 * PIXEL_SIZE, 4 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 1 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 6 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 0 * PIXEL_SIZE, y + 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				4 * PIXEL_SIZE);
		g.fillRect(x + 7 * PIXEL_SIZE, y + 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				4 * PIXEL_SIZE);
		g.fillRect(x + 2 * PIXEL_SIZE, y + 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 5 * PIXEL_SIZE, y + 2 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(x + 1 * PIXEL_SIZE, y + 6 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 6 * PIXEL_SIZE, y + 6 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 2 * PIXEL_SIZE, y + 7 * PIXEL_SIZE, 4 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		if (happinessFlag) {
			g.fillRect(x + 2 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(x + 5 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(x + 3 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 2 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
		} else {
			g.fillRect(x + 2 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(x + 5 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
			g.fillRect(x + 3 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 2 * PIXEL_SIZE,
					1 * PIXEL_SIZE);
		}

	}

	private void drawRock(Graphics2D g, int x, int y) {

		/*
		 * g.fillRect(x+2PIXEL_SIZE, y+0PIXEL_SIZE, 4PIXEL_SIZE, 1PIXEL_SIZE);
		 * g.fillRect(x+1PIXEL_SIZE, y+1PIXEL_SIZE, 6PIXEL_SIZE, 1PIXEL_SIZE);
		 * g.fillRect(x+0PIXEL_SIZE, y+2PIXEL_SIZE, 8PIXEL_SIZE, 4PIXEL_SIZE);
		 * g.fillRect(x+1PIXEL_SIZE, y+6PIXEL_SIZE, 6PIXEL_SIZE, 1PIXEL_SIZE);
		 * g.fillRect(x+2PIXEL_SIZE, y+7PIXEL_SIZE, 4PIXEL_SIZE, 1PIXEL_SIZE);
		 */

		g.fillRect(x + 1 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 6 * PIXEL_SIZE,
				6 * PIXEL_SIZE);

	}

	private void drawGoblin(Graphics2D g, int x, int y) {

		g.fillRect(x + 1 * PIXEL_SIZE, y + 0 * PIXEL_SIZE, 6 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(x + 0 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);
		g.fillRect(x + 3 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);
		g.fillRect(x + 6 * PIXEL_SIZE, y + 1 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				3 * PIXEL_SIZE);

		g.fillRect(x + 0 * PIXEL_SIZE, y + 3 * PIXEL_SIZE, 8 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(x + 0 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 2 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 5 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 7 * PIXEL_SIZE, y + 4 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);

		g.fillRect(x + 1 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				2 * PIXEL_SIZE);
		g.fillRect(x + 3 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 2 * PIXEL_SIZE,
				2 * PIXEL_SIZE);
		g.fillRect(x + 6 * PIXEL_SIZE, y + 5 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				2 * PIXEL_SIZE);

		g.fillRect(x + 0 * PIXEL_SIZE, y + 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 2 * PIXEL_SIZE, y + 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 5 * PIXEL_SIZE, y + 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
		g.fillRect(x + 7 * PIXEL_SIZE, y + 7 * PIXEL_SIZE, 1 * PIXEL_SIZE,
				1 * PIXEL_SIZE);
	}


	public static void main(String args[]) {
		new G();
	}
	
}

