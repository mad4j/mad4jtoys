package dolmisani.games.graphrace;


public class Vector {

	public static final Vector MOVE_NONE = new Vector(0, 0);
	public static final Vector MOVE_UP = new Vector(0, -1);
	public static final Vector MOVE_DOWN = new Vector(0, 1);
	public static final Vector MOVE_LEFT = new Vector(-1, 0);
	public static final Vector MOVE_RIGHT = new Vector(1, 0);
	
	
	public static final Vector[] DISPLACEMENTS = {
		
		MOVE_NONE,
		MOVE_UP,
		MOVE_RIGHT,
		MOVE_DOWN,
		MOVE_LEFT
	};
	
	private final int x; 
	private final int y;

	public Vector(int x, int y) {
		
		this.x = x;
		this.y = y;
	}

	public double getLength() {
		
		return Math.sqrt(x*x + y*y);
	}

	public final Vector add(Vector v) {
		
		return new Vector(x+v.x, y+v.y);
	}
	
	public final int getX() {
		
		return x;
	}

	public final int getY() {
		
		return y;
	}
	
	@Override
	public final String toString() {
		
		return String.format("[%d, %d]", x, y);
	}
}
