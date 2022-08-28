package expression;

public class Subtract extends BinaryOperation {
    public Subtract(PartOfExpression a, PartOfExpression b) {
        super(a, b, "-");
    }

    @Override
    protected int operation(int a, int b) {
        return a - b;
    }
}
