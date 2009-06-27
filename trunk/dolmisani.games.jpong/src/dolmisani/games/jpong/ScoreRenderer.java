package dolmisani.games.jpong;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ScoreRenderer {

	private Integer value;
	
	private Font scoreFont;

	private float centerX;
	private float centerY;
	
	public ScoreRenderer(Integer value, float centerX, float centerY, Font scoreFont) {
		
		this.centerX = centerX;
		this.centerY = centerY;
		
		this.scoreFont = scoreFont;
		this.value = value;

	}
	

	public void draw(Graphics2D g) {
	
		String stringValue = Integer.toString(value);
		
		g.setFont(scoreFont);
		FontMetrics fm = g.getFontMetrics();
	
		
		int scoreWidth = fm.stringWidth(stringValue);
		int fontHeight = fm.getHeight();
		
		//g.drawString(stringValue, x-(scoreWidth/2), y+(fontHeight/2));
		
		
		
	}
	
	
}
