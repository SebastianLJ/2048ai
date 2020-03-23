package ai;

import com.mzherdev.Cell;
import com.mzherdev.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AILauncher {

    private Game gameLogic;

    public AILauncher() {
        gameLogic = new Game();
    }

    private void helper(State s) {
        int optimal = hMiniMax(s, 0);
        System.out.println("Best Action is to go " + s.getMove() + " and the optimal value is " + optimal);
    }

    @SuppressWarnings("Duplicates")
    private int hMiniMax(State s, int d) {
        //System.out.println("current state: " + Arrays.toString(s.getCells()));
        if (cutoffTest(s, d)) {
            return eval(s);
        }

        List<String> actions = actions(s, d);

        //System.out.println("depth=" + d + " actions: " + Arrays.toString(actions));

        int searchValue;
        State resultingState;
        if (player(d)) {
            int max = Integer.MIN_VALUE;
            for (String action : actions) {
                resultingState = result(s, action);
                searchValue = hMiniMax(resultingState, d + 1);
                if (searchValue > max) {
                    max = searchValue;
                    s.setMove(action);
                }
            }
            //System.out.println("Maximizer: " + max);
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (String action : actions) {
                resultingState = result(s, action);
                searchValue = hMiniMax(resultingState, d + 1);
                if (searchValue < min) {
                    min = searchValue;
                    s.setMove(action);
                }
            }
            //System.out.println("Minimizer: " + min);
            return min;
        }
    }

    /**
     * Find out who's turn it is in a given state.
     * @param state the current game state containing (game, move)
     * @return 0 player, 1 nature
     */
    private boolean player(State state) {
        return state.getMove() == null;
    }

    private boolean player(int depth) {
        return depth%2 == 0;
    }

    private List<String> actions(State state, int depth) {
        if (player(depth)) {
            List<String> availableMoves = new ArrayList<>();
            String[] allMoves = {"left", "right", "up", "down"};

            for (String move : allMoves) {
                gameLogic.setCells(Arrays.copyOf(state.getCells(),16));

                switch (move) {
                    case "left":
                        if (gameLogic.left()) availableMoves.add(move);
                        break;
                    case "right":
                        if (gameLogic.right()) availableMoves.add(move);
                        break;
                    case "up":
                        if (gameLogic.up()) availableMoves.add(move);
                        break;
                    case "down":
                        if (gameLogic.down()) availableMoves.add(move);
                        break;
                }
            }

            return availableMoves;

        } else {
            gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
            return Arrays.asList(gameLogic.availableSpacesToString());
        }
    }

    private State result(State state, String action) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        switch (action) {
            case "left":
                gameLogic.left();
                break;
            case "right":
                gameLogic.right();
                break;
            case "up":
                gameLogic.up();
                break;
            case "down":
                gameLogic.down();
                break;
            default:
                gameLogic.spawnCell(action);
                break;
        }
        return new State(gameLogic.getCells(), action);
    }

    private boolean cutoffTest(State state, int d) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.winningState() || d > 5;
    }

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.availableSpace().size();
    }*/

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculateLineWithMostPoints();
    }*/

    private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculateLineWithMostPoints()*gameLogic.availableSpace().size();
    }

    public String getNextMove(Game game) {
        Cell[] cells = Arrays.copyOf(game.getCells(), 16);
        State s0 = new State(cells, "");
        helper(s0);
        return s0.getMove();
    }
}
