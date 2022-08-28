package expression.exceptions;

import expression.TripleExpression;
import jdk.dynalink.Operation;

public abstract class BinaryOperation implements TripleExpression {
    TripleExpression a;
    TripleExpression b;

    public BinaryOperation(TripleExpression a, TripleExpression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operation(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }

    protected abstract int operation(int a, int b);
}
