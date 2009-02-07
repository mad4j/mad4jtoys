package dolmisani.games.graphrace;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class Car {

	
	private Position currentPosition;
	
	private Position startPosition;
	
	private Color color;
	
	private int faultcount = 0;
	
	List<Vector> history = new ArrayList<Vector>();


	public Car(Position startPosition, Color color) {
		
		this.startPosition = startPosition;
		this.currentPosition = startPosition;
		
		history.add(new Vector(0, 0));

		this.color = color;
	}


	public void move(Vector v) {
		
		currentPosition = currentPosition.moveBy(v);
		history.add(v);
	}

	
	public Position getPosition() {
		
		return currentPosition;
	}
	
	public Position getStartPosition() {
		
		return startPosition;
	}
	
	public int getturns() {
		
		return history.size();
	}

	public int getplayerturns() {
		
		return getturns() - faultcount;
	}

	public Vector getHistory(int i) {

		return history.get(i);
	}

	public Vector getVelocity() {

		return history.get(history.size()-1);
	}

	public double getSpeed() {

		return getVelocity().getLength();
	}

	public Color getColor() {
		
		return color;
	}

	public void crash() {
		
		faultcount++;
		history.add(new Vector(0, 0));	
	}
	
	@Override
	public String toString() {
		
		return ("Car(" + currentPosition + "," + getVelocity() + ")");
	}

	
}
