package expression.exceptions;

public class IncorrectVariableException extends ParsingException {
    public IncorrectVariableException() {
        super("Incorrect variable");
    }
}
