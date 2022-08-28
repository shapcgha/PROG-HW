package expression.exceptions;

import expression.TripleExpression;

public class CheckedABS extends UnaryOperation{
    public CheckedABS(TripleExpression a) {
        super(a);
    }


    @Override
    public int operation(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverFlowException();
        } else {
            if (a <= 0) {
                return a * -1;
            } else {
                return a;
            }
        }
    }
}
