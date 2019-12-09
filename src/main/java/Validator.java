import org.w3c.dom.Document;

public class Validator {

    private Document document;

    public Validator(Document document){
        this.document = document;
        verifySVG();

    }

    private boolean verifySVG(){
        if (!document.getFirstChild().getNodeName().equals("svg")){
            System.err.println("Invalid SVG document, please check root svg tag.");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("version")){
            System.err.println("Please specify an SVG version 1.1 or greater");
            return false;
        }
        if (Float.parseFloat(document.getDocumentElement().getAttribute("version")) < 1.1 ||
                Float.parseFloat(document.getDocumentElement().getAttribute("version")) > 2.0){
            System.err.println("SVG version cannot be less than 1.1 or greater than 2");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("height")){
            System.err.println("Please specify the height of the SVG document");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("width")){
            System.err.println("Please specify the width of the SVG document");
            return false;
        }
        if (document.getDocumentElement().hasAttribute("viewBox")){
            isValidViewBoxRatio();
        }
        return true;
    }

    private boolean isValidViewBoxRatio(){
        float viewHeight;
        float viewWidth;
        float height;
        float width;

        String[] view = document.getDocumentElement().getAttribute("viewBox").split("\\s");
        if (!view[0].equals("0") || !view[1].equals("0")){
            System.err.println("ViewBox 'x' and 'y' should both equal to zero '0'");
            return false;
        }
        viewWidth = Float.parseFloat(view[2]);
        viewHeight = Float.parseFloat(view[3]);

        String h = document.getDocumentElement().getAttribute("height");
        height = Float.parseFloat(h.substring(0, h.length()-2));

        String w = document.getDocumentElement().getAttribute("width");
        width = Float.parseFloat(w.substring(0, w.length()-2));

        if ((width/height) != (viewWidth/viewHeight)){
            System.err.println("ViewBox has improper width to height ratio");
            return false;
        }


        return true;
    }


}
