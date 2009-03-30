import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

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


public class TemplateComplete extends JFrame {
	
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
	public static final int SCR_HEIGHT = HEIGHT+50;
	
	private static final int STATE_NEW_GAME = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_GAME_OVER = 2;
	private static final int STATE_INIT = 3;
	private static final int STATE_INIT_LEVEL = 4;
	private static final int STATE_LIFE_LOST = 5;
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
	private int mouseX,mouseY,mouseButton;
	
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
	
		
	public static void main(String[] x) {
		new TemplateComplete();
	}
	
	
	public TemplateComplete() {
		setSize(WIDTH,SCR_HEIGHT);
		setResizable(false);
		/*************************************************************
		 * GLOBAL INITIALIZATION
		 *************************************************************/
		Random rnd = new Random();
		BufferedImage[] images = new BufferedImage[MAX_ITEM_TYPES];
		BufferedImage playerImage = new BufferedImage(PLAYER_SIZE,PLAYER_SIZE,BufferedImage.TYPE_INT_ARGB);
		int state = STATE_NEW_GAME;
		int score = 0;
		int lives = 5;
		int level;
		Graphics2D g;
		
		// TODO : Dummy player image
		g = playerImage.createGraphics();
		g.setColor(Color.red);
		g.fillRect(0,0,PLAYER_SIZE,PLAYER_SIZE);
		
		// TODO : Dummy item images
		for (int i = 0; i < MAX_ITEM_TYPES; i++) {
			images[i] = new BufferedImage(ITEM_SIZE,ITEM_SIZE,BufferedImage.TYPE_INT_ARGB);
			g = images[i].createGraphics();
			g.setColor(Color.green);
			g.fillRect(0,0,ITEM_SIZE,ITEM_SIZE);
		}
		

		int playerX = 0, playerY = 0;
		int[] itemType = new int[MAX_ITEMS];
		int[] itemX = new int[MAX_ITEMS];
		int[] itemY = new int[MAX_ITEMS];
		int[] itemVX = new int[MAX_ITEMS];
		int[] itemVY = new int[MAX_ITEMS];
		int[] itemCurrentTimer = new int[MAX_ITEMS];
		int[] itemMaxTimer = new int[MAX_ITEMS];
		int[] itemSize = new int[MAX_ITEMS];
		
		
		// TODO : Scenery
		BufferedImage scenery = new BufferedImage(WIDTH,SCR_HEIGHT,BufferedImage.TYPE_INT_ARGB);
		g = scenery.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,WIDTH,SCR_HEIGHT);
		
	
		enableEvents(8 | 16 | 32);
		
		// For mouse events
		// enableEvents(KeyEvent.MOUSE_EVENT_MASK);
		
		
		show();
		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		while (true)
		{
			long t=System.currentTimeMillis();
			while (System.currentTimeMillis()-t < 20) Thread.yield();
			
			if (state == STATE_INIT || state == STATE_INIT_LEVEL) {
				// LEVEL INITIALIZATION
				
				itemType = new int[MAX_ITEMS];
				itemX = new int[MAX_ITEMS];
				itemY = new int[MAX_ITEMS];
				itemVX = new int[MAX_ITEMS];
				itemVY = new int[MAX_ITEMS];
				itemCurrentTimer = new int[MAX_ITEMS];
				itemMaxTimer = new int[MAX_ITEMS];
				itemSize = new int[MAX_ITEMS];
				
				if (state == STATE_INIT) {
					// GAME INITIALIZATION
					score = 0;
					lives = 5;
					
				}
				state = STATE_PLAYING;
			}
			
			/*****************************************************************
			 * EVOLUTION
			 *****************************************************************/
			if (key[KeyEvent.VK_ENTER]) {
				if (state == STATE_LIFE_LOST) {
					state = STATE_INIT_LEVEL;
				}
				if (state == STATE_NEW_GAME || state == STATE_GAME_OVER) {
					state =STATE_INIT ;
				}
			}			
			if (state == STATE_PLAYING) {
				
				// TODO: Level update formula
				level = 1+score/1000; 
				
				// Player evolution
				if (key[KeyEvent.VK_UP]) { playerY -= PLAYER_SPEED; }
				if (key[KeyEvent.VK_DOWN]) { playerY += PLAYER_SPEED; }
				if (key[KeyEvent.VK_LEFT]) { playerX -= PLAYER_SPEED; }
				if (key[KeyEvent.VK_RIGHT]) { playerX += PLAYER_SPEED; }
				
				// TODO : Check what happens if the player leves the screen
				if (playerY <0) {
					
				}
				if (playerY > HEIGHT) {
					
				}
				if (playerX < 0) {
					
				}
				if (playerX > WIDTH) {
					
				}
				
				
				// Item evolution
				for (int i = 0; i < MAX_ITEMS; i++) {
					if (itemType[i] != 0) {
						itemCurrentTimer[i]--;
						if (itemCurrentTimer[i] == 0) {
							itemCurrentTimer[i] = itemMaxTimer[i];
							itemX[i]+=itemVX[i];
							itemY[i]+=itemVY[i];
							// TODO : Check what happens if we exit the screen
						}
						
						// Collisions with player
						int dx = itemX[i]-playerX;
						int dy = itemY[i]-playerY;
						if (dx*dx+dy*dy < itemSize[i]*itemSize[i]) {
							// TODO : Collision
						}
						
					}
				}
				
			}

			
			/******************************************************************
			 * SCREEN UPDATE
			 ******************************************************************/
			g = (Graphics2D)strategy.getDrawGraphics();
			g.drawImage(scenery,0,0,null);
			
			// Draw items
			for (int i = 0; i < MAX_ITEMS; i++) {
				if (itemType[i] != 0) {
					g.drawImage(images[itemType[i]],itemX[i],itemY[i],null);
				}
			}
			
			// Draw player
			g.drawImage(playerImage,playerX,playerY,null);
			
			// Draw Score
			g.setColor(Color.white);
			g.drawString("Score ",SCORE_POSITION,50);
			g.drawString(String.valueOf(score),SCORE_POSITION+75,50);
			// Draw lives
			for (int i = 0; i < lives; i++) {
				g.drawImage(playerImage,
						LIVES_POSITION+20*i,35,
						LIVES_POSITION+20*i+16,35+16,0,0,PLAYER_SIZE,PLAYER_SIZE,null);
			}
			
			if (state == STATE_GAME_OVER || state == STATE_NEW_GAME) {
				g.setColor(Color.red);
				g.drawString("GAME OVER. Press 'Enter' To Start ",140,300);
			}
			if (state == STATE_LIFE_LOST) {
				g.setColor(Color.red);
				g.drawString("Booom ",140,300);
			}
			
			// TODO: Sample mouse click
			g.setColor(Color.green);
			if (mouseButton!=0)
				g.fillOval(mouseX-5,mouseY-5,10,10);
			else
				g.drawOval(mouseX-5,mouseY-5,10,10);
			
			
			strategy.show();
			
		}
	}
	
	protected void processEvent(AWTEvent e) {
		 super.processEvent(e);
		 if (e.getID() == MouseEvent.MOUSE_RELEASED) 
			 mouseButton = 0;
		 if (e.getID() == MouseEvent.MOUSE_PRESSED)
			 mouseButton = ((MouseEvent)e).getButton();
		 if (e.getID() == MouseEvent.MOUSE_MOVED ||
				 e.getID() == MouseEvent.MOUSE_DRAGGED) {
             mouseX = ((MouseEvent)e).getX();
             mouseY = ((MouseEvent)e).getY();
		 }
		 if (e.getID() == KeyEvent.KEY_PRESSED ||
				 e.getID() == KeyEvent.KEY_RELEASED) 
			 key[((KeyEvent)e).getKeyCode()] =e.getID() == KeyEvent.KEY_PRESSED;
	}
	
/*	
	protected void processKeyEvent(KeyEvent e) {
		key[e.getKeyCode()] =e.getID() == KeyEvent.KEY_PRESSED;
	}
*/	
}
