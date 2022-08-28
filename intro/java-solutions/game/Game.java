package game;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private final boolean log;
    private ArrayList<Player> players=new ArrayList<>();
    private final int countPlayers;
    private int circle = 1;

    public Game(final boolean log, String str) {
        this.log = log;
        if (str.equals("tournament")) {
            System.out.println("Enter number of players");
            int countPlayers;
            while (true) {
                Scanner sc = new Scanner(System.in);
                try {
                    countPlayers = Integer.parseInt(sc.next());
                    if (countPlayers < 2) {
                        System.out.println("Must be more than 1 players");
                        continue;
                    } else {
                        break;
                    }
                } catch (Exception ex) {
                    System.out.println("Put the values of the wrong type(required type is int)");
                    continue;
                }
            }
            this.countPlayers = countPlayers;
            for (int i = 0; i < countPlayers; i++) {
                while (true) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter type of the player(Human, Random, Sequential");
                    String type = sc.next();
                    if (type.equals("Human")) {
                        players.add(new HumanPlayer());
                        break;
                    } else if (type.equals("Random")) {
                        players.add(new RandomPlayer());
                        break;
                    } else if (type.equals("Sequential")) {
                        players.add(new SequentialPlayer());
                        break;
                    } else {
                        System.out.println("Wrong type of player");
                    }
                }
            }
            while (true) {
                System.out.println("Enter number of circles");
                int circle;
                try {
                    Scanner sc = new Scanner(System.in);
                    circle = Integer.parseInt(sc.next());
                    if (countPlayers < 2) {
                        System.out.println("Must be more than 1 circle");
                        continue;
                    } else {
                        this.circle = circle;
                        break;
                    }
                } catch (Exception ex) {
                    System.out.println("Put the values of the wrong type(required type is int)");
                    continue;
                }
            }
        } else if (str.equals("standart")) {
            countPlayers = 2;
            for (int i = 0; i < countPlayers; i++) {
                while (true) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter type of the player(Human, Random, Sequential");
                    String type = sc.next();
                    if (type.equals("Human")) {
                        players.add(new HumanPlayer());
                        break;
                    } else if (type.equals("Random")) {
                        players.add(new RandomPlayer());
                        break;
                    } else if (type.equals("Sequential")) {
                        players.add(new SequentialPlayer());
                        break;
                    } else {
                        System.out.println("Wrong type of player");
                    }
                }
            }
            circle = 1;
        } else {
            countPlayers = 0;
        }
    }

    public int play() {
        int[] results = new int[countPlayers];
        int winner=1;
        for (int i = 0; i < circle; i++) {
            for (int j = 1; j <= countPlayers - 1; j++) {
                L0:
                for (int k = j + 1; k <= countPlayers; k++) {
                    int maxpoints=results[0];
                    for (int p=0;p<countPlayers;p++){
                        System.out.println("player №"+(p+1)+" have "+results[p]+" points");
                        if(results[p]>maxpoints){
                            maxpoints=results[p];
                            winner=p;
                        }
                    }
                    Board board = new MNKBoard();
                    while (true) {
                        final int result1 = move(board, players.get(j-1), j);
                        if (result1 == 0) {
                            results[j-1]++;
                            results[k-1]++;
                            continue L0;
                        } else if(result1 == j){
                            results[j-1]+=3;
                            continue L0;
                        }
                        final int result2 = move(board, players.get(k-1), k);
                        if (result2 == 0) {
                            results[j-1]++;
                            results[k-1]++;
                            continue L0;
                        } else if(result2 == k){
                            results[k-1]+=3;
                            continue L0;
                        }
                    }
                }
            }
        }
        return winner;
    }

    private int move(final Board board, final Player player, final int no) {
        final Move move = player.move(board.getPosition(), board.getCell(), board.getM(), board.getN());
        final Result result = board.makeMove(move);
        log("Player " + no + " move: " + move);
        log("Position:\n" + board);
        if (result == Result.WIN) {
            log("Player " + no + " won");
            return no;
        } else if (result == Result.LOSE) {
            log("Player " + no + " lose");
            return 3 - no;
        } else if (result == Result.DRAW) {
            log("Draw");
            return 0;
        } else {
            return -1;
        }
    }

    private void log(final String message) {
        if (log) {
            System.out.println(message);
        }
    }
}
