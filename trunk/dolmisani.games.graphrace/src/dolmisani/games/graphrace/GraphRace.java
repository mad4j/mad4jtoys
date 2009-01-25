package dolmisani.games.graphrace;

import java.awt.*;
import java.awt.List;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.net.*;
import java.awt.image.*;

/*
 * full contact info:
 * Ernst van Rheenen
 * homepage www.bluering.nl
 * coauthor Sieuwert van Otterloo
 * thanks to Maarten Jansen for helping with ideas and the very fist version in C
 */

/**
 * Paper & Pencil Racing Designed by <a href="http://www.bluering.nl">bluering
 * software development </a>. You can send questions about this source to <a
 * href="mailto:ernst@bluering.nl">ernst@bluering.nl</a> this applet can be
 * freely used for whatever you want as long as you first ask permission from
 * the author. <h2>project overview</h2> This applet consists of 15 classes.We
 * have put all these classes in one file for easy downloading. To get the right
 * javadoc documentation, we had to make all classes and many methods public.
 * You might want to put each class in a separate file with the filename equal
 * to the class name. You can also make all classes except reversi non-public
 * (just remove the word public) to avoid errors. The drawback is that you will
 * not see those classes in the documentation.
 * <p>
 * The javadoc files contain much of the comment, but not all of it. Check the
 * sourcecode for the last details. The list of all classes is:
 * <dl>
 * <dt>Ppracing</dt>
 * <dd>the toplevel applet class. It contains and handles buttons.</dd>
 * <dt>Circuit</dt>
 * <dd>The class containing the circuit, and routines for generating a new one.</dd>
 * <dt>Player</dt>
 * <dd>Contains the routines for handling the player as well as information
 * about him or her.</dd>
 * <dt>Car</dt>
 * <dd>Contains the basic information about the car, it's speed and it's history
 * </dd>
 * <dt>Vector</dt>
 * <dd>Basic data class for handling movement-vectors</dd>
 * <dt>Mainview</dt>
 * <dd>Puts scroll bars around the board</dd>
 * <dt>Paperview</dt>
 * <dd>the class that paints the board and the cars.</dd>
 * <dt>newgamewindow</dt>
 * <dd>the window that allows you to set up a new game. It appears when you
 * press new game</dd>
 * <dt>newnetwindow</dt>
 * <dd>the window that allows you to set up a network game. It also shows a
 * chat-interface.</dd>
 * <dt>NetClient</dt>
 * <dd>The class that handles all events concerning a client</dd>
 * <dt>NetSClient</dt>
 * <dd>The class that handles all events concerning the server</dd>
 * <dt>NetSClient</dt>
 * <dd>The class that handles all events concerning the server</dd>
 * <dt>NetSend</dt>
 * <dd>This class handles the outgoing communication</dd>
 * <dt>NetReceive</dt>
 * <dd>This class handles the incoming communication</dd>
 * <dt>NetServer</dt>
 * <dd>a simple class for detecting new clients.</dd>
 * </dl>
 * <p>
 */
public class GraphRace extends java.applet.Applet implements MouseListener,
		KeyListener, ActionListener {
	Paperview paper; // The viewport for the circuit
	MainView main; // Scrollbar-window for the paperview
	Game game;

	/**
	 * Graphical items for displaying status-information and buttons
	 */
	Label playerlabel = new Label(), turnlabel = new Label("Turn: 0"),
			messagelabel = new Label("Do a move");

	Button newgame = new Button("new game"), undo = new Button("undo move"),
			move = new Button("computer move");
	Panel bottompanel = new Panel();

	/**
	 * The init method is called when the applet is loaded by the browser. It
	 * contains mainly layout-concerning items
	 */
	public void init() {
		System.out.println("Initialising..");
		game = new Game(this);
		paper = new Paperview(game.getcirc(), this);
		main = new MainView(this);
		main.add(paper);
		main.setSize(600, 500);

		setBackground(Color.black); // you can set the background color of the
									// applet in this line
		messagelabel.setForeground(Color.white);
		messagelabel.setAlignment(Label.CENTER);
		turnlabel.setForeground(Color.white);
		turnlabel.setAlignment(Label.CENTER);

		Panel buttonpanel = new Panel();// panel to keep buttons together
		buttonpanel.setLayout(new GridLayout(1, 3)); // add three buttons beside
														// each other
		buttonpanel.add(newgame);
		buttonpanel.add(move);
		buttonpanel.add(undo);
		Panel superpanel = new Panel();
		superpanel.setLayout(new GridLayout(2, 1));
		setLayout(new BorderLayout());

		bottompanel.setLayout(new GridLayout(1, 3));
		bottompanel.add(playerlabel);
		bottompanel.add(messagelabel);
		bottompanel.add(turnlabel);

		superpanel.add(buttonpanel);
		superpanel.add(bottompanel);
		add("Center", main);
		add("South", superpanel);

		paper.addMouseListener(this);
		paper.addKeyListener(this);
		addKeyListener(this);
		newgame.addActionListener(this);
		undo.addActionListener(this);
		move.addActionListener(this);

		int[] startplayers = { Player.COM, Player.HUM };
		String[] names = { "Computer", "Human" };
		game.newgame(startplayers, names);
	}

	public GraphRace() {
		init();
	}

	/**
	 * this method is quick & dirty trick for running this applet as an
	 * application. It is not used when a browser runs this class as an applet.
	 */
	public static void main(String[] ps) {
		Frame f = new Frame("Paper & Pencil racing");
		GraphRace p = new GraphRace();
		f.setSize(720, 640);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.add("Center", p);
		// p.init();
		p.setVisible(true);
		f.show();
	}

	// -----------------------------------
	// ------- Graphics and layout -------
	/**
	 * Updates the graphics
	 */
	public void paint(Graphics g) {
		main.paint(g);
	}

	/**
	 * sets a given text in the message label.
	 * 
	 * @param s
	 *            the message to display.
	 */
	public void message(String s) {
		messagelabel.setText(s);
	}

	/**
	 * sets a given text in the player label.
	 */
	public void showcurrentplayer() {
		playerlabel.setForeground(game.currentplayer().getcar().getcolor());
		playerlabel.setText("Player: " + game.currentplayer().getname()
				+ ", Moving: " + game.currentplayer().getcar().getvector());
	}

	public void setmessages() {
		showcurrentplayer();
		turnlabel.setText("Turn: "
				+ game.currentplayer().getcar().getplayerturns());
		if (game.finished())
			message(game.getplayer(game.winner).getname() + " WINS!!!");
		else if (game.currentplayer().isai())
			message("Click or press a key");
		else if (game.currentplayer().isnet())
			message("Waiting for networkplayer to move.");
		else
			message("Do a move");
	}

	// -------------------------------------------
	// ------ Some various util-functions --------
	/**
	 * @return The current game
	 */
	public Game getgame() {
		return game;
	}

	/**
	 * @return The current paperview
	 */
	public Paperview getpaper() {
		return paper;
	}

	// ---------------------------------------------------------
	// ------ Next all actionlistener methods are listed -------
	/**
	 *Detects mouse clicks
	 */
	public void mouseClicked(MouseEvent evt) {
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
		double x = evt.getX(), y = evt.getY();
		if (game.wait || game.finished())
			return;
		game.wait = true;
		if (game.currentplayer().type() != Player.HUM)
			game.currentplayer().ask();
		else
			game.currentplayer().clicked((int) Math.round(x / game.gridsize),
					(int) Math.round(y / game.gridsize));
		repaint();
		game.wait = false;
	}

	/**
	 * Detects when a key is pressed
	 */
	public void keyPressed(KeyEvent kvt) {
		int k = kvt.getKeyCode();
		boolean move = false;

		Point p = main.getScrollPosition();
		//Point p = main.getViewport().getLocation();
		
		switch (k) // Next four items are for scrolling
		{
		case KeyEvent.VK_LEFT:
			p.translate(-11, 0);
			move = true;
			break;
		case KeyEvent.VK_RIGHT:
			p.translate(11, 0);
			move = true;
			break;
		case KeyEvent.VK_UP:
			p.translate(0, -11);
			move = true;
			break;
		case KeyEvent.VK_DOWN:
			p.translate(0, 11);
			move = true;
			break;
		}
		main.setScrollPosition(p);
		//main.getViewport().setLocation(p);

		if (game.finished() || game.wait)
			return; // Check before move is made
		game.wait = true;

		if ((game.currentplayer().type() != Player.HUM) && (!move)) {
			game.currentplayer().ask();
			game.wait = false;
			return;
		}

		switch (k) {
		case KeyEvent.VK_NUMPAD4:
			game.currentplayer().move(0);
			break;
		case KeyEvent.VK_NUMPAD6:
			game.currentplayer().move(1);
			break;
		case KeyEvent.VK_NUMPAD8:
			game.currentplayer().move(2);
			break;
		case KeyEvent.VK_NUMPAD2:
			game.currentplayer().move(3);
			break;
		case KeyEvent.VK_NUMPAD5:
			game.currentplayer().move(4);
			break;
		}

		repaint();
		game.wait = false;
	}

	public void keyReleased(KeyEvent kvt) {
	}

	public void keyTyped(KeyEvent kvt) {
	}

	/**
	 * Detects when a button is pressed
	 */
	public void actionPerformed(ActionEvent avt) {
		if (avt.getSource() == newgame)
			new newgamewindow(this);
		else if ((avt.getSource() == undo) && !game.wait)
			game.undo();
		else if ((avt.getSource() == move) && !game.wait)
			game.currentplayer().ask();
		repaint();
	}
}

class Game {
	GraphRace ppr;
	Circuit circ;
	Paperview paper;
	Player[] player; // Array with all participating players
	int playercount; // Number of players
	int curplayer = 0; // The currentplayer
	int winner = 0; // The player who has won
	boolean finish = false, // A player has won
			wait = true, // Waiting for a routine to finish
			gamestarted = false; // True if the game is running
	final static int gridsize = 20; // The size of the grid

	NetClient nc;
	NetSClient nsc;
	boolean client = false, netgame = false;

	public Game(GraphRace p) {
		ppr = p;
		circ = new Circuit(50, 40, 30, ppr);
	}

	// --------------------------------------------------------
	// ------ Various functions for newgames and players ------
	/**
	 * This method is called to start a new game
	 * 
	 * @param ai
	 *            Array with value 'true' for computer player and 'false' for
	 *            human
	 * @param name
	 *            Array with the names of the players
	 */
	public void newgame(int[] type, String[] name) {
		System.out.println("\n---NEW GAME---");
		gamestarted = true;
		paper = ppr.getpaper();
		if (!netgame)
			circ.init(); // Initialise the circuit, in network-mode this is done
							// before
		int sx = circ.getstartx1(), // Get the start/finish location
		sy = circ.getstarty();

		playercount = type.length; // Get the number of players
		player = new Player[playercount];
		Color c;
		for (int i = 0; i < playercount; i++) {
			c = new Color(75 + (int) (Math.random() * 180), 75 + (int) (Math
					.random() * 180), 75 + (int) (Math.random() * 180));
			player[i] = new Player(name[i], type[i], ppr, startpos(i), sy, c);
		}

		if (netgame && !client) {
			nsc.outname();
			nsc.outcircuit();
			nsc.startgame();
		}

		paper.rebuffer();
		ppr.showcurrentplayer();
		ppr.turnlabel.setText("Turn: 0");
		finish = false;
		winner = 0;

		if (!netgame) {
			ppr.move.setEnabled(true);
			ppr.undo.setEnabled(true);
		}

		curplayer = playercount;
		nextplayer();
	}

	/**
	 * Shifts the turn to the next player
	 */
	public void nextplayer() {
		curplayer = ++curplayer >= playercount ? 0 : curplayer;
		if (curplayer == 0)
			win();
		ppr.main.start();
		if (!finished())
			ppr.setmessages();
	}

	// ------------------------------------------------
	// ------ Functions for networked games -----------
	/**
	 * Starts a new game with this player as client
	 */
	public void newnetgame(NetClient n) {
		netgame = true;
		client = true;
		nc = n;
		newgame(nc.getplayertype(), nc.getplayerlist());
	}

	/**
	 * Starts a new game with this player as server
	 */
	public void newnetgame(NetSClient n) {
		netgame = true;
		client = false;
		nsc = n;
		newgame(nsc.getplayertype(), nsc.getplayerlist());
	}

	public void sendmove(int i) {
		if (netgame && !client)
			nsc.outmove(i);
		else if (netgame && onturn())
			nc.outmove(i);
	}

	public boolean isnetgame() {
		return netgame;
	}

	public boolean isserver() {
		return !client;
	}

	public boolean onturn() {
		if (client) {
			if (curplayer == nc.getid())
				return true;
		} else {
			if (curplayer == nsc.getid())
				return true;
		}
		return false;
	}

	public void takeover(int id) {
		player[id].settype(Player.COM);
		if (curplayer == id)
			getplayer(id).ask();
	}

	// ---------------------------------------------------------------------
	// ------ Functions for checking if a player has finshed already -------
	/**
	 * This checks if there is a winner. If so, it stops the game and makes an
	 * anouncement
	 */
	public void win() {
		int w = checkfinished();
		if (w == -1)
			return;
		gamestarted = false;
		winner = w;
		finish = true;
		curplayer = winner;
		ppr.message(getplayer(winner).getname() + " WINS!!!");
		ppr.undo.setEnabled(false);
		ppr.move.setEnabled(false);
		if (netgame) {
			if (client) {
				nc.netwin.setState(java.awt.Frame.NORMAL);
			} else {
				nsc.netwin.setState(java.awt.Frame.NORMAL);
				nsc.endgame("[Player " + player[winner].getname()
						+ " finished first!]");
				nsc.game.gamestarted = false;
			}
		}
	}

	/**
	 * Returns the player who has won or -1 The player who started last is
	 * always in advantage, for starting in outer row. If more players finish in
	 * the same turn, the last of them wins the game.
	 */
	int checkfinished() {
		if (player[0].getcar().getturns() < 10)
			return -1; // Don't do nothing during the first turns
		Car c;
		int vx, vy, x1, y1, ret = -1;
		double rc = 0;
		double x;
		int sy = circ.starty, sx1 = circ.startx1 - 1, sx2 = circ.startx2 - 1;
		for (int i = 0; i < playercount; i++) // Repeat for all players
		{
			c = player[i].getcar();
			vx = c.getvector().getx();
			vy = c.getvector().gety();
			x1 = c.getx() - vx;
			y1 = c.gety() - vy;
			if (vx == 0) // Car goes Vertical
			{
				if ((x1 + vx >= sx1) && (x1 + vx <= sx2)) // Within finish line
															// on x-axis
				{
					if (((y1 <= sy) && (y1 + vy >= sy))
							|| ((y1 >= sy) && (y1 + vy <= sy))) // Around or on
																// finish on
																// y-axis
					{
						ret = i;
					}
				}
			} else {
				rc = (double) vy / (double) vx;
				if (rc == 0) // Car goes Horizontal
				{
					if ((x1 + vx >= sx1) && (x1 + vx <= sx2)) // Ending on
																// finsh-line on
																// x-axis
					{
						if (y1 + vy == sy) // Ending on finsh-line on y-axis
						{
							ret = i;
						}
					}
				} else // Car goes Diagonal
				{
					x = (double) (sy - y1) / rc;
					x += x1;
					if (((x >= x1) && (x <= x1 + vx))
							|| ((x <= x1) && (x >= x1 + vx))) // Within the
																// vector, not
																// just 'in
																// line'
					{
						if ((x >= sx1) && (x <= sx2)) // Within the start/finish
														// line
						{
							ret = i;
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * @return True if the game has ended
	 */
	public boolean finished() {
		return finish;
	}

	// -------------------------------------------
	// ------ Some various util-functions --------
	/**
	 * @return The size of the grid
	 */
	public int getgridsize() {
		return gridsize;
	}

	/**
	 * @return The current circuit
	 */
	public Circuit getcirc() {
		return circ;
	}

	/**
	 * @return the number of players
	 */
	public int getplayercount() {
		return playercount;
	}

	/**
	 * @return The requested player
	 * @param i
	 *            Index of the player requested
	 */
	public Player getplayer(int i) {
		if ((i < playercount) && (i >= 0))
			return player[i];
		// System.out.println("Special Player requested:"+i);
		return currentplayer();
	}

	/**
	 * @return The current player
	 */
	public Player currentplayer() {// System.out.println("Current player requested:"+curplayer+", "+player[curplayer]);
		return player[curplayer];
	}

	/**
	 * this method undoes a move, if possible.
	 */
	public void undo() {
		if (--curplayer < 0)
			curplayer = playercount - 1;
		currentplayer().getcar().undo();
		ppr.showcurrentplayer();
		ppr.main.start();
	}

	public int startpos(int i) {
		int x1 = circ.getstartx1(), // Get the start/finish location
		x2 = circ.getstartx2();
		return (i % (x2 - x1)) + x1;
	}
}

/**
 * This class implements both the data concerning each player, as well as
 * functions for calculating the route for the computer players.
 */
class Player {
	int type; // Decide if this is a computer player
	String name; // Name of the player
	Car car; // The player's car
	Circuit circ; // The Circuit
	GraphRace ppr; // The parental applet
	Game game;

	/**
	 * The directions to which a player can go
	 */
	public final static int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SAME = 4;

	/**
	 * The type of the player: Computer, Human or Network-player
	 */
	public final static int COM = 0, HUM = 1, NET = 2;

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
	public Player(String nm, int t, GraphRace p, int startx, int starty,
			Color col) {
		name = nm;
		type = t;
		ppr = p;
		game = ppr.getgame();
		circ = game.getcirc();
		car = new Car(startx, starty, col);
	}

	/**
	 * Is used to make the computer do a move
	 */
	public void ask() {
		if (type == COM) {
			ppr.message("Thinking...");
			game.wait = true;
			moveai();
		}
	}

	/**
	 * @return True if the player is a computer player
	 */
	public boolean isai() {
		return type == COM;
	}

	/**
	 * @return True if the player is a network player
	 */
	public boolean isnet() {
		return type == NET;
	}

	/**
	 * @return The type of this player: HUM, COM, or NET
	 */
	public int type() {
		return type;
	}

	public void settype(int i) {
		type = i;
	}

	/**
	 * For user input from mouse
	 * 
	 * @param x
	 *            Horizontal (circuit) location
	 * @param x
	 *            Vertical (circuit) location
	 */
	public synchronized void clicked(int x, int y) {
		int cx = car.getx() + car.getvector().getx(), cy = car.gety()
				+ car.getvector().gety();
		int m = -1;
		if ((x == cx) && (y == cy))
			m = SAME;
		else if ((cx - x == -1) && (cy - y == 0))
			m = RIGHT;
		else if ((cx - x == 1) && (cy - y == 0))
			m = LEFT;
		else if ((cx - x == 0) && (cy - y == -1))
			m = DOWN;
		else if ((cx - x == 0) && (cy - y == 1))
			m = UP;
		if (m >= 0)
			move(m);
	}

	/**
	 * For actually moving the car
	 * 
	 * @param m
	 *            One of: LEFT, RIGHT, UP, DOWN, SAME
	 */
	public synchronized void move(int m) {
		Vector v = car.getvector();
		if (game.finished())
			return;
		switch (m) {
		case LEFT:
			movecar(v.getx() - 1, v.gety());
			break;
		case RIGHT:
			movecar(v.getx() + 1, v.gety());
			break;
		case UP:
			movecar(v.getx(), v.gety() - 1);
			break;
		case DOWN:
			movecar(v.getx(), v.gety() + 1);
			break;
		case SAME:
			movecar(v.getx(), v.gety());
			break;
		}

		ppr.paper.rebuffer();
		ppr.repaint();

		if (game.isnetgame())
			if (game.onturn() || game.currentplayer().isai())
				game.sendmove(m);
		game.nextplayer();
		game.wait = false;
	}

	/**
	 *Internal function for moving the car to the new position.
	 */
	public synchronized void movecar(int x, int y) {
		car.move(new Vector(x, y));
	}

	/**
	 * Checks if the car has hit the grass. If so, the speed is reduced to (0,0)
	 */
	public synchronized void checkterrain() {
		if (circ.terrain(car.getx(), car.gety()) == 0) {
			car.move(new Vector(0, 0)); // Grass/gravel Speed reduced to zero
			car.fault();
		}
	}

	/**
	 * @return The name of the player
	 */
	public String getname() {
		return name;
	}

	/**
	 * @return The car of the player
	 */
	public Car getcar() {
		return car;
	}

	public String toString() {
		return getname();
	}

	// --------------------------------------------------------
	// ------- Functions for the computer player --------------

	int cx, cy, level, i, j;
	double distance = 0; // The current distance of the players' car
	boolean grass;

	/**
	 * This function is called by ask() to move the computer player
	 */
	public void moveai() {
		double mv;
		if (!isai())
			return; // To make sure this method isn't used illegally

		cx = circ.getsizex() / 2; // The x-coordinate of the center of the
									// circuit
		cy = circ.getsizey() / 2; // The y-coordinate of the center of the
									// circuit

		level = 6 + (int) Math.round(car.getspeed() * 1.4); // Level is the
															// depth of the
															// search
		grass = (circ.terrain(car.getx(), car.gety()) <= 0); // Is the car
																// currently at
																// the grass?

		Vector v = car.getvector();
		distance = Math.atan2(-(cy - car.gety()), (cx - car.getx())) + Math.PI; // Update
																				// the
																				// current
																				// distance
																				// of
																				// the
																				// car
		mv = ai(car.getx(), car.gety(), v.getx(), v.gety(), level);
		move((int) mv);
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
	public double ai(int x, int y, int vx, int vy, int l) {
		double[] e = new double[5];

		double d = Math.atan2(-(cy - y), (cx - x)) + Math.PI; // Calculate the
																// distance

		// The arc-distance values range from 0 to 2 PI. To solve border-cases
		// in the from 1.5 PI to 0.5 PI, checking is needed.
		if ((d - distance) > Math.PI)
			d -= 2 * Math.PI;
		if ((d - distance) < -Math.PI)
			d += 2 * Math.PI;

		if (l > 0) {
			// Check borders
			if (circ.terrain(x, y) <= 0) {
				vx = 0;
				vy = 0;
				if (!grass)
					l -= 2; // Penalty to get quicker results.
			}

			// If we're not done yet, check all possible routes.
			if (l > 0) {
				e[0] = ai(x + vx - 1, y + vy, vx - 1, vy, l - 1);
				e[1] = ai(x + vx + 1, y + vy, vx + 1, vy, l - 1);
				e[2] = ai(x + vx, y + vy - 1, vx, vy - 1, l - 1);
				e[3] = ai(x + vx, y + vy + 1, vx, vy + 1, l - 1);
				e[4] = ai(x + vx, y + vy, vx, vy, l - 1);

				// Next get the largest distance
				for (i = 0; i < 5; i++) {
					if (e[i] > d) {
						d = e[i];
						j = i;
					}
				}
			}
		}

		if (l == level)
			return j; // If this was the top-thread, return the best route
		return d; // Else return the distance
	}

	// Old routine which used the actual distance instead of the arc-distance.
	// This
	// resulted in some unexpected behaviour:
	// public int ai(int x, int y, int vx, int vy, int l, int k)
	// {
	// int d = 0, i, j = 0, dx,dy;
	//
	// int[] e = new int[5];
	// if (l > 0)
	// {
	// // Check borders
	// int t = circ.terrain(x,y);
	// if (t <= 0)
	// { l-=2;
	// vx=0; vy=0;
	// d = 0;
	// }
	//
	// if (l > 0)
	// { e[0] = ai(x+vx - 1, y+vy, vx - 1, vy , l-1,k);
	// e[1] = ai(x+vx + 1, y+vy, vx + 1, vy , l-1,k);
	// e[2] = ai(x+vx, y+vy - 1, vx, vy - 1, l-1,k);
	// e[3] = ai(x+vx, y+vy + 1, vx, vy + 1, l-1,k);
	// e[4] = ai(x+vx, y+vy, vx, vy , l-1,k);
	//
	// for (i=0; i<5; i++)
	// if (e[i] > d)
	// { d = e[i];
	// j = i; // j is de te kiezen route als l == m
	// }
	// }
	// }
	//
	// d += Math.sqrt(vx*vx + vy*vy);
	// if (l == k) return j;
	// return d;
	// }

}

/**
 * This is the data class for the users car. It contains the locationhistory of
 * the car. It also features an undo-function. The car is moved, using vectors.
 */
class Car {
	int x, y, // The current position of the car
			startx, starty; // The start position of the car
	Color color; // The color of the car
	int turn = 0; // The number of moves

	/**
	 * When a car hits the grass, the speed is reduced to zero. Therefore, after
	 * the players' move, a second move is made, reducing the speed to zero.
	 * This is counted seperately, to keep track of the real number of turns.
	 */
	boolean[] fault = new boolean[500];
	int faultcount = 0;

	Vector[] hist = new Vector[500]; // The history, containing all move-vectors

	/**
	 * Builds a new car
	 * 
	 * @param sx
	 *            Horizontal start location
	 * @param sy
	 *            Vertical start location
	 * @param c
	 *            Color of the car
	 */
	public Car(int sx, int sy, Color c) {
		x = sx;
		y = sy;
		startx = sx;
		starty = sy;
		hist[0] = new Vector(0, 0);
		color = c;
		for (int i = 0; i < 500; i++)
			fault[i] = false;
	}

	/**
	 * Moves the car using the given vector
	 * 
	 * @param vec
	 *            The new speed-vector
	 */
	public void move(Vector vec) {
		x += vec.getx();
		y += vec.gety();
		hist[++turn] = vec;
	}

	/**
	 * Is called when the grass is hit, to keep track of erronous counted turns
	 */
	public void fault() {
		fault[turn] = true;
		faultcount++;
	}

	/**
	 * @return The horizontal position of the car
	 */
	public int getx() {
		return x;
	}

	/**
	 * @return The vertical position of the car
	 */
	public int gety() {
		return y;
	}

	/**
	 * @return The horizontal start-position of the car
	 */
	public int getstartx() {
		return startx;
	}

	/**
	 * @return The vertical start-position of the car
	 */
	public int getstarty() {
		return starty;
	}

	/**
	 * @return The number of turns done, including erronous
	 */
	public int getturns() {
		return turn;
	}

	/**
	 * @return The number of turns done by the player
	 */
	public int getplayerturns() {
		return turn - faultcount;
	}

	/**
	 * @return The movement vector on a specific point of time
	 * @param i
	 *            Index of the desired vector
	 */
	public Vector gethistory(int i) {
		return hist[i];
	}

	/**
	 * @return The current movement-vector
	 */
	public Vector getvector() {
		return hist[turn];
	}

	/**
	 * @return The current speed of the car
	 */
	public double getspeed() {
		return hist[turn].length();
	}

	/**
	 * @return The color of the car
	 */
	public Color getcolor() {
		return color;
	}

	/**
	 * @return A string representation of the car, including position and
	 *         movement
	 */
	public String toString() {
		return ("Car(" + x + "," + y + "," + hist[turn] + ")");
	}

	/**
	 * This function undos the last players' move
	 */
	public void undo() {
		x -= hist[turn].getx();
		y -= hist[turn].gety();
		if (--turn < 0)
			turn = 0;
		else if (fault[turn + 1]) {
			undo();
			faultcount--;
			fault[turn + 1] = false;
		}
	}
}

/**
 * Dataclass for saving the car-movements
 */
class Vector {
	int vx, vy; // Horizontal and vertical speed

	/**
	 * Constructs a new Vector
	 * 
	 * @param x
	 *            Horizontal speed
	 * @param y
	 *            Vertical speed
	 */
	public Vector(int x, int y) {
		vx = x;
		vy = y;
	}

	/**
	 * @return The mathematical length of this Vector
	 */
	public double length() {
		return Math.sqrt(vx * vx + vy * vy);
	}

	/**
	 * @return The horizontal speed
	 */
	public int getx() {
		return vx;
	}

	/**
	 * @return The vertical speed
	 */
	public int gety() {
		return vy;
	}

	/**
	 * @param x
	 *            Horizontal speed to set
	 * @return the new set horizontal speed
	 */
	public int setx(int x) {
		vx = x;
		return vx;
	}

	/**
	 * @param x
	 *            Vertical speed to set
	 * @return the new set vertical speed
	 */
	public int sety(int y) {
		vy = y;
		return vy;
	}

	/**
	 * @return A String representation of this Vector
	 */
	public String toString() {
		return ("(" + vx + "," + vy + ")");
	}
}



/**
 * This class presents a window for starting a new game It consists of two
 * parts: - Number of players selection - Selection of players and names
 */
class newgamewindow extends Frame implements ActionListener {
	GraphRace ppr;
	Game game;

	Choice cm = new Choice();
	Choice[] cm2;
	TextField[] tf;
	Button ok = new Button("ok"), start = new Button("start"),
			cancel = new Button("cancel"), normal = new Button("normal"),
			network = new Button("network");
	Label desc = new Label("Number of players:");

	int count = 0;

	/**
	 * create a new newgamewindow. It shown immediately. You can use this dialog
	 * only once.
	 * 
	 * @param p
	 *            the applet that wants a new game.
	 */
	public newgamewindow(GraphRace p) {
		super("New game");
		ppr = p;
		game = ppr.getgame();
		game.wait = true;

		normal.addActionListener(this);
		network.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		start.addActionListener(this);

		setLayout(new GridLayout(1, 3));
		add(normal);
		add(network);
		add(cancel);

		Point point = ppr.getLocationOnScreen(); // Try to center the window to
													// the mainscreen
		point.translate((int) (ppr.getSize().getWidth() / 2 - 125), (int) (ppr
				.getSize().getHeight() / 2 - 50));
		setLocation(point);
		pack();
		setResizable(false);
		pack();
		show();
	}

	/**
	 * Create a dialog for a normal game, on one computer
	 */
	public void normalgame() {
		for (int i = 0; i < 8; i++)
			cm.add("" + i);
		cm.select("2");

		removeAll();
		setLayout(new GridLayout(2, 2));
		add(desc);
		add(cm);
		add(ok);
		add(cancel);
		pack();
	}

	/**
	 * Create a new dialog for selecting the type of the players and their names
	 */
	void newplayer() {
		int i;
		cm2 = new Choice[count];
		tf = new TextField[count];
		for (i = 0; i < count; i++) {
			cm2[i] = new Choice();
			tf[i] = new TextField(10);
			if (i == 0) {
				cm2[i].add("Human");
				cm2[i].add("Computer");
				tf[i].setText("Human");
			} else {
				cm2[i].add("Computer");
				cm2[i].add("Human");
				tf[i].setText("Computer " + i);
			}
		}

		removeAll(); // Clear the screen, and fill it again.
		setLayout(new GridLayout(count + 1, 2));
		for (i = 0; i < count; i++) {
			add(cm2[i]);
			add(tf[i]);
		}
		add(start);
		add(cancel);
		setSize(250, 300);
		pack();
	}

	/**
	 * this method is called any time a button is pressed or an option selected.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			count = Integer.parseInt(cm.getSelectedItem());
			newplayer();
		} else if (e.getSource() == start) {
			int[] player = new int[count];
			String[] name = new String[count];
			for (int i = 0; i < count; i++) {
				player[i] = cm2[i].getSelectedItem().equals("Computer") ? Player.COM
						: Player.HUM;
				name[i] = tf[i].getText();
			}
			game.newgame(player, name); // Start a new game, with the given
										// players
			game.wait = false;
			dispose();
		} else if (e.getSource() == cancel) {
			game.wait = false;
			dispose();
		} else if (e.getSource() == normal) {
			normalgame();
		} else if (e.getSource() == network) {
			dispose();
			new newnetwindow(ppr);
		}
	}
}

/**
 * This class presents a window for starting a new game It consists of several
 * parts: - Selection of name - Selection of client / server - Connection-window
 * with chat-functionality
 */
class newnetwindow extends Frame implements ActionListener {
	GraphRace ppr;
	Game game;

	boolean isclient = false;
	NetSClient nsc;
	NetClient nc;

	Button start = new Button("Start"), quit = new Button("Quit"),
			disconn = new Button("Disconnect"), send = new Button("Send"),
			server = new Button("Server"), client = new Button("Client"),
			connect = new Button("Connect"), ok = new Button("OK"),
			end = new Button("End game");

	TextArea chatbox = new TextArea("");
	TextField input = new TextField("");
	TextField ip = new TextField("");
	TextField name = new TextField("");
	List playerlist = new List(8, false);

	String nam = "", address = "";

	/**
	 * Constructs a new window for the networkgame
	 */
	public newnetwindow(GraphRace p) {
		super("Network game");
		ppr = p;
		game = ppr.getgame();
		game.gamestarted = false;

		ppr.newgame.setEnabled(false);
		ppr.move.setEnabled(false);
		ppr.undo.setEnabled(false);

		server.addActionListener(this);
		client.addActionListener(this);
		connect.addActionListener(this);
		start.addActionListener(this);
		quit.addActionListener(this);
		disconn.addActionListener(this);
		ok.addActionListener(this);
		input.addActionListener(this);
		ip.addActionListener(this);
		send.addActionListener(this);
		end.addActionListener(this);

		init();
		Point point = ppr.getLocationOnScreen(); // Try to center the window to
													// the mainscreen
		point.translate((int) (ppr.getSize().getWidth() / 2 - 125), (int) (ppr
				.getSize().getHeight() / 2 - 50));
		setLocation(point);
		pack();
		show();
		game.wait = true;
	}

	/**
	 * Initializes first window, with choice for server or client
	 */
	public void init() {
		removeAll(); // Clear the screen, and fill it again.
		setLayout(new BorderLayout());
		Panel top = new Panel(), bot = new Panel();
		top.setLayout(new GridLayout(1, 2));
		top.add(new Label("Name:"));
		top.add(name);
		name.requestFocus();
		bot.setLayout(new GridLayout(1, 3));
		bot.add(server);
		bot.add(client);
		bot.add(quit);
		add("North", top);
		add("South", bot);
		setSize(250, 150);
		pack();
	}

	/**
	 * Shows a dialog for entering the server-IP
	 */
	public void chooseserver() {
		removeAll(); // Clear the screen, and fill it again.
		setLayout(new GridLayout(2, 2));
		add(new Label("Server-IP or Hostname:"));
		add(ip);
		ip.requestFocus();
		add(connect);
		add(quit);
		setSize(250, 150);
		pack();
	}

	/**
	 * Shows the window with functionality for the server
	 */
	public void showserver() {
		Panel left = new Panel(new BorderLayout()), right = new Panel(
				new BorderLayout()), buttons = new Panel(new FlowLayout()), chatinput = new Panel(
				new BorderLayout());
		removeAll();
		String ip = "";
		try {
			ip += java.net.InetAddress.getLocalHost();
		} catch (Exception e) {
		}
		left.add("North", new Label("Server: " + ip));
		updatelist(nsc.name);
		left.add("Center", playerlist);
		buttons.add(disconn);

		if (game.gamestarted)
			buttons.add(end);
		else
			buttons.add(start);
		buttons.add(quit);
		left.add("South", buttons);

		right.add("Center", chatbox);
		chatinput.add("Center", input);
		chatinput.add("East", send);
		right.add("South", chatinput);

		setLayout(new GridLayout(1, 2));
		setSize(350, 350);
		add(left);
		add(right);
		show();
	}

	/**
	 * Shows the window with functionality for the client
	 */
	public void showclient() {
		Panel left = new Panel(new BorderLayout()), right = new Panel(
				new BorderLayout()), buttons = new Panel(new FlowLayout()), chatinput = new Panel(
				new BorderLayout());
		removeAll();

		left.add("North", new Label("Server: " + nc.client.getInetAddress()));
		left.add("Center", playerlist);
		buttons.add(disconn);
		left.add("South", buttons);

		right.add("Center", chatbox);
		chatinput.add("Center", input);
		chatinput.add("East", send);
		right.add("South", chatinput);

		setLayout(new GridLayout(1, 2));
		setSize(350, 350);
		add(left);
		add(right);
		show();
	}

	/**
	 * Displays an message and returns to the server/client selection
	 */
	public void message(String s) {
		removeAll();
		setLayout(new BorderLayout());
		TextArea tf = new TextArea(s);
		tf.setEditable(false);
		tf.setColumns(30);
		add("Center", tf);
		add("South", ok);
		pack();
	}

	/**
	 * Displays a message in the chatbox
	 */
	public void inmessage(String s) {
		chatbox.append(s + "\n");
	}

	/**
	 * Updates the playerlist, using the given String[]
	 */
	public void updatelist(String[] l) {
		playerlist.removeAll();
		int ID = isclient ? nc.ID : nsc.ID;
		for (int i = 0; i < l.length; i++)
			if (ID == i)
				playerlist.add("> " + l[i] + " <");
			else
				playerlist.add(l[i]);
	}

	/**
	 * this method is called any time a button is pressed or an option selected.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == client) {
			nam = name.getText();
			if (nam.equals(""))
				return;
			isclient = true;
			chooseserver();
		} else if (e.getSource() == server) {
			nam = name.getText();
			if (nam.equals(""))
				return;
			isclient = false;
			try {
				nsc = new NetSClient(ppr, nam, this);
				showserver();
			} catch (Exception ex) {
				message("Error:\nThe server could not be started:\n" + ex);
				ex.printStackTrace();
			}
		} else if ((e.getSource() == connect) || (e.getSource() == ip)) {
			address = ip.getText();
			if (address.equals(""))
				return;
			try {
				nc = new NetClient(address, ppr, nam, this);
				showclient();
			} catch (Exception ex) {
				message("Error:\nThe client could not connect to the server:\n"
						+ ex);
				ex.printStackTrace();
			}
		} else if (e.getSource() == disconn) {
			if (isclient) {
				nc.stop();
				message("You disconnected from the game");
				return;
			}
			int index = playerlist.getSelectedIndex();
			nsc.indisconnect(index > 0 ? index : 0);
		} else if (e.getSource() == quit) {
			if (isclient && (nc != null))
				nc.stop();
			else if (nsc != null)
				nsc.stop();

			game.wait = true;
			ppr.newgame.setEnabled(true);
			ppr.move.setEnabled(false);
			ppr.undo.setEnabled(false);
			dispose();
		} else if (e.getSource() == start) {
			if (isclient)
				return;
			game.getcirc().init();
			game.newnetgame(nsc);
			showserver();
			setState(Frame.ICONIFIED);
		} else if ((e.getSource() == send) || (e.getSource() == input)) {
			String s = input.getText();
			if (isclient)
				nc.outmessage(s);
			else
				nsc.outmessage(s);
			chatbox.append(nam + ">  " + s + "\n");
			input.setText("");
		} else if (e.getSource() == ok) {
			ppr.newgame.setEnabled(true);
			dispose();
		} else if (e.getSource() == end) {
			nsc.endgame("[Server ended game]");
			showserver();
		}
	}
}

/**
 * This class provides the functionality for playing a network game as client.
 * It takes care of the communication with the server
 */
class NetClient {
	NetReceive in; // Incoming connection
	NetSend out; // Outgoing connection
	Socket client; // The socket-implementation for the connections
	int ID = -1; // ID of this client
	public final static int PORT = NetServer.PORT; // Connection-port on the
													// server

	GraphRace ppr;
	Game game;
	newnetwindow netwin;

	String[] name; // List of names of all players
	String clientname; // Name of this player

	/**
	 * Creates a new client and connects to the server on port 8192
	 */
	public NetClient(String server, GraphRace p, String n, newnetwindow nw)
			throws IOException {
		client = new Socket(server, PORT);
		ppr = p;
		in = new NetReceive(client, ID, this);
		in.start();
		clientname = n;
		netwin = nw;
		game = ppr.getgame();
	}

	/**
	 * Constructor for testing
	 */
	NetClient(String server) throws IOException {
		client = new Socket(server, PORT);
		in = new NetReceive(client, ID, this);
		in.start();
		clientname = "Test Client";
	}

	/**
	 * Gives this client an id number
	 */
	public void inid(int i) {
		ID = i;
		System.out.println("This client got ID:" + ID);
		in.setid(ID);
		out = new NetSend(client, ID, this);
		out.start();
		outname();
		showmessage("[Connected to server]");
	}

	/**
	 * @return the client ID
	 */
	public int getid() {
		return ID;
	}

	/**
	 * Sends this clients' name to the server
	 */
	public void outname() {
		out.sendname(clientname);
	}

	/**
	 * Is called when a list of players is recieved from the server
	 */
	public void inname(String[] n) {
		name = n;
		netwin.updatelist(name);
	}

	/**
	 * Sends a message to the server
	 */
	public void outmessage(String s) {
		out.message(clientname + "> " + s);
	}

	/**
	 * Is called when a message is received from the server
	 */
	public void inmessage(String s) {
		showmessage(s);
	}

	/**
	 * Sends this clients' move to the server
	 */
	public void outmove(int m) {
		out.carmove("" + m);
	}

	/**
	 * Receives the current player's move from the server
	 */
	public void inmove(String s) {
		game.currentplayer().move(Integer.parseInt(s));
	}

	/**
	 * Is called when the startgame-signal is received
	 */
	public void startgame() {
		showmessage("[Starting game...]");
		game.newnetgame(this);
		netwin.setState(java.awt.Frame.ICONIFIED);
	}

	/**
	 * Is called when the endgame-signal is received
	 */
	public void endgame() {
		showmessage("[Game ended by server]");
	}

	/**
	 * Is called when the server disconnects this client
	 */
	public void indisconnect() {
		showmessage("[Server has disconnected or quit]");
		in.stop();
		out.stop();
	}

	/**
	 * Disconnects this client from the server
	 */
	public void outdisconnect() {
		showmessage("[Disconnected]");
		out.disconnect();
	}

	/**
	 * Loads the circuit send by the server
	 */
	public void incircuit(String s) {
		ppr.getgame().getcirc().upload(s);
	}

	/**
	 * Stops all in/outcoming connections
	 */
	public void stop() {
		outdisconnect();
		in.stop();
		out.stop();
	}

	// -------------------------------------------------
	// -------- Various util-functions -----------------
	/**
	 * @return The list of currently connected players
	 */
	public String[] getplayerlist() {
		return name;
	}

	/**
	 * @return The list of types of the connected players
	 */
	public int[] getplayertype() {
		int l = name.length;
		int[] t = new int[l];
		for (int i = 0; i < l; i++)
			t[i] = Player.NET;
		t[ID] = Player.HUM;
		return t;
	}

	/**
	 * Shows the incoming message in the chatbox of the newnetwindow
	 */
	public void showmessage(String s) {
		netwin.inmessage(s);
	}
}

/**
 * This class provides the functionality for playing a network game as server.
 * It takes care of the communication with the clients
 */
class NetSClient {
	NetServer server; // The server that detects new clients
	NetReceive in[]; // Incoming connections
	NetSend out[]; // Outgoing connections
	Socket client[]; // List of sockets, one for each client
	public boolean enabled[]; // List of enabled clients

	String[] name; // List of names of all players

	GraphRace ppr;
	Game game;
	newnetwindow netwin;

	int ID = 0; // ID of this server. Always equal to 0

	final static int maxplayers = 8; // Maximum number of participants
	int playercount = 1; // Current number of participants

	/**
	 * Creates a new server-client and starts the server
	 */
	public NetSClient(GraphRace p, String n, newnetwindow nw)
			throws IOException {
		ppr = p;
		server = new NetServer(this);
		server.start();
		in = new NetReceive[maxplayers];
		out = new NetSend[maxplayers];
		client = new Socket[maxplayers];
		enabled = new boolean[maxplayers];
		name = new String[maxplayers];
		name[0] = n;
		netwin = nw;
		game = ppr.getgame();
		enabled[0] = true;
	}

	/**
	 * Constructor for testing
	 */
	NetSClient() throws IOException {
		server = new NetServer(this);
		server.start();
		in = new NetReceive[maxplayers];
		out = new NetSend[maxplayers];
		client = new Socket[maxplayers];
		enabled = new boolean[maxplayers];
		name = new String[maxplayers];
		name[0] = "Test Server";
		enabled[0] = true;
	}

	/**
	 * Adds a player and creates in/outcoming connections
	 */
	public synchronized void addplayer(Socket s) {
		if (game.gamestarted)
			return;
		System.out.println("Add network player:" + playercount);
		if (playercount > maxplayers) {
			new NetSend(s, playercount, this).disconnect();
			System.out
					.println("Maximum netplayers reached... disconnecting from player");
			return;
		}
		client[playercount] = s;
		in[playercount] = new NetReceive(s, playercount, this);
		in[playercount].start();
		out[playercount] = new NetSend(s, playercount, this);
		out[playercount].start();
		out[playercount].acknowledge();
		enabled[playercount] = true;
		playercount++;
		netwin.updatelist(name);
	}

	/**
	 * This method disables the player on the given ID.
	 */
	public void removeplayer(int id) {
		if (id == ID)
			return;
		enabled[id] = false;
		if (!game.gamestarted)
			doremove(); // Removing should be done only, when not in an active
						// game.
		// This is to prevent discrepancy in the game and the network
	}

	/**
	 * This method removes all marked players.
	 */
	public void doremove() {
		String[] n = new String[maxplayers];
		Socket[] s = new Socket[maxplayers];
		NetSend[] ns = new NetSend[maxplayers];
		NetReceive[] nr = new NetReceive[maxplayers];

		int c = 0;
		for (int i = 0; i < maxplayers; i++)
			if (enabled[i]) {
				s[c] = client[i];
				n[c] = name[i];
				ns[c] = out[i];
				nr[c] = in[i];
				enabled[c] = true;
				c++;
			}
		playercount = c;
		name = n;
		client = s;
		netwin.updatelist(name); // Update the playerlist on this machine and
									// the clients'
		outname();
	}

	/**
	 * This function is called by NetReceive, to set the name of the connected
	 * player.
	 */
	public void inname(int i, String s) {
		name[i] = s;
		netwin.updatelist(name);
		outname();
	}

	/**
	 * Sends the player-list to all connected players
	 */
	public void outname() {
		for (int i = 1; i < playercount; i++)
			out[i].players(name);
	}

	/**
	 * @return the ID of the server
	 */
	public int getid() {
		return ID;
	}

	/**
	 * The server sends a message to the players The message goes to all other
	 * players
	 */
	public void outmessage(String s) {
		for (int i = 1; i < playercount; i++)
			if (enabled[i])
				out[i].message(name[0] + "> " + s);
	}

	/**
	 * The server gets a message from a player The message goes to all other
	 * players
	 */
	public void inmessage(int id, String s) {
		showmessage(s);
		for (int i = 1; i < playercount; i++)
			if ((i != id) && enabled[i])
				out[i].message(s);
	}

	/**
	 * The server does a move
	 */
	public void outmove(int m) {
		for (int i = 1; i < playercount; i++)
			if (enabled[i])
				out[i].carmove("" + m);
	}

	/**
	 * A player sends a move to the server
	 */
	public void inmove(int id, String s) {
		int m = Integer.parseInt(s);
		for (int i = 1; i < playercount; i++)
			if ((i != id) && enabled[i])
				out[i].carmove("" + m);
		game.currentplayer().move(m);
	}

	/**
	 * The server starts the game
	 */
	public void startgame() {
		showmessage("[Starting game]");
		for (int i = 1; i < playercount; i++)
			if (enabled[i])
				out[i].newgame();
		game.gamestarted = true;
	}

	/**
	 * The server ends the game
	 */
	public void endgame(String s) {
		for (int i = 1; i < playercount; i++)
			if (enabled[i])
				out[i].endgame(s);
		game.gamestarted = false;
		doremove();
		netwin.showserver();
	}

	/**
	 * A client disconnects from the server
	 */
	public void indisconnect(int id) {
		if (id == ID)
			return;
		out[id].stop();
		in[id].stop();
		removeplayer(id);
		if (game.gamestarted) {
			ppr.getgame().takeover(id);
			netwin.inmessage("[Player " + name[id] + " left the game]");
			outmessage("[Player " + name[id] + " left the game]");
		}
	}

	/**
	 * The server disconnects a client
	 */
	public void outdisconnect(int id) {
		out[id].disconnect();
		out[id].stop();
		in[id].stop();
		try {
			client[id].close();
		} catch (Exception e) {
		}
	}

	/**
	 * The server sends the circuit to all players
	 */
	public void outcircuit() {
		String s = ppr.getgame().getcirc().download();
		for (int i = 1; i < playercount; i++)
			if (enabled[i])
				out[i].circuit(s);
	}

	/**
	 * Stops the server and terminates all connections
	 */
	public void stop() {
		System.out.println("Sending stop-signal to server");
		server.stop();
		for (int i = 1; i < playercount; i++) {
			out[i].endgame("[Server quit the game]");
			out[i].stop();
			in[i].stop();
			try {
				client[i].close();
			} catch (Exception e) {
			}
		}
	}

	// -------------------------------------------------
	// -------- Various util-functions -----------------
	/**
	 * @return The list of currently connected players
	 */
	public String[] getplayerlist() {
		String[] n = new String[playercount];
		for (int i = 0; i < playercount; i++)
			n[i] = name[i];
		return n;
	}

	/**
	 * @return The list of types of the connected players
	 */
	public int[] getplayertype() {
		int[] t = new int[playercount];
		for (int i = 0; i < playercount; i++)
			t[i] = Player.NET;
		t[ID] = Player.HUM;
		return t;
	}

	/**
	 * Shows a message on the chatbox in the newnetwin
	 */
	public void showmessage(String s) {
		netwin.inmessage(s);
	}
}

/**
 * This class implements the procedures for sending data from the server or a
 * client
 */
class NetSend implements Runnable {
	Socket soc; // The socketimplementation
	NetClient nc; // The client using this NetSend
	NetSClient nsc; // The server using this NetSend
	String type, message; // The message and type to be send
	boolean succeed = false, // True if the message has been send
			client = true, // True if the owner is a client
			newmsg = false, // True if a new message is awaiting
			quit = false; // True if the thread has to stop
	int ID; // The ID of the client to which the socket is connected

	PrintWriter out; // Outputstream

	final static String ACKCONN = "!!!ackconn!!!", // Server sends
													// connect-acknowledgement
			DISCONN = "!!!disconn!!!", // Client or server sends disconnect
			PLAYERS = "!!!players!!!", // Server sends playerlist
			MESSAGE = "!!!message!!!", // Server or client sends a message
			NEWGAME = "!!!newgame!!!", // Server starts the newgame
			CARMOVE = "!!!carmove!!!", // Client or server sends carmove
			ENDGAME = "!!!endgame!!!", // Server sends endgame
			CIRCUIT = "!!!circuit!!!", // Server sends the circuit to play
			SETNAME = "!!!setname!!!"; // Client sets its name

	/**
	 * Creates a new sendingplatform for a client
	 */
	public NetSend(Socket s, int i, NetClient n) {
		soc = s;
		nc = n;
		ID = i;
	}

	/**
	 * Creates a new sendingplatform for the server
	 */
	public NetSend(Socket s, int i, NetSClient n) {
		soc = s;
		nsc = n;
		ID = i;
	}

	/**
	 * Function to start this thread, must be called once
	 */
	public void start() {
		new Thread(this).start();
	}

	/**
	 * Tells this class to start sending the new message. It waits until the
	 * previous operation is finished.
	 */
	public synchronized void send(String t, String m) {
		while (newmsg)
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		newmsg = true;
		type = t;
		message = m;
	}

	/**
	 * Stops this thread
	 */
	public void stop() {
		quit = true;
	}

	/**
	 * Resets the client id to the new value
	 */
	public void setid(int id) {
		ID = id;
	}

	/**
	 * Is called when the thread is started This function attempts to send the
	 * message 30 times, then cancelles the action
	 */
	public void run() {
		int i;
		while (!quit) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			if (newmsg) {
				try {
					out = new PrintWriter(soc.getOutputStream(), true);
					out.println(type);
					out.println("\t");
					out.println(ID);
					out.println("\t");
					out.println(message);
					out.println("-1-1-1End-1-1-1"); // Standard footer of a
													// message
				} catch (IOException ioe) {
					System.out.println(ID + " Send failed: " + ioe);
				}
				newmsg = false; // Message has been send or cancelled, ready for
								// new message
			}
		}

		try {
			out.flush();
			out.close();
			System.out.println(ID + " Outputstream closed");
		} catch (Exception e) {
			System.out.println(ID + " Closing outputstream failed");
		}
	}

	// ----------------------------------------------
	// ----- For each message-type a function -------
	/**
	 * Sends acknowledgement with ID to the client
	 */
	public void acknowledge() {
		send(ACKCONN, "You're in game!");
	}

	/**
	 * Disconnects client from server
	 */
	public void disconnect() {
		if (client)
			send(DISCONN, "Client disconnects");
		else
			send(DISCONN, "Disconnected by server");
	}

	/**
	 * Sends playerlist to each client
	 */
	public void players(String[] s) {
		String list = "";
		list += s[0];
		for (int i = 1; i < s.length; i++)
			if (s[i] != null)
				list += "\t" + s[i];
		send(PLAYERS, list);
	}

	/**
	 * Sends the clients name to the server
	 */
	public void sendname(String s) {
		send(SETNAME, s);
	}

	/**
	 * Sends a message to the server or each player
	 */
	public void message(String s) {
		send(MESSAGE, s);
	}

	/**
	 * The server starts a new game
	 */
	public void newgame() {
		send(NEWGAME, "Game started by server");
	}

	/**
	 * Sends a move to the server or players
	 */
	public void carmove(String s) {
		send(CARMOVE, s);
	}

	/**
	 * The server ends the current game
	 */
	public void endgame(String s) {
		send(ENDGAME, s);
	}

	/**
	 * The client sends it's name to the server
	 */
	public void setname(String s) {
		send(SETNAME, s);
	}

	/**
	 * The server sends the circuit to the client
	 */
	public void circuit(String s) {
		send(CIRCUIT, s);
	}
}

/**
 * This class implements the procedures for receiving data from the server or a
 * client
 */
class NetReceive implements Runnable {
	Socket soc; // The socket-implementation of the connection
	NetClient nc; // The client using this NetReceive
	NetSClient nsc; // The server using this NetReceive
	BufferedReader in; // Inputstream

	boolean quit = false, // True if the thread should be stopped
			client = true; // True if the owner is a client
	int ID = -1; // ID of the client, the socket is connected to

	final static String ACKCONN = "!!!ackconn!!!", // Server sends
													// connect-acknowledgement
			DISCONN = "!!!disconn!!!", // Client or server sends disconnect
			PLAYERS = "!!!players!!!", // Server sends playerlist
			MESSAGE = "!!!message!!!", // Server or client sends a message
			NEWGAME = "!!!newgame!!!", // Server starts the newgame
			CARMOVE = "!!!carmove!!!", // Client or server sends carmove
			ENDGAME = "!!!endgame!!!", // Server sends endgame
			CIRCUIT = "!!!circuit!!!", // Server sends the circuit to play
			SETNAME = "!!!setname!!!"; // Client sets its name

	/**
	 * Creates a new platform for the client to receive messages from the
	 * specified server
	 */
	public NetReceive(Socket s, int i, NetClient n) {
		soc = s;
		nc = n;
		ID = i;
		client = true;
	}

	/**
	 * Creates a new platform for the server to receive messages from the
	 * specified client
	 */
	public NetReceive(Socket s, int i, NetSClient n) {
		soc = s;
		nsc = n;
		ID = i;
		client = false;
	}

	/**
	 * Sets the ID of the client, only needed for feedback when debugging
	 */
	public void setid(int id) {
		if (client)
			ID = id;
	}

	/**
	 * Starts this class in a new thread
	 */
	public void start() {
		quit = false;
		new Thread(this).start();
	}

	/**
	 * Stops this Thread
	 */
	public void stop() {
		quit = true;
	}

	/**
	 * Is called when the thread is started This function keeps checking for new
	 * messages, until stopped
	 */
	public void run() {
		String st = "", buf = "";
		System.out.println(ID + " Connecting to socket: " + soc);

		try {
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			while (!quit) {
				try {
					st = "";
					buf = "";
					while (!buf.equals("-1-1-1End-1-1-1")) {
						st += buf;
						buf = in.readLine();
					}
					if (!st.equals(""))
						decode(st);
				} catch (Exception e) {
					System.out.println(ID + " Incoming connection broken! ==> "
							+ soc);
					// e.printStackTrace();
					disconnect(ID);
					stop();
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}

			try {
				in.close();
				System.out.println(ID + " Input stream closed");
			} catch (Exception e) {
				System.out.println(ID + " Could not close inputstream: " + e);
			}
		} catch (Exception e) {
			System.out.println(ID + " Failed connecting to inputstream:" + e);
		}

	}

	/**
	 * This functions decodes the specified message string, and calls the
	 * appropriate function
	 */
	public void decode(String s) {
		StringTokenizer st = new StringTokenizer(s, "\t");
		String header, data;
		int id;
		header = st.nextToken().trim();
		id = Integer.parseInt(st.nextToken().trim());
		data = st.nextToken("").trim();

		if (header.equals(ACKCONN))
			acknowledge(id);
		else if (header.equals(DISCONN))
			disconnect(id);
		else if (header.equals(PLAYERS))
			players(data);
		else if (header.equals(MESSAGE))
			message(id, data);
		else if (header.equals(NEWGAME))
			newgame();
		else if (header.equals(CARMOVE))
			carmove(id, data);
		else if (header.equals(ENDGAME))
			endgame();
		else if (header.equals(CIRCUIT))
			circuit(data);
		else if (header.equals(SETNAME))
			setname(id, data);
		else
			System.out.println(ID + " No valid network-message:" + s);
	}

	// ----------------------------------------------
	// ----- For each message-type a function -------

	/**
	 * The client receives it's ID from the server
	 */
	public void acknowledge(int id) {
		if (client)
			nc.inid(id);
	}

	/**
	 * Either a client disconnects from the server or the server disconnects
	 * this client
	 */
	public void disconnect(int id) {
		if (client)
			nc.indisconnect();
		else
			nsc.indisconnect(id);
	}

	/**
	 * Is called when a list of players is received, the list needs decoding.
	 */
	public void players(String s) {
		if (!client)
			return;
		String[] list = new String[50];
		StringTokenizer st = new StringTokenizer(s, "\t");
		boolean empty = false;
		int c = 0;
		while (!empty) {
			try {
				list[c++] = st.nextToken();
			} catch (Exception e) {
				empty = true;
			}
		}
		String[] player = new String[c - 1];
		for (int i = 0; i < c - 1; i++)
			player[i] = list[i];
		nc.inname(player);
	}

	/**
	 * Is called when a message from the server or a client is received
	 */
	public void message(int id, String s) {
		if (!client) {
			nsc.inmessage(id, s);
		} else {
			nc.inmessage(s);
		}
	}

	/**
	 * Is called when the server sends a newgame-request
	 */
	public void newgame() {
		nc.startgame();
	}

	/**
	 * Is called when a player has moved it's car
	 */
	public void carmove(int id, String s) {
		if (client)
			nc.inmove(s);
		else
			nsc.inmove(id, s);
	}

	/**
	 * Is called when the server ends the game
	 */
	public void endgame() {
		nc.endgame();
	}

	/**
	 * Is called when a client sends it's name to the server
	 */
	public void setname(int id, String s) {
		if (!client)
			nsc.inname(id, s);
	}

	/**
	 * Is called when a client receives the circuit from the server
	 */
	public void circuit(String s) {
		nc.incircuit(s);
	}
}

/**
 * This class runs as a seperate thread, and deals with incoming new clients
 */
class NetServer extends ServerSocket implements Runnable {
	Socket client; // Socket to the incoming client
	NetSClient nsc; // The owner of this server
	public final static int PORT = 8190; // Port on which this server runs

	boolean quit = false; // True if this thread has to stop.

	/**
	 * Constructs a new serversocket for communication with clientsockets
	 */
	public NetServer(NetSClient n) throws IOException {
		super(PORT);
		nsc = n;
		System.out.println("New server created at port:" + PORT);
	}

	/**
	 * Starts the server in a new thread
	 */
	public void start() {
		quit = false;
		new Thread(this).start();
	}

	/**
	 * Is called when the thread is started This function waits for clients to
	 * connect, and then reports them to the ServerClient
	 */
	public void run() {
		System.out.println("Server running!");
		quit = false;
		while (!quit) {
			System.out.println("Waiting for new clients to connect...");
			try {
				client = accept();
				nsc.addplayer(client);
			} catch (IOException e) {
				if (quit)
					System.out.println("Server stopped");
				else
					System.out
							.println("Client failed to connect, waiting for client to retry...");
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

		}
	}

	/**
	 * Stops this the thread
	 */
	public void stop() {
		quit = true;
		try {
			close();
		} catch (Exception e) {
		}
	}
}
