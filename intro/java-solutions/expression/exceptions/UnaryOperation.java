package expression.exceptions;

import expression.TripleExpression;

public abstract class UnaryOperation implements TripleExpression {
    TripleExpression a;
    public UnaryOperation(TripleExpression a){
        this.a = a;
    }

    @Override
    public int evaluate(int x, int y, int z){
        return operation(a.evaluate(x,y,z));
    }

    public abstract int operation(int a);
}
