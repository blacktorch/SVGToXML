package graphics;

import org.w3c.dom.Node;

public class SVGText {
    private int x;
    private int y;
    private int fontSize;
    private int strokeWidth;
    private String text;
    private Node parent;

    public SVGText(int x, int y, int fontSize, int strokeWidth, String text, Node parent){
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.strokeWidth = strokeWidth;
        this.text = text;
        this.parent = parent;

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
