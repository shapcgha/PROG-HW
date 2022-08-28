package expression.exceptions;

public class NoClosingBracketException extends ParsingException {
    public NoClosingBracketException() {
        super("No closing bracket");
    }
}
