package dolmisani.games.jpong;

import java.awt.Color;

public class ColorTheme {

	private Color background;
	private Color foreground;
	
	public ColorTheme(Color background, Color foreground) {
		
		this.background = background;
		this.foreground = foreground;
	}
	
	
	public Color getBackgroundColor() {
		
		return background;
	}
	public Color getForegroundColor() {
		
		return foreground;
	}
	
	
}
