package expression.exceptions;

public class EmptyBracketsException extends ParsingException {
    public EmptyBracketsException() {
        super("Empty brackets");
    }
}
