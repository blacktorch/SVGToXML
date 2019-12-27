package validation;

import connections.Connection;
import connections.EIC;
import connections.EOC;
import connections.IC;
import graphics.SVGRect;
import graphics.SVGText;
import models.CoupledModel;
import models.Model;
import org.w3c.dom.*;
import parser.NodeParser;

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
    private String markerId;
    private boolean isModelValid;

    public Validator(Document document) {
        this.document = document;
        topModelSVG = document.getElementById("TOP");
        baseAtomics = new ArrayList<>();
        baseCoupled = new ArrayList<>();
        baseConnections = new ArrayList<>();

        List<Node> baseNodes = NodeParser.getFilteredNodeList(topModelSVG.getChildNodes());

        for (int i = 0; i < baseNodes.size(); i++) {
            if (NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("submodel") &&
                    NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "type").equals("coupled")) {
                baseCoupled.add(NodeParser.parseCoupledModel(baseNodes.get(i)));
            } else if ((NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("submodel") &&
                    NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "type").equals("atomic"))) {
                baseAtomics.add(NodeParser.parseAtomicModel(baseNodes.get(i)));
            } else if ((NodeParser.getAttributeValue(baseNodes.get(i).getAttributes(), "component").equals("connections"))) {
                baseConnections = NodeParser.parseConnections(baseNodes.get(i));
            }
        }

        modelMap = new HashMap<>();
        mapModels(baseAtomics, baseCoupled);

    }

    public boolean runValidation() {
        boolean isValid = verifySVG() &&
                isMarkerValid() &&
                isTopModelValid();
        isModelsValid(baseCoupled);
        return  isValid && isModelValid;
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

        markerId = NodeParser.getAttributeValue(nodeList.item(0).getAttributes(), "id");

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

        validAttributes = (!markerId.equals("__NULL__"));


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

    private boolean isValidConnection(Connection connection) {
        if (connection.getLine() != null) {
            if (!connection.getLine().getMarkerId().equals(markerId)) {
                System.err.println("Incorrect marker used for " + connection.getText().getText() + " connection");
                return false;
            }
            switch (connection.getType()) {
                case "eoc":
                    EOC eoc = (EOC) connection;
                    Model eocModel = modelMap.get(eoc.getSubModel());
                    if (!eoc.getLine().isStraight()) {
                        System.err.println("connections.Connection " + eoc.getText().getText() + " is not straight, please check line");
                        return false;
                    }
                    /*Check if orientation is horizontal*/
                    if (!eoc.getLine().isVertical() && eoc.getLine().getX1() > eoc.getLine().getX2()) {
                        if (!isWithinRange(eoc.getLine().getX1(), eocModel.getGraphics().getRect().getX(), 3)) {
                            connectionErrorMessage(connection, eocModel, eocModel);
                            return false;
                        }
                    } else if (!eoc.getLine().isVertical() && eoc.getLine().getX1() < eoc.getLine().getX2()) {
                        if (!isWithinRange(eoc.getLine().getX1(), eocModel.getGraphics().getRect().getX() + eocModel.getGraphics().getRect().getWidth(), 3)) {
                            connectionErrorMessage(connection, eocModel, eocModel);
                            return false;
                        }
                    } else if (eoc.getLine().isVertical() && eoc.getLine().getY1() > eoc.getLine().getY2()) {
                        if (!isWithinRange(eoc.getLine().getY1(), eocModel.getGraphics().getRect().getY(), 3)) {
                            connectionErrorMessage(connection, eocModel, eocModel);
                            return false;
                        }
                    } else if (eoc.getLine().isVertical() && eoc.getLine().getY1() < eoc.getLine().getY2()) {
                        if (!isWithinRange(eoc.getLine().getY1(), eocModel.getGraphics().getRect().getY() + eocModel.getGraphics().getRect().getHeight(), 3)) {
                            connectionErrorMessage(connection, eocModel, eocModel);
                            return false;
                        }
                    }
                    break;
                case "eic":
                    EIC eic = (EIC) connection;
                    Model eicModel = modelMap.get(eic.getSubModel());
                    if (!eic.getLine().isStraight()) {
                        System.err.println("connections.Connection " + eic.getText().getText() + " is not straight, please check line");
                        return false;
                    }
                    /*Check if orientation is horizontal*/
                    if (!eic.getLine().isVertical() && eic.getLine().getX1() > eic.getLine().getX2()) {
                        if (!isWithinRange(eic.getLine().getX2(), eicModel.getGraphics().getRect().getX() + eicModel.getGraphics().getRect().getWidth(), 6)) {
                            connectionErrorMessage(connection, eicModel, eicModel);
                            return false;
                        }
                    } else if (!eic.getLine().isVertical() && eic.getLine().getX1() < eic.getLine().getX2()) {
                        if (!isWithinRange(eic.getLine().getX2(), eicModel.getGraphics().getRect().getX(), 6)) {
                            connectionErrorMessage(connection, eicModel, eicModel);
                            return false;
                        }
                    } else if (eic.getLine().isVertical() && eic.getLine().getY1() > eic.getLine().getY2()) {
                        if (!isWithinRange(eic.getLine().getY2(), eicModel.getGraphics().getRect().getY() + eicModel.getGraphics().getRect().getHeight(), 6)) {
                            connectionErrorMessage(connection, eicModel, eicModel);
                            return false;
                        }
                    } else if (eic.getLine().isVertical() && eic.getLine().getY1() < eic.getLine().getY2()) {
                        if (!isWithinRange(eic.getLine().getY2(), eicModel.getGraphics().getRect().getY(), 6)) {
                            connectionErrorMessage(connection, eicModel, eicModel);
                            return false;
                        }
                    }
                    break;
                case "ic":
                    IC ic = (IC) connection;
                    Model modelFrom = modelMap.get(ic.getFrom());
                    Model modelTo = modelMap.get(ic.getTo());
                    if (!ic.getLine().isStraight()) {
                        System.err.println("connections.Connection " + ic.getText().getText() + " is not straight, please check line");
                        return false;
                    }
                    /*Check if orientation is horizontal*/
                    if (!ic.getLine().isVertical() && ic.getLine().getX1() > ic.getLine().getX2()) {
                        if (!isWithinRange(ic.getLine().getX2(), modelTo.getGraphics().getRect().getX() + modelTo.getGraphics().getRect().getWidth(), 6) ||
                                !isWithinRange(ic.getLine().getX1(), modelFrom.getGraphics().getRect().getX(), 3)) {
                            connectionErrorMessage(connection, modelFrom, modelTo);
                            return false;
                        }
                    } else if (!ic.getLine().isVertical() && ic.getLine().getX1() < ic.getLine().getX2()) {
                        if (!isWithinRange(ic.getLine().getX1(), modelFrom.getGraphics().getRect().getX() + modelFrom.getGraphics().getRect().getWidth(), 3) ||
                                !isWithinRange(ic.getLine().getX2(), modelTo.getGraphics().getRect().getX(), 6)) {
                            connectionErrorMessage(connection, modelFrom, modelTo);
                            return false;
                        }
                    } else if (ic.getLine().isVertical() && ic.getLine().getY1() > ic.getLine().getY2()) {
                        if (!isWithinRange(ic.getLine().getY2(), modelTo.getGraphics().getRect().getY() + modelTo.getGraphics().getRect().getHeight(), 6) ||
                                !isWithinRange(ic.getLine().getY1(), modelFrom.getGraphics().getRect().getY(), 3)) {
                            connectionErrorMessage(connection, modelFrom, modelTo);
                            return false;
                        }
                    } else if (ic.getLine().isVertical() && ic.getLine().getY1() < ic.getLine().getY2()) {
                        if (!isWithinRange(ic.getLine().getY1(), modelFrom.getGraphics().getRect().getY() + modelFrom.getGraphics().getRect().getHeight(), 3) ||
                                !isWithinRange(ic.getLine().getY2(), modelTo.getGraphics().getRect().getY(), 6)) {
                            connectionErrorMessage(connection, modelFrom, modelTo);
                            return false;
                        }
                    }
                    break;
            }
        }

        return true;
    }

    private void connectionErrorMessage(Connection connection, Model a, Model b) {
        if (connection.getType().equals("eoc") || connection.getType().equals("eic")) {
            System.err.println("Check connection " + connection.getText().getText() + " with " + a.getGraphics().getText().getText());
        } else {
            System.err.println("Check connection " + connection.getText().getText() + " between " +
                    a.getGraphics().getText().getText() + " and " + b.getGraphics().getText().getText());
        }
    }

    private void isModelsValid(List<Model> models) {

        for (Model model : models) {
            if (model.getType().equals("coupled")) {
                CoupledModel coupledModel = (CoupledModel) model;

                if (validateModel(coupledModel)) {
                    for (Connection connection : coupledModel.getConnections()) {
                        isModelValid = isValidConnection(connection);
                        if (!isModelValid) {
                            break;
                        }
                    }
                    if (!isModelValid) {
                        break;
                    }
                    for (Model atomic : coupledModel.getAtomicSubModels()) {
                        isModelValid = validateModel(atomic);
                        if (!isModelValid) {
                            break;
                        }
                    }
                    if (!isModelValid) {
                        break;
                    }

                    isModelsValid(coupledModel.getCoupledSubModels());
                }
            } else {
                isModelValid = validateModel(model);
            }

        }

    }

    private boolean validateModel(Model model) {
        boolean isValid = true;
        String parentId = NodeParser.getAttributeValue(model.getParent().getAttributes(), "id");
        if (!parentId.equals("TOP")) {
            Model parent = modelMap.get(parentId);
            if (parent.getGraphics() != null) {
                if (isRectIntersect(parent.getGraphics().getRect(), model.getGraphics().getRect())) {
                    System.err.println("models.Model " + model.getGraphics().getText().getText() + " is out of parent bounds");
                    isValid = false;
                } else if (isTextOutOfBounds(model.getGraphics().getRect(), model.getGraphics().getText())) {
                    System.err.println("Text " + model.getGraphics().getText().getText() + " is out of bounds " + model.getId());
                    isValid = false;
                }
            }

        }


        return isValid;
    }

    private boolean isWithinRange(int a, int b, int range) {
        if (a == b) {
            return true;
        } else if (a < b && ((b - a) <= range)) {
            return true;
        } else if (a > b && ((a - b) <= range)) {
            return true;
        }

        return false;
    }

    private boolean isRectIntersect(SVGRect a, SVGRect b) {
        return b.getX() < a.getX() || (a.getWidth() + a.getX()) < (b.getX() + b.getWidth()) || b.getY() < a.getY() || (a.getHeight() + a.getY()) < (b.getY() + b.getHeight());
    }

    private boolean isTextOutOfBounds(SVGRect a, SVGText b) {
        return b.getX() < a.getX() || b.getX() > (a.getX() + a.getWidth()) || b.getY() < a.getY() || b.getY() > (a.getY() + a.getHeight());
    }

    private void mapModels(List<Model> atomics, List<Model> coupled) {

        for (Model atomic : atomics) {
            modelMap.put(atomic.getId(), atomic);
        }
        for (Model couple : coupled) {
            modelMap.put(couple.getId(), couple);
            CoupledModel coupledModel = (CoupledModel) couple;
            mapModels(coupledModel.getAtomicSubModels(), coupledModel.getCoupledSubModels());
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
