import org.w3c.dom.Node;

import java.util.List;

public class CoupledModel extends Model {
    private List<Model> subModels;
    private List<Connection> connections;

    public CoupledModel(String type, List<Model> subModels, String id, String cppClass, List<Parameter> parameters, Graphics graphics, Node parent, List<Connection> connections){
        this.setType(type);
        this.subModels = subModels;
        this.setId(id);
        this.setCppClass(cppClass);
        this.setParameters(parameters);
        this.setGraphics(graphics);
        this.setParent(parent);
        this.connections = connections;
    }

    public List<Model> getSubModels() {
        return subModels;
    }

    public void setSubModels(List<Model> subModels) {
        this.subModels = subModels;
    }
}
