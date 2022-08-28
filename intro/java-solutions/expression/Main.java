package expression;

import expression.exceptions.CheckedAdd;
import expression.exceptions.ExpressionParser;
import expression.exceptions.OverFlowException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println(new ExpressionParser().parse("x*y(+(z-1   )/10").evaluate(-1513946847, -2105608785, 471172016));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
