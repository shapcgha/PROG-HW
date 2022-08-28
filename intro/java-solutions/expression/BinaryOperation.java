package expression;

public abstract class BinaryOperation implements PartOfExpression {
    protected final PartOfExpression a;
    protected final PartOfExpression b;
    protected final String sign;

    public BinaryOperation(PartOfExpression a, PartOfExpression b, String sign) {
        this.a = a;
        this.b = b;
        this.sign = sign;
    }

    protected abstract int operation(int a, int b);

    @Override
    public String toString() {
        return "(" + a + " " + sign + " " + b + ")";
    }

    @Override
    public int evaluate(int x) {
        return operation(a.evaluate(x), b.evaluate(x));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryOperation && obj.hashCode() == hashCode()) {
            return ((BinaryOperation) obj).a.equals(a) &&
                    ((BinaryOperation) obj).b.equals(b) &&
                    ((BinaryOperation) obj).sign.equals(sign);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return a.hashCode() * 31 + b.hashCode() * 30 + sign.hashCode() * 29;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operation(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }
}
