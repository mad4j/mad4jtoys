package dolmisani.games.graphrace;

public class Position {
	
	private int x;
	private int y;
	
	public Position(int x, int y) {
		
		this.x = x;
		this.y = y;
	}

	public Position moveBy(Vector v) {
		
		return new Position(x+v.getX(), y+v.getY());		
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	
	public boolean isNear(Position p) {
		
		int dx = x - p.getX();
		int dy = y - p.getY();
		
		return ((dx <=1) && (dx >= -1)) && ((dy <=1) && (dy >= -1)); 		
	}
	
	@Override
	public String toString() {

		return String.format("[%d, %d]", x, y);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Position) {
			
			Position p = (Position)o;			
			return (x == p.getX()) && (y == p.getY());
		}
		
		System.out.println("FALSE");
		
		return false;
	}
	

}
