package expression;

public class Mines implements PartOfExpression {
    private final PartOfExpression part;

    public Mines(PartOfExpression part) {
        this.part = part;
    }

    @Override
    public int hashCode() {
        return part.hashCode() * 32;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mines && obj.hashCode() == hashCode()) {
            return ((Mines) obj).part.equals(part);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + "-" + part.toString() + ")";
    }

    @Override
    public int evaluate(int x) {
        return part.evaluate(x) * (-1);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return part.evaluate(x, y, z) * (-1);
    }
}
