package markup;

import java.util.List;

public class Strikeout extends AbstractClass {

    public Strikeout(List<MarkdownAndBBCode> list) {
        super(list, "~", "[s]", "[/s]");
    }

}
