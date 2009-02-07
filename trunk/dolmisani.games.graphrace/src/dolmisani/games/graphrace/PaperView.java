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
		drawcursor();
		
		if (game.finished()) {
			gr.setFont(new Font("Helvetica", Font.BOLD, 30));
			gr.setColor(new Color(230, 240, 240));
			
			Position p = game.currentplayer().getcar().getPosition();
			
			int x = p.getX() * Game.gridsize - 300;
			int y = p.getY() * Game.gridsize - 15;
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
		
		//TODO: check '<' instead of '<=', is a tricky way to render the current player as last player
		for (int playerIndex = 0; playerIndex<=game.getplayercount(); playerIndex++) {
		
			Car car = game.getplayer(playerIndex).getcar();
			
			Position sp = car.getStartPosition();
			Position p1 = sp;
			
			for (int i = 0; i < car.getturns(); i++) {
				
				Position p2 = p1.moveBy(car.getHistory(i));
				
				drawLine(p1, p2, car.getColor()); // Draw a line for between all locations
				fillCircle(p2, car.getColor()); // Mark all locations
				
				p1 = p2;				
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
		
		Position p = c.getPosition().moveBy(c.getVelocity());
		
		//int x = c.getX() + c.getVector().getX();
		//int y = c.getY() + c.getVector().getY();
		/**
		 * The player can move to 5 locations, some of them are on grass, and
		 * will be colored differently
		 */
		
		for(Vector offset: Vector.DISPLACEMENTS) {
			
			Position cp = p.moveBy(offset);
			drawcircle(cp, circ.terrain(cp) > 0 ? Color.cyan : Color.red);
			
		}
/*
		drawcircle(p, circ.terrain(p) > 0 ? Color.cyan : Color.red);
		drawcircle(x - 1, y, circ.terrain(x - 1, y) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x, y - 1, circ.terrain(x, y - 1) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x + 1, y, circ.terrain(x + 1, y) > 0 ? Color.cyan
				: Color.red);
		drawcircle(x, y + 1, circ.terrain(x, y + 1) > 0 ? Color.cyan
				: Color.red);
*/				
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
	public void drawcircle(Position p, Color c) {
		int s = (int) gridsize / 4;
		gr.setColor(c);
		gr.drawOval(p.getX() * gridsize - s, p.getY() * gridsize - s, 2 * s, 2 * s);
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
	public void fillCircle(Position p, Color c) {
		int s = (int) (gridsize / 3) - 1;
		gr.setColor(c);
		gr.fillOval(p.getX()*gridsize - s, p.getY()*gridsize - s, 2 * s, 2 * s);
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
	public void drawLine(Position p1, Position p2, Color c) {
		gr.setColor(c);
		gr.drawLine(p1.getX()*gridsize, p1.getY()*gridsize, p2.getX()*gridsize, p2.getY()*gridsize);
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
