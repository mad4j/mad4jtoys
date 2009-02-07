package dolmisani.games.graphrace;

public class Position {
	
	private final int x;
	private final int y;
	
	public Position(int x, int y) {
		
		this.x = x;
		this.y = y;
	}

	public final Position moveBy(Vector v) {
		
		return new Position(x+v.getX(), y+v.getY());		
	}
	
	public final Position moveBackBy(Vector v) {
		
		return new Position(x-v.getX(), y-v.getY());
	}
	
	public final int getX() {
		
		return x;
	}

	public final int getY() {
		return y;
	}
	
	public final boolean isNear(Position p) {
		
		int dx = x - p.getX();
		int dy = y - p.getY();
		
		return ((dx <=1) && (dx >= -1)) && ((dy <=1) && (dy >= -1)); 		
	}
	
	@Override
	public final String toString() {

		return String.format("[%d, %d]", x, y);
	}
	
	@Override
	public final boolean equals(Object o) {
		
		if(o instanceof Position) {
			
			Position p = (Position)o;			
			return (x == p.getX()) && (y == p.getY());
		}
		
		return false;
	}
	

}
