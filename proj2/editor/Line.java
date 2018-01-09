package editor;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.*;

public class Line {
	protected LinkedList<Text> preCursor;
	protected LinkedList<Text> postCursor;
	protected int width;
	protected int height;
	protected int lineNumber;
	protected String fontName;
	protected int fontSize;

	public Line() {
		preCursor = new LinkedList<Text>();
		postCursor = new LinkedList<Text>();
		this.width = 5;

	}

	public void setHeight(String fontName, int fontSize) {
		Text t = new Text(" ");
		t.setFont(Font.font(fontName, fontSize));

		this.fontName = fontName;
		this.fontSize = fontSize;
		this.height = (int) Math.round(t.getLayoutBounds().getHeight());
	}

	public void xAlign() {
		// Aligns all the text in this line
		if (preCursor.size() + postCursor.size() == 0) {
			return;
		}
		int position = 5;
		

		if (!preCursor.isEmpty()) {
			ListIterator<Text> preTravel = preCursor.listIterator(0);
			while (preTravel.hasNext()) {
				Text editing = preTravel.next();
				editing.setFont(Font.font(fontName, fontSize));
				editing.setX(position);
				position += (int) Math.round(editing.getLayoutBounds().getWidth());
			}
			ListIterator<Text> postTravel = postCursor.listIterator(0);
			while (postTravel.hasNext()) {
				Text editing = postTravel.next();
				editing.setFont(Font.font(fontName, fontSize));
				editing.setX(position);
				position += (int) Math.round(editing.getLayoutBounds().getWidth());
			}
		} else {
			position = 5;
			ListIterator<Text> postTravel = postCursor.listIterator(0);
			while (postTravel.hasNext()) {
				Text editing = postTravel.next();
				editing.setFont(Font.font(fontName, fontSize));
				editing.setX(position);
				position += (int) Math.round(editing.getLayoutBounds().getWidth());
			}
		}
		width = position;
	}

	public void yAlign() {
		// Sets all the y-values of every item in the line.
		// Should probably do this after wrapping text to the next line
		if (preCursor.size() + postCursor.size() == 0) {
			return;
		}
		int y = this.height * this.lineNumber;
		ListIterator<Text> preTravel = preCursor.listIterator(0);
		while (preTravel.hasNext()) {
			Text editing = preTravel.next();
			editing.setY(y);
		}
		ListIterator<Text> postTravel = postCursor.listIterator(0);
		while (postTravel.hasNext()) {
			Text editing = postTravel.next();
			editing.setY(y);
		}
	}

	public void insert(Text t) {
		// Inserted text already has the correct font and size
		int y = this.height * this.lineNumber;
		int x = 5;
		
		if (!preCursor.isEmpty()) {
			x = (int) preCursor.peekLast().getX() + (int) Math.round(preCursor.peekLast().getLayoutBounds().getWidth());
		}
		t.setX(x);
		t.setY(y);
		preCursor.addLast(t);
		width += (int) Math.round(t.getLayoutBounds().getWidth());
	}

	public Text backspace() {
		if (preCursor.size() == 0) {
			return null;
		}
		Text removed = preCursor.removeLast();
		width -= (int) Math.round(removed.getLayoutBounds().getWidth());
		return removed;
	}

	public void left() {
		if (preCursor.isEmpty()) {
			return;
		}
		postCursor.addFirst(preCursor.removeLast());
	}

	public void right() {
		if (postCursor.isEmpty()) {
			return;
		}
		preCursor.addLast(postCursor.removeFirst());
	}

	public boolean isEmpty() {
		if (preCursor.isEmpty() && postCursor.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}