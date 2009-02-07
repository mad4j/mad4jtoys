package dolmisani.games.graphrace;

import java.awt.geom.Line2D;

public class FinishLine {

	private int x1;
	private int x2;
	private int y;

	public FinishLine(int x1, int x2, int y) {

		this.x1 = x1;
		this.x2 = x2;
		this.y = y;
	}

	public int getX1() {

		return x1;
	}

	public int getX2() {

		return x2;
	}

	public int getY() {

		return y;
	}

	public Position getStartPosition(int playerIndex) {
		
		return new Position((playerIndex % (x2-x1)) + x1, y + playerIndex / (x2-x1));
	}
	
	public boolean hasFinished(Car car) {
		
		Position p1 = car.getPosition();
		Position p2 = car.getPosition().moveBackBy(car.getVelocity());
		
		return Line2D.linesIntersect(x1, y, x2, y, p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	public static FinishLine create(Circuit c) {

		int y = c.getHeight() / 2;
		int x = c.getWidth() / 2;

		while (c.terrain(x++, y) == 0)
			;

		int x1 = x - 1;

		while (c.terrain(x++, y) != 0)
			;

		int x2 = x;
		
		return new FinishLine(x1, x2, y);
	}
}