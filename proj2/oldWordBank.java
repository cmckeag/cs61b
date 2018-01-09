import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;

public class WordBank {
	protected ArrayList<Text> wordBank;
	protected int cursorLoc;

	public WordBank() {
		wordBank = new ArrayList<Text>(64);
		cursorLoc = 0;
	}

	public void insert(Text t) {
		wordBank.add(cursorLoc, t);
		cursorLoc += 1;
	}

	public void backspace() {
		if (cursorLoc == 0) {
			return;
		}
		wordBank.remove(cursorLoc - 1);
		cursorLoc -= 1;
	}

	public void delete() {
		if (cursorLoc >= wordBank.size()) {
			return;
		}
		wordBank.remove(cursorLoc);
	}

	public void left() {
		if (cursorLoc == 0) {
			return;
		}
		cursorLoc -= 1;
	}

	public void right() {
		if (cursorLoc >= wordBank.size()) {
			return;
		}
		cursorLoc += 1;
	}

	public void down() {

	}

	public void up() {

	}

	public int countLines(int windowWidth) {
		int index = 0;
		int line = 0;
		int lineWidth = 0;
		while (index < wordBank.size()) {
			if (wordBank.get(index).getText().charAt(0) == 13) {
				line += 1;
				index += 1;
				lineWidth = 0;
			} else if ((lineWidth + (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth())) <= windowWidth) {
				lineWidth += (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth());
				index += 1;
			} else if ((lineWidth + (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth())) > windowWidth) {
				line += 1;
				lineWidth = 0;
			}
		}
		return line;
	}

	public void coordinate(int windowWidth, String fontName, int fontSize) {
		index = 0;
		int lineWidth = 0;
		Text sample = new Text(0, 0, " ");
		int height = (int) Math.ceil(sample.getLayoutBounds.getHeight());
		int line = 0;
		while (index < wordBank.size()) {
			if (wordBank.get(index).getText().charAt(0) == 13) {
				wordBank.get(index).setX((double) lineWidth);
				wordBank.get(index).setY((double) height * line);
				lineWidth = 0;
				line += 1;
				index += 1;
			} else if ((lineWidth + (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth())) <= windowWidth) {
				wordBank.get(index).setX((double) (lineWidth + (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth())));
				wordBank.get(index).setY((double) height * line);
				lineWidth += (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth());
				index += 1;
			} else if ((lineWidth + (int) Math.round(wordBank.get(index).getLayoutBounds().getWidth())) > windowWidth) {
				lineWidth = 0;
				line += 1;
				wordBank.get(index).setX(0);
				wordBank.get(index).setY((double) height * line);
			}
		}
	}


}