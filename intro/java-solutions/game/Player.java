package game;

public interface Player {
    Move move(Position position, Cell cell, int m, int n);
}
