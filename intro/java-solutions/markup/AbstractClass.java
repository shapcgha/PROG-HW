package markup;

import java.util.List;

public abstract class AbstractClass implements MarkdownAndBBCode {

    private List<MarkdownAndBBCode> list;
    private String markElem;
    private String startBBcode;
    private String endBBcode;

    public AbstractClass(List<MarkdownAndBBCode> getList, String markElem, String startBBcode, String endBBcode) {
        list = getList;
        this.markElem = markElem;
        this.startBBcode = startBBcode;
        this.endBBcode = endBBcode;
    }


    public StringBuilder toMarkdown(StringBuilder line) {
        line.append(markElem);
        for (MarkdownAndBBCode text : list) {
            line.append(text.toMarkdown(new StringBuilder()));
        }
        line.append(markElem);
        return line;
    }

    public StringBuilder toBBCode(StringBuilder line) {
        line.append(startBBcode);
        for (MarkdownAndBBCode text : list) {
            line.append(text.toBBCode(new StringBuilder()));
        }
        line.append(endBBcode);
        return line;
    }

}
