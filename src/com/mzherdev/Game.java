package com.mzherdev;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by mzherdev on 18.08.2015.
 */
public class Game extends javafx.scene.canvas.Canvas {

    private Cell[] cells;
    boolean win = false;
    boolean lose = false;
    int score = 0;

    public Cell[] getCells() {
        return cells;
    }

    public Game() {
        super(330, 390);
        setFocused(true);
        resetGame();
    }

    public Game(double width, double height) {
        super(width, height);
        setFocused(true);
        resetGame();
    }


    private void resetGame() {
        score = 0;
        win = false;
        lose = false;
        cells = new Cell[4 * 4];
        for (int cell = 0; cell < cells.length; cell++) {
            cells[cell] = new Cell();
        }
        addCell();
        addCell();
    }

    private void addCell() {
        List<Cell> list = availableSpace();
        if(!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Cell emptyCell = list.get(index);
            emptyCell.number = Math.random() < 0.9 ? 2 : 4;
        }

    }

    public List<Cell> availableSpace() {
        List<Cell> list = new ArrayList<>(16);
        for(Cell c : cells)
            if(c.isEmpty())
                list.add(c);
        return list;
    }

    public String[] availableSpacesToString() {
        String[] list = new String[2*availableSpace().size()];
        int c = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].isEmpty()) {
                list[c] = i + " " + 2;
                c++;
                list[c] = i + " " + 4;
                c++;
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }

    private Cell cellAt(int x, int y) {
        return cells[x + y * 4];
    }

    boolean canMove() {
        if(!isFull()) return true;
        for(int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Cell cell = cellAt(x, y);
                if ((x < 3 && cell.number == cellAt(x + 1, y).number) ||
                        (y < 3) && cell.number == cellAt(x, y + 1).number) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(Cell[] line1, Cell[] line2) {
        if(line1 == line2) {
            return true;
        }
        if (line1.length != line2.length) {
            return false;
        }

        for(int i = 0; i < line1.length; i++) {
            if(line1[i].number != line2[i].number) {
                return false;
            }
        }
        return true;
    }

    private Cell[] rotate(int angle) {
        Cell[] tiles = new Cell[4 * 4];
        int offsetX = 3;
        int offsetY = 3;
        if(angle == 90) {
            offsetY = 0;
        } else if(angle == 270) {
            offsetX = 0;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 4; y++) {
                int newX = (x*cos) - (y*sin) + offsetX;
                int newY = (x*sin) + (y*cos) + offsetY;
                tiles[(newX) + (newY) * 4] = cellAt(x, y);
            }
        }
        return tiles;
    }

    private Cell[] moveLine(Cell[] oldLine) {
        LinkedList<Cell> list = new LinkedList<>();
        for(int i = 0; i < 4; i++) {
            if(!oldLine[i].isEmpty()){
                list.addLast(oldLine[i]);
            }
        }

        if(list.size() == 0) {
            return oldLine;
        } else {
            Cell[] newLine = new Cell[4];
            while (list.size() != 4) {
                list.add(new Cell());
            }
            for(int j = 0; j < 4; j++) {
                newLine[j] = list.removeFirst();
            }
            return newLine;
        }
    }

    private Cell[] mergeLine(Cell[] oldLine) {
        LinkedList<Cell> list = new LinkedList<>();
        for(int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].number;
            if (i < 3 && oldLine[i].number == oldLine[i+1].number) {
                num *= 2;
                score += num;
                if ( num == 2048) {
                    win = true;
                }
                i++;
            }
            list.add(new Cell(num));
        }

        if(list.size() == 0) {
            return oldLine;
        } else {
            while (list.size() != 4) {
                list.add(new Cell());
            }
            return list.toArray(new Cell[4]);
        }
    }

    private Cell[] getLine(int index) {
        Cell[] result = new Cell[4];
        for(int i = 0; i < 4; i++) {
            result[i] = cellAt(i, index);
        }
        return result;
    }

    private void setLine(int index, Cell[] re) {
        System.arraycopy(re, 0, cells, index * 4, 4);
    }

    public boolean left() {
        boolean moved = false;
        for(int i = 0; i < 4; i++) {
            Cell[] line = getLine(i);
            Cell[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if( !moved && !compare(line, merged)) {
                moved = true;
            }
        }
        return moved;
    }

    public boolean right() {
        cells = rotate(180);
        boolean moved = left();
        cells = rotate(180);
        return moved;
    }

    public boolean up() {
        cells = rotate(270);
        boolean moved = left();
        cells = rotate(90);
        return moved;
    }

    public boolean down() {
        cells = rotate(90);
        boolean moved = left();
        cells = rotate(270);
        return moved;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    void spawnCell() {
        addCell();
    }

    public void spawnCell(String action) {
        String[] indexAndNumber = action.split(" ");
        int index = Integer.parseInt(indexAndNumber[0]); // Index in game board
        int number = Integer.parseInt(indexAndNumber[1]); // Number to put on index (2 or 4)

        cells[index] = new Cell(number);
    }

    public int calculateScore() {
        int score = 0;
        for (Cell cell : cells) {
            score += cell.number;
        }
        return score;
    }

    public boolean winningState() {
        for (Cell cell : cells) {
            if (cell.number == 2048) {
                return true;
            }
        }
        return false;
    }

    public int calculateLineWithMostPoints() {
        int maxRow = Integer.MIN_VALUE;
        int[] cols = new int[4];

        int cellNumber;
        int localMax;

        for (int i = 0; i < 4; i++) {
            localMax = 0;
            for (int j = 0; j < 4; j++) {
                cellNumber = cellAt(i,j).number;
                localMax += cellNumber;
                cols[i] += cellNumber;
            }
            if (localMax > maxRow) {
                maxRow = localMax;
            }
        }
        return Integer.max(maxRow, Integer.max(cols[0], Integer.max(cols[1], Integer.max(cols[2], cols[3]))));
    }

    public int getScore() {
        return score;
    }

    public int calculateOuterLineWithMostPoints() {
        int maxRow = Integer.MIN_VALUE;
        int[] cols = new int[4];

        int cellNumber;
        int localMax;

        for (int i = 0; i < 2; i++) {
            localMax = 0;
            for (int j = 0; j < 4; j++) {
                cellNumber = cellAt(j,i*3).number;
                localMax += cellNumber;
                cols[i] += cellNumber;
            }
            if (localMax > maxRow) {
                maxRow = localMax;
            }
        }
        return Integer.max(maxRow, Integer.max(cols[0], cols[3]));
    }
}