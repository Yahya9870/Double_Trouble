import java.util.Random;
import java.util.Scanner;

public class Double_Trouble {

    private int greenMarkers = 3;
    private int yellowMarkers = 7;
    private int orangeMarkers = 5;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);

    public void startGame() {
        System.out.println("Welcome to Double Trouble!");
        System.out.print("Do you want to go first? (yes/no): ");
        boolean playerTurn = scanner.nextLine().trim().equalsIgnoreCase("yes");

        while (greenMarkers > 0 || yellowMarkers > 0 || orangeMarkers > 0) {
            System.out.println("Current markers - Green: " + greenMarkers + ", Yellow: " + yellowMarkers + ", Orange: " + orangeMarkers);

            if (playerTurn) {
                playerTurn();
            } else {
                computerTurn();
            }

            playerTurn = !playerTurn; // Switch turns

            if (greenMarkers + yellowMarkers + orangeMarkers == 0) {
                String winner = playerTurn ? "Computer" : "Player";
                System.out.println(winner + " wins!");
                break;
            }
        }

        scanner.close();
    }

    private void playerTurn() {
        int number;
        char colorChoice;
        boolean validMove = false;
        do {
            try {
                System.out.println("Your turn. Choose a color (g for green, y for yellow, o for orange) and number of markers to pick:");
                colorChoice = scanner.next().charAt(0);
                number = scanner.nextInt();
                validMove = makeMove(colorChoice, number);
                scanner.nextLine(); // Consume the newline character
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid color and number of markers.");
                scanner.nextLine(); // Clear the scanner buffer
            }
        } while (!validMove);
    }

    private void computerTurn() {
        System.out.println("Computer's turn.");
        int xorSum = greenMarkers ^ yellowMarkers ^ orangeMarkers;

        if (xorSum != 0) {
            // Play winning move
            makeWinningMove(xorSum);
        } else {
            // Play random move
            makeRandomMove();
        }
    }

    private boolean makeMove(char color, int number) {
        switch (color) {
            case 'g':
                if (number > 0 && number <= greenMarkers) {
                    greenMarkers -= number;
                    return true;
                }
                break;
            case 'y':
                if (number > 0 && number <= yellowMarkers) {
                    yellowMarkers -= number;
                    return true;
                }
                break;
            case 'o':
                if (number > 0 && number <= orangeMarkers) {
                    orangeMarkers -= number;
                    return true;
                }
                break;
            default:
                System.out.println("Invalid color choice. Please choose g, y, or o.");
                return false;
        }
        System.out.println("Invalid number of markers. Please choose a number greater than 0 and less than or equal to the number of available markers.");
        return false;
    }

    private void makeWinningMove(int xorSum) {
        int[] markers = new int[]{greenMarkers, yellowMarkers, orangeMarkers};
        char[] colors = new char[]{'g', 'y', 'o'};

        for (int i = 0; i < markers.length; i++) {
            int desiredValue = markers[i] ^ xorSum;
            if (desiredValue < markers[i]) {
                int removeCount = markers[i] - desiredValue;
                makeMove(colors[i], removeCount);
                System.out.println("Computer took " + removeCount + " from the " + colorName(colors[i]) + " pile.");
                break;
            }
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
        System.out.println("Computer took " + removeCount + " from the " + colorName(colors[pile]) + " pile.");
    }

    private String colorName(char color) {
        switch (color) {
            case 'g': return "green";
            case 'y': return "yellow";
            case 'o': return "orange";
            default: return "unknown";
        }
    }

    public static void main(String[] args) {
        new Double_Trouble().startGame();
    }
}
