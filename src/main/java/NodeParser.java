import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public final class NodeParser {
    private NodeParser(){
        //Do not initialize...
    }

    public static List<Connection> parseConnections(Node connection){
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

    public static SVGText parseText(Node textNode){
        int x = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "x"));
        int y = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "y"));
        int fontSize = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "font-size"));
        int strokeWidth = Integer.parseInt(getAttributeValue(textNode.getAttributes(), "stroke-width"));
        String text = textNode.getTextContent();

        return new SVGText(x, y, fontSize,strokeWidth, text, textNode.getParentNode());
    }

    public static SVGRect parseRect(Node rectNode){
        int x = Integer.parseInt(getAttributeValue(rectNode.getAttributes(), "x"));
        int y = Integer.parseInt(getAttributeValue(rectNode.getAttributes(), "y"));
        int width = Integer.parseInt(getAttributeValue(rectNode.getAttributes(), "width"));
        int height = Integer.parseInt(getAttributeValue(rectNode.getAttributes(), "height"));

        return new SVGRect(x, y, width,height,rectNode.getParentNode());
    }

    public static SVGLine parseLine(Node lineNode){
        String markerId = null;
        int x1 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "x1"));
        int x2 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "x2"));
        int y1 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "y1"));
        int y2 = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "y2"));
        int strokeWidth = Integer.parseInt(getAttributeValue(lineNode.getAttributes(), "stroke-width"));

        if (!getAttributeValue(lineNode.getAttributes(), "marker-end").equals("__NULL__")){
            String[] split = getAttributeValue(lineNode.getAttributes(), "marker-end").split("#");
            markerId = split[1].substring(0, split[1].length() -1);
        }
        return new SVGLine(x1, x2, y1, y2, strokeWidth, markerId, lineNode.getParentNode());
    }

    public static Graphics parseGraphics(Node graphNode){
        SVGRect rect = null;
        SVGLine line = null;
        SVGText text = null;

        List<Node> filtered = getFilteredNodeList(graphNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (filtered.get(j).getLocalName().equals("text")){
                    text = parseText(filtered.get(j));
                } else if (filtered.get(j).getLocalName().equals("line")){
                    line = parseLine(filtered.get(j));
                } else if (filtered.get(j).getLocalName().equals("rect")){
                    rect = parseRect(filtered.get(j));
                }

            }
        }

        return new Graphics(rect, text, line, graphNode.getParentNode());
    }

    public static EOC parseEOC(Node eocNode){
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

    public static EIC parseEIC(Node eicNode){
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

    public  static IC parseIC(Node icNode){
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

    private static List<Parameter> retrieveParameters(NamedNodeMap attr){
        List<Parameter> parameters = new ArrayList<>();
        boolean hasParam = Boolean.parseBoolean(getAttributeValue(attr, "has-param"));
        if (hasParam){
            int paramNo = Integer.parseInt(getAttributeValue(attr, "param-no"));
            for (int i = 0; i < paramNo; i++){
                String type = getAttributeValue(attr, "param" + (i+1) + "-type");
                String name = getAttributeValue(attr,"param" + (i+1) + "-name");
                String value = getAttributeValue(attr,"param" + (i+1) + "-value");
                Parameter parameter = new Parameter(type,name,value);
                parameters.add(parameter);
            }

        }
        return parameters;
    }

    public  static AtomicModel parseAtomicModel(Node atomicNode){
        NamedNodeMap attr = atomicNode.getAttributes();
        List<Parameter> parameters = retrieveParameters(attr);
        String id = getAttributeValue(attr, "id");
        String cppClass = getAttributeValue(attr, "class");
        Graphics graphics = null;

        List<Node> filtered = getFilteredNodeList(atomicNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (getAttributeValue(filtered.get(j).getAttributes(), "component").equals("graphics")){
                    graphics = parseGraphics(filtered.get(j));
                }
            }
        }

        AtomicModel atomicModel = new AtomicModel("atomic", id, cppClass, parameters, graphics, atomicNode.getParentNode());
        atomicModel.setHasParameters(!parameters.isEmpty());
        return atomicModel;

    }

    public static CoupledModel parseCoupledModel(Node coupledNode){
        NamedNodeMap attr = coupledNode.getAttributes();
        List<Parameter> parameters = retrieveParameters(attr);
        List<Model> atomicSubModels = new ArrayList<>();
        List<Model> coupledSubModels = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        String id = getAttributeValue(attr, "id");
        String cppClass = getAttributeValue(attr, "class");
        Graphics graphics = null;


        List<Node> filtered = getFilteredNodeList(coupledNode.getChildNodes());
        if (filtered.size() > 0){
            for(int j = 0; j < filtered.size(); j++){
                if (getAttributeValue(filtered.get(j).getAttributes(), "component").equals("graphics")){

                    graphics = parseGraphics(filtered.get(j));

                } else if (getAttributeValue(filtered.get(j).getAttributes(), "component").equals("submodel") &&
                        getAttributeValue(filtered.get(j).getAttributes(), "type").equals("atomic")){

                    atomicSubModels.add(parseAtomicModel(filtered.get(j)));

                } else if (getAttributeValue(filtered.get(j).getAttributes(), "component").equals("connections")){

                    connections = parseConnections(filtered.get(j));

                } else if (getAttributeValue(filtered.get(j).getAttributes(), "component").equals("submodel") &&
                        getAttributeValue(filtered.get(j).getAttributes(), "type").equals("coupled")){

                    coupledSubModels.add(parseCoupledModel(filtered.get(j)));
                }
            }
        }

        CoupledModel coupledModel = new CoupledModel("coupled", atomicSubModels, coupledSubModels, id, cppClass, parameters,graphics, coupledNode.getParentNode(), connections);
        coupledModel.setHasParameters(!parameters.isEmpty());
        return coupledModel;
    }

    public static List<Node> getFilteredNodeList(NodeList nodeList){
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++){
            if (nodeList.item(i).getLocalName() != null){
                list.add(nodeList.item(i));
            }
        }

        return list;
    }

    public static String getAttributeValue(NamedNodeMap attr, String name){

        if (attr.getNamedItem(name) != null){
            //System.out.println(attr.getNamedItem(name).getTextContent());
            return attr.getNamedItem(name).getTextContent();
        } else {
            return "__NULL__";
        }

    }
}
