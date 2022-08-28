package expression.exceptions;

import expression.TripleExpression;

public class CheckedSQRT extends UnaryOperation {
    public CheckedSQRT(TripleExpression a) {
        super(a);
    }

    @Override
    public int operation(int a) {
        if (a < 0) {
            throw new OverFlowException();
        } else {
            for (int i = 0; i < 46342; i++) {
                if (a < i * i) {
                    return i - 1;
                } else if (a == i * i) {
                    return i;
                }
            }
            return 46342;
        }
    }
}
