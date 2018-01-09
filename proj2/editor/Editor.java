
package editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollBar;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;


public class Editor extends Application {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private String fontName = "Verdana";
    private int fontSize = 12;
    private Cursor cursor;
    private static WordBank wordBank;
    private LinkedList<WordBank> undoBuffer;
    private LinkedList<WordBank> redoBuffer;
    private static Text sample;
    private static String fileName;
    private static Group root;
    private static Group textRoot;
    private static ScrollBar scrollBar;
    private static boolean debug;

    public Editor() {
        wordBank = new WordBank(fontName, fontSize);
        cursor = new Cursor();
        cursor.w = wordBank;
        undoBuffer = new LinkedList<WordBank>();
        redoBuffer = new LinkedList<WordBank>();
        sample = new Text(" ");
        root = new Group();
        textRoot = new Group();
        root.getChildren().add(textRoot);
        scrollBar = new ScrollBar();
        debug = false;

    }

    public void updateWordBank(WordBank w) {
        this.wordBank = w;
        cursor.w = w;
        this.textRoot.getChildren().clear();
        this.textRoot.getChildren().add(cursor.object);
        
        int index = 0;
        while (index < w.wordBank.size()) {
            Line copying = w.wordBank.get(index);
            ListIterator<Text> preCopy = copying.preCursor.listIterator(0);
            ListIterator<Text> postCopy = copying.postCursor.listIterator(0);
            while (preCopy.hasNext()) {
                textRoot.getChildren().add(preCopy.next());
            }
            while (postCopy.hasNext()) {
                textRoot.getChildren().add(postCopy.next());
            }
            index += 1;
        }
        
    }
    
    /** An EventHandler to handle keys that get pressed. */
    public class KeyEventHandler implements EventHandler<KeyEvent> {
        int windowWidth;
        int windowHeight;

        private static final int STARTING_FONT_SIZE = 12;

        /** The Text to display on the screen. */
        private int fontSize = STARTING_FONT_SIZE;
        private Group root;

        

        private String fontName = "Verdana";

        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            this.windowWidth = windowWidth - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
            this.windowHeight = windowHeight;

            this.root = root;
            sample.setFont(Font.font(fontName, fontSize));
            cursor.changeSize((int) Math.round(sample.getLayoutBounds().getHeight()));
        }

        public void updateWidth(int windowWidth) {
            this.windowWidth = windowWidth;
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.getCode() == KeyCode.P) {
                    System.out.println((int) cursor.object.getX() + ", " + (int) cursor.object.getY());
                    if (debug) {
                        System.out.println("Cursor is on line: " + wordBank.cursorIndex.y);
                        System.out.println("Current line width: " + wordBank.wordBank.get(wordBank.cursorIndex.y).width);
                        System.out.println("Current line preCursor size:" + wordBank.wordBank.get(wordBank.cursorIndex.y).preCursor.size());
                        System.out.println("Current line postCursor size:" + wordBank.wordBank.get(wordBank.cursorIndex.y).postCursor.size());
                    }
                } else if (keyEvent.getCode() == KeyCode.Z) {
                    if (!undoBuffer.isEmpty()) {
                        if (debug) {
                            System.out.println("Undoing");
                        }
                        redoBuffer.addFirst(wordBank.clone());
                        updateWordBank(undoBuffer.removeFirst());
                        cursor.update();
                        scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                    } else if (debug) {
                        System.out.println("Undo not possible");
                    }
                } else if (keyEvent.getCode() == KeyCode.Y) {
                    if (!redoBuffer.isEmpty()) {
                        if (debug) {
                            System.out.println("Redoing");
                        }
                        backupToBuffer(wordBank.clone());
                        updateWordBank(redoBuffer.removeFirst());
                        cursor.update();
                        scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                    } else if (debug) {
                        System.out.println("Redo not possible");
                    }
                } else if (keyEvent.getCode() == KeyCode.S) {
                    if (debug) {
                        System.out.println("Saving");
                    }
                    printToFile();
                } else if (keyEvent.getCode() == KeyCode.EQUALS) {
                    if (debug) {
                        System.out.println("Increased font size");
                    }
                    this.fontSize += 4;
                    sample.setFont(Font.font(fontName, fontSize));
                    wordBank.updateFontSize(fontSize, windowWidth);
                    cursor.changeSize((int) Math.round(sample.getLayoutBounds().getHeight()));
                    cursor.update();
                    scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                } else if (keyEvent.getCode() == KeyCode.MINUS) {
                    if (this.fontSize > 4) {
                        if (debug) {
                            System.out.println("Decreased font size");
                        }
                        this.fontSize -= 4;
                        sample.setFont(Font.font(fontName, fontSize));
                        cursor.changeSize((int) Math.round(sample.getLayoutBounds().getHeight()));
                        wordBank.updateFontSize(fontSize, windowWidth);
                        cursor.update();
                        scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                    }
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8 && characterTyped.charAt(0) != 13) {
                    // Ignore control keys, which have non-zero length, as well as the backspace
                    // key, which is represented as a character of value = 8 on Windows.
                    if (debug) {
                        System.out.println("Typed " + characterTyped);
                    }
                    backupToBuffer(wordBank.clone());
                    redoBuffer.clear();
                    wordBank.insert(new Text(characterTyped));
                    root.getChildren().add(wordBank.wordBank.get(wordBank.cursorIndex.y).preCursor.peekLast());
                    wordBank.wrap(windowWidth);
                    updateScrollBar(windowHeight);
                    cursor.update();
                    scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                } 
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP) {
                    wordBank.up();
                    cursor.update();
                    updateScrollBar(windowHeight);
                } else if (code == KeyCode.DOWN) {
                    wordBank.down();
                    cursor.update();
                    updateScrollBar(windowHeight);
                } else if (code == KeyCode.LEFT) {
                    wordBank.left();
                    cursor.update();
                    updateScrollBar(windowHeight);
                } else if (code == KeyCode.RIGHT) {
                    wordBank.right();
                    cursor.update();
                    updateScrollBar(windowHeight);
                } else if (code == KeyCode.BACK_SPACE) {
                    if (debug) {
                        System.out.println("Typed backspace");
                    }
                    backupToBuffer(wordBank.clone());
                    if (wordBank.cursorIndex.x == 0 && wordBank.wordBank.containsKey(wordBank.cursorIndex.y - 1)) {
                        if (debug) {
                            System.out.println("Going to previous line");
                        }
                        if (!wordBank.wordBank.get(wordBank.cursorIndex.y - 1).postCursor.isEmpty()) {
                            root.getChildren().remove(wordBank.wordBank.get(wordBank.cursorIndex.y - 1).postCursor.peekLast());
                            wordBank.backspace();
                            wordBank.wrap(windowWidth);
                            redoBuffer.clear();
                        } else if (!wordBank.wordBank.get(wordBank.cursorIndex.y - 1).preCursor.isEmpty()) {
                            root.getChildren().remove(wordBank.wordBank.get(wordBank.cursorIndex.y - 1).preCursor.peekLast());
                            wordBank.backspace();
                            wordBank.wrap(windowWidth);
                            redoBuffer.clear();
                        }
                    } else {
                        root.getChildren().remove(wordBank.wordBank.get(wordBank.cursorIndex.y).preCursor.peekLast());
                        Text returned = wordBank.backspace();
                        wordBank.wrap(windowWidth);
                        if (returned == null) {
                            undoBuffer.removeFirst();
                        }
                    }
                    cursor.update();
                    updateScrollBar(windowHeight);
                    scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                } else if (code == KeyCode.ENTER) {
                    if (debug) {
                        System.out.println("Pressed Enter");
                    }
                    redoBuffer.clear();
                    backupToBuffer(wordBank.clone());
                    //char enter = (char) 13;
                    //String insertEnter = Character.toString(enter);
                    wordBank.insert(new Text("\n"));
                    String test = "\n";
                    root.getChildren().add(wordBank.wordBank.get(wordBank.cursorIndex.y).preCursor.peekLast());
                    wordBank.wrap(windowWidth);
                    cursor.update();
                    updateScrollBar(windowHeight);
                    scrollBar.setMax(Math.max(0, wordBank.height() - windowHeight));
                }
            }
        }
    }

    public void updateWindowWidth(KeyEventHandler keyEventHandler, int windowWidth) {
        keyEventHandler.windowWidth = windowWidth;
    }

    public void updateWindowHeight(KeyEventHandler keyEventHandler, int windowHeight) {
        keyEventHandler.windowHeight = windowHeight;
    }

    /** An EventHandler to handle blinking the cursor */
    public class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.object.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the cursor blink */
    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /** An event handler that moves the cursor to the click locatoin. */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        private int windowHeight;

        public MouseClickEventHandler() {

        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            int mousePressedX = (int) Math.round(mouseEvent.getX());
            int mousePressedY = (int) Math.round(mouseEvent.getY());

            wordBank.moveCursorToCoordinates(new Coordinates(mousePressedX, mousePressedY));
            cursor.update();
        }
    }

    public void readFromFile() {
        File inputFile = new File(fileName);
        if (!inputFile.exists()) {
            return;
        }
        try {
            if (debug) {
                System.out.println("Opening file " + fileName);
            }
            FileReader reader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;
            while ((intRead = bufferedReader.read()) != -1) {
                char charRead = (char) intRead;
                Text add = new Text(Character.toString(charRead));
                wordBank.insert(add);
                textRoot.getChildren().add(wordBank.wordBank.get(0).preCursor.peekLast());
                
            }
            wordBank.wrap(WINDOW_WIDTH - (int) Math.round(scrollBar.getLayoutBounds().getWidth()));
            cursor.update();
            
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException ioException) {
            System.out.println("Error when reading; exception was: " + ioException);
        }
    }

    public void printToFile() {
        try {
            if (debug) {
                System.out.println("Saving to file " + fileName);
            }
            FileWriter writer = new FileWriter(fileName);
            int index = 0;
            while (index < wordBank.wordBank.size()) {
                Line writing = wordBank.wordBank.get(index);
                ListIterator<Text> preWriter = writing.preCursor.listIterator(0);
                ListIterator<Text> postWriter = writing.postCursor.listIterator(0);
                while (preWriter.hasNext()) {
                    Text currentWriting = preWriter.next();
                    writer.write(currentWriting.getText().charAt(0));
                }
                while (postWriter.hasNext()) {
                    Text currentWriting = postWriter.next();
                    writer.write(currentWriting.getText().charAt(0));
                }
                index += 1;
            }
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }

    public void backupToBuffer(WordBank w) {
        if (undoBuffer.size() >= 100) {
            if (debug) {
                System.out.println("Undo buffer full. 100 states have been saved.");
            }
            undoBuffer.removeLast();
        }
        undoBuffer.addFirst(w);
    }

    public void updateScrollBar(int windowHeight) {
        int cursorY = (int) cursor.object.getY();
        int heightAtTop = (int) Math.round(scrollBar.getValue());
        int heightAtBottom = heightAtTop + windowHeight;
        if (wordBank.height() < windowHeight) {
            scrollBar.setValue(0);
        }
        if (cursorY < heightAtTop) {
            scrollBar.setValue(cursorY);
        } else if ((cursorY + (int) Math.round(cursor.object.getLayoutBounds().getHeight())) > heightAtBottom) {
            int difference = windowHeight - (cursorY + (int) Math.round(cursor.object.getLayoutBounds().getHeight()));
            scrollBar.setValue((cursorY + (int) Math.round(cursor.object.getLayoutBounds().getHeight())) - windowHeight);
        }
    }


    @Override
    public void start(Stage primaryStage) {
        
        readFromFile();
        wordBank.moveCursorToCoordinates(new Coordinates(WINDOW_WIDTH, WINDOW_HEIGHT));
        cursor.update();

        KeyEventHandler keyEventHandler = new KeyEventHandler(textRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
                
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        
        scene.setOnMouseClicked(new MouseClickEventHandler());

        textRoot.getChildren().add(cursor.object);
        makeRectangleColorChange();

        primaryStage.setTitle("Editor");

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                int newWindowWidth = newScreenWidth.intValue();
                int usableScreenWidth = newWindowWidth - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
                scrollBar.setLayoutX(usableScreenWidth);
                wordBank.wrap(usableScreenWidth);
                updateWindowWidth(keyEventHandler, newWindowWidth);
                cursor.update();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                int newWindowHeight = newScreenHeight.intValue();
                updateWindowHeight(keyEventHandler, newWindowHeight);
                scrollBar.setPrefHeight(newWindowHeight);
                scrollBar.setMax(Math.max(0, wordBank.height() - newWindowHeight));
            }
        });

        // Make a vertical scroll bar on the right side of the screen.
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setLayoutX(WINDOW_WIDTH - Math.round(scrollBar.getLayoutBounds().getWidth()));

        // Set the range of the scroll bar
        scrollBar.setMin(0);
        scrollBar.setMax(Math.max(0, wordBank.height() - WINDOW_HEIGHT));
        root.getChildren().add(scrollBar);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                // newValue describes the value of the new position of the scroll bar.
                textRoot.setLayoutY(-1 * newValue.intValue());
            }
        });

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Expected usage: Editor <filename> <debug>");
            System.exit(1);
        }
        if (args.length == 1) {
            fileName = args[0];
        }
        if (args.length == 2) {
            fileName = args[0];
            if (args[1].equals("debug")) {
                debug = true;
                System.out.println("Debug");
            }
        }
        if (args.length > 2) {
            System.out.println("Expected usage: Editor <filename> <debug>");
            System.exit(1);
        }
        launch(args);
    }
}
