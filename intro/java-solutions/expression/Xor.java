package expression;

public class Xor extends BinaryOperation {
    public Xor(PartOfExpression a, PartOfExpression b) {
        super(a, b, "^");
    }

    @Override
    protected int operation(int a, int b) {
        return a ^ b;
    }
}
