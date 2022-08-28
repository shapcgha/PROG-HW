package expression.exceptions;

import expression.TripleExpression;

public class CheckedDivide extends BinaryOperation {

    public CheckedDivide(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    @Override
    public int operation(int a, int b) {
        if (b != 0) {
            if (a == Integer.MIN_VALUE && b == -1) {
                throw new OverFlowException();
            } else {
                return a / b;
            }
        } else {
            throw new DivisionByZeroException();
        }
    }
}
