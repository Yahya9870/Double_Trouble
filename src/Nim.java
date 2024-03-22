/*************************************************************************************************************************************************************

 This game is commonly known as "Nim." Nim is an ancient, mathematical game of strategy for two players.

 It was thoroughly analyzed and solved by the mathematician Charles L. Bouton of Harvard University, who introduced the complete theory of the game in 1901, making it one of the earliest games to be solved mathematically.

 Nim has appeared in various forms in popular culture over the years.

 One of the most notable appearances is in the film "Last Year at Marienbad" (1961), where the game plays a central role in the plot. Additionally, variations of Nim are often featured in puzzle books and mathematics challenges, given its historical importance in game theory and its application to the study of mathematical strategy.


 ***********************************************************************************************************************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Nim extends JFrame {    //Defines the Nim class which extends JFrame, making it a Swing application window.


    // Initializes the markers for each color pile with their starting counts.
    private int greenMarkers = 3;
    private int yellowMarkers = 7;
    private int orangeMarkers = 5;

    // Declares GUI components for displaying game status, buttons for each color pile, and text fields for inputting the number of markers to remove.
    private  JLabel statusLabel;
    private  JButton greenButton;
    private  JButton yellowButton;
    private  JButton orangeButton;
    private JTextField greenTextField;
    private JTextField yellowTextField;
    private JTextField orangeTextField;

    // Buttons for performing game actions: removing markers, quitting the game, and replaying and deciding who is the winner of the tournament.
    private JButton removeButton; // Single remove button for all colors
    private JButton quitButton; // Quit button for clean termination
    private JButton replayButton; // Replay button



    // Tracks scores of the computer and player throughout the tournament.
    private int computerScore = 0;
    private int playerScore = 0;


    // Labels to display the current scores of the computer and player.
    private JLabel computerScoreLabel;
    private JLabel playerScoreLabel;


    // Indicates whose turn it is. Initialized to true, meaning the player goes first by default.
    private boolean playerTurn = true;

    // A Random instance for making random decisions, used by the computer in certain game states.
    private final Random random = new Random();

    // Documentation for code present inside the constrcutor.
    // Sets the window title, size, close operation, and layout manager.
    //
    //Status Label and Buttons Panel
    //Sets up the top panel with a status label and buttons for each color pile. Buttons are colored according to their pile color for visual distinction.
    //
    //Input Panel
    //Sets up the input panel with text fields for each color and action buttons (Replay, Remove, Quit). Includes labels for displaying scores.
    public Nim() {   // The Constructor for class Nim
        setTitle("Nim");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Welcome to Nim (DoubleTrouble)", SwingConstants.CENTER);
        topPanel.add(statusLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        greenButton = new JButton("Green (" + greenMarkers + ")");
        greenButton.setBackground(Color.GREEN);
        greenButton.setOpaque(true);
        greenButton.setBorderPainted(false); // Ensure the background color is visible

        yellowButton = new JButton("Yellow (" + yellowMarkers + ")");
        yellowButton.setBackground(Color.YELLOW);
        yellowButton.setOpaque(true);
        yellowButton.setBorderPainted(false); // Ensure the background color is visible

        orangeButton = new JButton("Orange (" + orangeMarkers + ")");
        orangeButton.setBackground(new Color(255, 153, 0)); // There is no standard orange color
        orangeButton.setOpaque(true);
        orangeButton.setBorderPainted(false); // Ensure the background color is visible

        buttonPanel.add(greenButton);
        buttonPanel.add(yellowButton);
        buttonPanel.add(orangeButton);

        greenButton.addActionListener(new ButtonListener());
        yellowButton.addActionListener(new ButtonListener());
        orangeButton.addActionListener(new ButtonListener());

        topPanel.add(buttonPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 3));

        // Text fields for each color
        greenTextField = new JTextField(5);
        yellowTextField = new JTextField(5);
        orangeTextField = new JTextField(5);
        inputPanel.add(greenTextField);
        inputPanel.add(yellowTextField);
        inputPanel.add(orangeTextField);

        // Replay, Remove, and Quit buttons
        replayButton = new JButton("Replay");
        removeButton = new JButton("Remove");
        quitButton = new JButton("Quit");
        replayButton.addActionListener(new ButtonListener());
        removeButton.addActionListener(new ButtonListener());
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitTournament(); // Call quitTournament() when the Quit button is pressed
            }
        });

        inputPanel.add(replayButton);
        inputPanel.add(removeButton);
        inputPanel.add(quitButton);

        // Score labels
        computerScoreLabel = new JLabel("Computer Score: " + computerScore);
        playerScoreLabel = new JLabel("Player Score: " + playerScore);
        inputPanel.add(computerScoreLabel);
        inputPanel.add(new JLabel()); // Empty label for spacing
        inputPanel.add(playerScoreLabel);

        add(inputPanel, BorderLayout.CENTER);

        setVisible(true);

        askWhoGoesFirst();
    }
    // Prompts the user to choose who goes first at the beginning of the game.
    private void askWhoGoesFirst() {
        int choice = JOptionPane.showOptionDialog(null, "Who goes first?", "Choose", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Player", "Computer"}, "Player");
        playerTurn = (choice == JOptionPane.YES_OPTION);
        if (!playerTurn) {
            solveNim();
        }
    }
    // Defines the action listener for buttons, handling clicks on color buttons, Remove, Replay, and Quit.
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check if one of the color buttons is pressed
            if (e.getSource() == greenButton) {
                greenTextField.requestFocus();
            } else if (e.getSource() == yellowButton) {
                yellowTextField.requestFocus();
            } else if (e.getSource() == orangeButton) {
                orangeTextField.requestFocus();
            } else if (e.getSource() == removeButton) {
                // Remove button logic
                if (!greenTextField.getText().isEmpty()) {
                    removeMarkers('g', greenTextField);
                } else if (!yellowTextField.getText().isEmpty()) {
                    removeMarkers('y', yellowTextField);
                } else if (!orangeTextField.getText().isEmpty()) {
                    removeMarkers('o', orangeTextField);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a number in one of the color's text fields.");
                }
            } else if (e.getSource() == replayButton) {
                resetGame();
            }

            else if (e.getSource() == quitButton) {
                quitTournament(); // Call quitTournament() when the Quit button is pressed
            }
        }
    }
    // Attempts to remove markers from the specified pile. Validates the input and updates the game state accordingly.
    private void removeMarkers(char color, JTextField textField) {
        try {
            int number = Integer.parseInt(textField.getText());
            if (makeMove(color, number)) {
                updateStatusLabel();
                if (checkGameEnd()) {
                    return; // Game ended, no need to switch turns
                }
                playerTurn = false; // Switch turns
                solveNim(); // Trigger computer's turn
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
        }
        textField.setText(""); // Clear the input field
    }

    // Executes a move by removing a specified number of markers from a pile. Returns true if the move is valid.
    private boolean makeMove(char color, int number) {
        switch (color) {
            case 'g':
                if (number > 0 && number <= greenMarkers) {
                    greenMarkers -= number;
                    greenButton.setText("Green (" + greenMarkers + ")");
                    updateStatusLabel();
                    return true;
                }
                break;
            case 'y':
                if (number > 0 && number <= yellowMarkers) {
                    yellowMarkers -= number;
                    yellowButton.setText("Yellow (" + yellowMarkers + ")");
                    updateStatusLabel();
                    return true;
                }
                break;
            case 'o':
                if (number > 0 && number <= orangeMarkers) {
                    orangeMarkers -= number;
                    orangeButton.setText("Orange (" + orangeMarkers + ")");
                    updateStatusLabel();
                    return true;
                }
                break;
        }
        JOptionPane.showMessageDialog(null, "Invalid move. Please enter a valid number of markers.");
        return false;
    }
    //  Checks if the game has ended (no markers left) and declares the winner.
    private boolean checkGameEnd() {
        if (greenMarkers + yellowMarkers + orangeMarkers == 0) {
            // The game ends when there are no markers left.
            greenTextField.setText("");
            yellowTextField.setText("");
            orangeTextField.setText("");
            String message;
            String winner;
            if (!playerTurn) {
                // Computer wins
                winner = "Computer";
                computerScore++;
                computerScoreLabel.setText("Computer Score: " + computerScore);

                // Sad message for player loss
                message = "Hawwwww :((( You lost.";
                UIManager.put("OptionPane.background", new Color(255, 105, 97)); // Light red
                UIManager.put("Panel.background", new Color(255, 105, 97));
                JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Player wins
                winner = "Player";
                playerScore++;
                playerScoreLabel.setText("Player Score: " + playerScore);

                // Celebration message for player win
                message = "Hurrah! You won! 🎉 Yipeeeeeeee";
                UIManager.put("OptionPane.background", new Color(135, 206, 250)); // Light sky blue
                UIManager.put("Panel.background", new Color(135, 206, 250));
                JOptionPane.showMessageDialog(null, message, "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            }

            // Reset UIManager properties to default
            UIManager.put("OptionPane.background", null);
            UIManager.put("Panel.background", null);

            return true;
        }
        return false;
    }




    // Contains the logic for the computer's strategy to make a winning move or a random move if no winning move is available.
    private void solveNim() {
        int xorSum = greenMarkers ^ yellowMarkers ^ orangeMarkers;

        if (xorSum != 0) {
            makeWinningMove(xorSum);
        } else {
            makeRandomMove();
        }

        playerTurn = true; // It's now the player's turn
    }
    // Executes a random move by the computer when it cannot force a win.
    private void makeRandomMove() {
        char[] colors = new char[]{'g', 'y', 'o'};
        int[] markers = new int[]{greenMarkers, yellowMarkers, orangeMarkers};
        int pile;

        do {
            pile = random.nextInt(markers.length);
        } while (markers[pile] == 0);

        int removeCount = 1 + random.nextInt(markers[pile]);
        makeMove(colors[pile], removeCount);
        statusLabel.setText("Computer removed " + removeCount + " from the " + colorName(colors[pile]) + " pile.");
        checkGameEnd(); // Check if the game has ended after the computer's move
    }

    private String colorName(char color) {
        return switch (color) {
            case 'g' -> "green";
            case 'y' -> "yellow";
            case 'o' -> "orange";
            default -> "unknown";
        };
    }
    //
    private void makeWinningMove(int xorSum) {
        int[] markers = new int[]{greenMarkers, yellowMarkers, orangeMarkers};
        char[] colors = new char[]{'g', 'y', 'o'};

        for (int i = 0; i < markers.length; i++) {
            int desiredValue = markers[i] ^ xorSum;
            if (desiredValue < markers[i]) {
                int removeCount = markers[i] - desiredValue;
                makeMove(colors[i], removeCount);
                statusLabel.setText("Computer removed " + removeCount + " from the " + colorName(colors[i]) + " pile.");
                checkGameEnd(); // Check if the game has ended after the computer's move
                break;
            }
        }
    }
    // Updates the status label to reflect the current state of the game.
    private void updateStatusLabel() {
        statusLabel.setText("Current markers - Green: " + greenMarkers + ", Yellow: " + yellowMarkers + ", Orange: " + orangeMarkers);
    }
    // Updates the score based on the winner of a game round.
    private void updateScore(String winner) {
        if (winner.equals("Player")) {
            playerScore++;
            playerScoreLabel.setText("Player Score: " + playerScore);
        } else {
            computerScore++;
            computerScoreLabel.setText("Computer Score: " + computerScore);
        }
    }
    // Resets the game to its initial state for a new round.
    private void resetGame() {
        greenMarkers = 3;
        yellowMarkers = 7;
        orangeMarkers = 5;
        greenButton.setText("Green (" + greenMarkers + ")");
        yellowButton.setText("Yellow (" + yellowMarkers + ")");
        orangeButton.setText("Orange (" + orangeMarkers + ")");
        updateStatusLabel();
        playerTurn = true;
        askWhoGoesFirst();
    }

    // Determines the tournament winner based on scores and exits the game.
    private void quitTournament() {
        String winner;
        if (computerScore > playerScore) {
            winner = "Computer";
        } else if (playerScore > computerScore) {
            winner = "Player";
        } else {
            winner = "Tournament tied";
        }
        JOptionPane.showMessageDialog(null, "Tournament ended. Winner: " + winner);
        System.exit(0);
    }

    // The entry point of the application. It ensures the game's GUI is created on the Event Dispatch Thread for thread safety.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Nim::new);
    }
}