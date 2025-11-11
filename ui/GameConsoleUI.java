package ui;

import core.GameLogic;
import core.Player;
import java.util.Scanner;

public class GameConsoleUI {
    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);

        System.out.println("Welcome to Snakes And Ladders!");
        System.out.println("Goal: Land exactly on square 100.\n");

        // Modes
        System.out.println("Select Game Mode:");
        System.out.println("1. Player vs Player");
        System.out.println("2. Player vs Computer");
        System.out.print("Enter choice (1 or 2): ");
        String modeInput = console.nextLine().trim();
        boolean vsCPU = modeInput.startsWith("2");

        GameLogic game = new GameLogic(vsCPU);
        game.setRequireExactFinish(true); // players must land exactly on square 100 to win adds a twist to allow other
                                          // player to catch up or win from behind.

        while (!game.hasWinner()) {
            Player cur = game.getCurrentPlayer();

            for (String line : game.renderBoard()) {
                System.out.println(line);
            }

            System.out.println(); // Add a blank line for spacing

            if (game.isComputerTurn()) {
                System.out.println(cur.getName() + " [pos=" + cur.getPosition() + "] is rolling...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            } else {
                System.out.println(cur.getName() + " [pos=" + cur.getPosition() + "] press Enter to roll...");
                console.nextLine();
            }

            int roll = game.rollDie();
            System.out.println(cur.getName() + " rolled: " + roll);
            game.moveCurrentPlayer(roll);
            System.out.println("now at: " + cur.getPosition() + "\n");

            if (!game.hasWinner()) {
                game.nextTurn();
            }
        }

        Player winner = game.getWinner();
        System.out.println("Winner: " + (winner != null ? winner.getName() : "N/A"));
        console.close();
    }
}
