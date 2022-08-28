package expression.exceptions;

public class NegativeBalanceException extends ParsingException {
    public NegativeBalanceException() {
        super("Balance of brackets is negative");
    }
}
