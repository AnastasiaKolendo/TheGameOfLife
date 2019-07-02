package com.kolendoanastasia.gameoflife;

public class Life {

    private boolean[][] cells;

    Life(int numberOfRows, int numberOfColumns){
        cells = new boolean[numberOfRows][numberOfColumns];
    }

    public void evolve(){
        boolean[][] nextGeneration = new boolean[cells.length][cells[0].length];

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int numberOfNeighbors = countNeighbours(i, j);
                // apply game's rules
                if (cells[i][j]) {
                    nextGeneration[i][j] = numberOfNeighbors == 2 || numberOfNeighbors == 3;
                } else {
                    nextGeneration[i][j] = numberOfNeighbors == 3;
                }
            }
        }
        cells = nextGeneration;
    }

    private int countNeighbours(int i, int j) {
        int numberOfNeighbors = 0;
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (k == 0 && l == 0) {
                    continue;
                }
                int neighbourRow = i + k;
                int neighbourColumn = j + l;
                if (neighbourRow >= 0 && neighbourRow < cells.length &&
                        neighbourColumn >= 0 && neighbourColumn < cells[i].length &&
                        cells[neighbourRow][neighbourColumn]) {
                    numberOfNeighbors++;
                }
            }
        }
        return numberOfNeighbors;
    }

    public boolean isAlive(int rowNumber, int columnNumber) {
        return cells[rowNumber][columnNumber];
    }

    public void setAlive(int rowNumber, int columnNumber, boolean isAlive){
        cells[rowNumber][columnNumber] = isAlive;
    }

    public int getNumberOfRows() {
        return cells.length;
    }

    public int getNumberOfColumns() {
        return cells[0].length;
    }
}
