package game;

import java.io.PrintStream;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final PrintStream out;
    private Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position position, final Cell cell, int m, int n) {
        while (true) {
            out.println("Position");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");
            int temp1;
            int temp2;
            try {
                this.in = new Scanner(System.in);
                temp1 = Integer.parseInt(in.next());
                temp2 = Integer.parseInt(in.next());
            } catch (Exception ex){
                System.out.println("Put the values of the wrong type(required type is int)");
                continue;
            }
            final Move move = new Move(temp1, temp2, cell);
            if (position.isValid(move)) {
                return move;
            }
            final int row = move.getRow();
            final int column = move.getColumn();
            out.println("Move " + move + " is invalid");
        }
    }
}
