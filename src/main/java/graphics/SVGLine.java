package graphics;

import org.w3c.dom.Node;

public class SVGLine {
   private int x1;
   private int x2;
   private int y1;
   private int y2;
   private int strokeWidth;
   private String markerId;
   private Node parent;

   public SVGLine(int x1, int x2, int y1, int y2, int strokeWidth, String markerId, Node parent){
       this.x1 = x1;
       this.x2 = x2;
       this.y1 = y1;
       this.y2 = y2;
       this.strokeWidth = strokeWidth;
       this.markerId = markerId;
       this.parent = parent;
   }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Node getParent() {
        return parent;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isStraight(){
        return x1 == x2 || y1 == y2;
    }
    public boolean isVertical(){
       return x1 == x2;
    }
}
