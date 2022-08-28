package expression.exceptions;

public class OperationWithoutArgumentsException extends ParsingException{
    public OperationWithoutArgumentsException() {
        super("Operation without appropriate number of arguments");
    }
}
