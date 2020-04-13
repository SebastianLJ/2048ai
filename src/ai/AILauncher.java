package ai;

import com.mzherdev.Cell;
import com.mzherdev.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AILauncher {

    private Game gameLogic;
    private Cell emptyCell;
    private final int DEPTH = 5;

    public AILauncher() {
        gameLogic = new Game();
    }

    private void helper(State s) {
        //int optimal = expectiminimax(s, 0);
        int optimal = hMiniMax(s, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Best Action is to go " + s.getMove() + " and the optimal value is " + optimal);
    }

    @SuppressWarnings("Duplicates")
    private int hMiniMax(State s, int d, int alpha, int beta) {
        if (cutoffTest(s, d)) {
            return evalOuterLinesFreeSpacesMonotonicityMergeability(s);
        }

        List<String> actions = actions(s, d);

        int searchValue;
        State resultingState;
        if (player(d)) {
            int max = Integer.MIN_VALUE;
            for (String action : actions) {
                resultingState = result(s, action);
                searchValue = hMiniMax(resultingState, d + 1, alpha, beta);
                if (searchValue >= max) {
                    s.setMove(action);
                }
                max = Integer.max(max, searchValue);
                alpha = Integer.max(alpha, max);
                if (beta <= alpha) {
                    break;
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (String action : actions) {
                resultingState = result(s, action);
                searchValue = hMiniMax(resultingState, d + 1, alpha, beta);
                min = Integer.min(min, searchValue);
                beta = Integer.min(beta, min);
                if (beta <= alpha) {
                    break;
                }
            }
            return min;
        }
    }

    private boolean player(int depth) {
        return depth % 2 == 0;
    }

    private List<String> actions(State state, int depth) {
        if (player(depth)) {
            List<String> availableMoves = new ArrayList<>();
            String[] allMoves = {"left", "right", "up", "down"};

            for (String move : allMoves) {
                gameLogic.setCells(Arrays.copyOf(state.getCells(), 16));

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
            gameLogic.setCells(Arrays.copyOf(state.getCells(), 16));
            return Arrays.asList(gameLogic.availableSpacesToString());
        }
    }

    private State result(State state, String action) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(), 16));
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
        gameLogic.setCells(Arrays.copyOf(state.getCells(), 16));
        return /*gameLogic.winningState() ||*/ d > DEPTH;
    }

    private int evalScore(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.getScore();
    }

    private int evalFreeSpaces(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.availableSpace().size();
    }

    private int evalScoreFreeSpaces(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return (int) Math.sqrt(gameLogic.getScore())*gameLogic.availableSpace().size();
    }

    private int evalOuterLinesFreeSpaces(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculatePointsOnOuterLines() + 30*gameLogic.availableSpace().size();
    }

    private int evalOuterLinesFreeSpacesMonotonicityMergeability(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculatePointsOnOuterLines() + 200*gameLogic.availableSpace().size() - gameLogic.nonMonotonicPenalty() + gameLogic.mergeability();
    }

    private int evalWeight(State state) {
        int[] weigths = {-40, -38, -35, -30,
                -5, -15, -18, -20,
                5, 7, 10, 20,
                90, 70, 60, 55};
        int sum = 0;
        for (int i = 0; i < state.getCells().length; i++) {
            sum += weigths[i] * state.getCells()[i].getNumber();
        }
        return sum;
    }

    private int evalWeight2(State state) {
        int[] weigths = {20, 15, 10, 5,
                30, 20, 15, 10,
                40, 30, 20, 15,
                60, 40, 30, 20};
        int sum = 0;
        for (int i = 0; i < state.getCells().length; i++) {
            sum += weigths[i] * state.getCells()[i].getNumber();
        }
        return sum;
    }

    public String getNextMove(Game game) {
        Cell[] cells = Arrays.copyOf(game.getCells(), 16);
        State s0 = new State(cells, "");

        //int optimal = expectiminimax(s, 0);
        int optimal = hMiniMax(s0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("Best Action is to go " + s0.getMove() + " and the optimal value is " + optimal);

        return s0.getMove();
    }

    private int expectiminimax(State s, int height) {
        if (cutoffTest(s, height)) {
            return evalOuterLinesFreeSpacesMonotonicityMergeability(s);
        }

        if (player(height)) { // if it's our turn
            List<String> actions = actions(s, height);
            int alpha = Integer.MIN_VALUE;
            for (String action : actions) {
                State resultingState;
                resultingState = result(s, action);
                int searchValue = expectiminimax(resultingState, height + 1);
                if (searchValue > alpha) {
                    alpha = searchValue;
                    s.setMove(action);
                }
            }
            return alpha;
        } else { // if it's the game's turn
            int alpha = 0;
            List<String> actions = actions(s, height);
            for (int i = 0; i < actions.size(); i += 2) {
                State resultingState;
                resultingState = result(s, actions.get(i));
                alpha += 0.9 * expectiminimax(resultingState, height + 1);
                resultingState = result(s, actions.get(i + 1));
                alpha += 0.1 * expectiminimax(resultingState, height + 1);
            }
            return gameLogic.availableSpace().size() == 0 ? 0 : alpha / gameLogic.availableSpace().size();
        }
    }

    private int newEval() { //Noah's attempt at eval function. It isn't very good - yet.
        int[] directionScores = new int[4];
        //UP/DOWN

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int currentTile = j;
                int nextTile = j + 1;

                int currentValue = !gameLogic.cellAt(i, currentTile).isEmpty() ? gameLogic.cellAt(i, currentTile).getNumber() : 0;
                int nextValue = !gameLogic.cellAt(i, nextTile).isEmpty() ? gameLogic.cellAt(i, nextTile).getNumber() : 0;

                if (currentValue > nextValue) {
                    directionScores[0] += nextValue - currentValue; //down to uP
                }
                if (currentValue < nextValue) {
                    directionScores[1] += currentValue - nextValue; //up to down
                }


            }
        }

        //LEFT/RIGHT


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int currentTile = j;
                int nextTile = j + 1;

                int currentValue = !gameLogic.cellAt(currentTile, i).isEmpty() ? gameLogic.cellAt(currentTile, j).getNumber() : 0;
                int nextValue = !gameLogic.cellAt(nextTile, i).isEmpty() ? gameLogic.cellAt(nextTile, j).getNumber() : 0;

                if (currentValue > nextValue) {
                    directionScores[2] += nextValue - currentValue; //left to right
                }
                if (currentValue < nextValue) {
                    directionScores[3] += currentValue - nextValue; //right to left
                }
            }

        }

        return Math.max(directionScores[0] + directionScores[1], directionScores[2] + directionScores[3]) + gameLogic.availableSpace().size() * 3;

    }

}
