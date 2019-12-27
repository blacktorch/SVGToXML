package graphics;

import org.w3c.dom.Node;

public class SVGRect {
    private int x;
    private int y;
    private int width;
    private int height;
    private Node parent;

    public SVGRect(int x, int y, int width, int height, Node parent){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
