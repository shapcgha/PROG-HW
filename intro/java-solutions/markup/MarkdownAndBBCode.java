package markup;

interface MarkdownAndBBCode {

    public StringBuilder toMarkdown(StringBuilder line);

    public StringBuilder toBBCode(StringBuilder line);
}