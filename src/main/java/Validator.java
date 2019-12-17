import org.apache.xpath.operations.Mod;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {

    private Document document;
    private float viewHeight;
    private float viewWidth;
    private List<Model> baseAtomics;
    private List<Model> baseCoupled;
    private List<Connection> baseConnections;
    private Element topModelSVG;
    private Map<String, Model> modelMap;

    public Validator(Document document) {
        this.document = document;
        topModelSVG = document.getElementById("TOP");
        baseAtomics = new ArrayList<>();
        baseCoupled = new ArrayList<>();
        baseConnections = new ArrayList<>();

        List<Node> baseNodes = NodeParser.getFilteredNodeList(topModelSVG.getChildNodes());

        for (int i = 0; i < baseNodes.size(); i++){
            if (NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("submodel") &&
                    NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "type").equals("coupled")){
                baseCoupled.add(NodeParser.parseCoupledModel(baseNodes.get(i)));
            } else if ((NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("submodel") &&
                    NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "type").equals("atomic"))){
                baseAtomics.add(NodeParser.parseAtomicModel(baseNodes.get(i)));
            } else if ((NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("connections"))){
                baseConnections = NodeParser.parseConnections(baseNodes.get(i));
            }
        }

        mapModels(baseAtomics, baseCoupled);

    }

    public boolean runValidation() {
        return verifySVG() &&
        isMarkerValid() &&
        isTopModelValid();
    }

    private boolean verifySVG() {
        if (!document.getFirstChild().getNodeName().equals("svg")) {
            System.err.println("Invalid SVG document, please check root svg tag.");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("version")) {
            System.err.println("Please specify an SVG version 1.1 or greater");
            return false;
        }
        if (Float.parseFloat(document.getDocumentElement().getAttribute("version")) < 1.1 ||
                Float.parseFloat(document.getDocumentElement().getAttribute("version")) > 2.0) {
            System.err.println("SVG version cannot be less than 1.1 or greater than 2");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("height")) {
            System.err.println("Please specify the height of the SVG document");
            return false;
        }
        if (!document.getDocumentElement().hasAttribute("width")) {
            System.err.println("Please specify the width of the SVG document");
            return false;
        }
        if (document.getDocumentElement().hasAttribute("viewBox") && !isValidViewBoxRatio()) {
            System.err.println("Invalid ViewBox specification");
            return false;
        }
        return true;
    }

    private boolean isValidViewBoxRatio() {
        float height;
        float width;

        String[] view = document.getDocumentElement().getAttribute("viewBox").split("\\s");
        if (!view[0].equals("0") || !view[1].equals("0")) {
            System.err.println("ViewBox 'x' and 'y' should both equal to zero '0'");
            return false;
        }
        viewWidth = Float.parseFloat(view[2]);
        viewHeight = Float.parseFloat(view[3]);

        String h = document.getDocumentElement().getAttribute("height");
        height = Float.parseFloat(h.substring(0, h.length() - 2));

        String w = document.getDocumentElement().getAttribute("width");
        width = Float.parseFloat(w.substring(0, w.length() - 2));

        if ((width / height) != (viewWidth / viewHeight)) {
            System.err.println("ViewBox has improper width to height ratio");
            return false;
        }

        return true;
    }

    private boolean isMarkerValid() {
        String path = "M 0 0 L 10 5 L 0 10 z";
        String viewBox = "0 0 10 10";
        String ref = "5";
        NodeList nodeList = document.getElementsByTagName("marker");
        boolean validAttributes;

        if (nodeList.getLength() == 0) {
            System.err.println("Please specify a marker for connections");
            return false;
        } else if (nodeList.getLength() > 1) {
            System.err.println("Only one marker can be specified per document");
            return false;
        }
        if (!nodeList.item(0).getParentNode().getLocalName().equals("defs")) {
            System.err.println("All markers must be defined inside a <defs> tag");
            return false;
        }

        validAttributes = isValidAttribute(nodeList.item(0).getAttributes(), "markerWidth", "6")
                && isValidAttribute(nodeList.item(0).getAttributes(), "markerHeight", "6") &&
                isValidAttribute(nodeList.item(0).getAttributes(), "viewBox", viewBox) &&
                isValidAttribute(nodeList.item(0).getAttributes(), "refX", ref) &&
                isValidAttribute(nodeList.item(0).getAttributes(), "refY", ref) &&
                isValidAttribute(nodeList.item(0).getAttributes(), "orient", "auto-start-reverse");

        for (int i = 0; i < nodeList.item(0).getChildNodes().getLength(); i++) {
            if (nodeList.item(0).getChildNodes().item(i).getNodeName().equals("path")) {
                validAttributes = validAttributes &&
                        isValidAttribute(nodeList.item(0).getChildNodes().item(i).getAttributes(), "d", path);
            }
        }


        return validAttributes;
    }

    private boolean isValidAttribute(NamedNodeMap attribute, String name, String value) {
        if (!attribute.getNamedItem(name).getTextContent().equals(value)) {
            System.err.println("Invalid " + name + ", set " + name + " to '" + value + "'");
            return false;
        }
        return true;
    }

    private boolean isTopModelValid() {
        Element element = document.getElementById("TOP");
        if (element == null) {
            System.err.println("Please define top model with 'id' 'TOP'");
            return false;
        }
        return true;
    }

    private void mapModels(List<Model> atomics, List<Model> coupled){
        modelMap = new HashMap<>();
        for(Model atomic : atomics){
            modelMap.put(atomic.getId(), atomic);
        }
        for (Model couple : coupled ){
            modelMap.put(couple.getId(), couple);
        }
    }

    public Document getDocument() {
        return document;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public List<Model> getBaseAtomics() {
        return baseAtomics;
    }

    public List<Model> getBaseCoupled() {
        return baseCoupled;
    }

    public List<Connection> getBaseConnections() {
        return baseConnections;
    }

    public Element getTopModelSVG() {
        return topModelSVG;
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }
}
