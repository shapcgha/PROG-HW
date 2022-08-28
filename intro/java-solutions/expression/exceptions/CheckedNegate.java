package expression.exceptions;

import expression.TripleExpression;

public class CheckedNegate extends UnaryOperation {
    public CheckedNegate(TripleExpression a) {
        super(a);
    }

    @Override
    public int operation(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverFlowException();
        } else {
            return a * -1;
        }
    }
}
