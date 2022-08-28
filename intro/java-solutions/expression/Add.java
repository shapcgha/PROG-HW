package expression;

public class Add extends BinaryOperation {
    public Add(PartOfExpression a, PartOfExpression b) {
        super(a, b, "+");
    }

    @Override
    protected int operation(int a, int b) {
        return a+b;
    }
}
