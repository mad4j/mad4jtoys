package dolmisani.games.graphrace;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

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
public class GraphRace extends JFrame implements MouseListener, KeyListener,
		ActionListener {

	private static final long serialVersionUID = 8557283415547050607L;
	
	PaperView paper; // The viewport for the circuit
	//MainView main; // Scrollbar-window for the paperview
	Game game;

	/**
	 * Graphical items for displaying status-information and buttons
	 */
	Label playerlabel = new Label(), turnlabel = new Label("Turn: 0"),
			messagelabel = new Label("Do a move");

	Button newgame = new Button("new game");
	Panel bottompanel = new Panel();

	/**
	 * The init method is called when the applet is loaded by the browser. It
	 * contains mainly layout-concerning items
	 */
	public void init() {
		System.out.println("Initialising..");
		game = new Game(this);
		paper = new PaperView(game.getcirc(), this);

		//main = new MainView(this);

		setBackground(Color.black); // you can set the background color of the
		// applet in this line
		messagelabel.setForeground(Color.black);
		messagelabel.setAlignment(Label.CENTER);
		turnlabel.setForeground(Color.black);
		turnlabel.setAlignment(Label.CENTER);

		Panel buttonpanel = new Panel();// panel to keep buttons together
		buttonpanel.setLayout(new GridLayout(1, 2)); // add three buttons beside
		// each other
		buttonpanel.add(newgame);

		Panel superpanel = new Panel();
		superpanel.setLayout(new GridLayout(2, 1));
		setLayout(new BorderLayout());

		bottompanel.setLayout(new GridLayout(1, 1));
		bottompanel.add(playerlabel);
		bottompanel.add(messagelabel);
		bottompanel.add(turnlabel);

		superpanel.add(buttonpanel);
		superpanel.add(bottompanel);

		//add("Center", main);
		add(new JScrollPane(paper), BorderLayout.CENTER);

		add("South", superpanel);

		paper.addMouseListener(this);
		paper.addKeyListener(this);
		addKeyListener(this);
		newgame.addActionListener(this);

		int[] startplayers = { Player.COM, Player.HUM };
		String[] names = { "Computer", "Human" };
		game.newgame(startplayers, names);
	}

	public GraphRace() {

		super("Paper & Pencil racing");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(720, 640);
		init();
	}

	/**
	 * this method is quick & dirty trick for running this applet as an
	 * application. It is not used when a browser runs this class as an applet.
	 */
	public static void main(String[] ps) {

		JFrame f = new GraphRace();
		f.setVisible(true);
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
		playerlabel.setText("Player: " + game.currentplayer().getName()
				+ ", Moving: " + game.currentplayer().getcar().getVector());
	}

	public void setmessages() {
		showcurrentplayer();
		turnlabel.setText("Turn: "
				+ game.currentplayer().getcar().getplayerturns());
		if (game.finished())
			message(game.getplayer(game.winner).getName() + " WINS!!!");
		else if (game.currentplayer().isai())
			message("Click or press a key");
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
	public PaperView getpaper() {
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

		game.currentplayer().clicked((int) Math.round(x / Game.gridsize),
				(int) Math.round(y / Game.gridsize));
		repaint();
		game.wait = false;
	}

	/**
	 * Detects when a key is pressed
	 */
	public void keyPressed(KeyEvent kvt) {
		int k = kvt.getKeyCode();
		boolean move = false;
		/*
		 * //main.scrollRectToVisible(new Rectangle(point.x, point.y,
		 * getWidth(), getHeight())); //Point p = main.getScrollPosition();
		 * //Point p = main.getViewport().getLocation();
		 * 
		 * switch (k) // Next four items are for scrolling { case
		 * KeyEvent.VK_LEFT: p.translate(-11, 0); move = true; break; case
		 * KeyEvent.VK_RIGHT: p.translate(11, 0); move = true; break; case
		 * KeyEvent.VK_UP: p.translate(0, -11); move = true; break; case
		 * KeyEvent.VK_DOWN: p.translate(0, 11); move = true; break; }
		 * 
		 * //main.scrollRectToVisible(new Rectangle(point.x, point.y,
		 * getWidth(), getHeight())); //main.setScrollPosition(p);
		 * //main.getViewport().setLocation(p);
		 */
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
			new NewGameWindow(this);

		repaint();
	}
}
