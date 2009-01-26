package dolmisani.games.graphrace;


public class Vector {
	
	private int x; 
	private int y;

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double length() {
		
		return Math.sqrt(x*x + y*y);
	}

	public int getX() {
		
		return x;
	}

	public int getY() {
		return y;
	}
	public void setX(int x) {
		
		this.x = x;
	}

	public void setY(int y) {
		
		this.y = y;
	}

	public String toString() {
		
		return String.format("[%d, %d]", x, y);
	}
}
