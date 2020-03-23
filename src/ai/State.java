package ai;

import com.mzherdev.Cell;

public class State {
    private Cell[] cells;
    private String move; // move = null means that nature is playing

    public State(Cell[] cells, String move) {
        this.cells = cells;
        this.move = move;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
