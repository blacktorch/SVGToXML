import org.w3c.dom.Node;

import java.util.List;

public class AtomicModel extends Model {

    public AtomicModel(String type, String id, String cppClass, List<Parameter> parameters, Graphics graphics, Node parent){
        this.setType(type);
        this.setId(id);
        this.setCppClass(cppClass);
        this.setParameters(parameters);
        this.setGraphics(graphics);
        this.setParent(parent);
    }

}
