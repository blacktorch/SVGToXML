package graphics;

import org.w3c.dom.Node;

public class Graphics {
    private SVGRect rect;
    private SVGText text;
    private SVGLine line;
    private Node parent;

    public Graphics(SVGRect rect, SVGText text, SVGLine line, Node parent){
        this.rect = rect;
        this.text = text;
        this.line = line;
        this.parent = parent;
    }

    public SVGRect getRect() {
        return rect;
    }

    public void setRect(SVGRect rect) {
        this.rect = rect;
    }

    public SVGText getText() {
        return text;
    }

    public void setText(SVGText text) {
        this.text = text;
    }

    public SVGLine getLine() {
        return line;
    }

    public void setLine(SVGLine line) {
        this.line = line;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
