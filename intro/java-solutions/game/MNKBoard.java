package game;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class MNKBoard implements Board, Position {
    private final int m;
    private final int n;
    private final int k;
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;
    private int empty;

    public MNKBoard() {
        int m;
        int n;
        int k;
        while (true) {
            System.out.println("Enter the values M N K");
            try {
                Scanner sc = new Scanner(System.in);
                m = Integer.parseInt(sc.next());
                n = Integer.parseInt(sc.next());
                k = Integer.parseInt(sc.next());
            } catch (Exception ex) {
                System.out.println("Put the values of the wrong type(required type is int)");
                continue;
            }
            if (m < 1 || n < 1 || k < 1) {
                System.out.println("values must be natural");
                continue;
            } else if (k > Math.min(m, n)) {
                System.out.println("invalid value of the variable k(k must be bigger than min(m,n))");
                continue;
            } else {
                break;
            }
        }
        this.m = m;
        this.n = n;
        this.k = k;
        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
        empty = m * k;
    }

    @Override
    public int getM() {
        return m;
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();
        empty--;
        int colCount = 1;
        int rowCount = 1;
        int diag1Count = 1;
        int diag2Count = 1;
        for (int j = 0; j < 8; j++) {
            int tempCol = move.getColumn();
            int tempRow = move.getRow();
            for (int i = 0; i < k; i++) {
                if (tempRow < m - 1 && j == 0 && cells[tempRow][tempCol] == cells[tempRow + 1][tempCol]) {
                    tempRow++;
                    rowCount++;
                } else if (tempRow > 0 && j == 1 && cells[tempRow][tempCol] == cells[tempRow - 1][tempCol]) {
                    tempRow--;
                    rowCount++;
                } else if (tempCol < n - 1 && j == 2 && cells[tempRow][tempCol] == cells[tempRow][tempCol + 1]) {
                    tempCol++;
                    colCount++;
                } else if (tempCol > 0 && j == 3 && cells[tempRow][tempCol] == cells[tempRow][tempCol - 1]) {
                    tempCol--;
                    colCount++;
                } else if (tempCol < n - 1 && tempRow < m - 1 && j == 4 && cells[tempRow][tempCol] == cells[tempRow + 1][tempCol + 1]) {
                    tempRow++;
                    tempCol++;
                    diag1Count++;
                } else if (tempRow > 0 && tempCol > 0 && j == 5 && cells[tempRow][tempCol] == cells[tempRow - 1][tempCol - 1]) {
                    tempRow--;
                    tempCol--;
                    diag1Count++;
                } else if (tempCol > 0 && tempRow < m - 1 && j == 6 && cells[tempRow][tempCol] == cells[tempRow + 1][tempCol - 1]) {
                    tempRow++;
                    tempCol--;
                    diag2Count++;
                } else if (tempCol < n - 1 && tempRow > 0 && j == 7 && cells[tempRow][tempCol] == cells[tempRow - 1][tempCol + 1]) {
                    tempRow--;
                    tempCol++;
                    diag2Count++;
                } else {
                    break;
                }
            }
        }
        if (rowCount >= k || colCount >= k || diag1Count >= k || diag2Count >= k) {
            return Result.WIN;
        } else if (empty == 0) {
            return Result.DRAW;
        }
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");
        for (int c = 0; c < n; c++) {
            sb.append(c);
        }
        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r);
            for (int c = 0; c < n; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }

}
