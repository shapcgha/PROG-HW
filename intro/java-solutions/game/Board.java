package game;

public interface Board {
    int getM();

    int getN();

    Position getPosition();

    Cell getCell();

    Result makeMove(Move move);
}
