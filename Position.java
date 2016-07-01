package imageProcessing;

public class Position {
	int y, x;

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public String getCode() {
		return ""+y+""+x;
	}

	public boolean equals(Position obj) {
		return obj.x == this.x && obj.y == this.y;
	}

	@Override
	public String toString() {
		return "Position [y=" + y + ", x=" + x + "]";
	}
}