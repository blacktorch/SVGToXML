package parser;

import connections.Connection;
import connections.EIC;
import connections.EOC;
import connections.IC;
import models.AtomicModel;
import models.CoupledModel;
import models.Model;
import models.Parameter;
import org.w3c.dom.*;
import validation.Validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class XMLCreator {
    private String directory;
    private Validator validator;


    private final static String XML_FILE = ".xml";

    private final static String PORT_STREAM_PREFIX = "iestream_input_defs<";
    private final static String PORT_STREAM_SUFFIX = ">::out";
    private final static String PORT_SUFFIX = "_defs::";


    public XMLCreator(String directory, Validator validator) {
        this.directory = directory;
        this.validator = validator;

        File xmlDirectory = new File(directory);

        if (xmlDirectory.exists()) {
            xmlDirectory.delete();

        }
        xmlDirectory.mkdir();

    }

    public void createCoupledModel(String modelId, List<Model> atomics, List<Model> coupled, List<Connection> connections){
        File coupledModelFile = new File(directory, modelId+XML_FILE);
        if (coupledModelFile.exists()) {
            coupledModelFile.delete();
        }
        try {
            if (coupledModelFile.createNewFile()) {

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();

                Element root = document.createElement("coupledModel");
                document.appendChild(root);

                Attr attr = document.createAttribute("name");
                attr.setValue(modelId);
                root.setAttributeNode(attr);

                writePorts(document, root, connections);
                writeComponents(document, root, atomics, coupled);
                writeConnections(document, root,connections);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(coupledModelFile);
                transformer.transform(domSource, streamResult);
            }

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            System.err.println("Encountered error while creating coupled model file, deleting...");
            coupledModelFile.delete();
        }
    }

    public void createTopModel() {
        createCoupledModel("TOP", validator.getBaseAtomics(), validator.getBaseCoupled(),
                validator.getBaseConnections());

    }

    public void createModels(List<Model> models){
        for (Model model : models) {
            if (model.getType().equals("coupled")){
                CoupledModel coupledModel = (CoupledModel)model;
                createCoupledModel(coupledModel.getId(), coupledModel.getAtomicSubModels(),
                        coupledModel.getCoupledSubModels(), coupledModel.getConnections());
                createModels(coupledModel.getCoupledSubModels());
            }

        }

    }

    private void writePorts(Document doc, Element root, List<Connection> connections) {
        Element ports = doc.createElement("ports");
        root.appendChild(ports);
        for (Connection connection : connections) {
            Element port = doc.createElement("port");
            switch (connection.getType()) {
                case "eoc":
                    EOC eoc = (EOC) connection;
                    ports.appendChild(port);
                    port.setAttribute("type", "out");
                    port.setAttribute("name", eoc.getOutCoupled());
                    port.setAttribute("message_type", eoc.getMessageType());
                    break;
                case "eic":
                    EIC eic = (EIC) connection;
                    ports.appendChild(port);
                    port.setAttribute("type", "in");
                    port.setAttribute("name", eic.getInCoupled());
                    port.setAttribute("message_type", eic.getMessageType());
                    break;
                case "ic":
                    IC ic = (IC) connection;
                    if (ic.getFrom().equals("input_reader")) {
                        ports.appendChild(port);
                        port.setAttribute("type", "ir");
                        String messageTypeCap = ic.getMessageType().substring(0, 1).toUpperCase() + ic.getMessageType().substring(1);
                        port.setAttribute("name", "InputReader_" + messageTypeCap);
                        port.setAttribute("message_type", ic.getMessageType());
                    }
                    break;
            }
        }
    }

    private void writeComponents(Document doc, Element root, List<Model> atomics, List<Model> coupled) {
        Element components = doc.createElement("components");
        root.appendChild(components);
        writeAtomicComponents(doc, components, atomics);
        writeCoupledComponents(doc, components, coupled);
    }

    private void writeAtomicComponents(Document doc, Element components, List<Model> atomics){
        for (Model model : atomics) {
            Element submodel = doc.createElement("submodel");
            AtomicModel atomicModel = (AtomicModel) model;
            submodel.setAttribute("type", atomicModel.getType());
            submodel.setAttribute("name", atomicModel.getId());
            submodel.setAttribute("class_name", atomicModel.getCppClass());
            if (atomicModel.getCppClass().contains("_")) {
                String[] impl = atomicModel.getCppClass().split("_");
                submodel.setAttribute("xml_implementation", impl[0]);
            } else {
                submodel.setAttribute("xml_implementation", atomicModel.getCppClass() + ".devs");
            }

            checkAndWriteParameters(atomicModel, doc, submodel);
            components.appendChild(submodel);
        }
    }

    private void writeCoupledComponents(Document doc, Element components, List<Model> coupled){
        for (Model model : coupled) {
            Element submodel = doc.createElement("submodel");
            CoupledModel coupledModel = (CoupledModel) model;
            submodel.setAttribute("type", coupledModel.getType());
            submodel.setAttribute("name", coupledModel.getId());
            submodel.setAttribute("class_name", coupledModel.getCppClass());
            submodel.setAttribute("xml_implementation", coupledModel.getId() + ".xml");

            checkAndWriteParameters(coupledModel, doc, submodel);
            components.appendChild(submodel);
        }
    }

    private void checkAndWriteParameters(Model model, Document doc, Element submodel){
        if (model.isHasParameters() && model.getParameters().size() != 0) {
            for (Parameter parameter : model.getParameters()) {
                Element param = doc.createElement("param");
                param.setAttribute("type", parameter.getType());
                param.setAttribute("name", parameter.getName());
                param.setAttribute("value", parameter.getValue());
                submodel.appendChild(param);
            }
        }
    }

    private void writeConnections(Document doc, Element root, List<Connection> connections){
        Element connection = doc.createElement("connections");
        root.appendChild(connection);
        for (Connection con : connections){
            switch (con.getType()){
                case "eic":
                    Element eic = doc.createElement("eic");
                    EIC eicConnection = (EIC) con;
                    eic.setAttribute("in_port_coupled", eicConnection.getInCoupled());
                    eic.setAttribute("submodel", eicConnection.getSubModel());
                    if (validator.getModelMap().get(eicConnection.getSubModel()) != null){
                        if (validator.getModelMap().get(eicConnection.getSubModel()).getType().equals("atomic")){
                            String inSunModel = validator.getModelMap().get(eicConnection.getSubModel()).getCppClass()
                                    + PORT_SUFFIX + eicConnection.getInSubModel();
                            eic.setAttribute("in_port_submodel", inSunModel);
                        } else {
                            eic.setAttribute("in_port_submodel", eicConnection.getInSubModel());
                        }

                    } else {
                        eic.setAttribute("in_port_submodel", eicConnection.getInSubModel());
                    }
                    connection.appendChild(eic);
                    break;

                case "eoc":
                    Element eoc = doc.createElement("eoc");
                    EOC eocConnection = (EOC)con;
                    eoc.setAttribute("submodel", eocConnection.getSubModel());
                    if (validator.getModelMap().get(eocConnection.getSubModel()) != null){
                        if (validator.getModelMap().get(eocConnection.getSubModel()).getType().equals("atomic")){
                            String outSunModel = validator.getModelMap().get(eocConnection.getSubModel()).getCppClass()
                                    + PORT_SUFFIX + eocConnection.getOutSubModel();
                            eoc.setAttribute("out_port_submodel", outSunModel);
                        } else {
                            eoc.setAttribute("out_port_submodel", eocConnection.getOutSubModel());
                        }

                    } else {
                        eoc.setAttribute("out_port_submodel", eocConnection.getOutSubModel());
                    }
                    eoc.setAttribute("out_port_coupled", eocConnection.getOutCoupled());
                    connection.appendChild(eoc);
                    break;

                case "ic":
                    Element ic = doc.createElement("ic");
                    IC icConnection = (IC) con;
                    ic.setAttribute("from_submodel", icConnection.getFrom());
                    ic.setAttribute("to_submodel", icConnection.getTo());


                    if (icConnection.getFrom().equals("input_reader")){
                        ic.setAttribute("out_port_from", PORT_STREAM_PREFIX + icConnection.getMessageType() + PORT_STREAM_SUFFIX);
                        if (validator.getModelMap().get(icConnection.getTo()).getType().equals("atomic")){
                            String inTo = validator.getModelMap().get(icConnection.getTo()).getCppClass()
                                    + PORT_SUFFIX + icConnection.getInTo();
                            ic.setAttribute("in_port_to", inTo);
                        } else {
                            ic.setAttribute("in_port_to", icConnection.getInTo());
                        }

                    } else {
                        if (validator.getModelMap().get(icConnection.getTo()).getType().equals("atomic")){
                            String inTo = validator.getModelMap().get(icConnection.getTo()).getCppClass()
                                    + PORT_SUFFIX + icConnection.getInTo();
                            ic.setAttribute("in_port_to", inTo);
                        } else {
                            ic.setAttribute("in_port_to", icConnection.getInTo());
                        }

                        if (validator.getModelMap().get(icConnection.getFrom()).getType().equals("atomic")){
                            String outFrom = validator.getModelMap().get(icConnection.getFrom()).getCppClass()
                                    + PORT_SUFFIX + icConnection.getOutFrom();
                            ic.setAttribute("out_port_from", outFrom);
                        } else {
                            ic.setAttribute("out_port_from", icConnection.getOutFrom());
                        }
                    }
                    connection.appendChild(ic);
                    break;

            }
        }
    }

}
