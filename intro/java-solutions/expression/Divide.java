package expression;

public class Divide extends BinaryOperation {
    public Divide(PartOfExpression a, PartOfExpression b) {
        super(a, b, "/");
    }

    @Override
    protected int operation(int a, int b) {
        return a / b;
    }
}
