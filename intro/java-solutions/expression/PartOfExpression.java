package expression;

public interface PartOfExpression extends Expression, TripleExpression {
    String toString();

    boolean equals(Object obj);

    int hashCode();
}
