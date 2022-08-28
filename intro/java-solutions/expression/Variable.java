package expression;

public class Variable implements PartOfExpression {
    private final String sign;

    public Variable(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable && obj.hashCode() == hashCode()) {
            return ((Variable) obj).sign.equals(sign);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return sign.hashCode() * 31;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch (sign) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> 0;
        };
    }
}
