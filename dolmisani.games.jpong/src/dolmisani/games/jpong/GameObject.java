package dolmisani.games.jpong;

import java.awt.Graphics2D;



public class GameObject {

	protected int x;
	protected int y;

	protected int deltaX;
	protected int deltaY;

	protected int width; // The width of the shape.
	protected int height; // The height of the shape.

	/*
	 * Create the shape with supplied parameters.
	 */
	public GameObject(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;

		this.deltaX = 0;
		this.deltaY = 0;

		this.width = width;
		this.height = height;
	}

	/*
	 * Returns the current x position.
	 */
	public int getX() {
		return x;
	}

	/*
	 * Returns the current y position.
	 */
	public int getY() {
		return y;
	}

	public void setCenterX(int x) {
		
		this.x = x - width/2;
	}
	
	public int getCenterX() {
		
		return x + width/2;
	}
	
	public void setCenterY(int y) {
		
		this.y = y - height/2;
	}
	
	public int getCenterY() {
		
		return y + height/2;
	}
	
	public void setWidth(int width) {
		
		this.width = width;
	}
	
	/*
	 * Returns the current width.
	 */
	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		
		this.height = height;
	}
	
	/*
	 * Returns the current height.
	 */
	public int getHeight() {
		return height;
	}

	/*
	 * Sets the current x positions.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/*
	 * Sets the current y position
	 */
	public void setY(int y) {
		this.y = y;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

		
	public void draw(Graphics2D g) {

		g.fillRect(x, y, width, height);
	}

	public void move() {

		this.x += this.deltaX;
		this.y += this.deltaY;
	}
	
	public boolean detectCollision(GameObject gameObject) {
		
		return contains(gameObject.getCenterX(), gameObject.getCenterY());
		
	}
	
	public boolean contains(int pX, int pY) {
		
		return (x < pX) && (pX < x+width ) && (y < pY) && (pY < y+height);
		
	}
	
}
