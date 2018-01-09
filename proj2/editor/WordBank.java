package editor;

import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.*;

public class WordBank {
	protected HashMap<Integer, Line> wordBank;
	protected Coordinates cursorIndex;
	protected Coordinates cursorPos;
	protected String fontName;
	protected int fontSize;

	public WordBank(String fontName, int fontSize) {
		wordBank = new HashMap<Integer, Line>(64);
		wordBank.put(0, new Line());
		wordBank.get(0).lineNumber = 0;
		wordBank.get(0).setHeight(fontName, fontSize);
		cursorIndex = new Coordinates(0, 0);
		cursorPos = new Coordinates(0, 0);
		this.fontName = fontName;
		this.fontSize = fontSize;
	}

	public int height() {
		Text sample = new Text(" ");
		sample.setFont(Font.font(fontName, fontSize));
		int lineHeight = (int) Math.round(sample.getLayoutBounds().getHeight());
		return (lineHeight * wordBank.size());
	}

	public void moveCursor(Coordinates target) {
		int index = 0;
		if (target.y >= wordBank.size()) {
			target.y = wordBank.size() - 1;
		}
		// At the current line, move the cursor all the way to the left
		while (!wordBank.get(cursorIndex.y).preCursor.isEmpty()) {
			wordBank.get(cursorIndex.y).left();
		}
		
		// At the new line, first move the cursor all the way left
		while (index < wordBank.get(target.y).preCursor.size()) {
			wordBank.get(target.y).left();
			index += 1;
		}
		// If the target coordinates are too far right, move to the last
		// element in the line
		if (target.x > wordBank.get(target.y).postCursor.size()) {
			target.x = wordBank.get(target.y).postCursor.size();
		}
		index = 0;
		// At the new line, move the cursor right.
		while (index < target.x) {
			wordBank.get(target.y).right();
			index += 1;
		}
		// Change the cursor's index coordinates to the target coordinates
		cursorIndex = target;
	}

	public void moveCursorToCoordinates(Coordinates target) {
		// We are given position coordinates, and we want to move the cursor there
		Coordinates newPosition = new Coordinates(0, 0);
		Text sample = new Text(" ");
		sample.setFont(Font.font(fontName, fontSize));
		newPosition.y = (int) Math.floor(target.y / (int) Math.round(sample.getLayoutBounds().getHeight()));
		if (newPosition.y >= wordBank.size()) {
			newPosition.y = wordBank.size() - 1;
		}
		Line current = wordBank.get(newPosition.y);
		ListIterator<Text> preTravel = current.preCursor.listIterator(0);
		ListIterator<Text> postTravel = current.postCursor.listIterator(0);
		int currentWidth = 0;
		int indexClickedOn = -1;
		int preAfter = -1;
		int postAfter = -1;
		while (preTravel.hasNext()) {
			Text inspect = preTravel.next();
			currentWidth += (int) Math.round(inspect.getLayoutBounds().getWidth());
			if (currentWidth >= target.x) {
				preAfter = preTravel.previousIndex();
				break;
			}
		}
		if (preAfter < 0) {
			while (postTravel.hasNext()) {
				Text inspect = postTravel.next();
				currentWidth += (int) Math.round(inspect.getLayoutBounds().getWidth());
				if (currentWidth >= target.x) {
					postAfter = postTravel.previousIndex();
					break;
				}
			}
		} 
		if (preAfter < 0 && postAfter < 0) {
			newPosition.x = current.preCursor.size() + current.postCursor.size();
			moveCursor(newPosition);
		} else if (preAfter < 0 && postAfter > 0) {
			Text charClickedOn = current.postCursor.get(postAfter);
			int left = (int) charClickedOn.getX();
			int right = left + (int) Math.round(charClickedOn.getLayoutBounds().getWidth());
			if (Math.abs(target.x - left) > Math.abs(target.x - right)) {
				newPosition.x = current.preCursor.size() + postAfter + 1;
			} else {
				newPosition.x = current.preCursor.size() + postAfter;
			}
		} else if (preAfter > 0) {
			Text charClickedOn = current.preCursor.get(preAfter);
			int left = (int) charClickedOn.getX();
			int right = left + (int) Math.round(charClickedOn.getLayoutBounds().getWidth());
			if (Math.abs(target.x - left) >= Math.abs(target.x - right)) {
				newPosition.x = preAfter + 1;
			} else {
				newPosition.x = preAfter;
			}
		}
		moveCursor(newPosition);
		if (newPosition.x > 0 && wordBank.get(newPosition.y).preCursor.peekLast().getText().charAt(0) == 10) {
			left();
		}
	}

	public Coordinates cursorPosition() {
		// Updates the cursor's position based on the cursor's index
		Text t = new Text(" ");
		t.setFont(Font.font(fontName, fontSize));
		cursorPos.y = cursorIndex.y * (int) Math.round(t.getLayoutBounds().getHeight());
		if (wordBank.get(cursorIndex.y).preCursor.size() == 0) {
			cursorPos.x = 5;
		} else {
			cursorPos.x = (int) Math.round(wordBank.get(cursorIndex.y).preCursor.peekLast().getX() + wordBank.get(cursorIndex.y).preCursor.peekLast().getLayoutBounds().getWidth());
		}
		return cursorPos;
	}


	public void insert(Text t) {
		Line editing = wordBank.get(cursorIndex.y);
		t.setTextOrigin(VPos.TOP);
		t.setFont(Font.font(fontName, fontSize));
		editing.insert(t);
		cursorIndex.x += 1;
	}

	public Text backspace() {
		Line editing = wordBank.get(cursorIndex.y);
		if (editing.preCursor.size() == 0 && wordBank.containsKey(cursorIndex.y - 1)) {
			Line previousLine = wordBank.get(cursorIndex.y - 1);
			if (previousLine.postCursor.size() > 0) {
				moveCursor(new Coordinates((previousLine.preCursor.size() + previousLine.postCursor.size()), cursorIndex.y - 1));
				return previousLine.backspace();
			} else {
				moveCursor(new Coordinates(previousLine.preCursor.size(), cursorIndex.y - 1));
				return previousLine.backspace();
			}
		} else if (editing.preCursor.size() == 0 && !wordBank.containsKey(cursorIndex.y - 1)) {
			return null;
		} else {
			cursorIndex.x -= 1;
			return editing.backspace();
		}
	}

	public void left() {
		Line editing = wordBank.get(cursorIndex.y);
		if (editing.preCursor.isEmpty()) {
			// If we're at the far left, and there is a line before
			if (wordBank.containsKey(cursorIndex.y - 1)) {
				if (wordBank.get(cursorIndex.y - 1).postCursor.peekLast().getText().charAt(0) == 10 || wordBank.get(cursorIndex.y - 1).postCursor.peekLast().getText().charAt(0) == 32) {
					moveCursor(new Coordinates(wordBank.get(cursorIndex.y - 1).postCursor.size() - 1, cursorIndex.y - 1));
				} else {
					moveCursor(new Coordinates(wordBank.get(cursorIndex.y - 1).postCursor.size(), cursorIndex.y - 1));
				}
			}
			// If there isn't a previous line, do nothing
			return;
		}
		// If we're not at the far left, just do the normal thing
		editing.left();
		cursorIndex.x -= 1;
	}

	public void right() {
		Line editing = wordBank.get(cursorIndex.y);
		if (editing.postCursor.isEmpty()) {
			// If we're at the far right, and there is a next line
			if (wordBank.containsKey(cursorIndex.y + 1)) {
				moveCursor(new Coordinates(0, cursorIndex.y + 1));
			}
		} else if (editing.postCursor.size() == 1 && editing.postCursor.peekFirst().getText().charAt(0) == 10 && wordBank.containsKey(cursorIndex.y + 1)) {
			moveCursor(new Coordinates(0, cursorIndex.y + 1));
		} else if (editing.postCursor.size() == 1 && editing.postCursor.peekLast().getText().charAt(0) == 10 && wordBank.containsKey(cursorIndex.y + 1)) {
			moveCursor(new Coordinates(0, cursorIndex.y + 1));
		} else {
			editing.right();
			cursorIndex.x += 1;
		}
	}

	public void up() {
		if (cursorIndex.y == 0) {
			return;
		}
		if (cursorIndex.x == 0) {
			moveCursor(new Coordinates(0, cursorIndex.y - 1));
			return;
		}
		Coordinates currentLocation = cursorPosition();
		Text sample = new Text(" ");
		sample.setFont(Font.font(fontName, fontSize));
		Coordinates targetLocation = new Coordinates(currentLocation.x, currentLocation.y - (int) Math.round(sample.getLayoutBounds().getHeight()));
		moveCursorToCoordinates(targetLocation);
	}

	public void down() {
		if (!wordBank.containsKey(cursorIndex.y + 1)) {
			return;
		}
		Coordinates currentLocation = cursorPosition();
		Text sample = new Text(" ");
		sample.setFont(Font.font(fontName, fontSize));
		Coordinates targetLocation = new Coordinates(currentLocation.x, currentLocation.y + (int) Math.round(sample.getLayoutBounds().getHeight()));
		moveCursorToCoordinates(targetLocation);
	}

	public void wrap(int windowWidth) {
		int index = 0;
		while (index < wordBank.size()) {
			Line currentLine = wordBank.get(index);

			// Enter wrapping starts here!
			if (index != cursorIndex.y) {
				while (!currentLine.preCursor.isEmpty()){
					currentLine.left();
				}
			}
			ListIterator<Text> preTravel2 = currentLine.preCursor.listIterator(0);
			ListIterator<Text> postTravel2 = currentLine.postCursor.listIterator(0);
			int preEnterIndex = -1;
			int postEnterIndex = -1;
			// Iterate through preCursor, searching for the first Enter.
			while (preEnterIndex < 0 && preTravel2.hasNext()) {
				Text inspect = preTravel2.next();
				if (inspect.getText().charAt(0) == 10) {
					preEnterIndex = preTravel2.previousIndex();
				}
			}
			while (postEnterIndex < 0 && postTravel2.hasNext()) {
				Text inspect = postTravel2.next();
				if (inspect.getText().charAt(0) == 10) {
					postEnterIndex = postTravel2.previousIndex();
				}
			}
			// If there's an Enter in the preCursor, everything goes down
			if (preEnterIndex >= 0) {
				if (!wordBank.containsKey(index + 1)) {
					wordBank.put(index + 1, new Line());
					wordBank.get(index + 1).lineNumber = index + 1;
					wordBank.get(index + 1).setHeight(fontName, fontSize);
				}
				int preEIndex = preEnterIndex + 1;
				while (!currentLine.postCursor.isEmpty()) {
					Text recent = currentLine.postCursor.removeLast();
					currentLine.width -= (int) Math.round(recent.getLayoutBounds().getWidth());
					wordBank.get(index + 1).width += (int) Math.round(recent.getLayoutBounds().getWidth());
					if (cursorIndex.y == index + 1) {
						wordBank.get(index + 1).preCursor.addFirst(recent);
					} else {
						wordBank.get(index + 1).postCursor.addFirst(recent);
					}
				}
				int preTarget = currentLine.preCursor.size();
				while (preEIndex < preTarget) {
					Text recent = currentLine.preCursor.removeLast();
					currentLine.width -= (int) Math.round(recent.getLayoutBounds().getWidth());
					wordBank.get(index + 1).width += (int) Math.round(recent.getLayoutBounds().getWidth());
					if (cursorIndex.y == index + 1) {
						wordBank.get(index + 1).preCursor.addFirst(recent);
					} else {
						wordBank.get(index + 1).postCursor.addFirst(recent);
					}
					preEIndex += 1;
				}
				// Set the cursor to the next line
				if (index == cursorIndex.y) {
					moveCursor(new Coordinates(0, cursorIndex.y + 1));
				}
			} else if (postEnterIndex >= 0) {
				if (!wordBank.containsKey(index + 1)) {
					wordBank.put(index + 1, new Line());
					wordBank.get(index + 1).lineNumber = index + 1;
					wordBank.get(index + 1).setHeight(fontName, fontSize);
				}
				int postEIndex = postEnterIndex + 1;
				int postTarget = currentLine.postCursor.size();
				while (postEIndex < postTarget) {
					Text recent = currentLine.postCursor.removeLast();
					currentLine.width -= (int) Math.round(recent.getLayoutBounds().getWidth());
					wordBank.get(index + 1).width += (int) Math.round(recent.getLayoutBounds().getWidth());
					wordBank.get(index + 1).postCursor.addFirst(recent);
					postEIndex += 1;
				}
			}
			// Enter wrapping ends here!

			// Word wrap starts here!
			if (currentLine.width > windowWidth) {
				// Then wrap things to the next line
				// Find the index of the first character after the window.
				// Wrap that and everything after it.
				ListIterator<Text> preFindText = currentLine.preCursor.listIterator(0);
				ListIterator<Text> postFindText = currentLine.postCursor.listIterator(0);
				int firstTextIndex = -1;
				int currentWidth = 5;
				boolean preCursorContainsText = false;
				while (preFindText.hasNext()) {
					Text inspect = preFindText.next();
					currentWidth += (int) Math.round(inspect.getLayoutBounds().getWidth());
					if (currentWidth > windowWidth && inspect.getText().charAt(0) > 32) {
						firstTextIndex = preFindText.previousIndex();
						preCursorContainsText = true;
						break;
					}
				}
				if (!preCursorContainsText) {
					while (postFindText.hasNext()) {
						Text inspect = postFindText.next();
						currentWidth += (int) Math.round(inspect.getLayoutBounds().getWidth());
						if (currentWidth > windowWidth && inspect.getText().charAt(0) > 32) {
							firstTextIndex = postFindText.previousIndex();
							break;
						}
					}
				}
				// We now know the index of the first text after the window
				// Wrap it and everything after it
				if (firstTextIndex != -1) {
					// Generate new line if it doesn't already exist
					if (!wordBank.containsKey(index + 1)) {
						wordBank.put(index + 1, new Line());
						wordBank.get(index + 1).lineNumber = index + 1;
						wordBank.get(index + 1).setHeight(fontName, fontSize);
					}
					Line nextLine = wordBank.get(index + 1);
					if (preCursorContainsText) {
						int copied = 0;
						while (!currentLine.postCursor.isEmpty()) {
							Text moving = currentLine.postCursor.removeLast();
							currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
							nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
							if (cursorIndex.y == nextLine.lineNumber) {
								nextLine.preCursor.addFirst(moving);
							} else {
								nextLine.postCursor.addFirst(moving);
							}
						}
						int tempIndex = firstTextIndex;
						int target = currentLine.preCursor.size();
						while (tempIndex < target) {
							Text moving = currentLine.preCursor.removeLast();
							currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
							nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
							if (cursorIndex.y == nextLine.lineNumber) {
								nextLine.preCursor.addFirst(moving);
							} else {
								nextLine.postCursor.addFirst(moving);
							}
							tempIndex += 1;
							copied += 1;
						}
						if (currentLine.lineNumber == cursorIndex.y) {
							moveCursor(new Coordinates(copied, index + 1));
						}
					} else {
						int tempIndex = firstTextIndex;
						int target = currentLine.postCursor.size();
						while (tempIndex < target) {
							Text moving = currentLine.postCursor.removeLast();
							currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
							nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
							if (cursorIndex.y == nextLine.lineNumber) {
								nextLine.preCursor.addFirst(moving);
							} else {
								nextLine.postCursor.addFirst(moving);
							}
							tempIndex += 1;
						}
					}
					// So everything after the window has been wrapped.
					// Now we need to see if we can find a space, and wrap everything after that space.
					ListIterator<Text> preFindLastSpace = currentLine.preCursor.listIterator(currentLine.preCursor.size());
					ListIterator<Text> postFindLastSpace = currentLine.postCursor.listIterator(currentLine.postCursor.size());
					int lastSpaceIndex = -1;
					boolean postCursorContainsLastSpace = false;
					while (postFindLastSpace.hasPrevious()) {
						Text inspect = postFindLastSpace.previous();
						if (inspect.getText().charAt(0) == 32) {
							lastSpaceIndex = postFindLastSpace.nextIndex();
							postCursorContainsLastSpace = true;
							break;
						}
					}
					if (!postCursorContainsLastSpace) {
						while (preFindLastSpace.hasPrevious()) {
							Text inspect = preFindLastSpace.previous();
							if (inspect.getText().charAt(0) == 32) {
								lastSpaceIndex = preFindLastSpace.nextIndex();
								break;
							}
						}
					}
					// Now wrap everything after that space.
					if (lastSpaceIndex != -1) {
						if (!postCursorContainsLastSpace) {
							int copied = 0;
							while (!currentLine.postCursor.isEmpty()) {
								Text moving = currentLine.postCursor.removeLast();
								currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
								nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
								if (cursorIndex.y == nextLine.lineNumber) {
									nextLine.preCursor.addFirst(moving);
								} else {
									nextLine.postCursor.addFirst(moving);
								}
							}
							int tempIndex = lastSpaceIndex + 1;
							int target = currentLine.preCursor.size();
							while (tempIndex < target) {
								Text moving = currentLine.preCursor.removeLast();
								currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
								nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
								if (cursorIndex.y == nextLine.lineNumber) {
									nextLine.preCursor.addFirst(moving);
								} else {
									nextLine.postCursor.addFirst(moving);
								}
								tempIndex += 1;
								copied += 1;
							}
							if (currentLine.lineNumber == cursorIndex.y) {
								moveCursor(new Coordinates(copied, index + 1));
							}
						} else {
							int tempIndex = lastSpaceIndex + 1;
							int target = currentLine.postCursor.size();
							while (tempIndex < target) {
								Text moving = currentLine.postCursor.removeLast();
								currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
								nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
								if (cursorIndex.y == nextLine.lineNumber) {
									nextLine.preCursor.addFirst(moving);
								} else {
									nextLine.postCursor.addFirst(moving);
								}
								tempIndex += 1;
							}
						}
					}
				}
			} else {
				// Then wrap things from the previous line
				// But, only if currentLine does not end in enter
				// Also the next line must exist
				boolean currentLineEndsInEnter = false;
				boolean currentLineEndsInSpace = false;
				if (currentLine.postCursor.size() > 0) {
					if (currentLine.postCursor.peekLast().getText().charAt(0) == 10) {
						currentLineEndsInEnter = true;
					}
				} else if (currentLine.preCursor.size() > 0) {
					if (currentLine.preCursor.peekLast().getText().charAt(0) == 10) {
						currentLineEndsInEnter = true;
					}
				}
				if (currentLine.postCursor.size() > 0) {
					if (currentLine.postCursor.peekLast().getText().charAt(0) == 10) {
						currentLineEndsInSpace = true;
					}
				} else if (currentLine.preCursor.size() > 0) {
					if (currentLine.preCursor.peekLast().getText().charAt(0) == 32) {
						currentLineEndsInSpace = true;
					}
				}
				ListIterator<Text> preFindLastSpace = currentLine.preCursor.listIterator(currentLine.preCursor.size());
				ListIterator<Text> postFindLastSpace = currentLine.postCursor.listIterator(currentLine.postCursor.size());
				int lastSpaceIndex = -1;
				boolean postCursorContainsLastSpace = false;
				// Iterate through, from the back, to determine where the last space is. This info will be used later
				while (postFindLastSpace.hasPrevious()) {
					Text inspect = postFindLastSpace.previous();
					if (inspect.getText().charAt(0) == 32) {
						lastSpaceIndex = postFindLastSpace.nextIndex();
						postCursorContainsLastSpace = true;
						break;
					}
				}
				if (!postCursorContainsLastSpace) {
					while (preFindLastSpace.hasPrevious()) {
						Text inspect = preFindLastSpace.previous();
						if (inspect.getText().charAt(0) == 32) {
							lastSpaceIndex = preFindLastSpace.nextIndex();
							break;
						}
					}
				}
				if (!currentLineEndsInEnter && wordBank.containsKey(index + 1)) {
					Line nextLine = wordBank.get(index + 1);
					if ((nextLine.preCursor.size() + nextLine.postCursor.size()) > 0) {
						// Need to know the location of spaces in nextLine
						// We will store the index of the spaces in an arraylist
						// The coordinates will be X: index of the space, Y: size of the word up to and including that space
						boolean nextLineContainsSpace = false;
						ArrayList<Coordinates> preNextLineSpaces = new ArrayList<Coordinates>(16);
						ArrayList<Coordinates> postNextLineSpaces = new ArrayList<Coordinates>(16);
						ListIterator<Text> preFindSpaces = nextLine.preCursor.listIterator(0);
						ListIterator<Text> postFindSpaces = nextLine.postCursor.listIterator(0);
						int wordSizeToken = 0;
						while (preFindSpaces.hasNext()) {
							Text inspect = preFindSpaces.next();
							wordSizeToken += (int) Math.round(inspect.getLayoutBounds().getWidth());
							if (inspect.getText().charAt(0) == 32) {
								nextLineContainsSpace = true;
								preNextLineSpaces.add(new Coordinates(preFindSpaces.previousIndex(), wordSizeToken));
								wordSizeToken = 0;
							}
							if (!preFindSpaces.hasNext() && inspect.getText().charAt(0) != 32 && nextLine.postCursor.isEmpty()) {
								preNextLineSpaces.add(new Coordinates(preFindSpaces.previousIndex(), wordSizeToken));
								wordSizeToken = 0;
							}
						}
						while (postFindSpaces.hasNext()) {
							Text inspect = postFindSpaces.next();
							wordSizeToken += (int) Math.round(inspect.getLayoutBounds().getWidth());
							if (inspect.getText().charAt(0) == 32) {
								nextLineContainsSpace = true;
								postNextLineSpaces.add(new Coordinates(postFindSpaces.previousIndex(), wordSizeToken));
								wordSizeToken = 0;
							}
							if (!postFindSpaces.hasNext() && inspect.getText().charAt(0) != 32) {
								postNextLineSpaces.add(new Coordinates(postFindSpaces.previousIndex(), wordSizeToken));
							}
						}
						// Now our arraylists are populated with information about 
						// Where each space is.
						if (lastSpaceIndex == -1) {
							// This is the case where currentLine has no spaces
							// We should try to wrap as many words onto currentLine as we can
							int remainingSpace = windowWidth - currentLine.width;
							int preNextLineSpaceIndex = 0;
							int postNextLineSpaceIndex = 0;
							int copied = 0;
							if (nextLineContainsSpace) {
								// If the nextLine contains words
								// First, try to wrap characters of the first word.
								int preRemovalIndex = 0;
								int postRemovalIndex = 0;
								while (remainingSpace > 0) {
									if (!nextLine.preCursor.isEmpty()) {
										Text moving = nextLine.preCursor.removeFirst();
										if ((int) Math.round(moving.getLayoutBounds().getWidth()) <= remainingSpace) {
											nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
											currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
											remainingSpace -= (int) Math.round(moving.getLayoutBounds().getWidth());
											currentLine.postCursor.addLast(moving);
											if (!preNextLineSpaces.isEmpty() && preRemovalIndex == preNextLineSpaces.get(0).x) {
												break;
											}
											preRemovalIndex += 1;
										} else {
											break;
										}
									} else if (!nextLine.postCursor.isEmpty()) {
										Text moving = nextLine.postCursor.removeFirst();
										if ((int) Math.round(moving.getLayoutBounds().getWidth()) <= remainingSpace) {
											nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
											currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
											remainingSpace -= (int) Math.round(moving.getLayoutBounds().getWidth());
											currentLine.postCursor.addLast(moving);
											copied += 1;
											if (postRemovalIndex == postNextLineSpaces.get(0).x) {
												break;
											}
											postRemovalIndex += 1;
										} else {
											break;
										}
									} else {
										break;
									}
								}
								while (remainingSpace > 0) {
									if (!preNextLineSpaces.isEmpty()) {
										int nextWordSize = preNextLineSpaces.get(preNextLineSpaceIndex).y;
										if (nextWordSize <= remainingSpace) {
											int tempIndex = 0;
											while (tempIndex < (preNextLineSpaces.get(preNextLineSpaceIndex).x + 1)) {
												Text moving = nextLine.preCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
												tempIndex += 1;
											}
											remainingSpace -= preNextLineSpaces.get(preNextLineSpaceIndex).y;
											preNextLineSpaceIndex += 1;
										} else {
											break;
										}
									} else if (!postNextLineSpaces.isEmpty()) {
										int nextWordSize = postNextLineSpaces.get(postNextLineSpaceIndex).y;
										if (nextWordSize <= remainingSpace) {
											while (!nextLine.preCursor.isEmpty()) {
												Text moving = nextLine.preCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
											}
											int tempIndex = 0;
											while (tempIndex < (postNextLineSpaces.get(postNextLineSpaceIndex).x + 1)) {
												Text moving = nextLine.postCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
												tempIndex += 1;
												copied += 1;
											}
											remainingSpace -= postNextLineSpaces.get(postNextLineSpaceIndex).y;
											postNextLineSpaceIndex += 1;
										} else {
											break;
										}
									} else {
										break;
									}
								}
							} else {
								// If the nextLine contains no space, then just try to wrap characters
								while (currentLine.width < windowWidth) {
									if (!nextLine.preCursor.isEmpty()) {
										Text moving = nextLine.preCursor.peekFirst();
										if ((currentLine.width + (int) Math.round(moving.getLayoutBounds().getWidth())) > windowWidth) {
											break;
										}
										nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
										currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
										currentLine.postCursor.addLast(nextLine.preCursor.removeFirst());
									} else if (!nextLine.postCursor.isEmpty()) {
										Text moving = nextLine.postCursor.peekFirst();
										if ((currentLine.width + (int) Math.round(moving.getLayoutBounds().getWidth())) > windowWidth) {
											break;
										}
										nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
										currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
										currentLine.postCursor.addLast(nextLine.postCursor.removeFirst());
										copied += 1;
									} else {
										// If nextLine is empty, we're done
										break;
									}
								}
							}
							if (nextLine.lineNumber == cursorIndex.y && nextLine.preCursor.isEmpty()) {
								moveCursor(new Coordinates((currentLine.preCursor.size() + currentLine.postCursor.size() - copied), currentLine.lineNumber));
							}
						} else {
							// This is now the case where currentLine contains space.
							// This is separated into two cases, currentLine ends in space
							// or not.
							if (currentLineEndsInSpace) {
								// In this case, we should try to pull up as many whole words as possible
								int remainingSpace = windowWidth - currentLine.width;
								int preNextLineSpaceIndex = 0;
								int postNextLineSpaceIndex = 0;
								int copied = 0;
								while (remainingSpace > 0) {
									if (preNextLineSpaceIndex < preNextLineSpaces.size()) {
										if (preNextLineSpaces.get(preNextLineSpaceIndex).y <= remainingSpace) {
											int tempIndex = 0;
											int target = preNextLineSpaces.get(preNextLineSpaceIndex).x;
											while (tempIndex < (preNextLineSpaces.get(preNextLineSpaceIndex).x + 1)) {
												Text moving = nextLine.preCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
												tempIndex += 1;
											}
											remainingSpace -= preNextLineSpaces.get(preNextLineSpaceIndex).y;
											preNextLineSpaceIndex += 1;
										} else {
											break;
										}
									} else if (postNextLineSpaceIndex < postNextLineSpaces.size()) {
										if (postNextLineSpaces.get(postNextLineSpaceIndex).y <= remainingSpace) {
											while (!nextLine.preCursor.isEmpty()) {
												Text moving = nextLine.preCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
											}
											int tempIndex = 0;
											while (tempIndex < (postNextLineSpaces.get(postNextLineSpaceIndex).x + 1)) {
												Text moving = nextLine.postCursor.removeFirst();
												nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
												currentLine.postCursor.addLast(moving);
												tempIndex += 1;
												copied += 1;
											}
											remainingSpace -= postNextLineSpaces.get(postNextLineSpaceIndex).y;
											postNextLineSpaceIndex += 1;
										} else {
											break;
										}
									} else {
										break;
									}
								}
								if (nextLine.lineNumber == cursorIndex.y && nextLine.preCursor.isEmpty()) {
									moveCursor(new Coordinates((currentLine.preCursor.size() + currentLine.postCursor.size() - copied), currentLine.lineNumber));
								}
							} else {
								// currentLine contains space but does not end in space
								// If we can fit a word from nextLine onto currentLine, we should do that
								// if we cannot, we should move everything after the last space in
								// currentLine to the nextLine
								int remainingSpace = windowWidth - currentLine.width;
								int preNextLineSpaceIndex = 0;
								int postNextLineSpaceIndex = 0;
								int copied = 0;
								int nextWordSizee = -1;
								if (!preNextLineSpaces.isEmpty()) {
									nextWordSizee = preNextLineSpaces.get(preNextLineSpaceIndex).y;
								} else {
									nextWordSizee = postNextLineSpaces.get(postNextLineSpaceIndex).y;
								}
								if (nextWordSizee <= remainingSpace) {
									// Then we do our algorithm where we 
									// just wrap words
									while (remainingSpace > 0) {
										if (preNextLineSpaceIndex < preNextLineSpaces.size()) {
											int nextWordSize = preNextLineSpaces.get(preNextLineSpaceIndex).y;
											if (nextWordSize <= remainingSpace) {
												int tempIndex = 0;
												while (tempIndex < (preNextLineSpaces.get(preNextLineSpaceIndex).x + 1)) {
													Text moving = nextLine.preCursor.removeFirst();
													nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.postCursor.addLast(moving);
													tempIndex += 1;
												}
												remainingSpace -= preNextLineSpaces.get(preNextLineSpaceIndex).y;
												preNextLineSpaceIndex += 1;
											} else {
												break;
											}
										} else if (postNextLineSpaceIndex < postNextLineSpaces.size()) {
											int nextWordSize = postNextLineSpaces.get(postNextLineSpaceIndex).y;
											if (nextWordSize <= remainingSpace) {
												while (!nextLine.preCursor.isEmpty()) {
													Text moving = nextLine.preCursor.removeFirst();
													nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.postCursor.addLast(moving);
												}
												int tempIndex = 0;
												while (tempIndex < (postNextLineSpaces.get(postNextLineSpaceIndex).x + 1)) {
													Text moving = nextLine.postCursor.removeFirst();
													nextLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
													currentLine.postCursor.addLast(moving);
													tempIndex += 1;
													copied += 1;
												}
												remainingSpace -= postNextLineSpaces.get(postNextLineSpaceIndex).y;
												postNextLineSpaceIndex += 1;
											} else {
												break;
											}
										} else {
											break;
										}
									}
									if (nextLine.lineNumber == cursorIndex.y && nextLine.preCursor.isEmpty()) {
										moveCursor(new Coordinates((currentLine.preCursor.size() + currentLine.postCursor.size() - copied), currentLine.lineNumber));
									}
								} else {
									// The first word of nextLine won't fit on currentLine,
									// so we have to move the last word of currentLine
									// down to nextLine
									if (postCursorContainsLastSpace) {
										int tempIndex = lastSpaceIndex + 1;
										int target = currentLine.postCursor.size();
										while (tempIndex < target) {
											Text moving = currentLine.postCursor.removeLast();
											currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
											nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
											if (nextLine.lineNumber == cursorIndex.y) {
												nextLine.preCursor.addFirst(moving);
											} else {
												nextLine.postCursor.addFirst(moving);
											}
										}
									} else {
										int tempIndex = lastSpaceIndex + 1;
										int target = currentLine.preCursor.size();
										while (!currentLine.postCursor.isEmpty()) {
											Text moving = currentLine.postCursor.removeLast();
											currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
											nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
											if (nextLine.lineNumber == cursorIndex.y) {
												nextLine.preCursor.addFirst(moving);
											} else {
												nextLine.postCursor.addFirst(moving);
											}
										}
										while (tempIndex < target) {
											Text moving = currentLine.preCursor.removeLast();
											currentLine.width -= (int) Math.round(moving.getLayoutBounds().getWidth());
											nextLine.width += (int) Math.round(moving.getLayoutBounds().getWidth());
											if (nextLine.lineNumber == cursorIndex.y) {
												nextLine.preCursor.addFirst(moving);
											} else {
												nextLine.postCursor.addFirst(moving);
												copied += 1;
											}
											tempIndex += 1;
										}
									}
									if (currentLine.lineNumber == cursorIndex.y) {
										moveCursor(new Coordinates(copied, nextLine.lineNumber));
									}
								}
							}
						}
					} else {
						// if the next line is empty, and it has the cursor,
						// put the cursor back on the previous line.
						if (nextLine.lineNumber == cursorIndex.y) {
							moveCursor(new Coordinates((currentLine.preCursor.size() + currentLine.postCursor.size()), index));
						}
					}
				}
				// If currentLine ends with Enter, do nothing
				// If no next line exists, do nothing.
			}
			// Wrapping is complete for this line, so we should align the text
			currentLine.xAlign();
			currentLine.yAlign();
			purgeEmpty();
			index += 1;
		}
	}

	public void purgeEmpty() {
		int index = wordBank.size() - 1;
		while (index > 0) {
			Line inspect = wordBank.get(index);
			if (inspect.isEmpty() && inspect.lineNumber != cursorIndex.y) {
				wordBank.remove(index);
			} else {
				break;
			}
			index -= 1;
		}
	}

	public void updateFontSize(int fontSize, int windowWidth) {
		int oldFontSize = this.fontSize;
		this.fontSize = fontSize;
		int index = 0;
		while (index < wordBank.size()) {
			Line updating = wordBank.get(index);
			updating.setHeight(this.fontName, fontSize);
			updating.xAlign();
			updating.yAlign();
			index += 1;
		}
		wrap(windowWidth);
	}

	public WordBank clone() {
		WordBank output = new WordBank(new String(this.fontName), new Integer(this.fontSize));
		output.cursorIndex = this.cursorIndex.clone();
		output.cursorPos = this.cursorPos.clone();
		// Now we have to clone the hashmap
		int lineIndex = 0;
		while (lineIndex < this.wordBank.size()) {
			Line copying = this.wordBank.get(lineIndex);
			if (!output.wordBank.containsKey(lineIndex)) {
				output.wordBank.put(lineIndex, new Line());
				output.wordBank.get(lineIndex).lineNumber = lineIndex;
				output.wordBank.get(lineIndex).setHeight(output.fontName, output.fontSize);
			}
			Line copyTo = output.wordBank.get(lineIndex);
			ListIterator<Text> preLine = copying.preCursor.listIterator(0);
			ListIterator<Text> postLine = copying.postCursor.listIterator(0);
			while (preLine.hasNext()) {
				Text sample = preLine.next();
				Text sampleClone = new Text(new String(sample.getText()));
				sampleClone.setTextOrigin(VPos.TOP);
				sampleClone.setFont(Font.font(output.fontName, output.fontSize));
				sampleClone.setX(new Integer((int) sample.getX()));
				sampleClone.setY(new Integer((int) sample.getY()));
				output.wordBank.get(lineIndex).preCursor.addLast(sampleClone);
			}
			while (postLine.hasNext()) {
				Text sample = postLine.next();
				Text sampleClone = new Text(new String(sample.getText()));
				sampleClone.setTextOrigin(VPos.TOP);
				sampleClone.setFont(Font.font(output.fontName, output.fontSize));
				sampleClone.setX(new Integer((int) sample.getX()));
				sampleClone.setY(new Integer((int) sample.getY()));
				output.wordBank.get(lineIndex).postCursor.addLast(sampleClone);
			}
			lineIndex += 1;
		}
		return output;
	}
}
