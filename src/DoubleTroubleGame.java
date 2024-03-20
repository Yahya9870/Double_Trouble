import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class DoubleTroubleGame extends JFrame {
    private int greenMarkers = 3, yellowMarkers = 7, orangeMarkers = 5;
    private final JLabel statusLabel = new JLabel("Choose who starts: Player or Computer", SwingConstants.CENTER);
    private final JButton[] buttons = new JButton[3];
    private boolean playerTurn = true; // This flag determines whose turn it is.
    private final Random random = new Random();

    public DoubleTroubleGame() {
        setTitle("Double Trouble");
        setLayout(new BorderLayout());
        initializeGamePanel();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
        chooseStarter();
    }

    private void initializeGamePanel() {
        JPanel gamePanel = new JPanel(new GridLayout(1, 3, 10, 0));

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setPreferredSize(new Dimension(120, 80));
            buttons[i].setFont(new Font("Arial", Font.BOLD, 16));
            buttons[i].setOpaque(true);
            buttons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            int colorIndex = i;
            buttons[i].addActionListener(e -> playerMove(colorIndex));
            gamePanel.add(buttons[i]);
        }

        updateButtonColors();

        add(statusLabel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
    }

    private void updateButtonColors() {
        buttons[0].setText("Green: " + greenMarkers);
        buttons[0].setBackground(Color.GREEN);
        buttons[0].setForeground(Color.BLACK);

        buttons[1].setText("Yellow: " + yellowMarkers);
        buttons[1].setBackground(Color.YELLOW);
        buttons[1].setForeground(Color.BLACK);

        buttons[2].setText("Orange: " + orangeMarkers);
        buttons[2].setBackground(Color.ORANGE);
        buttons[2].setForeground(Color.BLACK);
    }

    private void chooseStarter() {
        int response = JOptionPane.showOptionDialog(this, "Who should start the game?",
                "Choose Starter", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"Player", "Computer"}, "Player");
        playerTurn = response == JOptionPane.YES_OPTION;
        if (!playerTurn) {
            computerMove();
        } else {
            updateStatus();
        }
    }

    private void playerMove(int color) {
        if (!playerTurn) return;

        // Prompt the player to enter the number of markers to remove
        int removeCount;
        try {
            removeCount = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter the number of markers to remove:"));
            // Check if the input is valid
            if (removeCount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number greater than 0.");
                return;
            }

            // Check if the player is attempting to remove more markers than available in the chosen pile
            int currentMarkers = color == 0 ? greenMarkers : (color == 1 ? yellowMarkers : orangeMarkers);
            if (removeCount > currentMarkers) {
                JOptionPane.showMessageDialog(this, "Cannot remove more markers than available in the chosen pile.");
                return;
            }

            // Update the number of markers based on the player's move
            makeMove(color == 0 ? 'g' : (color == 1 ? 'y' : 'o'), removeCount);

            // Update the buttons and check for game over
            updateButtons();
            playerTurn = false;
            if (!isGameOver()) {
                computerMove();
            } else {
                JOptionPane.showMessageDialog(this, "You win! Congratulations!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void computerMove() {
        int xorSum = greenMarkers ^ yellowMarkers ^ orangeMarkers;

        if (xorSum != 0) {
            int[] markers = new int[]{greenMarkers, yellowMarkers, orangeMarkers};
            char[] colors = new char[]{'g', 'y', 'o'};

            for (int i = 0; i < markers.length; i++) {
                int desiredValue = markers[i] ^ xorSum;
                if (desiredValue < markers[i]) {
                    int removeCount = markers[i] - desiredValue;
                    makeMove(colors[i], removeCount);
                    break;
                }
            }
        } else {
            // Play random move
            makeRandomMove();
        }

        updateButtons();
        playerTurn = true;
        if (isGameOver()) {
            JOptionPane.showMessageDialog(this, "Computer wins! Better luck next time!");
        } else {
            updateStatus();
        }
    }

    private void makeMove(char color, int number) {
        switch (color) {
            case 'g':
                greenMarkers -= number;
                break;
            case 'y':
                yellowMarkers -= number;
                break;
            case 'o':
                orangeMarkers -= number;
                break;
        }
    }

    private void makeRandomMove() {
        char[] colors = new char[]{'g', 'y', 'o'};
        int[] markers = new int[]{greenMarkers, yellowMarkers, orangeMarkers};
        int pile, removeCount;

        do {
            pile = random.nextInt(markers.length);
        } while (markers[pile] == 0); // Ensure we don't pick an empty pile

        removeCount = 1 + random.nextInt(markers[pile]); // Take at least one marker
        makeMove(colors[pile], removeCount);
    }

    private void updateButtons() {
        updateButtonColors();
        if (isGameOver()) {
            for (JButton button : buttons) {
                button.setEnabled(false);
            }
            statusLabel.setText("Game Over.");
        }
    }

    private boolean isGameOver() {
        return greenMarkers == 0 && yellowMarkers == 0 && orangeMarkers == 0;
    }

    private void updateStatus() {
        if (playerTurn) {
            statusLabel.setText("Your turn: Choose a color to remove markers from.");
        } else {
            statusLabel.setText("Computer's turn...");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoubleTroubleGame::new);
    }
}
