package expression.exceptions;

import expression.TripleExpression;

public class CheckedMultiply extends BinaryOperation {

    public CheckedMultiply(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    @Override
    public int operation(int a, int b) {
        int maximum;
        if (a == 0 || b == 0) {
            return 0;
        }
        if ((a < 0 && b < 0) || (a > 0 && b > 0)) {
            maximum = Integer.MAX_VALUE;
        } else {
            maximum = Integer.MIN_VALUE;
        }
        if (b > a) {
            int temp = a;
            a = b;
            b = temp;
        }
        if ((b > 0 && b > (maximum / a) || (b < 0 && b < maximum / a))) {
            throw new OverFlowException();

        }
        return a * b;
    }
}
