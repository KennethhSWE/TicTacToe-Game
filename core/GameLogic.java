package core;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List; 
import java.util.ArrayList; 

/**
 * Game logic for Snakes and Ladders
 * Author: Kenneth Hayes
 * 10/07/2025
 */

public class GameLogic {

    private static final int GRID_SIZE = 10; // 10x10 grid
    private static final int FINISH_SQUARE = GRID_SIZE * GRID_SIZE; // 100 is the finish square

    private final Player[] bench = new Player[2]; // index 0 for player 1 and index 1 for player 2
    private int turnOwner = 0; // whos turn is it.
    private int roundCount = 1; // counts the number of rounds played
    private boolean requireExactFinish = true; // stop game at 100

    private final Map<Integer, Integer> snakes;
    private final Map<Integer, Integer> ladders;

    public interface Dice {
        int roll();
    }

    public static final class RandomDice implements Dice {
        private final Random rng = new Random();

        @Override
        public int roll() {
            return rng.nextInt(6) + 1; // returns a number between 1 and 6
        }
    }

    private final Dice die;

    public GameLogic() {
        this.die = new RandomDice();
        bench[0] = new Player("Player 1");
        bench[1] = new Player("Player 2");
        this.snakes = initSnakes();
        this.ladders = initLadders();
    }

    public GameLogic(Dice die) {
        this.die = die;
        bench[0] = new Player("Player 1");
        bench[1] = new Player("Player 2");
        this.snakes = initSnakes();
        this.ladders = initLadders();
    }

    // new game logic to allow Player 2 to be a CPU player
    public GameLogic(boolean p2IsComputer) {
        this.die = new RandomDice();
        bench[0] = new Player("Player 1");
        bench[1] = p2IsComputer ? new ComputerPlayer("Player 2 CPU") : new Player("Player 2");
        this.snakes = initSnakes();
        this.ladders = initLadders(); 
    }

    public GameLogic(Dice die, boolean p2IsComputer) {
        this.die = die; 
        bench[0] = new Player("Player 1");
        bench[1] = p2IsComputer ? new ComputerPlayer("Player 2 CPU") : new Player("Player 2"); 
        this.snakes = initSnakes();
        this.ladders = initLadders(); 
    }

    // helper to know when CPU turn is
    public boolean isComputerTurn() {
        return bench[turnOwner] instanceof ComputerPlayer; 
    }

    public void setRequireExactFinish(boolean requireExactFinish) {
        this.requireExactFinish = requireExactFinish;
    }

    public Player getCurrentPlayer() {
        return bench[turnOwner];
    }

    public Player[] getPlayers() {
        return bench;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getFinishSquare() {
        return FINISH_SQUARE;
    }

    public int getGridSize() {
        return GRID_SIZE;
    }

    public int rollDie() {
        return die.roll();
    }

    public Map<Integer, Integer> getSnakes() {
        return snakes;
    }

    public Map<Integer, Integer> getLadders() {
        return ladders;
    }

    public void moveCurrentPlayer(int steps) {
        Player p = getCurrentPlayer();
        int pos = p.getPosition();
        int target = pos + steps;

        if (requireExactFinish) {
            if (target <= FINISH_SQUARE) {
                target = applyJumpIfAny(target);
                p.setPosition(target);
            }
        } else {
            if (target > FINISH_SQUARE) {
                target = FINISH_SQUARE;
            }
            target = applyJumpIfAny(target); // overshoot
            p.setPosition(target);
        }
    }

    public boolean hasWinner() {
        for (Player p : bench) {
            if (p.getPosition() >= FINISH_SQUARE)
                return true;
        }
        return false;
    }

    public Player getWinner() {
        for (Player p : bench) {
            if (p.getPosition() >= FINISH_SQUARE)
                return p;
        }
        return null;
    }

    public void nextTurn() {
        turnOwner = (turnOwner + 1) % bench.length;
        if (turnOwner == 0) {
            roundCount++;
        }
    }

    public Map<Integer, Integer> initSnakes() {
        Map<Integer, Integer> s = new HashMap<>();
        s.put(99, 80);
        s.put(74, 53);
        s.put(62, 19);
        s.put(37, 17);
        return Collections.unmodifiableMap(s);
    }

    private Map<Integer, Integer> initLadders() {
        Map<Integer, Integer> l = new HashMap<>();
        l.put(3, 22);
        l.put(8, 26);
        l.put(20, 41);
        l.put(28, 55);
        l.put(36, 44);
        l.put(51, 67);
        l.put(71, 91);
        return Collections.unmodifiableMap(l);
    }

    // helper to allow jumps after movement
    private int applyJumpIfAny(int square) {
        if (ladders.containsKey(square)) {
            return ladders.get(square);
        }

        if (snakes.containsKey(square)) {
            return snakes.get(square);
        }

        return square;
    }

    public List<String> renderBoard() {
        List<String> lines = new ArrayList<>();

       Map<Integer, String> labelAt = new HashMap<>();

        int ladderCount = 0;
        int snakeCount = 0;

        for (int sq = 1; sq <= FINISH_SQUARE; sq++) {
            if (ladders.containsKey(sq)) {
                ladderCount++;
                int top = ladders.get(sq);
                labelAt.put(sq, "L" + ladderCount); // The base of the ladder
                labelAt.put(top, "H" + ladderCount); // the top of the ladder
            }
            if (snakes.containsKey(sq)) {
                snakeCount++;
                int tail = snakes.get(sq);
                labelAt.put(sq, "S" + snakeCount); // The head of the snake
                labelAt.put(tail, "T" + snakeCount); // The tail of the snake
            }
        }

        Map<Integer, String> pmark = new HashMap<>();
        for (Player p : bench) {
            String tag = p.getName().equalsIgnoreCase("Player 1") ? "P1" : "P2";
            int where = p.getPosition();
            if (pmark.containsKey(where)) {
                pmark.put(where, pmark.get(where) + "&" + tag);
            } else {
                pmark.put(where, tag);
            }
        }

        for (int row = GRID_SIZE - 1; row >= 0; row--) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < GRID_SIZE; col++) {
                int base = row * GRID_SIZE;
                int n = (row % 2 == 0) ? (base + col + 1) : (base + (GRID_SIZE - col));

                String cell;
                if (pmark.containsKey(n)) {
                    cell = String.format("%-3s", pmark.get(n));
                } else if (labelAt.containsKey(n)) {
                    cell = String.format("%-3s", labelAt.get(n));
                } else {
                    cell = String.format("%-3d", n);
                }
                sb.append("|").append(cell);
            }
            sb.append("|");
            lines.add(sb.toString());
        }
        return lines;
    }
}
