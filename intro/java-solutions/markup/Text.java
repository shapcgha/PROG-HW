package markup;

public class Text implements MarkdownAndBBCode {
    private String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public StringBuilder toBBCode(StringBuilder line) {
        return line.append(text);
    }

    @Override
    public StringBuilder toMarkdown(StringBuilder line) {
        return line.append(text);
    }
}
