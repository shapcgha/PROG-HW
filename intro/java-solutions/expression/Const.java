package expression;

public class Const implements PartOfExpression {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int evaluate(int x) {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Const && obj.hashCode() == hashCode()) {
            return ((Const) obj).value == value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value)*31;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return value;
    }
}
