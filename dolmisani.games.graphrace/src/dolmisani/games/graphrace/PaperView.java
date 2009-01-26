package dolmisani.games.graphrace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * This class provides the visuals for the racing-board It renders the circuit
 * to a Graphics object, and adds the cars and their movement to it.
 */
class PaperView extends JPanel {

	
	private static final long serialVersionUID = -5370301291426995748L;
	
	Circuit circ; // The circuit
	Graphics2D gr;
	Game game;

	int sizex, sizey, hsize, vsize, gridsize; // Size of the board

	BufferedImage image;

	/**
	 * Creates a new Canvas for viewing the board
	 * 
	 * @param c
	 *            The circuit to be used
	 * @param p
	 *            The parental applet
	 */
	public PaperView(Circuit c, GraphRace p) {

		circ = c;

		game = p.getgame();
		sizex = circ.getWidth() + 2;
		sizey = circ.getHeight() + 2;
		gridsize = game.getgridsize();
		setSize(circ.getWidth() * gridsize, circ.getHeight() * gridsize);
		hsize = sizex * gridsize;
		vsize = sizey * gridsize;

		image = new BufferedImage(hsize, vsize, BufferedImage.TYPE_INT_RGB);
		gr = image.createGraphics();
	}

	/**
	 * Buffers the current image, for faster painting.
	 */
	public void rebuffer() {

		circ.paint(gr);
		drawcar();
		//if (cursordraw)
			drawcursor();
		if (game.finished()) {
			gr.setFont(new Font("Helvetica", Font.BOLD, 30));
			gr.setColor(new Color(230, 240, 240));
			int x = game.currentplayer().getcar().getX() * Game.gridsize - 300, y = game
					.currentplayer().getcar().getY()
					* Game.gridsize - 15;
			gr.drawString("Player " + game.currentplayer().getName()
					+ " has Finished!", x, y);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		rebuffer();
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Draws the cars of all players to the graphics object provided to
	 * paint(Graphics g)
	 */
	public void drawcar() {
		
		Car c;
		Color color;
		int l;
		int x1, y1, x2, y2, sx, sy, i, j;

		for (j = 0; j <= game.getplayercount(); j++) // Repeat for all players
		{
			c = game.getplayer(j).getcar();
			color = c.getcolor();
			l = c.getturns();
			sx = c.getstartx(); // Get the start position to draw from
			sy = c.getstarty();
			x1 = sx;
			y1 = sy;
			for (i = 0; i < l; i++) {
				x2 = x1 + c.getHistory(i).getX();
				y2 = y1 + c.getHistory(i).getY();
				drawline(x1, y1, x2, y2, color); // Draw a line for between all
				// locations
				fillcircle(x2, y2, color); // Mark all locations
				x1 = x2;
				y1 = y2;
			}
		}
	}

	/**
	 * Draws the cursor of the current player onto the Graphics object
	 */
	public void drawcursor() {
		game.currentplayer().checkterrain(); // Check if the speed is still
		// correct...
		Car c = game.currentplayer().getcar();
		int x = c.getX() + c.getVector().getX(), y = c.getY()
				+ c.getVector().getY();
		/**
		 * The player can move to 5 locations, some of them are on grass, and
		 * will be colored differently
		 */
		drawcircle(x, y, circ.terrain(x, y) > 0 ? Color.cyan : Color.red);
		drawcircle(x - 1, y, circ.terrain(x - 1, y) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x, y - 1, circ.terrain(x, y - 1) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x + 1, y, circ.terrain(x + 1, y) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x, y + 1, circ.terrain(x, y + 1) > 0 ? Color.cyan
				: Color.red);
	}

	/**
	 * Draws a circle on the given location with the given color
	 * 
	 * @param x
	 *            Horizontal location
	 * @param y
	 *            Vertical location
	 * @param c
	 *            Color of the circle
	 */
	public void drawcircle(int x, int y, Color c) {
		int s = (int) gridsize / 4;
		gr.setColor(c);
		gr.drawOval(x * gridsize - s, y * gridsize - s, 2 * s, 2 * s);
	}

	/**
	 * Draws a filled circle on the given location with the given color
	 * 
	 * @param x
	 *            Horizontal location
	 * @param y
	 *            Vertical location
	 * @param c
	 *            Color of the circle
	 */
	public void fillcircle(int x, int y, Color c) {
		int s = (int) (gridsize / 3) - 1;
		gr.setColor(c);
		gr.fillOval(x * gridsize - s, y * gridsize - s, 2 * s, 2 * s);
	}

	/**
	 * Draws a line on the given coordinates with the given color
	 * 
	 * @param x1
	 *            Horizontal start location
	 * @param y1
	 *            Vertical start location
	 * @param x2
	 *            Horizontal end location
	 * @param y2
	 *            Vertical end location
	 * @param c
	 *            Color of the line
	 */
	public void drawline(int x1, int y1, int x2, int y2, Color c) {
		gr.setColor(c);
		gr.drawLine(x1 * gridsize, y1 * gridsize, x2 * gridsize, y2 * gridsize);
	}

	@Override
	public Dimension getPreferredSize() {
		
		return new Dimension(hsize, vsize);
	}
	
	@Override
	public Dimension getMinimumSize() {
		
		return new Dimension(hsize, vsize);
	}
	
	@Override
	public Dimension getMaximumSize() {
		
		return new Dimension(hsize, vsize);
	}
	
}
