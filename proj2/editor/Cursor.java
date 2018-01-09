package editor;

import javafx.scene.shape.Rectangle;

public class Cursor {
	public Rectangle object;
	public int size;
	public WordBank w;

	public Cursor() {
		object = new Rectangle (0, 0);
		object.setHeight(60);
		object.setWidth(1);
		object.setX(5);
		object.setY(0);
	}

	public void update() {
		object.setX(w.cursorPosition().x);
		object.setY(w.cursorPosition().y);
	}

	public void changeSize(int size) {
		this.size = size;
		object.setHeight(size);
	}
}
