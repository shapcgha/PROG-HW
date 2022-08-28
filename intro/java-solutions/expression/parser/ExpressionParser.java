package expression.parser;

import expression.*;
import expression.exceptions.*;

public class ExpressionParser implements Parser {
    private String data;
    private int pos;
    private TripleExpression lastPartOfExpression;
    private char lastOperation;
    private char lastBinaryOperation;
    private int balance;


    @Override
    public TripleExpression parse(String expression) throws ParsingException {
        pos = 0;
        data = expression;
        balance = 0;
        reset();
        lastPartOfExpression = parsePartOfExpression();
        return lastPartOfExpression;
    }

    private void reset() {
        lastPartOfExpression = null;
        lastOperation = 's';
        lastBinaryOperation = 's';
    }

    private TripleExpression parsePartOfExpression() throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            char ch = next();
            if (ch == '(') {
                parseExpressionInBrackets();
            } else if (Character.isDigit(ch)) {
                final StringBuilder sb = new StringBuilder(Character.toString(ch));
                parseConst(sb);
            } else if (Character.isAlphabetic(ch)) {
                final StringBuilder sb = new StringBuilder(Character.toString(ch));
                parseVariable(sb);
            } else if (ch == '-' && lastOperation != ' ') {
                parseUnaryMines();
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                if (lastOperation == '+' || lastOperation == '-' || lastOperation == '*' || lastOperation == '/' || lastOperation == 's') {
                    throw new OperationWithoutArgumentsException();
                }
                parseBinaryOperation(ch);
            } else if (ch == ')') {
                if (lastPartOfExpression == null) {
                    throw new EmptyBracketsException();
                } else {
                    throw new OperationWithoutArgumentsException();
                }
            } else {
                throw new IncorrectSymbolException();
            }
            if (isNeededUp(lastOperation)) {
                return lastPartOfExpression;
            }
            if (isNeededUp(lastBinaryOperation)) {
                return lastPartOfExpression;
            }
            if (hasNext()) {
                lastPartOfExpression = parsePartOfExpression();
            } else {
                if (lastOperation != ' ') {
                    throw new OperationWithoutArgumentsException();
                }
            }
            return lastPartOfExpression;
        } else {
            if (lastOperation == ' ') {
                return lastPartOfExpression;
            } else {
                throw new OperationWithoutArgumentsException();
            }
        }

    }

    private void parseExpressionInBrackets() throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            if (lastOperation == ' ') {
                throw new ArgumentWithoutOperationException();
            }
            balance++;
            char tempLastOperation = lastOperation;
            char tempLastBinaryOperation = lastBinaryOperation;
            reset();
            parsePartOfExpression();
            if (!hasNext() && data.charAt(pos - 1) != ')') {
                throw new NoClosingBracketException();
            }
            next();
            lastOperation = tempLastOperation;
            lastBinaryOperation = tempLastBinaryOperation;
            balance--;
            skipWhiteSpaces();
        } else {
            throw new NoClosingBracketException();
        }
    }

    private void parseBinaryOperation(char ch) throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            char temp = lastBinaryOperation;
            lastOperation = ch;
            lastBinaryOperation = ch;
            if (ch == '-') {
                lastPartOfExpression = new CheckedSubtract(lastPartOfExpression, parsePartOfExpression());
            } else if (ch == '+') {
                lastPartOfExpression = new CheckedAdd(lastPartOfExpression, parsePartOfExpression());
            } else if (ch == '*') {
                lastPartOfExpression = new CheckedMultiply(lastPartOfExpression, parsePartOfExpression());
            } else if (ch == '/') {
                lastPartOfExpression = new CheckedDivide(lastPartOfExpression, parsePartOfExpression());
            }
            lastBinaryOperation = temp;
            skipWhiteSpaces();
        } else {
            throw new OperationWithoutArgumentsException();
        }
    }

    private void parseABS() throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            char temp = lastOperation;
            lastOperation = 'A';
            lastPartOfExpression = new CheckedABS(parsePartOfExpression());
            lastOperation = temp;
            skipWhiteSpaces();
        } else {
            throw new OperationWithoutArgumentsException();
        }
    }

    private void parseUnaryMines() throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            char temp = lastOperation;
            lastOperation = '~';
            char ch = next();
            if (Character.isDigit(ch)) {
                parseConst(new StringBuilder("-" + ch));
            } else {
                pos--;
                lastPartOfExpression = new CheckedNegate(parsePartOfExpression());
            }
            lastOperation = temp;
            skipWhiteSpaces();
        } else {
            throw new OperationWithoutArgumentsException();
        }
    }

    private void parseVariable(StringBuilder sb) throws ParsingException {
        while (hasNext() && Character.isAlphabetic(data.charAt(pos))) {
            sb.append(next());
        }
        switch (sb.toString()) {
            case "x", "y", "z" -> lastPartOfExpression = new Variable(sb.toString());
            case "abs" -> parseABS();
            case "sqrt" -> parseSQRT();
            default -> throw new IncorrectVariableException();
        }
        skipWhiteSpaces();
    }

    private void parseSQRT() throws ParsingException {
        skipWhiteSpaces();
        if (hasNext()) {
            char temp = lastOperation;
            lastOperation = 'S';
            lastPartOfExpression = new CheckedSQRT(parsePartOfExpression());
            lastOperation = temp;
            skipWhiteSpaces();
        } else {
            throw new OperationWithoutArgumentsException();
        }
    }

    private void parseConst(StringBuilder sb) {
        while (hasNext() && Character.isDigit(data.charAt(pos))) {
            sb.append(next());
        }
        try {
            lastPartOfExpression = new Const(Integer.parseInt(sb.toString()));
        } catch (NumberFormatException e) {
            throw new OverFlowException();
        }
        skipWhiteSpaces();
    }

    private boolean isNeededUp(char flag) throws ParsingException {
        lastOperation = ' ';
        if (!hasNext()) {
            return true;
        } else {
            char ch = data.charAt(pos);
            if (getPriority(flag) >= getPriority(ch)) {
                if (ch == ')') {
                    if (balance - 1 < 0) {
                        throw new NegativeBalanceException();
                    }
                } else if (getPriority(ch) == 0) {
                    throw new ArgumentWithoutOperationException();
                } else if (getPriority(ch) == -1) {
                    throw new IncorrectSymbolException();
                }
                return true;
            }
            return false;
        }
    }

    private void skipWhiteSpaces() {
        while (hasNext() && Character.isWhitespace(data.charAt(pos))) {
            next();
        }
    }

    private int getPriority(char ch) {
        if (ch == '~' || ch == 'A' || ch == 'S') {
            return 6;
        } else if (ch == '*' || ch == '/') {
            return 5;
        } else if (ch == '+' || ch == '-') {
            return 4;
        } else if (ch == '&') {
            return 3;
        } else if (ch == '^') {
            return 2;
        } else if (ch == '|') {
            return 1;
        } else if (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '(' || ch == ')') {
            return 0;
        }
        return -1;
    }

    public boolean hasNext() {
        return pos < data.length();
    }

    public char next() {
        return data.charAt(pos++);
    }
}
