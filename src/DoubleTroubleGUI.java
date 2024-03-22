import java.util.Random;
import java.util.Scanner;

public class DoubleTroubleGUI {
    private int greenMarkers = 3;
    private int yellowMarkers = 7;
    private int orangeMarkers = 5;
    private boolean playerTurn = true;
    private final Random random = new Random();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DoubleTroubleGUI game = new DoubleTroubleGUI();
        game.startGame();
    }

    private void startGame() {
        System.out.println("Welcome to Double Trouble Game!");
        while (!isGameOver()) {
            displayGameState();
            if (playerTurn) {
                playerTurn();
            } else {
                computerTurn();
            }
            playerTurn = !playerTurn;
        }
        declareWinner();
    }

    private void playerTurn() {
        System.out.println("Your turn. Choose a pile (G for Green, Y for Yellow, O for Orange) and the number to remove:");
        boolean moveMade = false;
        while (!moveMade) {
            try {
                String input = scanner.nextLine().toUpperCase();
                if (input.matches("[GYO]\\s+\\d+")) {
                    String[] parts = input.split("\\s+");
                    char pile = parts[0].charAt(0);
                    int numberToRemove = Integer.parseInt(parts[1]);
                    moveMade = attemptMove(pile, numberToRemove);
                    if (!moveMade) {
                        System.out.println("Invalid move, try again:");
                    }
                } else {
                    System.out.println("Invalid input format, try again:");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, try again:");
            }
        }
    }

    private void computerTurn() {
        System.out.println("Computer's turn...");
        int xorSum = greenMarkers ^ yellowMarkers ^ orangeMarkers;
        if (xorSum == 0) {
            randomMove();
        } else {
            strategicMove(xorSum);
        }
    }

    private boolean isGameOver() {
        return greenMarkers == 0 && yellowMarkers == 0 && orangeMarkers == 0;
    }

    private void displayGameState() {
        System.out.printf("Green markers: %d, Yellow markers: %d, Orange markers: %d%n", greenMarkers, yellowMarkers, orangeMarkers);
    }

    private void declareWinner() {
        if (playerTurn) {
            System.out.println("Congratulations! You won!");
        } else {
            System.out.println("Computer wins. Better luck next time!");
        }
    }

    private void randomMove() {
        int pile = random.nextInt(3);
        int numberToRemove;
        switch (pile) {
            case 0:
                if (greenMarkers > 0) {
                    numberToRemove = 1 + random.nextInt(greenMarkers);
                    greenMarkers -= numberToRemove;
                    System.out.println("Computer takes " + numberToRemove + " from Green.");
                }
                break;
            case 1:
                if (yellowMarkers > 0) {
                    numberToRemove = 1 + random.nextInt(yellowMarkers);
                    yellowMarkers -= numberToRemove;
                    System.out.println("Computer takes " + numberToRemove + " from Yellow.");
                }
                break;
            case 2:
                if (orangeMarkers > 0) {
                    numberToRemove = 1 + random.nextInt(orangeMarkers);
                    orangeMarkers -= numberToRemove;
                    System.out.println("Computer takes " + numberToRemove + " from Orange.");
                }
                break;
        }
    }

    private void strategicMove(int xorSum) {
        if ((xorSum ^ greenMarkers) < greenMarkers) {
            int takeFromGreen = greenMarkers - (xorSum ^ greenMarkers);
            greenMarkers -= takeFromGreen;
            System.out.println("Computer takes " + takeFromGreen + " from Green strategically.");
        } else if ((xorSum ^ yellowMarkers) < yellowMarkers) {
            int takeFromYellow = yellowMarkers - (xorSum ^ yellowMarkers);
            yellowMarkers -= takeFromYellow;
            System.out.println("Computer takes " + takeFromYellow + " from Yellow strategically.");
        } else if ((xorSum ^ orangeMarkers) < orangeMarkers) {
            int takeFromOrange = orangeMarkers - (xorSum ^ orangeMarkers);
            orangeMarkers -= takeFromOrange;
            System.out.println("Computer takes " + takeFromOrange + " from Orange strategically.");
        }
    }

    private boolean attemptMove(char pile, int numberToRemove) {
        switch (pile) {
            case 'G':
                if (numberToRemove > 0 && numberToRemove <= greenMarkers) {
                    greenMarkers -= numberToRemove;
                    System.out.println("You take " + numberToRemove + " from Green.");
                    return true;
                }
                break;
            case 'Y':
                if (numberToRemove > 0 && numberToRemove <= yellowMarkers) {
                    yellowMarkers -= numberToRemove;
                    System.out.println("You take " + numberToRemove + " from Yellow.");
                    return true;
                }
                break;
            case 'O':
                if (numberToRemove > 0 && numberToRemove <= orangeMarkers) {
                    orangeMarkers -= numberToRemove;
                    System.out.println("You take " + numberToRemove + " from Orange.");
                    return true;
                }
                break;
        }
        return false;
    }
}
