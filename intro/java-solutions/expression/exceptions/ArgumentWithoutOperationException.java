package expression.exceptions;

public class ArgumentWithoutOperationException extends ParsingException {
    public ArgumentWithoutOperationException() {
        super("Argument without operation");
    }
}
