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

                baseNodes = NodeParser.getFilteredNodeList(topModelSVG.getChildNodes());

                NodeParser.parseConnections(baseNodes.get(2));
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


}
