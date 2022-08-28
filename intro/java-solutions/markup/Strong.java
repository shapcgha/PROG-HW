package markup;

import java.util.List;

public class Strong extends AbstractClass {

    public Strong(List<MarkdownAndBBCode> list) {
        super(list, "__", "[b]", "[/b]");
    }

}
