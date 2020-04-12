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
            //System.out.println("Maximizer: " + max);
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (String action : actions) {
                resultingState = result(s, action);
                searchValue = hMiniMax(resultingState, d + 1, alpha, beta);
                /*if (searchValue < min) {
                    s.setMove(action);
                }*/
                min = Integer.min(min, searchValue);
                beta = Integer.min(beta, min);
                if (beta <= alpha) {
                    break;
                }
            }
            //System.out.println("Minimizer: " + min);
            return min;
        }
    }

    /**
     * Find out who's turn it is in a given state.
     *
     * @param state the current game state containing (game, move)
     * @return 0 player, 1 nature
     */
    private boolean player(State state) {
        return state.getMove() == null;
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

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.availableSpace().size();
    }*/

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculateLineWithMostPoints();
    }*/

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculateLineWithMostPoints()*gameLogic.availableSpace().size() + gameLogic.calculateLineWithMostPoints();
    }*/

    private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return gameLogic.calculatePointsOnOuterLines()*gameLogic.availableSpace().size() + gameLogic.calculatePointsOnOuterLines() - gameLogic.nonMonotonicPenalty()*10;
    }

    /*private int eval(State state) {
        gameLogic.setCells(Arrays.copyOf(state.getCells(),16));
        return evalWeight(state);
    }*/

    private int evalWeight(State state) {
        int[] weigths = {-40, -38, -35, -30,
                -5, -15, -18, -20,
                5, 7, 10, 20,
                110, 70, 60, 55};
        int sum = 0;
        for (int i = 0; i < state.getCells().length; i++) {
            sum += weigths[i]*state.getCells()[i].getNumber();
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
            sum += weigths[i]*state.getCells()[i].getNumber();
        }
        return sum;
    }

    public String getNextMove(Game game) {
        Cell[] cells = Arrays.copyOf(game.getCells(), 16);
        State s0 = new State(cells, "");
        helper(s0);
        return s0.getMove();
    }

    private int expectiminimax(State s, int height) {

        if (cutoffTest(s, height)) {
            return eval(s);
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
            int searchvalue = 0;
            List<String> actions = actions(s, height);

            for (String action : actions) {
                State resultingState;
                resultingState = result(s, action);
                Cell emptyCell = gameLogic.chooseCelVal(4);
                searchvalue = expectiminimax(resultingState, height + 1);
                if (searchvalue != Integer.MIN_VALUE) {
                    alpha += 0.1 * searchvalue;
                    s.setMove(action);
                }
                emptyCell.setNumber(2); //and remove the value 4 u just inserted above.
                searchvalue = expectiminimax(resultingState, height + 1);
                if (searchvalue != Integer.MIN_VALUE) {
                    alpha += 0.9 * searchvalue;
                    s.setMove(action);
                }
            }
            return alpha / 16;

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

        return Math.max(directionScores[0] + directionScores[1], directionScores[2] + directionScores[3]) + gameLogic.availableSpace().size()*3;

    }

}
