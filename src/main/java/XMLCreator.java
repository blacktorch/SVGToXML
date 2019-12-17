import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLCreator {
    private String directory;
    private Validator validator;
    private Element topModelSVG;
    private List<Model> baseAtomics;
    private List<Model> baseCoupled;
    private List<Connection> baseConnections;


    public XMLCreator(String directory, Validator validator) {
        this.directory = directory;
        this.validator = validator;

        File xmlDirectory = new File(directory);

        if (!xmlDirectory.exists()) {
            xmlDirectory.mkdir();
        }
        topModelSVG = validator.getDocument().getElementById("TOP");
        baseAtomics = new ArrayList<>();
        baseCoupled = new ArrayList<>();
        baseConnections = new ArrayList<>();
    }

    public void createTopModel() {
        File topModel = new File(directory, "TOP.xml");
        if (topModel.exists()) {
            topModel.delete();
        }
        try {
            if (topModel.createNewFile()) {
                List<Node> baseNodes;
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();

                Element root = document.createElement("coupledModel");
                document.appendChild(root);

                Attr attr = document.createAttribute("name");
                attr.setValue("TOP");
                root.setAttributeNode(attr);

                baseNodes = getFilteredNodeList(topModelSVG.getChildNodes());

                parseConnections(baseNodes.get(2));
                //System.out.println(baseNodes.get(2).getAttributes().getNamedItem("component").getTextContent());

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(topModel);
                transformer.transform(domSource, streamResult);
            }

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }

    private List<Connection> parseConnections(Node connection){
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < connection.getChildNodes().getLength(); i++){
            if (connection.getChildNodes().item(i).getLocalName() != null){
                Node con = connection.getChildNodes().item(i);
                NamedNodeMap attr = con.getAttributes();
                String type = attr.getNamedItem("type").getTextContent();

                switch (type){
                    case "eoc":
                        Connection eoc = parseEOC(con);
                        connections.add(eoc);
                        break;
                    case "eic":
                        Connection eic = parseEIC(con);
                        connections.add(eic);
                        break;
                    case "ic":
                        Connection ic = parseIC(con);
                        connections.add(ic);
                        break;
                }
            }
        }
        return connections;
    }

    private SVGText parseText(Node textNode){
        int x = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "x"));
        int y = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "y"));
        int fontSize = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "font-size"));
        int strokeWidth = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "stroke-width"));
        String text = textNode.getTextContent();

        return new SVGText(x, y, fontSize,strokeWidth, text, textNode.getParentNode());
    }

    private SVGLine parseLine(Node lineNode){
        int x1 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "x1"));
        int x2 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "x2"));
        int y1 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "y1"));
        int y2 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "y2"));
        int strokeWidth = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "stroke-width"));
        return new SVGLine(x1, x2, y1, y2, strokeWidth, lineNode.getParentNode());
    }

    private EOC parseEOC(Node eocNode){
        SVGLine line = null;
        SVGText text = null;
        NamedNodeMap attr = eocNode.getAttributes();
        String outCoupled = getAttributeValue(attr, "out-coupled");
        String subModel = getAttributeValue(attr, "submodel");
        String outSubModel = getAttributeValue(attr, "out-submodel");
        String messageType = getAttributeValue(attr, "message-type");
        List<Node> filtered = getFilteredNodeList(eocNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (filtered.get(j).getLocalName().equals("text")){
                    text = parseText(filtered.get(j));
                } else if (filtered.get(j).getLocalName().equals("line")){
                    line = parseLine(filtered.get(j));
                }

            }
        }

        return new EOC("eoc", outCoupled, outSubModel, messageType, subModel, line, text);

    }

    private EIC parseEIC(Node eicNode){
        SVGLine line = null;
        SVGText text = null;
        NamedNodeMap attr = eicNode.getAttributes();
        String inCoupled = getAttributeValue(attr, "in-coupled");
        String subModel = getAttributeValue(attr, "submodel");
        String inSubModel = getAttributeValue(attr, "in-submodel");
        String messageType = getAttributeValue(attr, "message-type");
        List<Node> filtered = getFilteredNodeList(eicNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (filtered.get(j).getLocalName().equals("text")){
                    text = parseText(filtered.get(j));
                } else if (filtered.get(j).getLocalName().equals("line")){
                    line = parseLine(filtered.get(j));
                }

            }
        }

        return new EIC("eic", inCoupled, inSubModel, messageType, subModel, line, text);

    }

    private IC parseIC(Node icNode){
        SVGLine line = null;
        SVGText text = null;
        NamedNodeMap attr = icNode.getAttributes();
        String from = getAttributeValue(attr, "from");
        String outFrom = getAttributeValue(attr, "out-from");
        String to = getAttributeValue(attr, "to");
        String inTo = getAttributeValue(attr, "in-to");
        String messageType = getAttributeValue(attr, "message-type");
        List<Node> filtered = getFilteredNodeList(icNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (filtered.get(j).getLocalName().equals("text")){
                    text = parseText(filtered.get(j));
                } else if (filtered.get(j).getLocalName().equals("line")){
                    line = parseLine(filtered.get(j));
                }

            }
        }

        return new IC("ic",from, outFrom, to, inTo,messageType,line,text);
    }

    private List<Node> getFilteredNodeList(NodeList nodeList){
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++){
            if (nodeList.item(i).getLocalName() != null){
                list.add(nodeList.item(i));
            }
        }

        return list;
    }

    private String getAttributeValue(NamedNodeMap attr, String name){
        return attr.getNamedItem(name).getTextContent();
    }
}
