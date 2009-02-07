package dolmisani.games.graphrace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This class constructs a new circuit, and makes a representation of it in a
 * BufferedImage
 */
public class Circuit {

	private int[][] circ; // Storage array for circuit-points
	private int maxCols, maxRows; // Horizontal and vertical size of the circuit

	// Location of the start/finish line
	private FinishLine finishLine;

	private Graphics2D gr; // Used to edit the image
	private BufferedImage image;// Graphical representation of the circuit
	private GraphRace ppr; // Parental class
	
	// Inner field and outer field
	private Polygon curbout;
	private Polygon curbin; 

	

	private int checkpoints; // Number of checkpoints; this is used to render the
						// circuit

	private int hsize, vsize, gridsize; // Actual size of the circuit on the screen and
	// the size of the grid
	private final int MG = 5; // Margin around the circuit

	/**
	 * Constructs a new circuit
	 * 
	 * @param sx
	 *            Horizontal size of the circuit
	 * @param sy
	 *            Vertical size of the circuit
	 * @param chk
	 *            Number of checkpoints, or corners in the circuit
	 * @param p
	 *            Parental game object
	 */
	public Circuit(int sx, int sy, int chk, GraphRace p) {
		
		maxCols = sx + 2 * MG;
		maxRows = sy + 2 * MG;
		checkpoints = chk;
		ppr = p;
		// init();
	}

	/**
	 * Initializes a new circuit
	 */
	public void init() {

		circ = new int[maxCols][maxRows];

		generate(); // Find some random checkpoints, and render a circuit

		finishLine = FinishLine.create(this);
		
		//setstart(); // Find start/finishline

		gridsize = ppr.getgame().getgridsize();
		dographics();
	}

	/**
	 * Draws the circuit
	 */
	public void dographics() {
		hsize = maxCols * gridsize;
		vsize = maxRows * gridsize;

		image = new BufferedImage(hsize, vsize, BufferedImage.TYPE_INT_RGB);
		gr = image.createGraphics();

		drawGrid(); // Draw the circuit-points
		curbout = new Polygon();
		curbin = new Polygon();
		omtrek(); // Draw the curbstones

		// fill(curbin, Color.cyan);
		drawstart(); // Draw the start/finish line
	}

	/*
	 * @param x Desired x-coordinate
	 * 
	 * @param y Desired y-coordinate
	 * 
	 * @return -1 if the if coordinates are out of range
	 * 
	 * @return 0 if the coordinate contains grass
	 * 
	 * @return 1 or higher if the coordinate contains tarmac
	 */
	public int terrain(int x, int y) {
		if ((x < 0) || (x >= maxCols) || (y < 0) || (y >= maxRows))
			return -1;
		return circ[x][y];
	}

	public int terrain(Position p) {
		
		return terrain(p.getX(), p.getY());
	}
	
	/*
	 * @return the horizontal size of the circuit
	 */
	public int getWidth() {
		
		return maxCols;
	}

	/*
	 * @return the vertical size of the circuit
	 */
	public int getHeight() {
		
		return maxRows;
	}

	public FinishLine getFinishLine() {
		
		return finishLine;
	}
	

	/*--------------------------------------------------------------*/
	/*-------------- These functions render the circuit ------------*/

	/**
	 * This function generates a random circuit, following the given number of
	 * checkpoints. These marks are stored in the circuit-matrix with value 1.
	 * The circuit always has a width of three, but because of overlapping it
	 * has sometimes more.
	 */
	public void generate() {
		
		int xRadius = (maxCols - 2*MG) / 2;
		int yRadius = (maxRows - 2*MG) / 2;

		List<Position> checkpointList = new ArrayList<Position>(checkpoints);
		for (int i = 0; i < checkpoints; i++) {

			double angle = i * ((2.0 * Math.PI) / checkpoints);

			//int cx = MG + (int)(0.5*xRadius*Math.cos(angle)*(Math.random()+1))+xRadius;
			//int cy = MG + (int)(0.5*xRadius*Math.sin(angle)*(Math.random()+1))+xRadius;
			
			int cx = MG + (int) ((Math.random() * (.5 * xRadius) + .5 * xRadius)
					* Math.cos(angle) + xRadius);
			int cy = MG + (int) ((Math.random() * (.5 * yRadius) + .5 * yRadius)
					* Math.sin(angle) + yRadius);
			
			checkpointList.add(new Position(cx, cy));
		}		
		
		for (int i = 0; i < checkpointList.size(); i++) {
			
			trace(checkpointList.get(i), checkpointList.get((i+1)%checkpoints));
		}
	}

	
	private void dig(Position p) {
	
		int x = p.getX();
		int y = p.getY();
		
		circ[x  ][y  ] = 1;
		
		circ[x+1][y  ] = 1;
		circ[x  ][y+1] = 1;
		circ[x+1][y+1] = 1;
		circ[x-1][y  ] = 1;
		circ[x  ][y-1] = 1;
		circ[x-1][y-1] = 1;
		circ[x+1][y-1] = 1;
		circ[x-1][y+1] = 1;
	}
	
	private void trace(Position p1, Position p2) {
		
		dig(p1);
		dig(p2);
		
		if(p1.isNear(p2)) {
			return;
		}
				
		Position middle = new Position((p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2);
		
		trace(p1, middle);
		trace(middle, p2);		
	}
	

	/*--------------------------------------------------------------*/
	/*-----From here all functions apply to the graphical image.----*/

	/**
	 * Paint the buffered image onto the screen
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Draws the circuit-grid into the Graphics-object.
	 */
	void drawGrid() {
		
		for (int x = 0; x < maxCols; x++)
			// Draw vertical gridlines
			line(x, 0, x, maxRows, Color.darkGray);
		for (int y = 0; y < maxRows; y++)
			// Draw horizontal gridlines
			line(0, y, maxCols, y, Color.darkGray);

		for (int x = 0; x < maxCols; x++) {
			for (int y = 0; y < maxRows; y++) {
				
				if (terrain(x, y) != 0) {
					pixel(x, y, Color.white);
				}
			}
		}
	}

	int x, y, vx, vy, x_oud, y_oud, vx_oud, vy_oud, curbcount;
	int[] curboutx, curbouty, curbinx, curbiny;

	/**
	 * Call this function to Draw the curb-stones into the Buffered image.
	 */
	void omtrek() {
		curbcount = 2 * maxCols + 2 * maxRows; // Maximum number of curbstones
		x = 0;
		y = 0;

		do // Search first white dot for the outer curb-stones
		{
			if (x > maxCols) {
				x = 0;
				y++;
			}
			x++;
		} while (terrain(x, y) <= 0);
		x -= 1;
		vx = -1;
		vy = 0;
		do_omtrek(curbout, curbcount); // Calculate the outer curbstones

		x = (int) (MG + maxCols) / 2;
		y = (int) (MG + maxRows) / 2;
		do {
			x++; // Search first white dot for the inner curb-stones
		} while (terrain(x, y) <= 0);

		x -= 1;
		vx = -1;
		vy = 0;
		do_omtrek(curbin, curbcount); // Calculate the inner curbstones
	}

	/**
	 * Calculates the circuit-contour This function uses the functions links,
	 * rechts, terug and rechtdoor to find the borders of the circuit step by
	 * step.
	 * 
	 * @param curbx
	 *            Array with x-coordinates for the curb-polygon
	 * @param curby
	 *            Array with y-coordinates for the curb-polygon
	 * @param end
	 *            Number of iterations.
	 */
	void do_omtrek(Polygon pol, int end) {
		int z = 0, i = 0, x1, y1, n = 0;

		boolean curb = true;
		boolean baan = false;

		pol.addPoint(x * gridsize, y * gridsize);

		x_oud = x;
		y_oud = y;
		x1 = x_oud;
		y1 = y_oud;
		vx_oud = vx;
		vy_oud = vy;

		do {
			x1 = x;
			y1 = y;

			i = 0;
			baan = false;

			if (rechtdoor()) {
				while (links() && i < 4)
					i++;
				if (i >= 3)
					z = 9999;
			} else {
				if (!baan)
					baan = rechts();
				if (!baan)
					baan = rechts();
				if (!baan)
					baan = rechtdoor();
				if (!baan)
					baan = rechts();
				if (!baan)
					baan = rechtdoor();
				if (!baan)
					baan = rechts();
				if (!baan)
					baan = rechtdoor();
				terug();
				curb = !curb;
				line(x1, y1, x, y, curb ? Color.yellow : Color.red);
				n++;
				pol.addPoint(x * gridsize, y * gridsize);
				z++;
			}
		} while (z <= end);
	}

	void terug() {
		x = x_oud;
		y = y_oud;
		vx = vx_oud;
		vy = vy_oud;
	}

	boolean rechtdoor() {
		x_oud = x;
		y_oud = y;
		vx_oud = vx;
		vy_oud = vy;
		x += vx;
		y += vy;

		if (terrain(x, y) != 0) {
			x -= vx;
			y -= vy;
			return true;
		}
		return false;
	}

	boolean rechts() {
		x_oud = x;
		y_oud = y;
		vx_oud = vx;
		vy_oud = vy;
		if (vy == 0) {
			if (vx == 1) {
				vy = 1;
				vx = 0;
			} else if (vx == -1) {
				vy = -1;
				vx = 0;
			}
		} else {
			if (vy == 1) {
				vy = 0;
				vx = -1;
			} else if (vy == -1) {
				vy = 0;
				vx = 1;
			}
		}
		x += vx;
		y += vy;

		if (terrain(x, y) != 0) {
			x -= vx;
			y -= vy;
			return true;
		}
		return false;
	}

	boolean links() {
		x_oud = x;
		y_oud = y;
		vx_oud = vx;
		vy_oud = vy;

		if (vy == 0) {
			if (vx == 1) {
				vy = -1;
				vx = 0;
			} else if (vx == -1) {
				vy = 1;
				vx = 0;
			}
		} else {
			if (vy == 1) {
				vy = 0;
				vx = 1;
			} else if (vy == -1) {
				vy = 0;
				vx = -1;
			}
		}
		x += vx;
		y += vy;

		if (terrain(x, y) != 0) {
			x -= vx;
			y -= vy;
			return true;
		}
		x -= vx;
		y -= vy;
		return false;
	}

	/**
	 * Draws a line between two points
	 */
	void line(int x1, int y1, int x2, int y2, Color c) {
		gr.setColor(c);
		if ((x1 < 0) || (y1 < 0) || (x2 < 0) || (y2 < 0))
			return;
		gr.drawLine(x1 * gridsize, y1 * gridsize, x2 * gridsize, y2 * gridsize);
	}

	/**
	 * Draws a single pixel on a given location
	 */
	void pixel(int x, int y, Color c) {
		gr.setColor(c);
		if ((x < 0) || (y < 0))
			return;
		gr.drawLine(x * gridsize, y * gridsize, x * gridsize, y * gridsize);
	}

	/**
	 *Little function for drawing the start-finish line
	 */
	public void drawstart() {
		
		gr.setColor(Color.white);
		for (int i = 0; i < finishLine.getX2() - finishLine.getX1(); i += 2)
			gr.drawLine((finishLine.getX1() + i - 1) * gridsize, finishLine.getY() * gridsize,
					(finishLine.getX1() + i) * gridsize, finishLine.getY() * gridsize);
		for (int i = 1; i < finishLine.getX2() - finishLine.getX1(); i += 2)
			gr.drawLine((finishLine.getX1() + i - 1) * gridsize, finishLine.getY() * gridsize + 1,
					(finishLine.getX1() + i) * gridsize, finishLine.getY() * gridsize + 1);
		
		gr.setColor(Color.gray);		
		gr.drawLine((finishLine.getX1() - 1) * gridsize, finishLine.getY() * gridsize - 1,
				(finishLine.getX2() - 1) * gridsize, finishLine.getY() * gridsize - 1);
		gr.drawLine((finishLine.getX1() - 1) * gridsize, finishLine.getY() * gridsize + 2,
				(finishLine.getX2() - 1) * gridsize, finishLine.getY() * gridsize + 2);
	}	
	
}
