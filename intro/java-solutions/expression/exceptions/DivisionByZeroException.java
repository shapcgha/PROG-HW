package expression.exceptions;

public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException(){
        super("division by zero");
    }
}
