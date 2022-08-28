package markup;

import java.util.List;

public class Emphasis extends AbstractClass {

    public Emphasis(List<MarkdownAndBBCode> list) {
        super(list, "*", "[i]", "[/i]");
    }

}
