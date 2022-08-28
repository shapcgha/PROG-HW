package expression;

public class Or extends BinaryOperation {
    public Or(PartOfExpression a, PartOfExpression b) {
        super(a, b, "|");
    }

    @Override
    protected int operation(int a, int b) {
        return a | b;
    }
}
