package dolmisani.games.jrogue;

import java.awt.Color;

public class Tile {

	private char symbol;
	private Color color;

	public Tile(char symbol, Color color) {

		this.symbol = symbol;
		this.color = color;
	}

	public char getSymbol() {
		return symbol;
	}

	public Color getColor() {
		return color;
	}

}
