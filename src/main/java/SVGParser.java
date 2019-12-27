import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import parser.XMLCreator;
import validation.Validator;

import java.io.File;

public class SVGParser {
    public static void main(String[] args) {
        try {
            boolean svgValid;
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String dir = "."+ File.separator+"svgfiles"+ File.separator;
            String file = dir + args[0];
            String uri = file;
            Document doc = f.createDocument(uri);
            Validator validator = new Validator(doc);
            svgValid = validator.runValidation();

            if (svgValid){
                XMLCreator xmlCreator = new XMLCreator("."+ File.separator+"XMLFiles", validator);
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
