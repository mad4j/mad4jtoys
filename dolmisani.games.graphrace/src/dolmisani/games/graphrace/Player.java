package dolmisani.games.graphrace;

import java.awt.Color;

/**
 * This class implements both the data concerning each player, as well as
 * functions for calculating the route for the computer players.
 */
public class Player {

	private int type; // Decide if this is a computer player
	private String name; // Name of the player
	private Car car; // The player's car
	private Circuit circ; // The Circuit
	private GraphRace ppr; // The parental applet
	private Game game;


	/**
	 * The type of the player: Computer, Human or Network-player
	 */
	public final static int COM = 0, HUM = 1;

	/**
	 * Constructs a new player
	 * 
	 * @param nm
	 *            The name of the player
	 * @param aip
	 *            Is the player a computer player?
	 * @param p
	 *            Parental applet
	 * @param startx
	 *            Horizontal start location
	 * @param starty
	 *            Vertical start location
	 * @param col
	 *            Color of the player
	 */
	public Player(String nm, int t, GraphRace p, Position startPosition,
			Color col) {
		name = nm;
		type = t;
		ppr = p;
		game = ppr.getgame();
		circ = game.getcirc();
		car = new Car(startPosition, col);
	}

	/**
	 * Is used to make the computer do a move
	 */
	public void ask() {
		if (type == COM) {
			ppr.message("Thinking...");
			game.wait = true;
			moveai(6);
		}
	}

	/**
	 * @return The type of this player: HUM, COM, or NET
	 */
	public int type() {
		return type;
	}

	/**
	 * For user input from mouse
	 * 
	 * @param x
	 *            Horizontal (circuit) location
	 * @param x
	 *            Vertical (circuit) location
	 */

	public void clicked(Position p) {

		Position cp = car.getPosition().moveBy(car.getVelocity());

		int cx = cp.getX();
		int cy = cp.getY();
		Vector m = null;

		if ((p.getX() == cx) && (p.getY() == cy))
			m = Vector.MOVE_NONE;
		else if ((cx - p.getX() == -1) && (cy - p.getY() == 0))
			m = Vector.MOVE_RIGHT;
		else if ((cx - p.getX() == 1) && (cy - p.getY() == 0))
			m = Vector.MOVE_LEFT;
		else if ((cx - p.getX() == 0) && (cy - p.getY() == -1))
			m = Vector.MOVE_DOWN;
		else if ((cx - p.getX() == 0) && (cy - p.getY() == 1))
			m = Vector.MOVE_UP;
		
		if (m != null) {
			move(m);
		}
	}

	/**
	 * For actually moving the car
	 * 
	 * @param m
	 *            One of: LEFT, RIGHT, UP, DOWN, SAME
	 */
	public synchronized void move(Vector a) {
		
		Vector v = car.getVelocity().add(a);
		
		car.move(v);

		ppr.paper.rebuffer();
		ppr.repaint();

		//TODO: move this logic in the Game class
		game.nextplayer();
		game.wait = false;
	}

	/**
	 * Checks if the car has hit the grass. If so, the speed is reduced to (0,0)
	 */
	public synchronized void checkterrain() {
		if (circ.terrain(car.getPosition()) == 0) {

			car.crash();

			// car.move(new Vector(0, 0)); // Grass/gravel Speed reduced to zero
			// car.fault();
		}
	}

	/**
	 * @return The name of the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The car of the player
	 */
	public Car getcar() {
		return car;
	}

	public String toString() {
		return getName();
	}

	// --------------------------------------------------------
	// ------- Functions for the computer player --------------

	//int cx, cy, level, i, j;
	int j;
	double distance = 0; // The current distance of the players' car
	boolean grass;

	/**
	 * This function is called by ask() to move the computer player
	 */
	public void moveai(int maxLevel) {
		
		double mv;

		Position center = new Position(circ.getWidth()/2, circ.getHeight() / 2);

		maxLevel += (int) Math.round(car.getSpeed() * 1.4); // Level is the
		// depth of the
		// search
		grass = (circ.terrain(car.getPosition()) <= 0); // Is the car
		// currently at
		// the grass?

		Vector v = car.getVelocity();
		
		distance = computeDistance(center, car.getPosition());
		
				
		// the current distance of the car
		mv = ai(center, car.getPosition(), v, maxLevel, maxLevel);
		move(Vector.DISPLACEMENTS[(int) mv]);
	}

	/**
	 * This method does a depth-first search for the longest arc-distance that
	 * can be made within the given number of moves (level). As soon as the
	 * required depth is reached, the method returns the distance of the trip.
	 * For each level, the longest trip is chosen, until the best direction is
	 * found. When all possibilities have been searched, the function returns
	 * the best move to make. To reduce the number of searches, the hit of grass
	 * will reduce the depthlevel by 2. If the car actually hits the grass, it
	 * will crash... :-) (Due to the algorithm) Probably the only way to solve
	 * this problem accurately, is by using a genetic alghorithm. Yet this
	 * routine is clean and simple :-)
	 * 
	 * @param x
	 *            Horizontal start location
	 * @param y
	 *            Vertical start location
	 * @param vx
	 *            Current horizontal speed
	 * @param vy
	 *            Current vertical speed
	 * @param l
	 *            Level of search (or number of moves to maximize)
	 */

	public double ai(Position c, Position p, Vector v, int maxLevel, int l) {
		
		double[] e = new double[Vector.DISPLACEMENTS.length];

		double d = computeDistance(c, p);
		

		// The arc-distance values range from 0 to 2 PI. To solve border-cases
		// in the from 1.5 PI to 0.5 PI, checking is needed.
		if ((d - distance) > Math.PI)
			d -= 2 * Math.PI;
		if ((d - distance) < -Math.PI)
			d += 2 * Math.PI;

		if (l > 0) {
			// Check borders
			if (circ.terrain(p.getX(), p.getY()) <= 0) {
				v = Vector.MOVE_NONE;
				
				if (!grass)
					l -= 2; // Penalty to get quicker results.
			}

			// If we're not done yet, check all possible routes.
			if (l > 0) {								
								
				for(int i=0; i<e.length; i++) {
					
					Vector nv = v.add(Vector.DISPLACEMENTS[i]);
					e[i] = ai(c, p.moveBy(nv), nv, maxLevel, l-1);	
				}

				
				// Next get the largest distance
				for (int i = 0; i < e.length; i++) {
					if (e[i] > d) {
						d = e[i];
						j = i;
					}
				}
			}
		}

		if (l == maxLevel)
			return j; // If this was the top-thread, return the best route
		return d; // Else return the distance
	}
	
	private static final double computeDistance(Position c, Position p) {
		
		return Math.atan2(-(c.getY()-p.getY()), (c.getX()-p.getX())) + Math.PI;		
	}
	
}