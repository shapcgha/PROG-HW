package expression;

public class Multiply extends BinaryOperation {
    public Multiply(PartOfExpression a, PartOfExpression b) {
        super(a, b, "*");
    }

    @Override
    protected int operation(int a, int b) {
        return a * b;
    }
}
