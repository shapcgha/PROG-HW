package expression.exceptions;

import expression.TripleExpression;

public class CheckedAdd extends BinaryOperation {

    public CheckedAdd(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    @Override
    public int operation(int a, int b) {
        int r = a + b;
        if (((a ^ r) & (b ^ r)) < 0) {
            throw new OverFlowException();
        }
        return r;
    }
}
