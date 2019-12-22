import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

public class SVGParser {
    public static void main(String[] args) {
        try {
            boolean svgValid;
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String uri = args[1];
            Document doc = f.createDocument(uri);
            Validator validator = new Validator(doc);
            svgValid = validator.runValidation();

            if (svgValid){
                XMLCreator xmlCreator = new XMLCreator("/Users/chidiebereonyedinma/Desktop/XMLFiles", validator);
                xmlCreator.createTopModel();
                xmlCreator.createModels(validator.getBaseCoupled());
            } else {
                System.err.println("Invalid SVG File, please make sure SVG follows SVG DEVS Standard");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
