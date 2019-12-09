import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

public class SVGParser {
    public static void main(String[] args) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String uri = args[1];
            Document doc = f.createDocument(uri);
            Validator validator = new Validator(doc);

        } catch (Exception ex) {
            ex.printStackTrace();
            // ...
        }
    }
}
