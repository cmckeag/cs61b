package editor;

public class Coordinates {
	protected int x;
	protected int y;

	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinates clone() {
		Coordinates output = new Coordinates(this.x, this.y);
		return output;
	}
}