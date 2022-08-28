package expression.exceptions;

import expression.TripleExpression;

public class CheckedSubtract extends BinaryOperation {

    public CheckedSubtract(TripleExpression a, TripleExpression b) {
        super(a,b);
    }

    @Override
    public int operation(int a, int b) {
        int r = a - b;
        if (((a ^ b) & (a ^ r)) < 0) {
            throw new OverFlowException();
        }
        return r;
    }
}
