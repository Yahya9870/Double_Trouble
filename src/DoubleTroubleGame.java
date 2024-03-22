import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class DoubleTroubleGame extends JFrame {
    private int greenMarkers = 3, yellowMarkers = 7, orangeMarkers = 5;
    private int playerWins = 0, computerWins = 0;
    private final Random random = new Random();
    private JButton greenButton, yellowButton, orangeButton, newGameButton;
    private JButton quitButton;

    private JLabel statusLabel, scoreLabel;
    private boolean playerTurn;
    private final int roundsNeededToWin;

    public DoubleTroubleGame(int bestOfRounds) {
        roundsNeededToWin = (bestOfRounds / 2) + 1;
        setupGUI();
        resetGame();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); // Set the size of the window
        setTitle("Double Trouble Tournament"); // Set the title of the window
        setLayout(new FlowLayout()); // Use a FlowLayout for simplicity

        // Initialize buttons for marker piles with initial counts and colors
        greenButton = createButton("Green: " + greenMarkers, Color.GREEN);
        yellowButton = createButton("Yellow: " + yellowMarkers, Color.YELLOW);
        orangeButton = createButton("Orange: " + orangeMarkers, Color.ORANGE);
        greenButton.addActionListener(e -> initiatePlayerMove('G'));
        yellowButton.addActionListener(e -> initiatePlayerMove('Y'));
        orangeButton.addActionListener(e -> initiatePlayerMove('O'));


        // Button to start a new tournament, initially disabled until needed
        newGameButton = new JButton("Start New Tournament");
        newGameButton.addActionListener(e -> startNewTournament());
        newGameButton.setEnabled(false); // Disabled by default, enabled after a tournament is completed

        // Status label for messages like "Player's turn" or "Computer wins!"
        statusLabel = new JLabel("Welcome to Double Trouble! Press 'Start New Tournament' to begin.", SwingConstants.CENTER);

        // Score label for displaying the current tournament scores
        scoreLabel = new JLabel("Player Wins: 0, Computer Wins: 0", SwingConstants.CENTER);

        // Add all components to the JFrame's content pane
        add(greenButton);
        add(yellowButton);
        add(orangeButton);
        add(newGameButton);
        add(statusLabel);
        add(scoreLabel);
        // Initialize the Quit button and its action listener
        quitButton = new JButton("Quit Tournament");
        quitButton.addActionListener(e -> System.exit(0));
        add(quitButton); // Add the Quit button to the frame


        // Ensure the window is correctly sized and components are visible
        pack();
        setLocationRelativeTo(null); // Center the window on the screen
    }

    private void startNewTournament() {
        // Reset scores for a new tournament
        playerWins = 0;
        computerWins = 0;

        // Reset the markers in each pile to their initial values
        resetGame();

        // Optionally ask who starts the new game
        determineFirstTurn();

        // Update the GUI components to reflect the reset state
        updateGUI();

        // Enable the marker buttons for gameplay
        enableGameButtons(true);

        // The new game button should be disabled now as the tournament has started
        newGameButton.setEnabled(false);

        // Set a status message indicating the start of the tournament or who's turn it is
        if(playerTurn) {
            statusLabel.setText("New tournament started. Player's turn.");
        } else {
            statusLabel.setText("New tournament started. Computer's turn.");
            SwingUtilities.invokeLater(this::computerTurn);
        }
    }

    private void initiatePlayerMove(char color) {
        // Ask the player for the number of markers they want to remove
        String input = JOptionPane.showInputDialog(this, "How many markers do you want to remove from " + color + " pile?");
        try {
            int number = Integer.parseInt(input); // Convert the input to an integer
            if (number < 1) {
                // If the number is not positive, show an error message
                JOptionPane.showMessageDialog(this, "Please enter a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Now, execute the player's move with the specified color and number
            playerMove(color, number);
        } catch (NumberFormatException ex) {
            // If the input is not an integer, show an error message
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false); // For buttons to appear fully colored without a border
        return button;
    }


    private void resetGame() {
        // Reset marker counts to their initial values
        greenMarkers = 3;
        yellowMarkers = 7;
        orangeMarkers = 5;

        // Reset scores
        playerWins = 0;
        computerWins = 0;

        // Update the GUI to reflect the reset state
        updateGUI();

        // Optionally, decide who goes first in the new game or tournament
        determineFirstTurn();

        // Enable the game buttons for a new start
        enableGameButtons(true);

        // Disable the "Start New Tournament" button until the current game/tournament is completed
        newGameButton.setEnabled(false);

        // Set the initial status message
        statusLabel.setText("Welcome to Double Trouble! Who starts?");
    }

    private void updateGUI() {
        // Update buttons with the initial marker counts
        greenButton.setText("Green: " + greenMarkers);
        yellowButton.setText("Yellow: " + yellowMarkers);
        orangeButton.setText("Orange: " + orangeMarkers);

        // Update the score label
        scoreLabel.setText("Player Wins: " + playerWins + ", Computer Wins: " + computerWins);

        // Repaint the frame to ensure updates are visible
        repaint();
    }

    private void enableGameButtons(boolean enable) {
        // Enable or disable game buttons based on the boolean parameter
        greenButton.setEnabled(enable);
        yellowButton.setEnabled(enable);
        orangeButton.setEnabled(enable);
    }

    private void determineFirstTurn() {
        // This method asks the player if they want to go first and sets the playerTurn accordingly
        int response = JOptionPane.showConfirmDialog(this, "Do you want to go first?", "Choose Starting Player", JOptionPane.YES_NO_OPTION);
        playerTurn = response == JOptionPane.YES_OPTION;

        if (!playerTurn) {
            // If the player chooses not to start, immediately trigger the computer's turn
            computerTurn();
        } else {
            // Update the status label to indicate it's the player's turn
            statusLabel.setText("Player's turn. Choose a pile and number of markers to remove.");
        }
    }

    private void computerTurn() {
        int xorSum = greenMarkers ^ yellowMarkers ^ orangeMarkers;

        // Check if there is a winning move available
        if (xorSum != 0) {
            // Find and make a winning move
            makeSmartMove(xorSum);
        } else {
            // No winning move available, make a random move
            makeRandomMove();
        }

        updateGUI(); // Update the GUI with the new state
        checkForWin(); // Check if this move wins the game
    }

    private void makeSmartMove(int xorSum) {
        // Attempt to make a move that results in a zero XOR sum
        if (greenMarkers > 0 && ((greenMarkers ^ xorSum) < greenMarkers)) {
            int removeCount = greenMarkers - (greenMarkers ^ xorSum);
            greenMarkers -= removeCount;
            JOptionPane.showMessageDialog(this, "Computer removed " + removeCount + " from the green pile.");
        } else if (yellowMarkers > 0 && ((yellowMarkers ^ xorSum) < yellowMarkers)) {
            int removeCount = yellowMarkers - (yellowMarkers ^ xorSum);
            yellowMarkers -= removeCount;
            JOptionPane.showMessageDialog(this, "Computer removed " + removeCount + " from the yellow pile.");
        } else if (orangeMarkers > 0 && ((orangeMarkers ^ xorSum) < orangeMarkers)) {
            int removeCount = orangeMarkers - (orangeMarkers ^ xorSum);
            orangeMarkers -= removeCount;
            JOptionPane.showMessageDialog(this, "Computer removed " + removeCount + " from the orange pile.");
        } else {
            makeRandomMove(); // Fallback if no direct smart move is found
        }
    }

    private void makeRandomMove() {
        // Make a random move if no winning move is available
        int pile = random.nextInt(3); // Randomly select a pile
        int removeCount = 1; // Simplify to always remove 1 marker for the example

        switch (pile) {
            case 0:
                if (greenMarkers > 0) {
                    greenMarkers -= removeCount;
                    statusLabel.setText("Computer removed 1 from Green");
                }
                break;
            case 1:
                if (yellowMarkers > 0) {
                    yellowMarkers -= removeCount;
                    statusLabel.setText("Computer removed 1 from Yellow");
                }
                break;
            case 2:
                if (orangeMarkers > 0) {
                    orangeMarkers -= removeCount;
                    statusLabel.setText("Computer removed 1 from Orange");
                }
                break;
        }
    }

    private void playerMove(char color, int number) {
        boolean validMove = false;

        // Check if the move is valid based on the selected color and number
        switch (color) {
            case 'G': // Player chose Green
                if (number > 0 && number <= greenMarkers) {
                    greenMarkers -= number;
                    validMove = true;
                    statusLabel.setText("Player removed " + number + " from Green");
                }
                break;
            case 'Y': // Player chose Yellow
                if (number > 0 && number <= yellowMarkers) {
                    yellowMarkers -= number;
                    validMove = true;
                    statusLabel.setText("Player removed " + number + " from Yellow");
                }
                break;
            case 'O': // Player chose Orange
                if (number > 0 && number <= orangeMarkers) {
                    orangeMarkers -= number;
                    validMove = true;
                    statusLabel.setText("Player removed " + number + " from Orange");
                }
                break;
        }

        if (!validMove) {
            // If the move is not valid, show a message and do not switch turns
            JOptionPane.showMessageDialog(this, "Invalid move. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Move is valid, update the GUI to reflect the new state
        updateGUI();

        // After the player's move, check for a win before switching to the computer's turn
        if (!checkForWin()) {
            // No win yet, proceed with the computer's turn
            SwingUtilities.invokeLater(this::computerTurn);
        }
    }

    private boolean checkForWin() {
        if (greenMarkers == 0 && yellowMarkers == 0 && orangeMarkers == 0) {
            // Determine the winner based on who made the last move
            if (!playerTurn) { // Since computerTurn method sets playerTurn to true at the end, this checks for computer's win
                computerWins++;
                JOptionPane.showMessageDialog(this, "Computer wins the round!");
            } else {
                playerWins++;
                JOptionPane.showMessageDialog(this, "Player wins the round!");
            }

            updateScoreLabel(); // Update scores display

            if (playerWins == roundsNeededToWin || computerWins == roundsNeededToWin) {
                String winner = playerWins == roundsNeededToWin ? "Player" : "Computer";
                JOptionPane.showMessageDialog(this, winner + " wins the tournament!");
                prepareForNewTournament();
            } else {
                resetGame(); // Reset for a new round
            }

            // Toggle the turn AFTER win checking
            playerTurn = !playerTurn;

            return true;
        }
        return false;
    }

    private void updateScoreLabel() {
        // Update the text to include matches won
        scoreLabel.setText(String.format("Player Wins: %d, Computer Wins: %d", playerWins, computerWins));
        System.out.println("Updating score label: Player Wins: " + playerWins + ", Computer Wins: " + computerWins);
    }

    private void prepareForNewTournament() {
        // Reset scores and markers for a new tournament
        playerWins = 0;
        computerWins = 0;
        resetGame(); // Resets the game for the next round or tournament
        updateScoreLabel(); // Update the score display after resetting scores
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoubleTroubleGame(3).setVisible(true));
    }
}
