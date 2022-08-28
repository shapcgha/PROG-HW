package expression;

public class And extends BinaryOperation {
    public And(PartOfExpression a, PartOfExpression b) {
        super(a, b, "&");
    }

    @Override
    protected int operation(int a, int b) {
        return a & b;
    }
}
