package game;

public class Main {
    public static void main(String[] args) {
        final Game game = new Game(true,"tournament");
        System.out.println("Winner of this game " + game.play());
    }
}
