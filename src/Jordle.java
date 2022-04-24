import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class that represents a Jordle game.
 * @author Madeline Hou
 * @version 1.0.0
 */
public class Jordle extends Application {

    private static GridPane table = new GridPane();
    private static ArrayList<TextField> row1 = addRow(0);
    private static ArrayList<TextField> row2 = addRow(1);
    private static ArrayList<TextField> row3 = addRow(2);
    private static ArrayList<TextField> row4 = addRow(3);
    private static ArrayList<TextField> row5 = addRow(4);
    private static ArrayList<TextField> row6 = addRow(5);
    private static String answerString = getRandomWord();
    private static ArrayList<Character> answerChars = getAnswerArrayList();
    private static Label status = new Label("Try guessing a word!");
    private static Font wordleFont = Font.font("Helvetica", 20);
    private static int numGuesses = 1;
    private static Pane layout = new Pane();

    /**
     * Main method to launch program.
     * @param args string argument
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //Create items for Stage
        //Add name of window (title), instructions/start/restart button

        primaryStage.setTitle("JORDLE");

        Label title = new Label("JORDLE");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 50));

        Button instructions = new Button();
        instructions.setText("Instructions");
        instructions.setFont(wordleFont);
        status.setFont(wordleFont);
        instructions.setOnAction(e -> {
            displayInstructions();
        });

        Button start = new Button();
        start.setText("Start Game");
        start.setFont(wordleFont);
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                unblockRow(row1);
                play();
            }
        });

        Button restart = new Button("Restart Game");
        restart.setOnAction(event -> {
            answerString = getRandomWord();
            answerChars = getAnswerArrayList();
            row1 = addRow(0);
            row2 = addRow(1);
            row3 = addRow(2);
            row4 = addRow(3);
            row5 = addRow(4);
            row6 = addRow(5);
            blockOtherRows(0);
            blockRow(row1);
            numGuesses = 0;
            status.setText("Try guessing a word!");
            status.relocate(300, 600);
        });

        blockOtherRows(0);
        blockRow(row1);

        //END of creating items for Stage-------------

        //Add items to Stage

        layout.getChildren().addAll(title, start, instructions, status, table, restart);

        title.relocate(300, 10);
        status.relocate(300, 600);
        start.relocate(450, 650);
        table.relocate(200, 100);
        instructions.relocate(200, 650);
        restart.relocate(350, 725);

        Scene scene = new Scene(layout, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        //Close program with exit button (X)
        primaryStage.setOnCloseRequest(e -> closeProgram(primaryStage));
    }

    /**
     * Method that plays the guesses of the game.
     */
    public static void play() {
        guess(row1, 0);
        guess(row2, 1);
        guess(row3, 2);
        guess(row4, 3);
        guess(row5, 4);
        guess(row6, 5);
    }

    /**
     * Creates instructions button.
     * When clicked, opens a new window with how to play Jordle.
     */
    public static void displayInstructions() {
        Stage instructionsStage = new Stage();

        //blocks user interaction with other windows until this one is taken care of
        instructionsStage.initModality(Modality.APPLICATION_MODAL);
        instructionsStage.setTitle("Instructions");
        instructionsStage.setMinHeight(250);

        Label label = new Label();
        label.setText(instructionsString());
        label.setFont(Font.font("Helvetica", 14.5));

        Button closeInstructions = new Button("Close the window");
        closeInstructions.setOnAction(c -> closeProgram(instructionsStage));

        VBox layoutVBox = new VBox(50);
        layoutVBox.getChildren().addAll(label, closeInstructions);
        layoutVBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layoutVBox);
        instructionsStage.setScene(scene);
        instructionsStage.showAndWait();
    }

    /**
     * Generates a random word from Words.java, access with Words.list.
     * @return random word to be guessed
     */
    public static String getRandomWord() {
        Random rand = new Random();
        ArrayList<String> wordsList = Words.list;
        int listLength = wordsList.size();
        return wordsList.get(rand.nextInt(listLength));
    }

    /**
     * Creates a TextField that represents a letter in a word.
     * @return a Text field
     */
    public static TextField createTextField() {
        TextField textField = new TextField();
        textField.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        textField.setPrefHeight(75);
        textField.setPrefWidth(75);
        textField.setAlignment(Pos.CENTER);

        setTextLimit(textField);
        textField.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }

        });
        return textField;
    }

    /**
     * Sets limit of what can be typed in a TextField (one letter max).
     * @param textField TextField to be limited
     */
    public static void setTextLimit(TextField textField) {
        textField.setOnKeyTyped(event -> {
            String string = textField.getText();
            if (string.length() > 1) {
                textField.setText(string.substring(0, 1).toUpperCase());
                textField.positionCaret(string.length());
            }
        });
    }


    /**
     * Method that adds 5 TextFields to an ArrayList of TextFields to represent a row of letters.
     * @param row which row the TextFields will be added to
     * @return an ArrayList of TextFields
     */
    public static ArrayList<TextField> addRow(int row) {

        TextField spot1 = createTextField();
        TextField spot2 = createTextField();
        TextField spot3 = createTextField();
        TextField spot4 = createTextField();
        TextField spot5 = createTextField();

        table.add(spot1, 0, row);
        table.add(spot2, 1, row);
        table.add(spot3, 3, row);
        table.add(spot4, 4, row);
        table.add(spot5, 5, row);

        ArrayList<TextField> textFields = new ArrayList<>();
        textFields.add(spot1);
        textFields.add(spot2);
        textFields.add(spot3);
        textFields.add(spot4);
        textFields.add(spot5);

        return textFields;
    }

    /**
     * Method that processes the guess of a five-letter word, inputted by the user.
     * If the word is less than five letters, an error occurs.
     * @param textFields the letters of the word
     * @param rowNum which row this guess is at
     */
    public static void guess(ArrayList<TextField> textFields, int rowNum) {
        Alert a = new Alert(Alert.AlertType.NONE, "Your guess is invalid; it must be exactly "
                + "five letters.", ButtonType.OK);

        TextField spot1 = textFields.get(0);
        TextField spot2 = textFields.get(1);
        TextField spot3 = textFields.get(2);
        TextField spot4 = textFields.get(3);
        TextField spot5 = textFields.get(4);

        changeFocus(spot1, spot2);
        changeFocus(spot2, spot3);
        changeFocus(spot3, spot4);
        changeFocus(spot4, spot5);

        backspace(spot1, spot2);
        backspace(spot2, spot3);
        backspace(spot3, spot4);
        backspace(spot4, spot5);

        spot5.setOnKeyReleased(e -> {
//            System.out.println("real word: " + answerString);
            ArrayList<Character> guessChars = getGuessArrayList(textFields);
            if (e.getCode() == KeyCode.ENTER && guessChars.size() == 5) {
                blockOtherRows(rowNum + 1);
                checkGuess(textFields, guessChars, answerChars);
                numGuesses++;
            } else if (e.getCode() == KeyCode.ENTER && guessChars.size() < 5) {
                a.show();
            } else if (e.getCode() == KeyCode.BACK_SPACE) {
                spot4.requestFocus();
                spot5.setText("");
            }
        });
    }

    /**
     * Method that moves the input to the next TextField box after a letter is inputted.
     * @param spot1 the original letter spot
     * @param spot2 the letter spot the focus will go to
     */
    public static void changeFocus(TextField spot1, TextField spot2) {
        spot1.setOnKeyTyped(e -> {
            if (spot1.getText().matches("[a-zA-Z0-9]+")) {
                spot2.requestFocus();
            }
        });
    }

    /**
     * Method that backspaces a letter and goes to the previous letter.
     * @param spot1 the letter spot that the backspace will go
     * @param spot2 the original letter spot
     */
    public static void backspace(TextField spot1, TextField spot2) {
        spot2.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                spot1.requestFocus();
                spot2.setText("");
            }
        });
    }

    /**
     * Method that checks which letters in the guess are in the answer.
     * The method changes the color of the TextFields depending on the answer.
     * @param textFields the TextFields in the row being evaluated
     * @param guess the letters in the guess, as individual characters
     * @param answer the letters in the answer, as individual characters
     */
    public static void checkGuess(ArrayList<TextField> textFields, ArrayList<Character> guess,
                                  ArrayList<Character> answer) {
        for (int i = 0; i < 5; i++) {
            Character guessLetter = Character.toUpperCase(guess.get(i));
            Character answerLetter = Character.toUpperCase(answer.get(i));
//            System.out.println("guess letter at index " + i + ": " + guessLetter);
//            System.out.println("answer letter at index " + i + ": " + answerLetter);
            if (guessLetter.equals(answerLetter)) {
                textFields.get(i).setStyle("-fx-background-color: #6cac64; -fx-text-fill: white;");
//                System.out.println(guess.get(i) + " is in the right spot");
            } else if (answer.contains(guessLetter)) {
                textFields.get(i).setStyle("-fx-background-color: #ccb35d; -fx-text-fill: white;");
//                System.out.println(guess.get(i) + " is in the answer");
                //make textfield yellow
            } else {
                textFields.get(i).setStyle("-fx-background-color: #7c7c7c; -fx-text-fill: white;");
                //make textfield gray
//                System.out.println(guess.get(i) + " is not in the answer");
            }
        }
//        System.out.println(numGuesses);
        if (checkWin(guess, answer)) {
            status.setText("Congratulations! You've guessed the word!");
            status.relocate(195, 600);
        } else if (numGuesses == 6) {
            status.setText("Game over. The word was " + answerString + ".");
            status.relocate(245, 600);
        }
    }

    /**
     * Method that checks if the user guessed the word correctly.
     * @param guess the letters in the guess, as individual characters
     * @param answer the letters in the answer, as individual characters
     * @return whether the user guessed the word correctly as a boolean
     */
    public static boolean checkWin(ArrayList<Character> guess, ArrayList<Character> answer) {
        for (int i = 0; i < 5; i++) {
            char guessLetter = Character.toUpperCase(guess.get(i));
            char answerLetter = Character.toUpperCase(answer.get(i));
            if (guessLetter != answerLetter) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method that places all the letters of the guess into an Arraylist of Characters.
     * @param textFields row representing the letters
     * @return Arraylist of Characters that contains the letters of the guess
     */
    public static ArrayList<Character> getGuessArrayList(ArrayList<TextField> textFields) {
        String guess = textFields.get(0).getText() + textFields.get(1).getText() + textFields.get(2).getText()
                + textFields.get(3).getText() + textFields.get(4).getText();
        ArrayList<Character> guessCharacters = new ArrayList<>();
        if (guess.length() == 5) {
            for (int i = 0; i < 5; i++) {
                guessCharacters.add(guess.charAt(i));
            }
        }
        return guessCharacters;
    }

    /**
     * Blocks a certain row from user interaction.
     * @param textFields row that is blocked from user interaction
     */
    public static void blockRow(ArrayList<TextField> textFields) {

        for (int i = 0; i < 5; i++) {
            textFields.get(i).setEditable(false);
        }
    }

    /**
     * Unblocks a certain row from user interaction.
     * @param textFields row that is unblocked from user interaction
     */
    public static void unblockRow(ArrayList<TextField> textFields) {
        for (int i = 0; i < 5; i++) {
            textFields.get(i).setEditable(true);
        }
    }

    /**
     * Method that blocks all the rows from being edited, except for the one the user is on.
     * @param row the row that should be unblocked
     */
    public static void blockOtherRows(int row) {
        blockRow(row1);
        blockRow(row2);
        blockRow(row3);
        blockRow(row4);
        blockRow(row5);
        blockRow(row6);

        if (row == 0) {
            unblockRow(row1);
        } else if (row == 1) {
            unblockRow(row2);
        } else if (row == 2) {
            unblockRow(row3);
        } else if (row == 3) {
            unblockRow(row4);
        } else if (row == 4) {
            unblockRow(row5);
        } else if (row == 5) {
            unblockRow(row6);
        }
    }

    /**
     * Method that puts all the letters of the answer into an ArrayList of Characters.
     * @return ArrayList of Characters that represents the letters of the answer.
     */
    public static ArrayList<Character> getAnswerArrayList() {
        ArrayList<Character> answerC = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            answerC.add(Character.toUpperCase(answerString.charAt(i)));
        }
        return answerC;
    }

    /**
     * Method that closes program.
     * @param stage stage of program
     */
    public static void closeProgram(Stage stage) {
        stage.close();
    }

    /**
     * Method that stores the instructions for the game.
     * @return the instructions
     */
    public static String instructionsString() {
        return "How to Play:\n"
                + "Guess a five letter Java related word in six tries or less.\n"
                + "Your guesses do not have to be valid words.\n"
                + "Use the enter button to submit each guess.\n"
                + "After each guess, the color of each tile will change "
                + "to show how close your guess was to the word.\n"
                + "If a letter's box turns green, that letter is in the word "
                + "and in the correct spot.\n"
                + "If a letter's box turns yellow, that letter is in"
                + " the word but in the wrong spot.\n"
                + "If a letter's box turns gray, that letter is not in the "
                + "word in any spot.\n"
                + "You can replay as many times as you want, by pressing"
                + " the restart button, then the start button after.\n"
                + "Inspired by WORDLE.";
    }




}