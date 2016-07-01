package imageProcessing;

import java.util.HashMap;

public class Circle {
	private HashMap<String, Position> pixels;
	private int id;
	private Position position;
	private Circle connectsTo;
	private int connectionDistance;
	private int[][] matrix;

	public Circle(HashMap<String, Position> pixels, int id, Position position) {
		super();
		this.pixels = pixels;
		this.id = id;
		this.position = position;
		this.connectsTo = null;
	}
	
	public Circle(Position p){
		this.position = p;
	}

	public HashMap<String, Position> getPixels() {
		return pixels;
	}

	public void setPixels(HashMap<String, Position> pixels) {
		this.pixels = pixels;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Circle getConnectsTo() {
		return connectsTo;
	}

	public void setConnectsTo(Circle connectsTo) {
		this.connectsTo = connectsTo;
	}

	public int getConnectionDistance() {
		return connectionDistance;
	}

	public void setConnectionDistance(int connectionDistance) {
		this.connectionDistance = connectionDistance;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}
}
