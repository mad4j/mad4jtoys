package dolmisani.games.graphrace;

import java.awt.Color;

public class Game {

	GraphRace ppr;
	Circuit circ;
	PaperView paper;
	Player[] player; // Array with all participating players
	int playercount; // Number of players
	int curplayer = 0; // The current player
	private Player winnerPlayer = null; // The player who has won
	boolean finish = false, // A player has won
			wait = true, // Waiting for a routine to finish
			gamestarted = false; // True if the game is running
	final static int gridsize = 20; // The size of the grid

	
	private int turnCount;
	
	
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

		turnCount = 0;
		
		System.out.println("\n---NEW GAME---");
		gamestarted = true;
		paper = ppr.getpaper();

		circ.init(); // Initialize the circuit, in network-mode this is done
		// before

		playercount = type.length; // Get the number of players
		player = new Player[playercount];

		for (int i = 0; i < playercount; i++) {
			Color c = new Color(75 + (int) (Math.random() * 180),
					75 + (int) (Math.random() * 180),
					75 + (int) (Math.random() * 180));

			Position startPosition = circ.getFinishLine().getStartPosition(i);
			player[i] = new Player(name[i], type[i], ppr, startPosition, c);
		}

		paper.repaint();
		ppr.showcurrentplayer();
		ppr.turnlabel.setText("Turn: 0");
		finish = false;
		winnerPlayer = null;

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
		// ppr.main.start();
		if (!finished()) {
			ppr.setmessages();
			player[curplayer].ask();
		}
	}

	// ---------------------------------------------------------------------
	// ------ Functions for checking if a player has finshed already -------
	/**
	 * This checks if there is a winner. If so, it stops the game and makes an
	 * anouncement
	 */
	public void win() {

		winnerPlayer = checkfinished();

		if (winnerPlayer != null) {

			gamestarted = false;
			finish = true;
			// TODO: fix this
			// curplayer = winnerPlayer;
			ppr.message(winnerPlayer.getName() + "WINS!!!");
		}

	}

	/**
	 * Returns the player who has won or -1 The player who started last is
	 * always in advantage, for starting in outer row. If more players finish in
	 * the same turn, the last of them wins the game.
	 */
	Player checkfinished() {

		// TODO: turns shall be a Game field
		if (player[0].getcar().getturns() < 10) {
			return null; // Don't do nothing during the first turns
		}

		Player winnerPlayer = null;
		FinishLine finishLine = circ.getFinishLine();

		for (int i = 0; i < playercount; i++) {
			Car c = player[i].getcar();

			if (finishLine.hasFinished(c)) {

				if ((winnerPlayer == null)
						|| (c.getSpeed()) > winnerPlayer.getcar().getSpeed()) {
					winnerPlayer = player[i];
				}
			}

		}
		return winnerPlayer;
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

	public Player getWinnerPlayer() {

		return winnerPlayer;
	}

	/**
	 * @return The current player
	 */
	public Player currentplayer() {// System.out.println("Current player requested:"+curplayer+", "+player[curplayer]);
		return player[curplayer];
	}

}
