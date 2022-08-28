package expression.exceptions;

public class IncorrectSymbolException extends ParsingException{
    public IncorrectSymbolException(){
        super("Incorrect symbol");
    }
}
